/**
 * 路由常量和配置
 */

// 路由路径常量
export const ROUTER_PATHS = {
  HOME: '/', // 首页
  LOGIN: '/login', // 登录页面
}

// 路由权限级别配置
export const ROUTER_PERMISSION = {
  PUBLIC: 'public', // 公开访问 - 无需登录
  AUTHENTICATED: 'authenticated', // 需要登录
  ADMIN: 'admin', // 仅管理员
}
