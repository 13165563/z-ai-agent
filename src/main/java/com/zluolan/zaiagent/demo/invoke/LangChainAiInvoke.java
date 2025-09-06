//package com.zluolan.zaiagent.demo.invoke;
//
//import dev.langchain4j.community.model.dashscope.QwenChatModel;
//import dev.langchain4j.model.chat.ChatModel;
//
//public class LangChainAiInvoke {
//
//    public static void main(String[] args) {
//        ChatModel qwenModel = QwenChatModel.builder()
//                .apiKey(TestApiKey.API_KEY)
//                .modelName("qwen-max")
//                .build();
//        String answer = qwenModel.chat("你好");
//        System.out.println(answer);
//    }
//}
//
