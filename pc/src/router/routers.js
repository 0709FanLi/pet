/**
 * 路由配置集合 - 基础框架路由
 */
import { ROUTER_PATHS, ROUTER_PERMISSION } from '@/router/constants';

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
    ...options
});


const HomeView = () => import('@/views/Home.vue');
const LoginView = () => import('@/views/Login.vue');

// ===== 移动端组件懒加载 =====

// ===== PC端路由配置 =====
const getPCRoutes = () => {
    // 公开访问路由 - 无需登录
    const publicRoutes = [
        {
            path: ROUTER_PATHS.HOME,
            name: 'home',
            component: HomeView,
            meta: createRouteMeta(import.meta.env.VITE_APP_TITLE, ROUTER_PERMISSION.PUBLIC)
        },
        {
            path: ROUTER_PATHS.LOGIN,
            name: 'login',
            component: LoginView,
            meta: createRouteMeta('登录/注册', ROUTER_PERMISSION.PUBLIC)
        }
    ];

    // 需要登录的路由
    const authenticatedRoutes = [
        // 暂无
    ];

    // 仅管理员访问的路由
    const adminRoutes = [
        // 暂无
    ];

    return [
        ...publicRoutes,
        ...authenticatedRoutes,
        ...adminRoutes
    ];
};

// ===== 条件导出路由 =====
export const routes = getPCRoutes();
