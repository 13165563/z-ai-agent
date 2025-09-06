package com.zluolan.zaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * Spring Ai 框架调用 AI 大模型
 * @author zluolan
 */
//@Component
public class SpringAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashscopeChatModel;


    @Override
    public void run(String... args) throws Exception {
        AssistantMessage message = dashscopeChatModel.call(new Prompt("你好,你叫什么名字？"))
                        .getResult()
                                .getOutput();
        System.out.println(message.getText());
    }

}
