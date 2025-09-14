package com.zluolan.zaiagent.rag;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.Map;

/**
 * RAG（检索增强生成）配置类
 * 用于配置聊天模型、向量存储以及初始化示例数据
 */
@Configuration
public class RagConfig {

    /**
     * 配置默认聊天模型
     * 使用@Primary注解指定当存在多个ChatModel Bean时，该Bean作为首选
     * 
     * @param dashscopeChatModel 阿里云百炼平台的聊天模型
     * @return 默认聊天模型
     */
    @Bean
    @Primary   // 告诉 Spring 这是默认的
    public ChatModel defaultChatModel(ChatModel dashscopeChatModel) {
        return dashscopeChatModel;  // 这里你可以换成 ollamaChatModel
    }
    
    /**
     * 配置向量存储
     * 当前使用SimpleVectorStore作为示例实现，生产环境建议使用持久化向量存储如PgVector等
     * 
     * @param embeddingModel 嵌入模型，用于将文本转换为向量表示
     * @return 向量存储实例
     */
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        // 简单内存向量存储实现（测试用）
        // 生产环境建议使用 PgVector / RedisVector / Milvus 等持久化向量存储
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    /**
     * 配置数据初始化器Bean
     * 
     * @param vectorStore 向量存储实例
     * @return 数据初始化器实例
     */
    @Bean
    public DataInitializer dataInitializer(VectorStore vectorStore) {
        return new DataInitializer(vectorStore);
    }

    /**
     * 数据初始化器内部类
     * 用于在应用启动时向向量存储中添加示例文档数据
     */
    public static class DataInitializer {
        private final VectorStore vectorStore;

        /**
         * 构造函数，初始化向量存储并加载示例数据
         * 
         * @param vectorStore 向量存储实例
         */
        public DataInitializer(VectorStore vectorStore) {
            this.vectorStore = vectorStore;
            initializeData();
        }

        /**
         * 初始化示例数据
         * 添加三个示例文档到向量存储中，用于演示RAG功能
         */
        private void initializeData() {
            List<Document> documents = List.of(
                    new Document("Spring AI 提供了模块化的RAG架构，支持多种查询转换和扩展技术。",
                            Map.of("source", "spring-ai-docs", "type", "technical")),
                    new Document("阿里云百炼平台支持多种大语言模型，包括通义千问系列。",
                            Map.of("source", "alibaba-cloud-docs", "type", "cloud")),
                    new Document("RAG系统通过检索增强生成技术提高大模型回答的准确性和相关性。",
                            Map.of("source", "ai-research-papers", "type", "research"))
            );

            vectorStore.add(documents);
        }
    }
}