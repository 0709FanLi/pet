/**
 * 移动端应用入口文件
 * 初始化Vue应用和移动端路由系统
 */
import { createApp } from 'vue';
import App from './mobile-app.vue';
import { setupStore } from '@/store'
import i18n from './language'
import './styles/color.scss'
// 移动端路由系统
import router from './router/index.js';

// Vant UI组件库
import Vant from 'vant';
import 'vant/lib/index.css';

// 全局媒体任务轮询管理器
import { initMediaTaskPolling } from './presenter/media-task-polling'
//全局通知
import {showNotificationBase} from '@/utils/mobile-ui-feedback.js'

// 创建Vue应用实例
const app = createApp(App);

app.use(i18n)

// 注册Vant UI组件库
app.use(Vant);
// app.use(ElementPlusX)

// 注册store
setupStore(app)
// 注册移动端路由
if (router) {
    app.use(router);
    console.log('📱 [MAIN] 移动端路由注册成功');
} else {
    console.error('📱 [MAIN] 移动端路由初始化失败');
}

// 全局错误处理
app.config.errorHandler = (err, vm, info) => {
    console.error('📱 [APP-ERROR]', err, info);
    
    // 生产环境可以发送错误到监控系统
    if (import.meta.env.PROD) {
        // sendErrorToMonitoring(err, info);
    }
};

// 初始化全局媒体任务轮询管理器
initMediaTaskPolling(showNotificationBase)

// 挂载应用
app.mount('#app');

console.log('📱 [MAIN] 移动端应用启动完成');

// 开发环境下的全局调试信息
if (import.meta.env.DEV) {
    console.log('📱 [DEV] 开发环境启动');
    console.log('📱 [DEV] 路由信息:', router?.getRoutes());
    
    // 开发环境下的全局对象，方便调试
    window.__MOBILE_APP__ = {
        app,
        router,
        version: '1.0.0',
        buildTime: new Date().toISOString()
    };
}
