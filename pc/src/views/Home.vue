<template>
    <div class="home-page">
        <!-- 移动端底部导航栏 -->
        <div class="mobile-nav" v-if="isMobile">
            <div class="nav-item active">
                <span class="nav-text">首页</span>
            </div>
            <div class="nav-item">
                <span class="nav-text">我的发布</span>
            </div>
            <div class="nav-item">
                <span class="nav-text">个人中心</span>
            </div>
        </div>
        
        <!-- PC端顶部导航栏 -->
        <div class="pc-nav" v-if="!isMobile">
            <div class="nav-container">
                <div class="logo-section">
                    <l-img src="/static/logo3.png" w="32px" h="32px" />
                    <span class="app-name">{{ $t('appName') }}</span>
                </div>
                <div class="nav-items">
                    <div class="nav-item active">首页</div>
                    <div class="nav-item">我的发布</div>
                    <div class="nav-item">个人中心</div>
                </div>
            </div>
        </div>
        
        <!-- 主内容区域 -->
        <div class="main-content">
            <!-- 发布按钮 -->
            <div class="publish-section">
                <l-button 
                    text="发布丢失信息"
                    @click="handlePublish"
                    :class="isMobile ? 'mobile-publish-btn' : 'pc-publish-btn'"
                />
            </div>
            
            <!-- 宠物列表 -->
            <div class="pet-list">
                <div class="list-header" v-if="!isMobile">
                    <h2>最新发布</h2>
                </div>
                
                <!-- 示例卡片 -->
                <div class="pet-card" v-for="i in 6" :key="i">
                    <div class="pet-image">
                        <l-img src="/static/avatar_def.png" w="100%" h="120px" />
                    </div>
                    <div class="pet-info">
                        <h3 class="pet-location">最后出现地点：公园附近</h3>
                        <p class="pet-amount">悬赏金额：¥500</p>
                        <p class="pet-time">发布时间：2小时前</p>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- 移动端悬浮发布按钮 -->
        <div class="floating-publish" v-if="isMobile" @click="handlePublish">
            <span class="plus-icon">+</span>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import LButton from '@/components/l-button.vue';
import LImg from '@/components/l-img.vue';

const { t } = useI18n();
const router = useRouter();

// 响应式数据
const windowWidth = ref(window.innerWidth);

// 计算属性
const isMobile = computed(() => windowWidth.value <= 768);

// 窗口大小变化监听
const handleResize = () => {
    windowWidth.value = window.innerWidth;
};

// 发布按钮点击
const handlePublish = () => {
    console.log('发布丢失信息');
    // 跳转到发布页面
    // router.push('/publish');
};

// 生命周期
onMounted(() => {
    window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
    window.removeEventListener('resize', handleResize);
});
</script>

<style scoped>
/* 移动端样式 (默认) */
.home-page {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
  position: relative;
}

/* 移动端底部导航栏 */
.mobile-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: #FFFFFF;
  border-top: 1px solid #E0E0E0;
  display: flex;
  justify-content: space-around;
  align-items: center;
  z-index: 1000;
}

.mobile-nav .nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px;
  cursor: pointer;
}

.mobile-nav .nav-item.active {
  color: #87CEEB;
}

.pc-nav .nav-item.active {
  background: #87CEEB;
}

.nav-text {
  font-size: 12px;
  margin-top: 4px;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  padding: 16px 0 80px 0; /* 底部留出导航栏空间 */
  overflow-y: auto;
}

/* 发布按钮区域 */
.publish-section {
  display: none; /* 移动端隐藏，使用悬浮按钮 */
}

/* 宠物列表 */
.pet-list {
  padding: 0;
}

.pet-card {
  background: #FFFFFF;
  margin-bottom: 1px;
  padding: 16px;
  display: flex;
  gap: 12px;
}

.pet-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.pet-info {
  flex: 1;
}

.pet-location {
  font-size: 16px;
  font-weight: 500;
  color: #333333;
  margin: 0 0 8px 0;
}

.pet-amount {
  font-size: 14px;
  color: #FF5252;
  font-weight: 500;
  margin: 0 0 4px 0;
}

.pet-time {
  font-size: 12px;
  color: #999999;
  margin: 0;
}

/* 移动端悬浮发布按钮 */
.floating-publish {
  position: fixed;
  bottom: 80px;
  right: 16px;
  width: 56px;
  height: 56px;
  background: #FF5252;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(255, 82, 82, 0.3);
  z-index: 999;
}

.plus-icon {
  color: #FFFFFF;
  font-size: 24px;
  font-weight: bold;
}

/* PC端样式 */
@media (min-width: 769px) {
  .home-page {
    background: #FFFFFF;
  }
  
  /* PC端顶部导航栏 */
  .pc-nav {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 64px;
    background: #FFFFFF;
    border-bottom: 1px solid #E0E0E0;
    z-index: 1000;
  }
  
  .nav-container {
    max-width: 1200px;
    margin: 0 auto;
    height: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 40px;
  }
  
  .logo-section {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  .app-name {
    font-size: 20px;
    font-weight: 600;
    color: #333333;
  }
  
  .nav-items {
    display: flex;
    gap: 32px;
  }
  
  .pc-nav .nav-item {
    padding: 8px 16px;
    cursor: pointer;
    border-radius: 6px;
    transition: background-color 0.3s ease;
  }
  
  .pc-nav .nav-item.active {
    background: #87CEEB;
    color: #FFFFFF;
  }
  
  .pc-nav .nav-item:hover:not(.active) {
    background: #f5f5f5;
  }
  
  /* PC端主内容 */
  .main-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 80px 40px 40px 40px; /* 顶部留出导航栏空间 */
  }
  
  /* PC端发布按钮 */
  .publish-section {
    display: block;
    margin-bottom: 32px;
  }
  
  .pc-publish-btn {
    background: #FF5252;
    color: #FFFFFF;
    padding: 12px 24px;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 500;
  }
  
  /* PC端列表标题 */
  .list-header h2 {
    font-size: 24px;
    color: #333333;
    margin: 0 0 24px 0;
  }
  
  /* PC端网格布局 */
  .pet-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 24px;
  }
  
  .list-header {
    grid-column: 1 / -1;
  }
  
  .pet-card {
    background: #FFFFFF;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 0;
    flex-direction: column;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
  }
  
  .pet-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  }
  
  .pet-image {
    width: 100%;
    height: 200px;
    margin-bottom: 16px;
  }
  
  .pet-location {
    font-size: 18px;
    margin-bottom: 12px;
  }
  
  .pet-amount {
    font-size: 16px;
    margin-bottom: 8px;
  }
  
  .pet-time {
    font-size: 14px;
  }
  
  /* PC端隐藏移动端元素 */
  .mobile-nav,
  .floating-publish {
    display: none;
  }
}
</style>