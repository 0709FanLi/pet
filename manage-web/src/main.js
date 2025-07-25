import { createApp } from 'vue'
import App from './App.vue'
import i18n from './language'
import 'element-plus/dist/index.css'
import './styles/color.scss'
import './styles/global.scss'
import './styles/scroll.scss'

import { setupStore } from '@/store'
import router from './router'

const app = createApp(App)

// 注册Vue I18n实例
app.use(i18n)

// 注册Store
setupStore(app)

// 注册路由
app.use(router)

// 全局错误处理
app.config.errorHandler = (err, vm, info) => {
    console.error('🖥️ [APP-ERROR]', err, info);
    
    // 生产环境可以发送错误到监控系统
    if (import.meta.env.PROD) {
        // sendErrorToMonitoring(err, info);
    }
};

app.mount('#app')

console.log('🖥️ [MAIN] PC端应用启动完成');

// 开发环境下的全局调试信息
if (import.meta.env.DEV) {
    console.log('🖥️ [DEV] 开发环境启动');
    console.log('🖥️ [DEV] 路由信息:', router?.getRoutes());
    
    // 开发环境下的全局对象，方便调试
    window.__PC_APP__ = {
        app,
        router,
        version: '1.0.0',
        buildTime: new Date().toISOString()
    };
}
