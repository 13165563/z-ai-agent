package com.zluolan.zaiagent.rag;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

@Slf4j
@Configuration
public class PgVectorVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Bean
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {
        PgVectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1536)
                .distanceType(COSINE_DISTANCE)
                .indexType(HNSW)
                .initializeSchema(true)
                .schemaName("public")
                .vectorTableName("vector_store")
                .maxDocumentBatchSize(10000)
                .build();

        // 检查是否已有数据
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM vector_store", Integer.class);

        if (count == null || count == 0) {
            // 只有表为空时才加载文档
            List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
            vectorStore.add(documents);
            log.info("✅ 已加载 " + documents.size() + " 个文档到向量库");
        } else {
            log.info("✅ 已检测到 vector_store 中存在 {} 条数据，跳过文档插入。", count);
        }

        return vectorStore;
    }
}
