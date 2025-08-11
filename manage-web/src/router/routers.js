/**
 * 路由配置集合 - 基础框架路由
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

const HomeView = () => import('@/views/Home.vue')
const LoginView = () => import('@/views/Login.vue')
const AuditPending = () => import('@/views/audit/Pending.vue')
const AuditDetail = () => import('@/views/audit/Detail.vue')
const AuditHistory = () => import('@/views/audit/History.vue')
const OpsPin = () => import('@/views/ops/Pin.vue')
const OpsBlock = () => import('@/views/ops/Block.vue')
const DetectiveList = () => import('@/views/detectives/List.vue')
const DetectiveRejected = () => import('@/views/detectives/Rejected.vue')
const OrdersList = () => import('@/views/orders/List.vue')
const StatsOverview = () => import('@/views/stats/Overview.vue')
const StatsDetails = () => import('@/views/stats/Details.vue')
const SettingsBase = () => import('@/views/settings/Base.vue')
const SettingsReview = () => import('@/views/settings/Review.vue')

// ===== 移动端组件懒加载 =====

// ===== PC端路由配置 =====
const getPCRoutes = () => {
  // 公开访问路由 - 无需登录
  const publicRoutes = [
    {
      path: ROUTER_PATHS.HOME,
      name: 'home',
      component: HomeView,
      meta: createRouteMeta('管理后台', ROUTER_PERMISSION.PUBLIC),
      children: [
        {
          path: 'audit/pending',
          component: AuditPending,
          meta: createRouteMeta('启事·待审核', ROUTER_PERMISSION.PUBLIC),
        },
        {
          path: 'audit/detail/:id',
          component: AuditDetail,
          meta: createRouteMeta('启事·详情', ROUTER_PERMISSION.PUBLIC),
        },
        {
          path: 'audit/history',
          component: AuditHistory,
          meta: createRouteMeta('启事·历史', ROUTER_PERMISSION.PUBLIC),
        },
        {
          path: 'ops/pin',
          component: OpsPin,
          meta: createRouteMeta('运营·置顶推荐', ROUTER_PERMISSION.PUBLIC),
        },
        {
          path: 'ops/block',
          component: OpsBlock,
          meta: createRouteMeta('运营·屏蔽', ROUTER_PERMISSION.PUBLIC),
        },
        {
          path: 'detectives/list',
          component: DetectiveList,
          meta: createRouteMeta('侦探·列表', ROUTER_PERMISSION.PUBLIC),
        },
        {
          path: 'detectives/rejected',
          component: DetectiveRejected,
          meta: createRouteMeta('侦探·未通过', ROUTER_PERMISSION.PUBLIC),
        },
        {
          path: 'orders/list',
          component: OrdersList,
          meta: createRouteMeta('订单·列表', ROUTER_PERMISSION.PUBLIC),
        },
        {
          path: 'stats/overview',
          component: StatsOverview,
          meta: createRouteMeta('统计·概览', ROUTER_PERMISSION.PUBLIC),
        },
        {
          path: 'stats/details',
          component: StatsDetails,
          meta: createRouteMeta('统计·详情', ROUTER_PERMISSION.PUBLIC),
        },
        {
          path: 'settings/base',
          component: SettingsBase,
          meta: createRouteMeta('设置·基础', ROUTER_PERMISSION.PUBLIC),
        },
        {
          path: 'settings/review',
          component: SettingsReview,
          meta: createRouteMeta('设置·审核', ROUTER_PERMISSION.PUBLIC),
        },
      ],
    },
    {
      path: ROUTER_PATHS.LOGIN,
      name: 'login',
      component: LoginView,
      meta: createRouteMeta('登录/注册', ROUTER_PERMISSION.PUBLIC),
    },
  ]

  // 需要登录的路由
  const authenticatedRoutes = [
    // 暂无
  ]

  // 仅管理员访问的路由
  const adminRoutes = [
    // 暂无
  ]

  return [...publicRoutes, ...authenticatedRoutes, ...adminRoutes]
}

// ===== 条件导出路由 =====
export const routes = getPCRoutes()
