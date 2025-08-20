<template>
  <view class="success-page">
    <!-- 成功状态区域 -->
    <view class="success-header">
      <view class="success-animation">
        <view class="success-circle">
          <text class="success-icon">🎉</text>
        </view>
        <view class="success-ripple"></view>
      </view>
      <view class="success-title">发布成功！</view>
      <view class="success-subtitle"
        >您的寻宠信息已成功发布，审核通过后将在首页展示</view
      >
    </view>

    <!-- 宠物信息卡片 -->
    <view class="pet-preview" v-if="pet">
      <view class="pet-card">
        <view class="pet-image-container">
          <image
            class="pet-image"
            :src="coverSrc"
            mode="aspectFill"
            @error="onImageError"
            @load="onImageLoad"
          />
          <view class="pet-status-badge">
            <text class="badge-icon">⏳</text>
            <text class="badge-text">审核中</text>
          </view>
        </view>

        <view class="pet-info">
          <view class="pet-name">{{ pet.petName }}</view>
          <view class="pet-details">
            <view class="detail-item">
              <text class="detail-icon">🐾</text>
              <text class="detail-text">{{ pet.petType }}</text>
            </view>
            <view class="detail-item">
              <text class="detail-icon">📍</text>
              <text class="detail-text">{{ getLostLocation(pet) }}</text>
            </view>
            <view class="detail-item" v-if="pet.reward">
              <text class="detail-icon">💰</text>
              <text class="detail-text reward">¥{{ pet.reward }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 提示信息 -->
    <view class="tips-section">
      <view class="tip-item">
        <text class="tip-icon">🔍</text>
        <text class="tip-text"
          >审核通过后，您的信息将在首页展示给更多人看到</text
        >
      </view>
      <view class="tip-item">
        <text class="tip-icon">📱</text>
        <text class="tip-text">您可以随时在"我的"页面查看发布状态</text>
      </view>
    </view>

    <!-- 操作按钮 -->
    <view class="action-buttons">
      <u-button
        type="primary"
        :customStyle="buttonPrimaryStyle"
        @click="goDetail"
        class="btn-detail"
      >
        <text class="btn-icon">👀</text>
        查看发布详情
      </u-button>
      <u-button
        :customStyle="buttonSecondaryStyle"
        @click="goHome"
        class="btn-home"
      >
        <text class="btn-icon">🏠</text>
        返回首页
      </u-button>
    </view>
  </view>
</template>

<script setup>
  import { ref, onMounted, computed } from 'vue'
  import { BASE_URL } from '@/common/config'
  const pet = ref(null)
  onMounted(() => {
    const t = uni.getStorageSync('temp_published_pet')
    if (t) {
      pet.value = typeof t === 'string' ? JSON.parse(t) : t
      console.log('[Success] 获取发布的宠物数据:', pet.value)
    } else {
      console.log('[Success] 未找到发布的宠物数据')
    }
  })

  const getLostLocation = pet => {
    if (!pet) return ''
    // 新格式：城市 + 具体地点
    if (pet.city && pet.address) {
      return `${pet.city} ${pet.address}`
    }
    // 兼容老格式
    return pet.lostLocation || pet.city || pet.address || '未知'
  }
  const coverSrc = computed(() => {
    try {
      console.log('[Success] 处理图片数据:', pet.value?.images)

      // 处理图片数据
      let imgs = []
      if (Array.isArray(pet.value?.images)) {
        imgs = pet.value.images
      } else if (typeof pet.value?.images === 'string') {
        try {
          imgs = JSON.parse(pet.value.images)
        } catch {
          imgs = [pet.value.images]
        }
      }

      console.log('[Success] 解析后的图片数组:', imgs)

      const first = imgs?.[0]
      if (!first) {
        console.log('[Success] 没有图片，使用默认图片')
        return '/static/images/default-pet.jpg'
      }

      // 处理图片URL
      let imageUrl = first
      if (typeof first === 'object' && first.url) {
        imageUrl = first.url
      } else if (typeof first === 'object' && first.raw) {
        imageUrl = first.raw
      }

      console.log('[Success] 处理的图片URL:', imageUrl)

      // 如果是相对路径，添加BASE_URL
      if (typeof imageUrl === 'string' && imageUrl.startsWith('/uploads/')) {
        const fullUrl = `${BASE_URL}${imageUrl}`
        console.log('[Success] 完整图片URL:', fullUrl)
        return fullUrl
      }

      return imageUrl || '/static/images/default-pet.jpg'
    } catch (error) {
      console.error('[Success] 图片处理错误:', error)
      return '/static/images/default-pet.jpg'
    }
  })
  // 图片加载事件
  const onImageLoad = () => {
    console.log('[Success] 图片加载成功')
  }

  const onImageError = () => {
    console.log('[Success] 图片加载失败')
  }

  // 按钮样式配置
  const buttonPrimaryStyle = {
    background: 'linear-gradient(135deg, #5b8ff9 0%, #36cfc9 100%)',
    borderRadius: '12px',
    height: '48px',
    fontSize: '16px',
    fontWeight: '600',
    color: '#ffffff',
    border: 'none',
    boxShadow: '0 4px 16px rgba(91, 143, 249, 0.3)',
  }

  const buttonSecondaryStyle = {
    background: '#ffffff',
    borderRadius: '12px',
    height: '48px',
    fontSize: '16px',
    fontWeight: '500',
    color: '#5b8ff9',
    border: '1px solid #5b8ff9',
    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.06)',
  }

  const goHome = () => uni.switchTab({ url: '/pages/home/home' })
  const goDetail = () => {
    if (!pet.value?.id) {
      uni.showToast({ title: '数据异常，返回首页', icon: 'none' })
      return goHome()
    }
    uni.redirectTo({ url: `/pages/pet/detail?id=${pet.value.id}` })
  }
</script>

<style lang="scss" scoped>
  .success-page {
    min-height: 100vh;
    background: linear-gradient(180deg, #f8f8f8 0%, #ffffff 100%);
    padding: 32px 16px 16px;
    display: flex;
    flex-direction: column;
  }

  /* 成功状态区域 */
  .success-header {
    text-align: center;
    margin-bottom: 32px;
  }

  .success-animation {
    position: relative;
    display: inline-block;
    margin-bottom: 24px;
  }

  .success-circle {
    width: 80px;
    height: 80px;
    background: linear-gradient(135deg, #52c41a 0%, #36cfc9 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 8px 24px rgba(82, 196, 26, 0.3);
    animation: bounce 0.6s ease-out;
    position: relative;
    z-index: 2;
  }

  .success-icon {
    font-size: 32px;
    line-height: 1;
  }

  .success-ripple {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 80px;
    height: 80px;
    border: 2px solid rgba(82, 196, 26, 0.3);
    border-radius: 50%;
    transform: translate(-50%, -50%);
    animation: ripple 1.5s ease-out infinite;
  }

  .success-title {
    font-size: 24px;
    font-weight: 700;
    color: #303133;
    margin-bottom: 8px;
    letter-spacing: 0.5px;
  }

  .success-subtitle {
    font-size: 14px;
    color: #606266;
    line-height: 1.5;
    padding: 0 20px;
  }

  /* 宠物信息卡片 */
  .pet-preview {
    margin-bottom: 24px;
  }

  .pet-card {
    background: #ffffff;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    border: 1px solid #f0f0f0;
  }

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

  .pet-status-badge {
    position: absolute;
    top: 12px;
    right: 12px;
    background: rgba(250, 173, 20, 0.95);
    color: #ffffff;
    padding: 6px 12px;
    border-radius: 20px;
    display: flex;
    align-items: center;
    gap: 4px;
    backdrop-filter: blur(8px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  }

  .badge-icon {
    font-size: 12px;
    line-height: 1;
  }

  .badge-text {
    font-size: 12px;
    font-weight: 500;
  }

  .pet-info {
    padding: 20px;
  }

  .pet-name {
    font-size: 20px;
    font-weight: 700;
    color: #303133;
    margin-bottom: 16px;
    text-align: center;
  }

  .pet-details {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .detail-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    background: #f8f9fa;
    border-radius: 12px;
    border-left: 4px solid #5b8ff9;
  }

  .detail-icon {
    font-size: 16px;
    line-height: 1;
    width: 20px;
    text-align: center;
  }

  .detail-text {
    flex: 1;
    font-size: 14px;
    color: #606266;
    font-weight: 500;
  }

  .detail-text.reward {
    color: #f5222d;
    font-weight: 700;
  }

  /* 提示信息 */
  .tips-section {
    margin-bottom: 32px;
  }

  .tip-item {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    padding: 16px;
    background: #ffffff;
    border-radius: 12px;
    margin-bottom: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    border-left: 3px solid #36cfc9;
  }

  .tip-icon {
    font-size: 16px;
    line-height: 1;
    margin-top: 2px;
  }

  .tip-text {
    flex: 1;
    font-size: 13px;
    color: #606266;
    line-height: 1.6;
  }

  /* 操作按钮 */
  .action-buttons {
    margin-top: auto;
    padding-top: 16px;
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .btn-detail,
  .btn-home {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    height: 48px;
    border-radius: 12px;
    font-size: 16px;
    font-weight: 600;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
  }

  .btn-detail {
    background: linear-gradient(135deg, #5b8ff9 0%, #36cfc9 100%);
    color: #ffffff;
    box-shadow: 0 4px 16px rgba(91, 143, 249, 0.3);
  }

  .btn-detail:active {
    transform: translateY(1px);
    box-shadow: 0 2px 8px rgba(91, 143, 249, 0.4);
  }

  .btn-home {
    background: #ffffff;
    color: #5b8ff9;
    border: 1px solid #5b8ff9;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  .btn-home:active {
    transform: translateY(1px);
    background: #f8f9fa;
  }

  .btn-icon {
    font-size: 16px;
    line-height: 1;
  }

  /* 动画效果 */
  @keyframes bounce {
    0% {
      transform: scale(0.3);
      opacity: 0;
    }
    50% {
      transform: scale(1.1);
    }
    100% {
      transform: scale(1);
      opacity: 1;
    }
  }

  @keyframes ripple {
    0% {
      transform: translate(-50%, -50%) scale(1);
      opacity: 1;
    }
    100% {
      transform: translate(-50%, -50%) scale(2);
      opacity: 0;
    }
  }

  /* 响应式适配 */
  @media (max-width: 375px) {
    .success-page {
      padding: 24px 12px 12px;
    }

    .success-title {
      font-size: 22px;
    }

    .pet-info {
      padding: 16px;
    }

    .pet-name {
      font-size: 18px;
    }
  }

  @media (min-width: 415px) {
    .pet-card {
      max-width: 400px;
      margin: 0 auto;
    }

    .action-buttons {
      max-width: 300px;
      margin-left: auto;
      margin-right: auto;
    }
  }
</style>
