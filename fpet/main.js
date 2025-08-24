import App from './App'

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
  ...App,
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
import './uni.scss'
import uviewPlus from 'uview-plus'
import 'uview-plus/index.scss'
export function createApp() {
  const app = createSSRApp(App)
  app.use(uviewPlus)

  // MQTT包装器将在登录时初始化
  console.log('应用初始化完成')

  return {
    app,
  }
}
// #endif
