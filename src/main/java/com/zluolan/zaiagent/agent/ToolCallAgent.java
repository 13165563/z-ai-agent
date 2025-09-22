package com.zluolan.zaiagent.agent;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.zluolan.zaiagent.agent.modle.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 处理工具调用的基础代理类，具体实现了 think 和 act 方法，可以用作创建实例的父类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {

    // 可用的工具
    private final ToolCallback[] availableTools;

    // 保存了工具调用信息的响应
    private ChatResponse toolCallChatResponse;

    // 工具调用管理者
    private final ToolCallingManager toolCallingManager;

    // 禁用内置的工具调用机制，自己维护上下文
    private final ChatOptions chatOptions;

    // 首次注入的提示
    private String nextStepPrompt;
    private boolean injectedFirstStep = false;  // 标记位

    // ✅ 用正则匹配各种“任务完成”提示
    private static final Pattern FINISH_PATTERN =
            Pattern.compile(".*(已完成任务|任务完成|执行完毕|完成).*");

    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        // 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
        this.chatOptions = DashScopeChatOptions.builder()
                .withInternalToolExecutionEnabled(false)
                .build();
    }

    /**
     * 处理当前状态并决定下一步行动
     *
     * @return 是否需要执行行动
     */
    @Override
    public boolean think() {
        // ✅ 第一步：只在第一轮注入 nextStepPrompt
        if (!injectedFirstStep && nextStepPrompt != null) {
            getMessageList().add(new UserMessage(getNextStepPrompt()));
            injectedFirstStep = true;
        }

        List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList, chatOptions);
        try {
            // 获取带工具选项的响应
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .toolCallbacks(availableTools)
                    .call()
                    .chatResponse();

            // 记录响应，用于 Act
            this.toolCallChatResponse = chatResponse;
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();

            // 输出提示信息
            String result = assistantMessage.getText();
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            log.info("{} 的思考: {}", getName(), result);
            log.info("{} 选择了 {} 个工具来使用", getName(), toolCallList.size());

            String toolCallInfo = toolCallList.stream()
                    .map(toolCall -> String.format("工具名称：%s，参数：%s",
                            toolCall.name(),
                            toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            if (!toolCallInfo.isEmpty()) {
                log.info(toolCallInfo);
            }

            // ✅ 第二步：兜底逻辑（正则匹配）
            if (toolCallList.isEmpty()) {
                if (result != null && FINISH_PATTERN.matcher(result).find()) {
                    log.info("{} 没有工具调用，但输出包含完成提示 → 自动追加 doTerminate", getName());

                    // 构造一个 doTerminate 工具调用
                    AssistantMessage.ToolCall terminateCall =
                            new AssistantMessage.ToolCall(
                                    "call_terminate",   // id: 工具调用唯一标识
                                    "function",         // type: 调用类型
                                    "doTerminate",      // 工具名称
                                    "{}"                // 参数
                            );

                    // 用 metadata 包装，符合构造函数要求
                    Map<String, Object> metadata = Map.of("tool_calls", List.of(terminateCall));
                    assistantMessage = new AssistantMessage(result, metadata);

                    // 覆盖响应，后续 act() 可以正常执行
                    this.toolCallChatResponse =
                            new ChatResponse(List.of(new Generation(assistantMessage)));
                    return true;
                }
                // 没有工具调用，正常记录助手消息
                getMessageList().add(assistantMessage);
                return false;
            } else {
                // 有工具调用，不需要单独记录助手消息
                return true;
            }
        } catch (Exception e) {
            log.error("{} 的思考过程遇到了问题: {}", getName(), e.getMessage());
            getMessageList().add(
                    new AssistantMessage("处理时遇到错误: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 执行工具调用并处理结果
     *
     * @return 执行结果
     */
    @Override
    public String act() {
        if (!toolCallChatResponse.hasToolCalls()) {
            return "没有工具调用";
        }
        // 调用工具
        Prompt prompt = new Prompt(getMessageList(), chatOptions);
        ToolExecutionResult toolExecutionResult =
                toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);

        // 记录消息上下文，conversationHistory 已经包含了助手消息和工具调用返回的结果
        setMessageList(toolExecutionResult.conversationHistory());

        // 当前工具调用的结果
        ToolResponseMessage toolResponseMessage =
                (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());

        String results = toolResponseMessage.getResponses().stream()
                .map(response -> "工具 " + response.name() + " 完成了它的任务！结果: " + response.responseData())
                .collect(Collectors.joining("\n"));

        // 判断是否调用了终止工具
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> "doTerminate".equals(response.name()));
        if (terminateToolCalled) {
            setState(AgentState.FINISHED);
        }

        log.info(results);
        return results;
    }
}
