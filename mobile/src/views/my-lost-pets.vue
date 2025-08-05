<template>
  <div class="my-lost-pets-page">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="nav-left" @click="goBack">
        <van-icon name="arrow-left" size="20" color="#333" />
      </div>
      <div class="nav-title">我的发布</div>
      <div class="nav-right" @click="goToPublish">
        <van-icon name="plus" size="20" color="#667eea" />
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <van-loading size="24px" color="#667eea" />
      <div class="loading-text">加载中...</div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="lostPets.length === 0" class="empty-state">
      <div class="empty-icon">🐾</div>
      <h3 class="empty-title">还没有发布信息</h3>
      <p class="empty-description">发布宠物丢失信息，让更多人帮助您寻找爱宠</p>
      <van-button 
        round 
        type="primary" 
        class="empty-action-btn"
        @click="goToPublish"
      >
        发布寻宠信息
      </van-button>
    </div>

    <!-- 宠物列表 -->
    <div v-else class="pets-content">
      <!-- 统计信息 -->
      <div class="stats-section">
        <div class="stats-card">
          <div class="stats-item">
            <div class="stats-number">{{ lostPets.length }}</div>
            <div class="stats-label">总发布</div>
          </div>
          <div class="stats-divider"></div>
          <div class="stats-item">
            <div class="stats-number">{{ findingCount }}</div>
            <div class="stats-label">寻找中</div>
          </div>
          <div class="stats-divider"></div>
          <div class="stats-item">
            <div class="stats-number">{{ foundCount }}</div>
            <div class="stats-label">已找到</div>
          </div>
        </div>
      </div>

      <!-- 筛选栏 -->
      <div class="filter-section">
        <div class="filter-tabs">
          <div 
            class="filter-tab"
            :class="{ active: activeFilter === 'all' }"
            @click="setFilter('all')"
          >
            全部
          </div>
          <div 
            class="filter-tab"
            :class="{ active: activeFilter === 'lost' }"
            @click="setFilter('lost')"
          >
            寻找中
          </div>
          <div 
            class="filter-tab"
            :class="{ active: activeFilter === 'found' }"
            @click="setFilter('found')"
          >
            已找到
          </div>
        </div>
      </div>

      <!-- 宠物卡片列表 -->
      <div class="pets-list">
        <div 
          v-for="pet in filteredPets" 
          :key="pet.id" 
          class="pet-card"
          @click="viewDetails(pet.id)"
        >
          <!-- 宠物图片 -->
          <div class="pet-image-container">
            <img 
              :src="getPetImage(pet)" 
              :alt="pet.petName"
              class="pet-image"
            />
            <div class="status-badge" :class="getStatusClass(pet.status)">
              {{ getStatusText(pet.status) }}
            </div>
          </div>

          <!-- 宠物信息 -->
          <div class="pet-info">
            <div class="pet-header">
              <h3 class="pet-name">{{ pet.petName || '未命名' }}</h3>
              <div class="pet-time">{{ formatTime(pet.lostTime) }}</div>
            </div>
            
            <div class="pet-details">
              <div class="detail-item">
                <van-icon name="location-o" size="14" color="#666" />
                <span>{{ pet.lostLocation || '位置未知' }}</span>
              </div>
              <div class="detail-item">
                <van-icon name="gold-coin-o" size="14" color="#666" />
                <span>悬赏 ¥{{ pet.reward || '0' }}</span>
              </div>
            </div>

            <div class="pet-meta">
              <span class="pet-type">{{ pet.petType || '未知' }}</span>
              <span class="separator">·</span>
              <span class="pet-breed">{{ pet.petBreed || '未知品种' }}</span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="pet-actions">
            <van-button 
              size="small" 
              type="primary" 
              plain
              @click.stop="viewDetails(pet.id)"
            >
              查看详情
            </van-button>
            <van-button 
              v-if="pet.status === 'lost'"
              size="small" 
              type="success"
              plain
              @click.stop="markAsFound(pet.id)"
            >
              标记找到
            </van-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部导航栏 -->
    <div class="bottom-nav">
      <div class="nav-item" @click="goToHome">
        <van-icon name="home-o" size="20" />
        <span>首页</span>
      </div>
      <div class="nav-item active">
        <van-icon name="orders-o" size="20" />
        <span>我的发布</span>
      </div>
      <div class="nav-item" @click="goToProfile">
        <van-icon name="user-o" size="20" />
        <span>个人中心</span>
      </div>
    </div>

    <!-- 悬浮发布按钮 -->
    <div class="floating-btn" @click="goToPublish">
      <van-icon name="plus" size="24" color="#fff" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Icon,
  Loading,
  Button,
  showToast,
  showSuccessToast,
  showConfirmDialog
} from 'vant'
import axios from 'axios'

const router = useRouter()

// 数据状态
const loading = ref(true)
const lostPets = ref([])
const activeFilter = ref('all')

// 计算属性
const filteredPets = computed(() => {
  if (activeFilter.value === 'all') {
    return lostPets.value
  }
  return lostPets.value.filter(pet => {
    if (activeFilter.value === 'lost') {
      return pet.status === 'lost'
    } else if (activeFilter.value === 'found') {
      return pet.status === 'found'
    }
    return true
  })
})

const findingCount = computed(() => {
  return lostPets.value.filter(pet => pet.status === 'lost').length
})

const foundCount = computed(() => {
  return lostPets.value.filter(pet => pet.status === 'found').length
})

// 方法
const goBack = () => {
  router.go(-1)
}

const goToHome = () => {
  router.push('/')
}

const goToProfile = () => {
  router.push('/profile')
}

const goToPublish = () => {
  router.push('/publish')
}

const setFilter = (filter) => {
  activeFilter.value = filter
}

const fetchLostPets = async () => {
  loading.value = true
  try {
    // 先尝试从后端获取数据
    try {
      const userId = 1 // TODO: 获取当前用户ID
      const response = await axios.get(`http://localhost:8080/api/lost-pets/user/${userId}`)
      lostPets.value = response.data
    } catch (apiError) {
      console.log('后端API暂不可用，使用示例数据')
      // 如果后端API不可用，使用示例数据
      const allPetsResponse = await axios.get('http://localhost:8080/api/lost-pets')
      lostPets.value = allPetsResponse.data || []
    }
  } catch (error) {
    console.error('获取失败:', error)
    // 完全失败时使用本地示例数据
    lostPets.value = [
      {
        id: 1,
        petName: '小白',
        petType: '狗',
        petBreed: '金毛',
        lostLocation: '朝阳公园',
        lostTime: '2023-12-01T10:00:00',
        reward: '500',
        status: 'lost',
        images: '[]'
      },
      {
        id: 2,
        petName: '咪咪',
        petType: '猫',
        petBreed: '英短',
        lostLocation: '望京SOHO',
        lostTime: '2023-11-28T15:30:00',
        reward: '800',
        status: 'found',
        images: '[]'
      }
    ]
    showToast('网络连接失败，显示示例数据')
  } finally {
    loading.value = false
  }
}

const viewDetails = (id) => {
  router.push(`/mobile/pet/${id}`)
}

const markAsFound = async (id) => {
  try {
    await showConfirmDialog({
      title: '确认找到',
      message: '确认已经找到这只宠物了吗？',
    })
    
    // TODO: 调用后端API更新状态
    // await axios.put(`http://localhost:8080/api/lost-pets/${id}/status`, { status: 'found' })
    
    // 暂时更新本地数据
    const pet = lostPets.value.find(p => p.id === id)
    if (pet) {
      pet.status = 'found'
    }
    
    showSuccessToast('已标记为找到')
  } catch {
    // 用户取消
  }
}

const getPetImage = (pet) => {
  if (pet.images) {
    try {
      const images = JSON.parse(pet.images)
      if (images.length > 0) {
        const firstImage = images[0]
        return firstImage.startsWith('/uploads/') 
          ? `http://localhost:8080${firstImage}` 
          : firstImage
      }
    } catch (e) {
      console.error('解析图片失败:', e)
    }
  }
  return '/static/images/default-pet.jpg'
}

const getStatusClass = (status) => {
  const statusClasses = {
    'lost': 'status-lost',
    'found': 'status-found',
  }
  return statusClasses[status] || 'status-unknown'
}

const getStatusText = (status) => {
  const statusTexts = {
    'lost': '寻找中',
    'found': '已找到',
  }
  return statusTexts[status] || '未知'
}

const formatTime = (lostTime) => {
  if (!lostTime) return '时间未知'
  
  const now = new Date()
  const lost = new Date(lostTime)
  const diff = now - lost
  
  const totalHours = Math.floor(diff / (1000 * 60 * 60))
  
  if (totalHours < 1) {
    const minutes = Math.floor(diff / (1000 * 60))
    return minutes < 10 ? '刚刚丢失' : `${minutes}分钟前`
  }
  
  return `${totalHours}小时前`
}

// 页面初始化
onMounted(() => {
  fetchLostPets()
})
</script>

<style scoped>
.my-lost-pets-page {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}

/* 顶部导航 */
.top-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 50px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.nav-left, .nav-right {
  width: 40px;
  display: flex;
  justify-content: center;
  cursor: pointer;
}

.nav-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

/* 加载状态 */
.loading-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 50px;
}

.loading-text {
  margin-top: 12px;
  color: #666;
  font-size: 14px;
}

/* 空状态 */
.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 20px 100px;
  text-align: center;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
  opacity: 0.6;
}

.empty-title {
  font-size: 20px;
  color: #333;
  margin: 0 0 12px 0;
}

.empty-description {
  font-size: 14px;
  color: #666;
  margin: 0 0 30px 0;
  line-height: 1.5;
}

.empty-action-btn {
  width: 200px;
  height: 44px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  font-size: 16px;
  font-weight: 600;
}

/* 主要内容 */
.pets-content {
  flex: 1;
  padding-top: 50px;
  padding-bottom: 80px;
}

/* 统计信息 */
.stats-section {
  padding: 16px;
  background: #fff;
  margin-bottom: 10px;
}

.stats-card {
  display: flex;
  align-items: center;
  justify-content: space-around;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 20px;
  color: white;
}

.stats-item {
  text-align: center;
  flex: 1;
}

.stats-number {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 4px;
}

.stats-label {
  font-size: 12px;
  opacity: 0.9;
}

.stats-divider {
  width: 1px;
  height: 40px;
  background: rgba(255, 255, 255, 0.3);
  margin: 0 20px;
}

/* 筛选栏 */
.filter-section {
  background: #fff;
  padding: 16px;
  margin-bottom: 10px;
}

.filter-tabs {
  display: flex;
  background: #f8fafc;
  border-radius: 8px;
  overflow: hidden;
}

.filter-tab {
  flex: 1;
  padding: 10px;
  text-align: center;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-tab.active {
  background: #667eea;
  color: #fff;
  font-weight: 600;
}

/* 宠物列表 */
.pets-list {
  padding: 0 16px;
}

.pet-card {
  background: #fff;
  border-radius: 12px;
  margin-bottom: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.pet-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

.pet-card:active {
  transform: translateY(0);
}

/* 宠物图片区域 */
.pet-image-container {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.pet-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.pet-card:hover .pet-image {
  transform: scale(1.05);
}

.status-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
}

.status-badge.status-lost {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
}

.status-badge.status-found {
  background: linear-gradient(135deg, #51cf66 0%, #40c057 100%);
}

.status-badge.status-unknown {
  background: linear-gradient(135deg, #868e96 0%, #6c757d 100%);
}

/* 宠物信息区域 */
.pet-info {
  padding: 16px;
}

.pet-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.pet-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
  flex: 1;
}

.pet-time {
  font-size: 12px;
  color: #999;
  margin-left: 8px;
}

.pet-details {
  margin-bottom: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
  font-size: 14px;
  color: #666;
}

.detail-item:last-child {
  margin-bottom: 0;
}

.detail-item span {
  margin-left: 6px;
}

.pet-meta {
  font-size: 13px;
  color: #999;
  margin-bottom: 16px;
}

.separator {
  margin: 0 6px;
}

/* 操作按钮 */
.pet-actions {
  display: flex;
  gap: 8px;
}

.pet-actions .van-button {
  flex: 1;
  height: 36px;
  font-size: 13px;
}

/* 底部导航 */
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-around;
  border-top: 1px solid #f0f0f0;
  z-index: 1000;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #666;
  font-size: 12px;
  cursor: pointer;
  transition: color 0.3s;
}

.nav-item.active {
  color: #667eea;
}

.nav-item span {
  margin-top: 4px;
}

/* 悬浮发布按钮 */
.floating-btn {
  position: fixed;
  bottom: 80px;
  right: 20px;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 999;
}

.floating-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.floating-btn:active {
  transform: translateY(0);
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.pet-card {
  animation: fadeInUp 0.5s ease-out;
}

.pet-card:nth-child(2) {
  animation-delay: 0.1s;
}

.pet-card:nth-child(3) {
  animation-delay: 0.2s;
}

.pet-card:nth-child(4) {
  animation-delay: 0.3s;
}

/* 响应式适配 */
@media (max-width: 375px) {
  .stats-card {
    padding: 16px;
  }
  
  .stats-number {
    font-size: 20px;
  }
  
  .stats-divider {
    margin: 0 15px;
  }
  
  .pet-image-container {
    height: 180px;
  }
  
  .pet-info {
    padding: 14px;
  }
}
</style>