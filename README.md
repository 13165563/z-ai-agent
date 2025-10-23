# Z AI Agent

一个基于Spring Boot和Vue.js的智能AI助手项目，提供恋爱咨询和通用AI对话功能。

## 🚀 项目特性

- **AI恋爱大师**: 专业的恋爱心理咨询服务
- **AI超级智能体**: 通用AI对话助手
- **流式对话**: 支持SSE流式响应
- **记忆管理**: 支持对话记忆和上下文管理
- **知识库集成**: 支持RAG检索增强生成
- **工具调用**: 支持外部工具集成
- **现代化UI**: 基于Vue 3的响应式前端界面

## 🏗️ 技术栈

### 后端
- **Spring Boot 3.4.5** - 主框架
- **Spring AI** - AI集成框架
- **Spring AI Alibaba** - 阿里云AI服务
- **MySQL/PostgreSQL** - 数据库支持
- **Knife4j** - API文档
- **Maven** - 依赖管理

### 前端
- **Vue 3** - 前端框架
- **TypeScript** - 类型安全
- **Vite** - 构建工具
- **Vue Router** - 路由管理
- **Axios** - HTTP客户端

## 📁 项目结构

```
z-ai-agent/
├── src/main/java/com/zluolan/zaiagent/     # 后端源码
│   ├── agent/                             # AI智能体
│   ├── app/                               # 应用服务
│   ├── config/                            # 配置类
│   ├── controller/                        # 控制器
│   └── exception/                         # 异常处理
├── src/main/resources/                    # 资源文件
│   ├── application.yml                    # 配置文件
│   ├── document/                          # 知识库文档
│   └── sql/                               # 数据库脚本
├── z-ai-agent-frontend/                   # 前端项目
│   ├── src/
│   │   ├── components/                    # Vue组件
│   │   ├── pages/                         # 页面
│   │   └── utils/                         # 工具类
│   └── package.json
└── z-image-search-mcp-server/            # 图像搜索MCP服务
```

## 🚀 快速开始

### 环境要求

- Java 21+
- Node.js 18+
- MySQL 8.0+ 或 PostgreSQL 13+
- Maven 3.6+

### 后端启动

1. **克隆项目**
```bash
git clone <repository-url>
cd z-ai-agent
```

2. **配置数据库**
```yaml
# src/main/resources/application-local.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/z_ai_agent
    username: your_username
    password: your_password
```

3. **配置AI服务**
```yaml
# 配置阿里云DashScope API Key
spring:
  ai:
    alibaba:
      dashscope:
        api-key: your-api-key
```

4. **启动后端服务**
```bash
mvn spring-boot:run
```

服务将在 `http://localhost:8123` 启动

### 前端启动

1. **进入前端目录**
```bash
cd z-ai-agent-frontend
```

2. **安装依赖**
```bash
npm install
```

3. **启动开发服务器**
```bash
npm run dev
```

前端将在 `http://localhost:5173` 启动

## 📚 API文档

启动后端服务后，访问以下地址查看API文档：

- **Swagger UI**: http://localhost:8123/api/swagger-ui.html
- **Knife4j**: http://localhost:8123/api/doc.html
- **OpenAPI JSON**: http://localhost:8123/api/v3/api-docs

## 🔧 主要功能

### 1. AI恋爱大师
- 专业的恋爱心理咨询
- 支持单身、恋爱、已婚三种状态
- 个性化建议和解决方案
- 对话记忆管理

### 2. AI超级智能体
- 通用AI对话功能
- 工具调用能力
- 流式响应
- 多模态支持

### 3. 知识库集成
- 本地向量存储
- 云端知识库服务
- PostgreSQL向量存储
- RAG检索增强

## 🛠️ 开发指南

### 添加新的AI工具

1. 创建工具类实现 `ToolCallback` 接口
2. 在配置类中注册工具
3. 在AI智能体中使用工具

### 扩展前端功能

1. 在 `src/pages/` 下创建新页面
2. 在 `src/components/` 下创建组件
3. 更新路由配置

## 📝 配置说明

### 数据库配置
- 支持MySQL和PostgreSQL
- 自动创建表结构
- 支持向量存储

### AI服务配置
- 阿里云DashScope集成
- 支持多种模型
- 流式响应配置

### 前端配置
- Vite构建配置
- TypeScript支持
- 路由配置

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- 作者: zluolan
- 项目地址: [GitHub Repository]
- 文档: [在线文档]

---

⭐ 如果这个项目对你有帮助，请给它一个星标！
