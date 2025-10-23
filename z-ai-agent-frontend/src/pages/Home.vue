<template>
  <div class="home">
    <section class="hero card glass">
      <div class="hero-inner container">
        <div class="hero-text">
          <div class="chip">Next-Gen</div>
          <h2 class="neon-title">极客级 AI 体验中心</h2>
          <p class="neon-sub">两种能力形态 · 流式对话 · 极速响应 · 统一美学</p>
          <div class="cta">
            <router-link to="/love" class="btn primary">开始恋爱大师</router-link>
            <router-link to="/manus" class="btn">启动超级智能体</router-link>
          </div>
        </div>
      </div>
    </section>

    <section class="container grid cols-2 cards-grid">
      <router-link to="/love" class="card app-card">
        <div class="app-card-inner">
          <div class="app-avatar love">💗</div>
          <div>
            <h3>AI 恋爱大师</h3>
            <p>恋爱沟通与情感建议，温柔可靠的陪伴与引导。</p>
          </div>
        </div>
      </router-link>
      <router-link to="/manus" class="card app-card">
        <div class="app-card-inner">
          <div class="app-avatar manus">🧠</div>
          <div>
            <h3>AI 超级智能体</h3>
            <p>面向任务编排与工具调用的多步骤智能执行。</p>
          </div>
        </div>
      </router-link>
    </section>
  </div>
  </template>

<script setup lang="ts">
import { useSEO } from '@/composables/useSEO'
useSEO({
  title: 'Z AI Agent - 极客级 AI 体验中心',
  description: 'AI 恋爱大师与 AI 超级智能体，流式对话与多步骤执行的统一体验。',
  keywords: 'AI, 恋爱大师, 超级智能体, 流式对话, SSE, Vue3'
})
</script>

<style scoped>
.home { display: grid; gap: 22px; }
.hero { border-radius: 22px; overflow: hidden; }
.hero-inner { display: grid; place-items: center; min-height: 300px; background: radial-gradient(800px 300px at 30% 0%, rgba(124,58,237,0.25), transparent 60%); }
.hero-text { display: grid; gap: 12px; text-align: center; }
.cta { display: inline-flex; gap: 10px; justify-content: center; margin-top: 6px; }
.cards-grid { margin-top: 6px; }
.app-card { display: block; padding: 18px; color: var(--text); text-decoration: none; transition: transform .15s ease, box-shadow .15s ease; }
.app-card:hover { transform: translateY(-2px); box-shadow: 0 14px 40px rgba(0,0,0,0.25); }
.app-card-inner { display: grid; grid-template-columns: 56px 1fr; align-items: center; gap: 14px; }
.app-avatar { display: grid; place-items: center; width: 56px; height: 56px; border-radius: 16px; background: rgba(255,255,255,0.05); border: 1px solid var(--border); font-size: 26px; }
.app-avatar.love { box-shadow: 0 0 24px rgba(236,72,153,0.35) inset; }
.app-avatar.manus { box-shadow: 0 0 24px rgba(59,130,246,0.35) inset; }
h3 { margin: 0 0 6px; }
p { margin: 0; color: var(--muted); }
</style>


