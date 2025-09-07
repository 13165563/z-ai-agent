-- 使用官方要求的表名和结构
CREATE TABLE ai_chat_memory (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                conversation_id VARCHAR(256) NOT NULL,
                                content LONGTEXT NOT NULL,
                                type VARCHAR(100) NOT NULL,
                                timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                CONSTRAINT chk_message_type CHECK (type IN ('USER', 'ASSISTANT', 'SYSTEM', 'TOOL'))
);

-- 创建索引
CREATE INDEX idx_conversation_id ON ai_chat_memory(conversation_id);
CREATE INDEX idx_timestamp ON ai_chat_memory(timestamp);