<template>
  <div class="app">
    <header class="app-header">
      <div class="container header-inner">
        <div class="brand">
          <span class="logo">⚡</span>
          <span class="brand-text">Z AI Agent</span>
        </div>
        <nav class="nav">
          <router-link to="/">主页</router-link>
          <router-link to="/love">AI 恋爱大师</router-link>
          <router-link to="/manus">AI 超级智能体</router-link>
        </nav>
      </div>
    </header>
    <main class="app-main">
      <div class="container">
        <router-view />
      </div>
    </main>
    <footer class="site-footer">
      <div class="container inner">
        <div>© 2025 我的花园 | Inspired by <a href="https://www.yuque.com/pussy-geu0l" target="_blank" rel="noopener">zluolan</a></div>
        <div>
          <a href="/" @click.prevent>关于我们</a>
          ·
          <a href="/" @click.prevent>隐私政策</a>
          ·
          <a href="/" @click.prevent>用户协议</a>
        </div>
      </div>
    </footer>
  </div>
  </template>

<script setup lang="ts">
</script>

<style>
* { box-sizing: border-box; }
.app { min-height: 100%; display: flex; flex-direction: column; }
.app-header { position: sticky; top: 0; z-index: 20; border-bottom: 1px solid var(--border); background: rgba(11,15,26,0.6); backdrop-filter: blur(8px); }
.header-inner { display: flex; align-items: center; justify-content: space-between; padding: 12px 0; }
.brand { display: flex; align-items: center; gap: 8px; font-weight: 700; }
.logo { display: inline-flex; width: 26px; height: 26px; align-items: center; justify-content: center; background: linear-gradient(135deg, var(--primary), var(--secondary)); border-radius: 8px; box-shadow: 0 0 18px rgba(124,58,237,0.5); }
.brand-text { letter-spacing: 0.3px; }
.nav { display: flex; gap: 10px; }
.nav a { text-decoration: none; color: var(--text); padding: 6px 10px; border-radius: 8px; border: 1px solid transparent; }
.nav a.router-link-active { border-color: var(--border); background: rgba(255,255,255,0.04); }
.app-main { flex: 1; padding: 20px 0; }
</style>


