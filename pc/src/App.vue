<script setup>
    import NotFoundView from '@/views/NotFound.vue';
    import { ref, computed } from 'vue';
    import { useRoute } from 'vue-router';
    
    const route = useRoute();
    
    // 路由加载状态
    const isNavigating = ref(false);
    
    // 404检测
    const isNotFoundPage = computed(() => {
        if (isNavigating.value) {
            return false;
        }
        return route.matched.length === 0;
    });

</script>

<template>
    <!-- 404页面 -->
    <div v-if="isNotFoundPage" class="app-container">
        <NotFoundView />
    </div>

    <!-- 正常页面：路由过渡动画容器 -->
    <div v-else class="app-container">
        <div class="app-content">
            <router-view v-slot="{ Component, route }">
                <transition name="fade" mode="out-in">
                    <keep-alive>
                        <component :is="Component" :key="route.path" v-if="route.meta.keepAlive" />
                    </keep-alive>
                    <component :is="Component" :key="route.path" v-if="!route.meta.keepAlive" />
                </transition>
            </router-view>
        </div>
    </div>
</template>

<style lang="scss">
@use './styles/element-plus-override.scss' as *;
    /* 全局样式 */
    html, body {
        margin: 0;
        padding: 0;
        font-family: 'SF Pro Text', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
        /* 背景色和文本颜色在color.scss中设置 */
        background-color: var(--bg-primary);
        color: var(--text-title);
    }

    * {
        box-sizing: border-box;
    }

    .app-container {
        display: flex;
        flex-direction: column;
        height: 100vh;
        overflow: hidden;
        flex: 1;
    }

    .app-content {
        flex: 1;
        display: flex;
    }
    @media (max-width: 768px) {
        .hide-scroll-bar{
          -ms-overflow-style: none;
          scrollbar-width: none;
      }
      *{
        -webkit-tap-highlight-color: transparent;
        tap-highlight-color: transparent;
      }
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
</style>