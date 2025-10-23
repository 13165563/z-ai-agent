import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import Home from './pages/Home.vue'
import LoveChat from './pages/LoveChat.vue'
import ManusChat from './pages/ManusChat.vue'
import './styles/global.css'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: Home },
    { path: '/love', name: 'love', component: LoveChat },
    { path: '/manus', name: 'manus', component: ManusChat }
  ]
})

createApp(App).use(router).mount('#app')


