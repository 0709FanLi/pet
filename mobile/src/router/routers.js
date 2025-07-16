/**
 * 路由配置集合 - 支持PC端和移动端条件编译
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


const Home = () => import('@/views/home.vue');
const Login = () => import('@/views/login.vue');

const getRoutes = () => {
    console.log('📱 [MOBILE-ROUTER] 构建移动端路由配置...');

    // 公开访问路由 - 无需登录
    const publicRoutes = [
        {
            path: ROUTER_PATHS.HOME,
            name: 'home',
            component: Home,
            meta: createRouteMeta(import.meta.env.VITE_APP_TITLE, ROUTER_PERMISSION.PUBLIC)
        },
        {
            path: ROUTER_PATHS.LOGIN,
            name: 'login',
            component: Login,
            meta: createRouteMeta('登录', ROUTER_PERMISSION.PUBLIC)
        },
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
export const routes = getRoutes();

console.log(`✅ [ROUTER] 移动端路由配置完成，共${routes.length}个路由`);