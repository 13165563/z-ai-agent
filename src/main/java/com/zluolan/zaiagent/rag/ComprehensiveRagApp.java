package com.zluolan.zaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * 综合RAG应用类
 * 实现了基于检索增强生成技术的聊天功能，包含查询转换、文档检索和上下文记忆等功能
 */
@Component
@Slf4j
public class ComprehensiveRagApp {

    private final ChatClient chatClient;
    private final MessageChatMemoryAdvisor chatMemoryAdvisor;
    private final RetrievalAugmentationAdvisor retrievalAugmentationAdvisor;
    private final VectorStore vectorStore;

    /**
     * 构造函数，初始化RAG应用所需的各个组件
     * 
     * @param chatClientBuilder ChatClient构建器，用于创建聊天客户端
     * @param chatMemory 聊天记忆，用于存储对话历史
     * @param vectorStore 向量存储，用于存储和检索文档向量
     * @param embeddingModel 嵌入模型，用于将文本转换为向量表示
     */
    public ComprehensiveRagApp(ChatClient.Builder chatClientBuilder,
                               ChatMemory chatMemory,
                               VectorStore vectorStore,
                               EmbeddingModel embeddingModel) {
        // 构建聊天客户端
        this.chatClient = chatClientBuilder.build();

        // 创建聊天记忆顾问，用于管理对话历史
        this.chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        // 创建文档检索器，用于从向量存储中检索相关文档
        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(0.60)  // 相似度阈值，低于此值的文档将被过滤
                .topK(5)  // 返回最相似的5个文档
                .build();

        // 创建查询重写转换器，用于优化用户查询
        RewriteQueryTransformer rewriteTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .targetSearchSystem("vector store")  // 目标搜索系统
                .build();

        // 创建查询压缩转换器，用于简化复杂查询
        CompressionQueryTransformer compressionTransformer = CompressionQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();

        // 创建查询翻译转换器，用于将查询翻译为目标语言
        TranslationQueryTransformer translationTransformer = TranslationQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .targetLanguage("english")  // 目标语言为英语
                .build();

        // 创建多查询扩展器，用于生成多个相关的查询以提高检索效果
        MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(chatClientBuilder)
                .numberOfQueries(3)  // 生成3个额外的查询
                .includeOriginal(true)  // 包含原始查询
                .build();

        // 创建检索增强顾问，整合文档检索和查询转换功能
        this.retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryTransformers(rewriteTransformer, compressionTransformer, translationTransformer)
                .queryExpander(queryExpander)
                .build();

        this.vectorStore = vectorStore;
    }

    /**
     * 执行综合RAG聊天
     * 结合聊天记忆和文档检索功能，提供上下文感知的回答
     * 
     * @param userMessage 用户消息
     * @param conversationId 会话ID，用于区分不同的对话
     * @return AI助手的回答
     */
    public String doComprehensiveChat(String userMessage, String conversationId) {
        ChatResponse response = chatClient.prompt()
                .system(""" 
                        你是一个专业的AI助手，基于提供的上下文信息回答问题。
                        请确保回答准确、专业，并且严格基于给定的上下文。
                        如果上下文信息不足，请明确说明。
                        """)
                .user(userMessage)
                .advisors(chatMemoryAdvisor, retrievalAugmentationAdvisor)
                .advisors(spec -> spec.param(CONVERSATION_ID, conversationId))
                .call()
                .chatResponse();

        String content = response.getResult().getOutput().getText();
        log.info("ComprehensiveChat: {}", content);
        return content;
    }

    /**
     * 执行基础RAG聊天（无记忆功能）
     * 仅使用文档检索功能，不保存对话历史
     * 
     * @param userMessage 用户消息
     * @return AI助手的回答
     */
    public String doBasicChat(String userMessage) {
        return chatClient.prompt()
                .advisors(retrievalAugmentationAdvisor)
                .user(userMessage)
                .call()
                .content();
    }

    /**
     * 检索相关文档（用于调试目的）
     * 根据查询文本检索最相关的文档
     * 
     * @param query 查询文本
     * @return 检索到的相关文档列表
     */
    public List<Document> retrieveDocuments(String query) {
        VectorStoreDocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(this.vectorStore)
                .similarityThreshold(0.5)  // 相似度阈值
                .topK(3)  // 返回最相似的3个文档
                .build();

        return retriever.retrieve(Query.builder().text(query).build());
    }
}