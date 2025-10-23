# 贡献指南

感谢您对Z AI Agent项目的关注！本文档将指导您如何为项目做出贡献。

## 贡献方式

### 1. 报告问题

如果您发现了bug或有功能建议，请通过以下方式报告：

- **GitHub Issues**: 在项目仓库创建Issue
- **Bug报告**: 使用Bug报告模板
- **功能请求**: 使用功能请求模板

### 2. 代码贡献

我们欢迎各种形式的代码贡献：

- 修复bug
- 添加新功能
- 改进文档
- 优化性能
- 重构代码

## 开发环境设置

### 1. 环境要求

- Java 21+
- Node.js 18+
- Maven 3.6+
- MySQL 8.0+ 或 PostgreSQL 13+
- Git

### 2. 克隆项目

```bash
# Fork项目到您的GitHub账户
# 然后克隆您的fork
git clone https://github.com/your-username/z-ai-agent.git
cd z-ai-agent

# 添加上游仓库
git remote add upstream https://github.com/original-owner/z-ai-agent.git
```

### 3. 配置开发环境

```bash
# 后端配置
cp src/main/resources/application.yml src/main/resources/application-local.yml
# 编辑application-local.yml，配置数据库和AI服务

# 前端配置
cd z-ai-agent-frontend
npm install
```

## 开发流程

### 1. 创建分支

```bash
# 从main分支创建功能分支
git checkout main
git pull upstream main
git checkout -b feature/your-feature-name

# 或创建bug修复分支
git checkout -b fix/issue-number
```

### 2. 开发规范

#### 代码风格

**Java代码规范**:
- 使用4个空格缩进
- 类名使用PascalCase
- 方法名和变量名使用camelCase
- 常量使用UPPER_SNAKE_CASE

```java
public class ExampleService {
    private static final String CONSTANT_VALUE = "example";
    
    public String processData(String inputData) {
        // 实现逻辑
        return processedResult;
    }
}
```

**TypeScript/Vue代码规范**:
- 使用2个空格缩进
- 组件名使用PascalCase
- 文件名使用kebab-case
- 使用TypeScript严格模式

```typescript
// components/ExampleComponent.vue
<template>
  <div class="example-component">
    <h1>{{ title }}</h1>
  </div>
</template>

<script setup lang="ts">
interface Props {
  title: string
}

defineProps<Props>()
</script>
```

#### 提交信息规范

使用约定式提交格式：

```bash
# 功能开发
git commit -m "feat: 添加新的AI工具支持"

# 问题修复
git commit -m "fix: 修复聊天记忆丢失问题"

# 文档更新
git commit -m "docs: 更新API文档"

# 代码重构
git commit -m "refactor: 重构AI服务层代码"

# 性能优化
git commit -m "perf: 优化数据库查询性能"

# 测试相关
git commit -m "test: 添加单元测试用例"
```

### 3. 测试要求

#### 后端测试

```java
@SpringBootTest
class LoveAppTest {
    
    @Autowired
    private LoveApp loveApp;
    
    @Test
    @DisplayName("测试同步对话功能")
    void testDoChat() {
        // Given
        String message = "测试消息";
        String chatId = "test123";
        
        // When
        String response = loveApp.doChat(message, chatId);
        
        // Then
        assertThat(response).isNotEmpty();
        assertThat(response).contains("恋爱心理专家");
    }
    
    @Test
    @DisplayName("测试流式对话功能")
    void testDoChatByStream() {
        // Given
        String message = "测试流式消息";
        String chatId = "test456";
        
        // When
        Flux<String> response = loveApp.doChatByStream(message, chatId);
        
        // Then
        StepVerifier.create(response)
            .expectNextMatches(text -> text.contains("恋爱心理专家"))
            .verifyComplete();
    }
}
```

#### 前端测试

```typescript
// tests/components/ChatRoom.test.ts
import { mount } from '@vue/test-utils'
import ChatRoom from '@/components/ChatRoom.vue'

describe('ChatRoom', () => {
  it('renders chat interface correctly', () => {
    const wrapper = mount(ChatRoom)
    expect(wrapper.find('.chat-container').exists()).toBe(true)
  })
  
  it('sends message when form is submitted', async () => {
    const wrapper = mount(ChatRoom)
    const input = wrapper.find('input[type="text"]')
    const form = wrapper.find('form')
    
    await input.setValue('Hello')
    await form.trigger('submit')
    
    expect(wrapper.emitted('send-message')).toBeTruthy()
  })
})
```

### 4. 代码审查

#### 提交前检查

```bash
# 运行测试
mvn test
npm test

# 代码格式化
mvn spotless:apply
npm run format

# 代码检查
mvn checkstyle:check
npm run lint

# 构建项目
mvn clean package
npm run build
```

#### 代码审查清单

- [ ] 代码符合项目规范
- [ ] 所有测试通过
- [ ] 新功能有对应的测试
- [ ] 文档已更新
- [ ] 没有引入新的警告
- [ ] 性能影响已考虑

## Pull Request流程

### 1. 创建Pull Request

```bash
# 推送分支到您的fork
git push origin feature/your-feature-name

# 在GitHub上创建Pull Request
```

### 2. PR模板

```markdown
## 变更描述
简要描述本次变更的内容和目的。

## 变更类型
- [ ] Bug修复
- [ ] 新功能
- [ ] 文档更新
- [ ] 代码重构
- [ ] 性能优化
- [ ] 其他

## 测试说明
描述如何测试这些变更。

## 检查清单
- [ ] 代码符合项目规范
- [ ] 所有测试通过
- [ ] 新功能有对应的测试
- [ ] 文档已更新
- [ ] 没有引入新的警告

## 相关Issue
关联的Issue: #123
```

### 3. 审查流程

1. **自动检查**: CI/CD流水线自动运行测试和检查
2. **代码审查**: 维护者审查代码质量和设计
3. **测试验证**: 确保所有测试通过
4. **合并**: 审查通过后合并到主分支

## 项目结构说明

### 后端结构

```
src/main/java/com/zluolan/zaiagent/
├── agent/                    # AI智能体
│   ├── BaseAgent.java       # 基础智能体
│   └── YuManus.java         # 超级智能体
├── app/                     # 应用服务
│   └── LoveApp.java         # 恋爱大师应用
├── config/                  # 配置类
│   └── SpringDocConfig.java # API文档配置
├── controller/              # 控制器
│   ├── AiController.java    # AI接口控制器
│   └── HealthController.java # 健康检查控制器
├── exception/               # 异常处理
│   └── GlobalExceptionHandler.java
└── chatmemeory/            # 聊天记忆
    └── FileBasedChatMemoryRepository.java
```

### 前端结构

```
z-ai-agent-frontend/src/
├── components/             # 可复用组件
│   └── ChatRoom.vue       # 聊天室组件
├── pages/                  # 页面组件
│   ├── Home.vue           # 首页
│   ├── LoveChat.vue       # 恋爱大师页面
│   └── ManusChat.vue      # 超级智能体页面
├── composables/           # 组合式函数
│   └── useSEO.ts          # SEO相关功能
├── styles/                # 样式文件
│   └── global.css         # 全局样式
└── utils/                 # 工具函数
    └── http.ts            # HTTP请求工具
```

## 开发最佳实践

### 1. 代码质量

- 编写清晰的代码和注释
- 遵循SOLID原则
- 使用设计模式
- 保持代码简洁

### 2. 性能考虑

- 避免N+1查询问题
- 使用适当的缓存策略
- 优化数据库查询
- 考虑并发处理

### 3. 安全考虑

- 输入验证和清理
- 防止SQL注入
- 使用HTTPS
- 保护敏感信息

### 4. 可维护性

- 模块化设计
- 清晰的接口定义
- 完整的文档
- 版本控制

## 社区指南

### 1. 行为准则

- 保持友好和尊重
- 建设性的反馈
- 包容性环境
- 专业沟通

### 2. 沟通渠道

- **GitHub Issues**: 技术讨论和问题报告
- **GitHub Discussions**: 功能讨论和想法分享
- **Pull Request**: 代码审查和讨论

### 3. 获取帮助

- 查看现有文档
- 搜索已关闭的Issues
- 创建新的Issue
- 参与社区讨论

## 发布流程

### 1. 版本管理

使用语义化版本控制：

- **主版本号**: 不兼容的API修改
- **次版本号**: 向下兼容的功能性新增
- **修订号**: 向下兼容的问题修正

### 2. 发布检查清单

- [ ] 所有测试通过
- [ ] 文档已更新
- [ ] 版本号已更新
- [ ] 变更日志已更新
- [ ] 发布说明已准备

### 3. 发布步骤

```bash
# 1. 更新版本号
mvn versions:set -DnewVersion=1.1.0

# 2. 创建发布标签
git tag -a v1.1.0 -m "Release version 1.1.0"
git push origin v1.1.0

# 3. 构建发布包
mvn clean package
```

## 贡献者认可

我们感谢所有贡献者的努力！贡献者将在以下地方被认可：

- README.md中的贡献者列表
- 发布说明中的贡献者致谢
- 项目文档中的贡献者信息

## 许可证

本项目采用MIT许可证。通过贡献代码，您同意您的贡献将在相同的许可证下发布。

## 联系方式

如果您有任何问题或建议，请通过以下方式联系：

- 创建GitHub Issue
- 发送邮件到项目维护者
- 参与GitHub Discussions

感谢您的贡献！🎉
