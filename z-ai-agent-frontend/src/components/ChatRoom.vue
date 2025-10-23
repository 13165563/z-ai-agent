<template>
  <div class="chat-room">
    <div class="messages card" ref="messagesContainer">
      <div v-for="m in messages" :key="m.id" class="msg" :class="m.role">
        <div class="avatar" v-if="m.role==='ai'">
          <span v-if="aiIcon" class="icon">{{ aiIcon }}</span>
        </div>
        <div class="bubble" v-html="m.html"></div>
      </div>
    </div>
    <form class="input-bar" @submit.prevent="handleSend">
      <input v-model="input" type="text" placeholder="输入你的消息..." :disabled="loading" />
      <button type="submit" :disabled="!input.trim() || loading">发送</button>
    </form>
  </div>
  </template>

<script setup lang="ts">
import { onMounted, ref, watch, nextTick, computed } from 'vue'
import axios from 'axios'

type Role = 'user' | 'ai'

const props = defineProps<{ mode: 'love' | 'manus' }>()

const input = ref('')
const loading = ref(false)
const messages = ref<{ id: string; role: Role; html: string }[]>([])
const messagesContainer = ref<HTMLDivElement | null>(null)
const chatId = ref<string>('')
const aiIcon = computed(() => (props.mode === 'love' ? '💗' : '🧠'))

function generateChatId(): string {
  return 'chat_' + Math.random().toString(36).slice(2) + Date.now().toString(36)
}

function scrollToBottom() {
  nextTick(() => {
    const el = messagesContainer.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

function appendMessage(role: Role, html: string): string {
  const id = crypto.randomUUID ? crypto.randomUUID() : Math.random().toString(36).slice(2)
  messages.value.push({ id, role, html })
  scrollToBottom()
  return id
}

function updateMessage(id: string, html: string) {
  const idx = messages.value.findIndex(m => m.id === id)
  if (idx !== -1) {
    messages.value[idx] = { ...messages.value[idx], html }
    scrollToBottom()
  }
}

async function handleSend() {
  const text = input.value.trim()
  if (!text) return
  input.value = ''

  appendMessage('user', escapeHtml(text))
  loading.value = true

  // Prepare SSE request per mode
  if (props.mode === 'love') {
    await streamFromLove(text)
  } else {
    await streamFromManus(text)
  }

  loading.value = false
}

function escapeHtml(str: string): string {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;')
}

async function streamFromLove(message: string) {
  const params = new URLSearchParams({ message, chatId: chatId.value })
  const url = `/api/ai/love_app/chat/sse?${params.toString()}`
  await streamSseIntoSingleBubble(url)
}

async function streamFromManus(message: string) {
  const params = new URLSearchParams({ message })
  const url = `/api/ai/manus/chat?${params.toString()}`
  await streamSseIntoStepBubbles(url)
}

async function streamSseIntoSingleBubble(url: string) {
  const aiId = appendMessage('ai', '')
  const response = await fetch(url, { headers: { Accept: 'text/event-stream' } })
  if (!response.ok || !response.body) {
    updateMessage(aiId, '请求失败，请稍后重试')
    return
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''
  let aiText = ''

  try {
    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })

      // Process complete SSE events separated by blank line
      let sepIndex: number
      while ((sepIndex = buffer.indexOf('\n\n')) !== -1) {
        const eventBlock = buffer.slice(0, sepIndex)
        buffer = buffer.slice(sepIndex + 2)

        // Parse only data: lines (ignore id:, event:, retry:, comments)
        const lines = eventBlock.split(/\r?\n/)
        const dataPayload = lines
          .filter((l) => l.startsWith('data:'))
          .map((l) => l.slice(5).replace(/^\s/, '')) // remove leading space after colon if present
          .join('\n')

        if (dataPayload) {
          aiText += dataPayload
          updateMessage(aiId, escapeHtml(aiText))
        }
      }
    }

    // Flush leftover (in case stream ended without trailing blank line)
    if (buffer.trim().length > 0) {
      const lines = buffer.split(/\r?\n/)
      const dataPayload = lines
        .filter((l) => l.startsWith('data:'))
        .map((l) => l.slice(5).replace(/^\s/, ''))
        .join('\n')
      if (dataPayload) {
        aiText += dataPayload
        updateMessage(aiId, escapeHtml(aiText))
      }
    }
  } catch (e) {
    // Optionally show partial content already streamed
    updateMessage(aiId, escapeHtml(aiText))
  }
}

// For Manus: each SSE event (separated by blank line) becomes one new bubble
async function streamSseIntoStepBubbles(url: string) {
  const response = await fetch(url, { headers: { Accept: 'text/event-stream' } })
  if (!response.ok || !response.body) {
    appendMessage('ai', '请求失败，请稍后重试')
    return
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''

  try {
    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })

      let sepIndex: number
      while ((sepIndex = buffer.indexOf('\n\n')) !== -1) {
        const eventBlock = buffer.slice(0, sepIndex)
        buffer = buffer.slice(sepIndex + 2)

        const lines = eventBlock.split(/\r?\n/)
        const dataPayload = lines
          .filter((l) => l.startsWith('data:'))
          .map((l) => l.slice(5).replace(/^\s/, ''))
          .join('\n')

        if (dataPayload) {
          appendMessage('ai', escapeHtml(dataPayload))
        }
      }
    }

    // Flush leftover
    if (buffer.trim().length > 0) {
      const lines = buffer.split(/\r?\n/)
      const dataPayload = lines
        .filter((l) => l.startsWith('data:'))
        .map((l) => l.slice(5).replace(/^\s/, ''))
        .join('\n')
      if (dataPayload) {
        appendMessage('ai', escapeHtml(dataPayload))
      }
    }
  } catch (e) {
    // ignore, show whatever already appended
  }
}

onMounted(() => {
  chatId.value = generateChatId()
})

watch(messages, scrollToBottom)
</script>

<style scoped>
.chat-room { height: calc(100vh - 180px); max-width: 980px; margin: 0 auto; display: grid; grid-template-rows: 1fr auto; gap: 12px; }
.messages { overflow-y: auto; padding: 14px; }
.msg { display: grid; grid-template-columns: 36px 1fr; align-items: flex-end; gap: 10px; margin: 10px 0; }
.msg.user { grid-template-columns: 1fr; justify-items: end; }
.msg.ai { justify-items: start; }
.avatar { width: 36px; height: 36px; border-radius: 10px; display: grid; place-items: center; background: rgba(255,255,255,0.06); border: 1px solid var(--border); }
.icon { filter: drop-shadow(0 0 10px rgba(124,58,237,0.4)); }
.bubble { width: 100%; max-width: 70%; padding: 10px 12px; border-radius: 12px; background: rgba(255,255,255,0.05); border: 1px solid var(--border); white-space: pre-wrap; word-break: break-word; text-align: left; }
.msg.user .bubble { background: linear-gradient(180deg, rgba(124,58,237,0.25), rgba(124,58,237,0.15)); border-color: rgba(124,58,237,0.35); color: #fff; text-align: left; }
.input-bar { display: flex; gap: 8px; }
.input-bar input { flex: 1; padding: 12px 14px; border: 1px solid var(--border); border-radius: 10px; background: rgba(255,255,255,0.04); color: var(--text); }
.input-bar button { padding: 12px 16px; border: 1px solid rgba(124,58,237,0.45); border-radius: 10px; background: rgba(124,58,237,0.18); color: var(--text); cursor: pointer; }
.input-bar button[disabled] { opacity: .6; cursor: not-allowed; }
@media (max-width: 640px) { .bubble { max-width: 86%; } }
</style>


