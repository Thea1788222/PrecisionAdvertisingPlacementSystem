import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { initAdTracker } from './utils/adTracker'

// 初始化广告追踪SDK
initAdTracker();

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
