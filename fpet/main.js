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
import { mqttWrapper } from './common/mqtt-wrapper'
export function createApp() {
  const app = createSSRApp(App)
  app.use(uviewPlus)

  // 初始化MQTT包装器
  console.log('MQTT包装器已初始化')

  return {
    app,
  }
}
// #endif
