/**
 * 路由配置集合 - 基础框架路由
 */
import { ROUTER_PATHS, ROUTER_PERMISSION } from '@/router/constants';
import { t } from '@/language/index.js';

// 获取构建目标
const BUILD_TARGET = process.env.BUILD_TARGET || 'pc';

console.log(`🚀 [ROUTER] 当前构建目标: ${BUILD_TARGET}`);

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

// ===== PC端组件懒加载 =====
let HomeView, LoginView;

// ===== 移动端组件懒加载 =====
let MobileHomeView, MobileLoginView;

// 根据构建目标条件加载组件
if (BUILD_TARGET === 'pc') {
    console.log('📦 [PC-ROUTER] 加载PC端路由组件...');

    // PC端组件
    HomeView = () => import('@/views/Home.vue');
    LoginView = () => import('@/views/Login.vue');

} else if (BUILD_TARGET === 'mobile') {
    console.log('📱 [MOBILE-ROUTER] 加载移动端路由组件...');

    // 移动端组件
    MobileHomeView = () => import('@/mobile/views/MobileHome.vue');
    MobileLoginView = () => import('@/mobile/views/MobileLogin.vue');
}

// ===== PC端路由配置 =====
const getPCRoutes = () => {
    console.log('🖥️ [PC-ROUTER] 构建PC端路由配置...');

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

// 导出路由
export const routes = getRoutes();

console.log(`✅ [ROUTER] PC端路由配置完成，共${routes.length}个路由`);

// ===== 移动端路由配置 =====
const getMobileRoutes = () => {
    console.log('📱 [MOBILE-ROUTER] 构建移动端路由配置...');

    // 基础路由配置
    const basicRoutes = [
        {
            path: ROUTER_PATHS.HOME,
            name: 'home',
            component: MobileHomeView,
            meta: createRouteMeta(process.env.VITE_APP_TITLE, ROUTER_PERMISSION.PUBLIC, {
                keepAlive: true
            })
        },
        {
            path: ROUTER_PATHS.LOGIN,
            name: 'login',
            component: MobileLoginView,
            meta: createRouteMeta('登录/注册', ROUTER_PERMISSION.PUBLIC)
        }
    ];

    return basicRoutes;
};

// ===== 条件导出路由 =====
export const routes = BUILD_TARGET === 'mobile' ? getMobileRoutes() : getPCRoutes();

console.log(`✅ [ROUTER] ${BUILD_TARGET.toUpperCase()}端路由配置完成，共${routes.length}个路由`);