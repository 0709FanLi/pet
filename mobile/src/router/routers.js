/**
 * 路由配置集合 - 支持PC端和移动端条件编译
 */
import { ROUTER_PATHS, ROUTER_PERMISSION } from '@/router/constants'

/**
 * 路由元信息工厂函数
 * @param {string} title - 页面标题
 * @param {string} permissionLevel - 权限级别
 * @param {Object} options - 其他选项
 * @returns {Object} 路由元信息对象
 */
const createRouteMeta = (title, permissionLevel, options = {}) => ({
  title,
  permissionLevel,
  ...options,
})

const Home = () => import('@/views/home.vue')
const Login = () => import('@/views/login.vue')
const Publish = () => import('@/views/publish.vue')
const PublishSuccess = () => import('@/views/publish-success.vue')
const PetDetail = () => import('@/views/pet-detail.vue')
const MyLostPets = () => import('@/views/my-lost-pets.vue')

const getRoutes = () => {
  console.log('📱 [MOBILE-ROUTER] 构建移动端路由配置...')

  // 公开访问路由 - 无需登录
  const publicRoutes = [
    {
      path: ROUTER_PATHS.HOME,
      name: 'home',
      component: Home,
      meta: createRouteMeta(
        import.meta.env.VITE_APP_TITLE,
        ROUTER_PERMISSION.PUBLIC
      ),
    },
    {
      path: ROUTER_PATHS.LOGIN,
      name: 'login',
      component: Login,
      meta: createRouteMeta('登录', ROUTER_PERMISSION.PUBLIC),
    },
  ]

  // 需要登录的路由
  const authenticatedRoutes = [
    {
      path: '/publish',
      name: 'publish',
      component: Publish,
      meta: createRouteMeta('发布丢失信息', ROUTER_PERMISSION.AUTHENTICATED),
    },
    {
      path: '/publish-success',
      name: 'publish-success',
      component: PublishSuccess,
      meta: createRouteMeta('发布成功', ROUTER_PERMISSION.AUTHENTICATED),
    },
    {
      path: '/mobile/pet/:id',
      name: 'pet-detail',
      component: PetDetail,
      meta: createRouteMeta('宠物详情', ROUTER_PERMISSION.AUTHENTICATED),
    },
    {
      path: '/my-lost-pets',
      name: 'my-lost-pets',
      component: MyLostPets,
      meta: createRouteMeta('我的发布', ROUTER_PERMISSION.AUTHENTICATED),
    },
  ]

  // 仅管理员访问的路由
  const adminRoutes = [
    // 暂无
  ]

  return [...publicRoutes, ...authenticatedRoutes, ...adminRoutes]
}

// ===== 条件导出路由 =====
export const routes = getRoutes()

console.log(`✅ [ROUTER] 移动端路由配置完成，共${routes.length}个路由`)
