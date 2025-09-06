package com.zluolan.zaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Spring Ai Ollama本地 调用
 * @author zluolan
 */
@Component
public class OllamaAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel ollamaChatModel;


    @Override
    public void run(String... args) throws Exception {
        // 检查是否是OllamaChatModel实例
        if (ollamaChatModel != null) {
            System.out.println("使用Ollama模型进行对话");
            AssistantMessage message = ollamaChatModel.call(new Prompt("你好，你是谁？"))
                    .getResult()
                    .getOutput();
            System.out.println(message.getText());
        } else {
            System.out.println("当前使用的不是Ollama模型，跳过Ollama测试");
        }
    }

}