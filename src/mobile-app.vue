<template>
  <div class="mobile-app-status-navbar"></div>
  <div id="mobile-app" class="mobile-app" :style="{ height: mainHeight }">
    <!-- 404页面：显示移动端404组件 -->
    <MobileNotFoundView v-if="isNotFoundPage" />
    
    <!-- 正常页面：路由过渡动画容器 -->
    <router-view v-else v-slot="{ Component, route }">
      <keep-alive>
        <component :is="Component" :key="route.path" v-if="route.meta.keepAlive" style="flex: 1;" />
      </keep-alive>
      <component :is="Component" :key="route.path" v-if="!route.meta.keepAlive" style="flex: 1;" />
    </router-view>
  </div>
  <div class="mobile-app-status-bottom"></div>
</template>

<script setup>
import { ref, computed, onMounted, watch ,onUnmounted, getCurrentInstance, nextTick} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { pxToRem } from '@/utils/px-to-rem';
import { useNetworkStatusStore } from '@/store/modules/network-status';
import { useI18n } from 'vue-i18n';
import MobileNotFoundView from '@/mobile/views/mobile-not-found.vue';

 // 使用网络状态store
 const networkStatusStore = useNetworkStatusStore();
 const { t } = useI18n();
    // 网络状态变化处理函数
    const handleNetworkStatusChange = (isOnline) => {
        if (!isOnline) {
            showToast(t('youAreOffline'));
        } else {
            showToast(t('youAreOnline'));
        }
    };
// 路由相关
const route = useRoute();
const router = useRouter();

// 响应式数据
const isGlobalLoading = ref(false);
const isDevelopment = computed(() => import.meta.env.DEV);
const mainHeight = ref(pxToRem(window.innerHeight));

// 路由加载状态
const isNavigating = ref(false);

// 移动端404检测逻辑 - 参考PC端App.vue的实现
const isNotFoundPage = computed(() => {
  // 如果路由还没有准备好或正在导航中，不显示404
  if (isNavigating.value) {
    return false;
  }
  
  // 只有在路由完全准备好且没有匹配时，才显示404
  return route.matched.length === 0;
});

// 处理路由变化
const handleRouteChange = (newRoute, oldRoute) => {
  // 页面切换时的逻辑
  console.log('📱 [APP] 页面切换:', newRoute.meta?.title);
  
  // 设置页面标题
  if (newRoute.meta?.title) {
    document.title = newRoute.meta.title;
  }
  
  // 可以在这里添加页面统计、埋点等逻辑
  if (typeof window !== 'undefined' && window._hmt) {
    window._hmt.push(['_trackPageview', newRoute.path]);
  }
};

// 监听路由变化
watch(route, (newRoute, oldRoute) => {
  console.log('📱 [APP] 路由变化:', oldRoute?.path, '->', newRoute.path);
  
  // 路由变化时的逻辑处理
  handleRouteChange(newRoute, oldRoute);
}, { immediate: true });

// 监听路由变化，管理导航状态
router.beforeEach((to, from, next) => {
  isNavigating.value = true;
  next();
});

router.afterEach((to, from, failure) => {
  // 使用 nextTick 确保组件完全渲染后再设置状态
  nextTick(() => {
    isNavigating.value = false;
  });
});

onMounted(() => {
  networkStatusStore.setNetworkStatusChangeCallback(handleNetworkStatusChange);
  networkStatusStore.initNetworkListener();
  // 设置全局变量
  const { proxy } = getCurrentInstance();
  proxy.$isMobile = true;
  let agent = navigator.userAgent.toLowerCase();
  let iLastTouch = null;

  if (agent.indexOf('iphone') >= 0 || agent.indexOf('ipad') >= 0) {
    document.body.addEventListener('touchend', function (event) {
      let a = new Date().getTime();
      iLastTouch = iLastTouch || a + 1;
      let c = a - iLastTouch;
      if (c < 500 && c > 0) {
        event.preventDefault();
        return false;
      }
      iLastTouch = a;
    }, false);
  }
});
onUnmounted(() => {
  networkStatusStore.removeNetworkListener();
});
</script>

<style lang="scss">
/* 移动端全局样式重置 */
* {
  box-sizing: border-box;
  -webkit-tap-highlight-color: transparent;
}

html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  overflow-x: hidden;
  -webkit-overflow-scrolling: touch;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: #f5f5f5;
}

.mobile-app-status-navbar{
  height: env(safe-area-inset-top);
  background-color: #f5f5f5;
}
.mobile-app-status-bottom{
  background-color: #f5f5f5;
}
/* 移动端应用容器 */
#mobile-app {
  width: 100%;
  position: relative;
  box-sizing: border-box;
}

.mobile-app {
  display: flex;
  flex-direction: column;
  padding-bottom: env(safe-area-inset-bottom);

}

/* 路由过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-left-enter-active,
.slide-left-leave-active {
  transition: transform 0.3s ease;
}

.slide-left-enter-from {
  transform: translateX(100%);
}

.slide-left-leave-to {
  transform: translateX(-100%);
}

.slide-right-enter-active,
.slide-right-leave-active {
  transition: transform 0.3s ease;
}

.slide-right-enter-from {
  transform: translateX(-100%);
}

.slide-right-leave-to {
  transform: translateX(100%);
}

/* 调试信息样式 */
.debug-info {
  position: fixed;
  top: 20px;
  right: 20px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 10px;
  border-radius: 8px;
  font-size: 12px;
  z-index: 9999;
  
  p {
    margin: 2px 0;
  }
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: transform 0.3s ease;
}

.slide-up-enter-from {
  transform: translateY(100%);
}

.slide-up-leave-to {
  transform: translateY(-100%);
}

/* 深色模式支持 */
[data-theme="dark"] {
  background-color: #1a1a1a;
  color: white;
}

/* 移动端滚动优化 */
.mobile-scroll {
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  scroll-behavior: smooth;
}

/* 禁用选择和缩放 */
.mobile-app {
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
  
  -webkit-touch-callout: none;
  -webkit-text-size-adjust: none;
}

/* 允许输入框选择文本 */
input, textarea {
  -webkit-user-select: text;
  -moz-user-select: text;
  -ms-user-select: text;
  user-select: text;
}
  .hide-scroll-bar{
    -ms-overflow-style: none;  /* IE 和 Edge */
  scrollbar-width: none;     /* Firefox */
  
  &::-webkit-scrollbar {     /* Chrome, Safari 和 Opera */
    display: none;
    width: 0;
  }
  }
  *{
    -webkit-tap-highlight-color: transparent;
    tap-highlight-color: transparent;
  }
  .mobile-toast .van-toast__text{
    // 移动端toast样式 文字空格处换行
    white-space: pre-wrap; /* 保留换行符，合并空格 */
    overflow-wrap: break-word; /* 允许单词在必要时换行 */
    word-break: keep-all; /* 防止非空格处断开 */
    word-wrap: break-word; /* 允许单词在必要时换行 */
  }
</style>
