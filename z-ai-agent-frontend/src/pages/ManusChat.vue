<template>
  <div class="container">
    <div class="page-head">
      <h2>AI 超级智能体</h2>
      <span class="chip">工具链 · 多步骤编排</span>
    </div>
    <ChatRoom mode="manus" />
  </div>
  </template>

<script setup lang="ts">
import ChatRoom from '@/components/ChatRoom.vue'
import { useSEO } from '@/composables/useSEO'
useSEO({
  title: 'AI 超级智能体 - Z AI Agent',
  description: '多步骤任务编排与工具调用，SSE 事件逐步输出。',
  keywords: '智能体, Agent, 多步骤, 工具, 流式, SSE'
})
</script>

<style scoped>
.page-head { display: flex; align-items: center; gap: 10px; margin: 8px 0 16px; }
h2 { margin: 0; }
</style>


