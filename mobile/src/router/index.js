/**
 * 路由入口文件 - 优化版本
 */
import { createRouter, createWebHistory } from 'vue-router';
import { routes } from './routers';

// 创建路由实例
const router = createRouter({
    // 使用History模式
    history: createWebHistory(),
    routes
});


// 路由导航完成后的钩子
router.afterEach((to, from, failure) => {
    if (failure) {
        console.error('🏁 [ROUTER] 导航失败:', failure);
    }
});

// 路由错误处理
router.onError((error) => {
    console.error('💥 [ROUTER] 路由错误:', error);
});

export default router;