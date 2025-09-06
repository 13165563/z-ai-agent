package com.zluolan.zaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;

/**
 * 自定义日志 Advisor
 * 打印 info 级别日志、输出用户提示词和 AI 回复的文本
 */
@Slf4j
public class MyLoggerAdvisor implements CallAdvisor, StreamAdvisor {

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        // 记录用户输入 - 获取第一条用户消息的文本
        String userText = getFirstUserText(request);
        log.info("AI Request: {}", userText);

        // 继续执行链并获取最终响应
        ChatClientResponse response = chain.nextCall(request);

        // 记录AI回复的文本
        log.info("AI Response: {}", getResponseText(response));

        return response;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        // 记录用户输入
        String userText = getFirstUserText(request);
        log.info("AI Request (Stream): {}", userText);

        // 继续执行链并获取响应流
        Flux<ChatClientResponse> responseFlux = chain.nextStream(request);

        // 方案一：聚合后记录完整响应（简单）
        return new ChatClientMessageAggregator().aggregateChatClientResponse(responseFlux, aggregatedResponse -> {
            log.info("AI Full Response (Stream): {}", getResponseText(aggregatedResponse));
        });

        // 方案二：实时记录每个流块（推荐用于调试）
        // return responseFlux.doOnNext(chunk -> {
        //     String chunkText = getResponseText(chunk);
        //     if (chunkText != null && !chunkText.isEmpty()) {
        //         log.debug("AI Response Chunk: {}", chunkText);
        //     }
        // });
    }

    /**
     * 从请求中提取第一条用户消息的文本
     */
    private String getFirstUserText(ChatClientRequest request) {
        // 方法1: 尝试获取单条用户消息
        try {
            UserMessage userMessage = request.prompt().getUserMessage();
            if (userMessage != null) {
                return userMessage.getText(); // 或者 userMessage.getContent()，根据实际API
            }
        } catch (Exception e) {
            // 可能不存在单条消息，继续尝试其他方式
        }

        // 方法2: 尝试从用户消息列表中获取第一条
        try {
            List<UserMessage> userMessages = request.prompt().getUserMessages();
            if (userMessages != null && !userMessages.isEmpty()) {
                return userMessages.get(0).getText(); // 或者 get(0).getContent()
            }
        } catch (Exception e) {
            log.warn("Failed to get user messages from prompt", e);
        }

        // 方法3: 最后的手段， toString()
        return request.prompt().toString();
    }

    /**
     * 从响应中提取文本内容
     */
    private String getResponseText(ChatClientResponse response) {
        if (response == null || response.chatResponse() == null) {
            return "[No response]";
        }

        ChatResponse chatResponse = response.chatResponse();
        if (chatResponse.getResult() == null || chatResponse.getResult().getOutput() == null) {
            return "[Empty output]";
        }

        return chatResponse.getResult().getOutput().getText();
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 100; // 调整为合适的优先级
    }
}