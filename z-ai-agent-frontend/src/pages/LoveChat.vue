<template>
  <div class="container">
    <div class="page-head">
      <h2>AI 恋爱大师</h2>
      <span class="chip">温柔建议 · 情感陪伴</span>
    </div>
    <ChatRoom mode="love" />
  </div>
  </template>

<script setup lang="ts">
import ChatRoom from '@/components/ChatRoom.vue'
import { useSEO } from '@/composables/useSEO'
useSEO({
  title: 'AI 恋爱大师 - Z AI Agent',
  description: '情感沟通建议与陪伴，支持 SSE 流式打字机效果。',
  keywords: '恋爱, 情感, AI, 聊天, 流式, SSE'
})
</script>

<style scoped>
.page-head { display: flex; align-items: center; gap: 10px; margin: 8px 0 16px; }
h2 { margin: 0; }
</style>


