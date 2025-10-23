# 前端开发文档

## 项目概述

Z AI Agent 前端是一个基于 Vue 3 + TypeScript + Vite 的现代化单页应用，提供AI对话界面和用户交互功能。

## 技术栈

- **Vue 3.5.8** - 渐进式JavaScript框架
- **TypeScript 5.6.2** - 类型安全的JavaScript超集
- **Vite 5.4.8** - 快速的前端构建工具
- **Vue Router 4.4.5** - Vue.js官方路由管理器
- **Axios 1.7.7** - HTTP客户端库

## 项目结构

```
z-ai-agent-frontend/
├── index.html                 # 入口HTML文件
├── package.json              # 项目依赖配置
├── tsconfig.json             # TypeScript配置
├── tsconfig.node.json        # Node.js TypeScript配置
├── vite.config.ts            # Vite构建配置
└── src/
    ├── App.vue               # 根组件
    ├── main.ts              # 应用入口
    ├── components/          # 可复用组件
    │   └── ChatRoom.vue     # 聊天室组件
    ├── pages/               # 页面组件
    │   ├── Home.vue         # 首页
    │   ├── LoveChat.vue     # 恋爱大师页面
    │   └── ManusChat.vue    # 超级智能体页面
    ├── composables/         # 组合式函数
    │   └── useSEO.ts        # SEO相关功能
    ├── styles/              # 样式文件
    │   └── global.css       # 全局样式
    └── utils/               # 工具函数
        └── http.ts          # HTTP请求工具
```

## 开发环境设置

### 1. 环境要求

- Node.js 18.0+
- npm 8.0+ 或 yarn 1.22+

### 2. 安装依赖

```bash
cd z-ai-agent-frontend
npm install
```

### 3. 启动开发服务器

```bash
npm run dev
```

访问 `http://localhost:5173` 查看应用

### 4. 构建生产版本

```bash
npm run build
```

### 5. 预览生产构建

```bash
npm run preview
```

## 核心组件

### 1. App.vue - 根组件

**功能**: 应用主布局，包含导航和路由视图

**特性**:
- 响应式导航栏
- 路由视图容器
- 全局样式管理

```vue
<template>
  <div class="app">
    <header class="app-header">
      <!-- 导航栏 -->
    </header>
    <main class="app-main">
      <router-view />
    </main>
    <footer class="site-footer">
      <!-- 页脚 -->
    </footer>
  </div>
</template>
```

### 2. ChatRoom.vue - 聊天室组件

**功能**: 提供AI对话界面

**特性**:
- 消息列表显示
- 实时消息接收
- 流式响应支持
- 消息历史管理

### 3. 页面组件

#### Home.vue - 首页
- 应用介绍
- 功能导航
- 使用指南

#### LoveChat.vue - 恋爱大师
- 恋爱咨询对话
- 专业建议展示
- 情感分析

#### ManusChat.vue - 超级智能体
- 通用AI对话
- 工具调用展示
- 多模态交互

## 路由配置

### 路由结构

```typescript
const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../pages/Home.vue')
  },
  {
    path: '/love',
    name: 'LoveChat',
    component: () => import('../pages/LoveChat.vue')
  },
  {
    path: '/manus',
    name: 'ManusChat',
    component: () => import('../pages/ManusChat.vue')
  }
]
```

### 导航配置

```vue
<nav class="nav">
  <router-link to="/">主页</router-link>
  <router-link to="/love">AI 恋爱大师</router-link>
  <router-link to="/manus">AI 超级智能体</router-link>
</nav>
```

## 样式系统

### 1. 全局样式 (global.css)

**CSS变量定义**:
```css
:root {
  --primary: #7c3aed;
  --secondary: #a855f7;
  --text: #ffffff;
  --background: #0b0f1a;
  --border: rgba(255, 255, 255, 0.1);
}
```

**响应式设计**:
- 移动端优先
- 断点设计
- 弹性布局

### 2. 组件样式

**样式特性**:
- CSS变量使用
- 渐变背景
- 毛玻璃效果
- 动画过渡

## HTTP请求管理

### 1. Axios配置 (http.ts)

```typescript
import axios from 'axios'

const http = axios.create({
  baseURL: 'http://localhost:8123/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
http.interceptors.request.use(
  config => {
    // 添加认证token等
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
http.interceptors.response.use(
  response => response.data,
  error => Promise.reject(error)
)
```

### 2. API调用示例

```typescript
// 同步对话
export const chatWithLoveApp = (message: string, chatId: string) => {
  return http.get(`/ai/love_app/chat/sync`, {
    params: { message, chatId }
  })
}

// 流式对话
export const chatWithLoveAppStream = (message: string, chatId: string) => {
  return new EventSource(
    `/ai/love_app/chat/sse?message=${encodeURIComponent(message)}&chatId=${chatId}`
  )
}
```

## 状态管理

### 1. 组件状态

使用Vue 3的Composition API管理组件状态:

```typescript
import { ref, reactive } from 'vue'

export default {
  setup() {
    const messages = ref([])
    const isLoading = ref(false)
    const chatId = ref('')
    
    return {
      messages,
      isLoading,
      chatId
    }
  }
}
```

### 2. 全局状态

使用provide/inject或Pinia进行全局状态管理:

```typescript
// 全局状态提供
const globalState = reactive({
  user: null,
  theme: 'dark',
  language: 'zh-CN'
})

provide('globalState', globalState)
```

## 性能优化

### 1. 代码分割

```typescript
// 路由懒加载
const routes = [
  {
    path: '/love',
    component: () => import('../pages/LoveChat.vue')
  }
]
```

### 2. 组件优化

```vue
<template>
  <div>
    <!-- 使用v-memo优化列表渲染 -->
    <div v-for="message in messages" :key="message.id" v-memo="[message.id, message.content]">
      {{ message.content }}
    </div>
  </div>
</template>
```

### 3. 资源优化

- 图片懒加载
- 代码压缩
- Tree Shaking
- 缓存策略

## 开发工具

### 1. TypeScript配置

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "module": "ESNext",
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "preserve",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true
  }
}
```

### 2. Vite配置

```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8123',
        changeOrigin: true
      }
    }
  }
})
```

## 部署指南

### 1. 构建配置

```bash
# 开发环境
npm run dev

# 生产构建
npm run build

# 预览构建结果
npm run preview
```

### 2. 部署选项

**静态部署**:
- Nginx
- Apache
- CDN

**容器化部署**:
- Docker
- Kubernetes

**云平台部署**:
- Vercel
- Netlify
- AWS S3

### 3. 环境变量

```bash
# .env.development
VITE_API_BASE_URL=http://localhost:8123/api

# .env.production
VITE_API_BASE_URL=https://api.example.com/api
```

## 测试

### 1. 单元测试

```bash
# 安装测试依赖
npm install --save-dev @vue/test-utils vitest

# 运行测试
npm run test
```

### 2. 端到端测试

```bash
# 安装E2E测试工具
npm install --save-dev playwright

# 运行E2E测试
npm run test:e2e
```

## 常见问题

### 1. 开发环境问题

**问题**: 端口冲突
**解决**: 修改vite.config.ts中的端口配置

**问题**: 代理配置不生效
**解决**: 检查代理配置和API地址

### 2. 构建问题

**问题**: 构建失败
**解决**: 检查TypeScript类型错误和依赖版本

**问题**: 资源加载失败
**解决**: 检查public目录和资源路径

### 3. 运行时问题

**问题**: SSE连接失败
**解决**: 检查后端服务状态和CORS配置

**问题**: 样式不生效
**解决**: 检查CSS导入和样式作用域

## 贡献指南

### 1. 代码规范

- 使用ESLint进行代码检查
- 遵循Vue 3 Composition API最佳实践
- 保持TypeScript类型安全

### 2. 提交规范

```bash
# 功能开发
git commit -m "feat: 添加新的聊天功能"

# 问题修复
git commit -m "fix: 修复消息显示问题"

# 文档更新
git commit -m "docs: 更新API文档"
```

### 3. 代码审查

- 确保代码质量
- 检查性能影响
- 验证功能完整性
