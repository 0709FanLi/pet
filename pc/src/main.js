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

app.mount('#app')
