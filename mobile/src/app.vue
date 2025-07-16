<template>
  <div id="mobile-app" class="mobile-app">
    
    <!-- 正常页面：路由过渡动画容器 -->
    <router-view v-slot="{ Component, route }">
      <keep-alive>
        <component :is="Component" :key="route.path" v-if="route.meta.keepAlive" style="flex: 1;" />
      </keep-alive>
      <component :is="Component" :key="route.path" v-if="!route.meta.keepAlive" style="flex: 1;" />
    </router-view>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch ,onUnmounted, getCurrentInstance, nextTick} from 'vue';
import { useRoute, useRouter } from 'vue-router';

// 路由相关
const route = useRoute();
const router = useRouter();

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
