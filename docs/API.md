# API 文档

## 概述

Z AI Agent 提供RESTful API接口，支持AI对话、流式响应和工具调用功能。

**基础URL**: `http://localhost:8123/api`

## 认证

当前版本无需认证，所有接口均为公开访问。

## 接口列表

### 1. AI恋爱大师接口

#### 1.1 同步对话

**接口地址**: `GET /ai/love_app/chat/sync`

**功能描述**: 与AI恋爱大师进行同步对话

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| message | String | 是 | 用户消息内容 |
| chatId | String | 是 | 对话ID，用于记忆管理 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": "作为恋爱心理专家，我很乐意帮助您解决恋爱中的困扰..."
}
```

#### 1.2 流式对话 (SSE)

**接口地址**: `GET /ai/love_app/chat/sse`

**功能描述**: 与AI恋爱大师进行流式对话，支持实时响应

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| message | String | 是 | 用户消息内容 |
| chatId | String | 是 | 对话ID，用于记忆管理 |

**响应格式**: Server-Sent Events (SSE)

**响应示例**:
```
data: 作为恋爱心理专家
data: 我很乐意帮助您
data: 解决恋爱中的困扰...
```

#### 1.3 流式对话 (Server-Sent Event)

**接口地址**: `GET /ai/love_app/chat/server_sent_event`

**功能描述**: 返回标准SSE格式的流式响应

**请求参数**: 同上

**响应示例**:
```
event: message
data: {"content": "作为恋爱心理专家，我很乐意帮助您..."}

event: message
data: {"content": "请告诉我您遇到的具体问题..."}
```

#### 1.4 流式对话 (SseEmitter)

**接口地址**: `GET /ai/love_app/chat/sse/emitter`

**功能描述**: 使用SseEmitter实现流式响应，支持3分钟超时

**请求参数**: 同上

**响应格式**: Server-Sent Events

### 2. AI超级智能体接口

#### 2.1 智能体对话

**接口地址**: `GET /ai/manus/chat`

**功能描述**: 与YuManus超级智能体进行对话，支持工具调用

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| message | String | 是 | 用户消息内容 |

**响应格式**: Server-Sent Events

**功能特性**:
- 支持工具调用
- 多模态输入
- 智能推理
- 实时响应

## 错误处理

### 错误响应格式

```json
{
  "code": 500,
  "message": "Internal Server Error",
  "timestamp": "2024-01-01T12:00:00Z",
  "path": "/api/ai/love_app/chat/sync"
}
```

### 常见错误码

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| 400 | 请求参数错误 | 检查请求参数格式 |
| 404 | 接口不存在 | 检查接口地址 |
| 500 | 服务器内部错误 | 查看服务器日志 |
| 503 | 服务不可用 | 检查AI服务配置 |

## 使用示例

### JavaScript (前端)

```javascript
// 同步对话
async function chatWithLoveApp(message, chatId) {
  const response = await fetch(
    `/api/ai/love_app/chat/sync?message=${encodeURIComponent(message)}&chatId=${chatId}`
  );
  return await response.text();
}

// 流式对话
function chatWithLoveAppStream(message, chatId) {
  const eventSource = new EventSource(
    `/api/ai/love_app/chat/sse?message=${encodeURIComponent(message)}&chatId=${chatId}`
  );
  
  eventSource.onmessage = function(event) {
    console.log('收到消息:', event.data);
  };
  
  eventSource.onerror = function(event) {
    console.error('连接错误:', event);
  };
}
```

### Java (后端)

```java
// 同步调用
@GetMapping("/test")
public String testChat() {
    String message = "我最近在追求一个女生，但不知道如何表达";
    String chatId = "user123";
    return loveApp.doChat(message, chatId);
}

// 流式调用
@GetMapping("/test-stream")
public Flux<String> testChatStream() {
    String message = "请给我一些恋爱建议";
    String chatId = "user123";
    return loveApp.doChatByStream(message, chatId);
}
```

### cURL

```bash
# 同步对话
curl -X GET "http://localhost:8123/api/ai/love_app/chat/sync?message=你好&chatId=test123"

# 流式对话
curl -N -H "Accept: text/event-stream" \
  "http://localhost:8123/api/ai/love_app/chat/sse?message=你好&chatId=test123"
```

## 性能优化

### 1. 连接管理
- 使用连接池管理数据库连接
- 合理设置超时时间
- 及时关闭SSE连接

### 2. 内存管理
- 限制对话历史长度
- 定期清理过期记忆
- 使用流式处理减少内存占用

### 3. 缓存策略
- 缓存常用响应
- 使用Redis存储会话状态
- 实现智能缓存失效

## 监控和日志

### 日志级别
- `DEBUG`: 详细调试信息
- `INFO`: 一般信息
- `WARN`: 警告信息
- `ERROR`: 错误信息

### 关键指标
- 响应时间
- 并发连接数
- 错误率
- 内存使用率

## 版本历史

### v1.0.0
- 基础AI对话功能
- 恋爱大师模块
- 流式响应支持

### v1.1.0
- 添加超级智能体
- 工具调用支持
- 知识库集成

## 更新日志

- **2024-01-01**: 初始版本发布
- **2024-01-15**: 添加流式响应支持
- **2024-02-01**: 集成知识库功能
