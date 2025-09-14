package com.zluolan.zaiagent.rag;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ComprehensiveRagAppTest {

    @Resource
    private ComprehensiveRagApp ragApp;

    @Test
    void testBasicChat() {
        String message = "RAG系统的作用是什么？";
        String answer = ragApp.doBasicChat(message);
        Assertions.assertNotNull(answer);
        System.out.println("BasicChat: " + answer);
    }

    @Test
    void testComprehensiveChat() {
        String chatId = UUID.randomUUID().toString();
        String message = "阿里云百炼平台支持什么模型？";
        String answer = ragApp.doComprehensiveChat(message, chatId);
        Assertions.assertNotNull(answer);
        System.out.println("ComprehensiveChat: " + answer);
    }

    @Test
    void testRetrieveDocs() {
        List<Document> docs = ragApp.retrieveDocuments("Spring AI");
        Assertions.assertFalse(docs.isEmpty());
        docs.forEach(doc -> System.out.println("Doc: " + doc.toString())); // 替换为 toString() 或其他有效方法
    }
}
