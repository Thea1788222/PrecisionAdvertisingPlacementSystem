import { createRouter, createWebHashHistory } from 'vue-router'
import VideoList from '@/views/VideoList.vue'
import VideoPlayer from '@/views/VideoPlayer.vue'

const routes = [
  { path: '/', component: VideoList },
  { path: '/video/:id', component: VideoPlayer },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

export default router
