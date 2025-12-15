import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import Layout from '../layouts/Layout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView
    },
    {
      path: '/',
      component: Layout,
      children: [
        {
          path: '/dashboard',
          name: 'dashboard',
          component: () => import('../views/DashboardView.vue')
        },
        {
          path: '/materials',
          name: 'materials',
          component: () => import('../views/MaterialsView.vue')
        },
        {
          path: '/positions',
          name: 'positions',
          component: () => import('../views/PositionsView.vue')
        },
        {
          path: '/advertisers',
          name: 'advertisers',
          component: () => import('../views/AdvertisersView.vue')
        },
        {
          path: '/statistics',
          name: 'statistics',
          component: () => import('../views/StatisticsView.vue')
        }
      ]
    }
  ]
})

// 添加路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  // 如果访问的是登录或注册页面，且已经登录，则重定向到仪表板
  if ((to.name === 'login' || to.name === 'register') && token) {
    next({ name: 'dashboard' })
    return
  }
  
  // 如果访问其他页面但没有登录，则重定向到登录页
  if (to.name !== 'login' && to.name !== 'register' && !token) {
    next({ name: 'login' })
    return
  }
  
  next()
})

export default router