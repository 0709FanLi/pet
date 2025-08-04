/**
 * 路由入口文件 - 优化版本
 */
import { createRouter, createWebHistory } from 'vue-router'
import { routes } from './routers'
import { getAccessToken } from '@/utils/auth'
import { ROUTER_PERMISSION } from './constants'

// 创建路由实例
const router = createRouter({
  // 使用History模式
  history: createWebHistory(),
  routes,
})

// 路由守卫 - 认证检查
router.beforeEach((to, from, next) => {
  const token = getAccessToken()
  const isAuthenticated = !!token

  // 检查是否需要认证
  if (to.meta.permissionLevel === ROUTER_PERMISSION.AUTHENTICATED) {
    if (!isAuthenticated) {
      console.log('🔐 [ROUTER] 需要登录才能访问:', to.path)
      // 跳转到登录页面
      next('/login')
      return
    }
  }

  // 如果已登录但访问登录页，跳转到首页
  if (to.path === '/login' && isAuthenticated) {
    console.log('👤 [ROUTER] 已登录用户访问登录页，重定向到首页')
    next('/')
    return
  }

  next()
})

// 路由导航完成后的钩子
router.afterEach((to, from, failure) => {
  if (failure) {
    console.error('🏁 [ROUTER] 导航失败:', failure)
  }
})

// 路由错误处理
router.onError(error => {
  console.error('💥 [ROUTER] 路由错误:', error)
})

export default router
