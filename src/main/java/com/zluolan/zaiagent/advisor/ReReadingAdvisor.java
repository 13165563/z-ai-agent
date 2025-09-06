package com.zluolan.zaiagent.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 自定义 Re2 Advisor - 完整功能版
 * 使用正式的Prompt API来保留所有消息和历史
 */
public class ReReadingAdvisor implements CallAdvisor, StreamAdvisor {

    private static final Logger log = LoggerFactory.getLogger(ReReadingAdvisor.class);

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        ChatClientRequest modifiedRequest = processRequest(request);
        return chain.nextCall(modifiedRequest);
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        ChatClientRequest modifiedRequest = processRequest(request);
        return chain.nextStream(modifiedRequest);
    }

    /**
     * 处理请求：增强用户消息并保留所有上下文
     */
    private ChatClientRequest processRequest(ChatClientRequest originalRequest) {
        String originalUserText = extractUserText(originalRequest);
        log.info("Original user text: {}", originalUserText);
        if (originalUserText == null || originalUserText.trim().isEmpty()) {
            return originalRequest;
        }

        // 构建新的用户消息：原始问题 + 重新阅读指令
        String newUserText = String.format("""
                %s
                Read the question again: %s
                """, originalUserText, originalUserText);

        // 使用正式的API来增强用户消息，保留所有其他消息
        Prompt enhancedPrompt = originalRequest.prompt().augmentUserMessage(newUserText);
        log.info("Enhanced user text: {}", newUserText);
        return ChatClientRequest.builder()
                .prompt(enhancedPrompt)
                .context(originalRequest.context())
                .build();
    }

    /**
     * 提取用户消息文本
     */
    private String extractUserText(ChatClientRequest request) {
        try {
            // 方法1: 获取最后一条用户消息
            List<UserMessage> userMessages = request.prompt().getUserMessages();
            if (userMessages != null && !userMessages.isEmpty()) {
                UserMessage lastUserMessage = userMessages.get(userMessages.size() - 1);
                return lastUserMessage.getText();
            }

            // 方法2: 获取单条用户消息
            UserMessage userMessage = request.prompt().getUserMessage();
            if (userMessage != null) {
                return userMessage.getText();
            }

            // 方法3: 最后的手段
            return request.prompt().toString();

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getOrder() {
        return 50;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}