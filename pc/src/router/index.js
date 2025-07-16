/**
 * 路由入口文件
 */
import { createRouter, createWebHistory } from 'vue-router';
import { routes } from './routers';

// 创建路由实例
const router = createRouter({
    history: createWebHistory(),
    routes
});

// 全局前置导航守卫
router.beforeEach((to, from, next) => {
    // 设置页面标题
    if (to.meta?.title) {
        document.title = to.meta.title;
    }
    next();
});

export default router;