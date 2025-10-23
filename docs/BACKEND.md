# 后端开发文档

## 项目概述

Z AI Agent 后端是一个基于Spring Boot 3.4.5的AI智能体服务，集成了Spring AI、阿里云DashScope、数据库存储和工具调用功能。

## 技术栈

- **Spring Boot 3.4.5** - 主框架
- **Spring AI 1.0.0** - AI集成框架
- **Spring AI Alibaba 1.0.0.2** - 阿里云AI服务
- **MySQL 8.0+** - 主数据库
- **PostgreSQL 13+** - 向量数据库
- **Maven** - 依赖管理
- **Java 21** - 开发语言

## 项目结构

```
src/main/java/com/zluolan/zaiagent/
├── ZAiAgentApplication.java          # 应用启动类
├── agent/                            # AI智能体
│   ├── BaseAgent.java               # 基础智能体
│   └── YuManus.java                 # 超级智能体
├── app/                             # 应用服务
│   └── LoveApp.java                 # 恋爱大师应用
├── config/                          # 配置类
│   └── SpringDocConfig.java         # API文档配置
├── controller/                       # 控制器
│   ├── AiController.java            # AI接口控制器
│   └── HealthController.java        # 健康检查控制器
├── exception/                       # 异常处理
│   └── GlobalExceptionHandler.java # 全局异常处理器
└── chatmemeory/                     # 聊天记忆
    └── FileBasedChatMemoryRepository.java # 文件记忆存储
```

## 核心功能模块

### 1. AI智能体系统

#### BaseAgent.java - 基础智能体

**功能**: 提供AI智能体的基础功能

**特性**:
- 工具调用支持
- 流式响应
- 记忆管理
- 错误处理

```java
@Component
public class BaseAgent {
    private final ChatModel chatModel;
    private final ToolCallback[] tools;
    
    public Flux<String> runStream(String message) {
        // 流式AI对话实现
    }
}
```

#### YuManus.java - 超级智能体

**功能**: 高级AI智能体，支持复杂任务处理

**特性**:
- 多工具集成
- 智能推理
- 上下文管理
- 任务规划

### 2. 应用服务层

#### LoveApp.java - 恋爱大师应用

**功能**: 专业的恋爱心理咨询服务

**核心方法**:
- `doChat()` - 同步对话
- `doChatByStream()` - 流式对话
- `doChatWithRag()` - 知识库增强对话
- `doChatWithTools()` - 工具调用对话

**系统提示词**:
```java
private static final String SYSTEM_PROMPT = 
    "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
    "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
    "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。";
```

### 3. 控制器层

#### AiController.java - AI接口控制器

**功能**: 提供AI对话的RESTful接口

**主要接口**:
- `GET /ai/love_app/chat/sync` - 同步对话
- `GET /ai/love_app/chat/sse` - 流式对话
- `GET /ai/manus/chat` - 超级智能体对话

**流式响应实现**:
```java
@GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> doChatWithLoveAppSSE(String message, String chatId) {
    return loveApp.doChatByStream(message, chatId);
}
```

## 配置管理

### 1. 应用配置 (application.yml)

```yaml
spring:
  application:
    name: z-ai-agent
  profiles:
    active: local
  main:
    allow-bean-definition-overriding: true

server:
  port: 8123
  servlet:
    context-path: /api

# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/z_ai_agent
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver

# AI服务配置
spring:
  ai:
    alibaba:
      dashscope:
        api-key: ${DASHSCOPE_API_KEY:your-api-key}
```

### 2. 本地配置 (application-local.yml)

```yaml
# 开发环境特定配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/z_ai_agent_dev
    username: dev_user
    password: dev_password

logging:
  level:
    com.zluolan.zaiagent: DEBUG
    org.springframework.ai: DEBUG
```

## 数据库设计

### 1. 聊天记忆表

```sql
CREATE TABLE chat_memory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    conversation_id VARCHAR(255) NOT NULL,
    message_type ENUM('USER', 'ASSISTANT') NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_conversation_id (conversation_id)
);
```

### 2. 向量存储表 (PostgreSQL)

```sql
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE vector_store (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    metadata JSONB,
    embedding vector(1536),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX ON vector_store USING ivfflat (embedding vector_cosine_ops);
```

## 依赖管理

### 1. 核心依赖

```xml
<!-- Spring Boot Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring AI Alibaba DashScope -->
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-starter-dashscope</artifactId>
</dependency>

<!-- Spring AI Ollama -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-ollama</artifactId>
</dependency>
```

### 2. 数据库依赖

```xml
<!-- MySQL连接器 -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>

<!-- PostgreSQL连接器 -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>

<!-- PGVector存储 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-pgvector-store</artifactId>
</dependency>
```

### 3. 工具和工具库

```xml
<!-- 网页抓取 -->
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.19.1</version>
</dependency>

<!-- PDF生成 -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext-core</artifactId>
    <version>9.1.0</version>
</dependency>

<!-- 工具库 -->
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.8.37</version>
</dependency>
```

## 开发指南

### 1. 环境设置

**要求**:
- Java 21+
- Maven 3.6+
- MySQL 8.0+
- PostgreSQL 13+

**IDE配置**:
- IntelliJ IDEA 2023.3+
- Eclipse 2023-03+
- VS Code with Java Extension Pack

### 2. 本地开发

```bash
# 克隆项目
git clone <repository-url>
cd z-ai-agent

# 配置数据库
# 创建数据库
mysql -u root -p
CREATE DATABASE z_ai_agent;

# 运行应用
mvn spring-boot:run
```

### 3. 配置环境变量

```bash
# 设置数据库连接
export DB_USERNAME=your_username
export DB_PASSWORD=your_password

# 设置AI服务API Key
export DASHSCOPE_API_KEY=your_api_key

# 设置Ollama服务地址
export OLLAMA_BASE_URL=http://localhost:11434
```

## 核心功能实现

### 1. 聊天记忆管理

```java
@Component
public class FileBasedChatMemoryRepository implements ChatMemoryRepository {
    
    private final String basePath = "./chat_memories";
    
    @Override
    public void save(ChatMemory chatMemory) {
        // 保存聊天记忆到文件
    }
    
    @Override
    public Optional<ChatMemory> findById(String id) {
        // 从文件加载聊天记忆
    }
}
```

### 2. 流式响应处理

```java
public Flux<String> doChatByStream(String message, String chatId) {
    return chatClient
        .prompt()
        .user(message)
        .advisors(spec -> spec.param(CONVERSATION_ID, chatId))
        .stream()
        .content();
}
```

### 3. 工具调用集成

```java
@Component
public class ImageSearchTool {
    
    @Tool("搜索图片")
    public String searchImage(String query) {
        // 实现图片搜索逻辑
        return "搜索到相关图片: " + query;
    }
}
```

## 测试

### 1. 单元测试

```java
@SpringBootTest
class LoveAppTest {
    
    @Autowired
    private LoveApp loveApp;
    
    @Test
    void testDoChat() {
        String message = "我最近在追求一个女生";
        String chatId = "test123";
        String response = loveApp.doChat(message, chatId);
        assertThat(response).isNotEmpty();
    }
}
```

### 2. 集成测试

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AiControllerTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testChatEndpoint() {
        String url = "/ai/love_app/chat/sync?message=你好&chatId=test";
        String response = restTemplate.getForObject(url, String.class);
        assertThat(response).isNotEmpty();
    }
}
```

## 部署

### 1. 构建应用

```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包应用
mvn package

# 运行JAR文件
java -jar target/z-ai-agent-0.0.1-SNAPSHOT.jar
```

### 2. Docker部署

```dockerfile
FROM openjdk:21-jdk-slim

COPY target/z-ai-agent-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8123

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# 构建Docker镜像
docker build -t z-ai-agent .

# 运行容器
docker run -p 8123:8123 -e DASHSCOPE_API_KEY=your_key z-ai-agent
```

### 3. 生产环境配置

```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://prod-db:3306/z_ai_agent
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    
logging:
  level:
    root: INFO
    com.zluolan.zaiagent: INFO
```

## 监控和日志

### 1. 日志配置

```yaml
logging:
  level:
    com.zluolan.zaiagent: INFO
    org.springframework.ai: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/z-ai-agent.log
```

### 2. 健康检查

```java
@RestController
public class HealthController {
    
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", Instant.now().toString());
        return status;
    }
}
```

## 性能优化

### 1. 数据库优化

- 使用连接池
- 索引优化
- 查询优化
- 缓存策略

### 2. AI服务优化

- 请求批处理
- 响应缓存
- 并发控制
- 超时设置

### 3. 内存管理

- JVM参数调优
- 垃圾回收优化
- 内存泄漏检测

## 安全考虑

### 1. API安全

- 输入验证
- SQL注入防护
- XSS防护
- CSRF防护

### 2. 数据安全

- 敏感数据加密
- 访问控制
- 审计日志
- 数据备份

## 故障排除

### 1. 常见问题

**问题**: 数据库连接失败
**解决**: 检查数据库配置和网络连接

**问题**: AI服务调用失败
**解决**: 验证API Key和网络连接

**问题**: 内存不足
**解决**: 调整JVM堆内存设置

### 2. 调试技巧

- 启用DEBUG日志
- 使用IDE调试器
- 分析堆栈跟踪
- 监控系统资源

## 扩展开发

### 1. 添加新的AI工具

```java
@Component
public class CustomTool {
    
    @Tool("自定义工具")
    public String customFunction(String input) {
        // 实现自定义功能
        return "处理结果: " + input;
    }
}
```

### 2. 集成新的AI模型

```java
@Configuration
public class AiModelConfig {
    
    @Bean
    public ChatModel customChatModel() {
        // 配置新的AI模型
        return new CustomChatModel();
    }
}
```

### 3. 添加新的存储后端

```java
@Component
public class CustomVectorStore implements VectorStore {
    
    @Override
    public void add(List<Document> documents) {
        // 实现自定义存储逻辑
    }
}
```
