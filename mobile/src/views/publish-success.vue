<template>
  <div class="publish-success-page">
    <!-- 顶部状态栏占位 -->
    <div class="status-bar-placeholder"></div>

    <!-- 主要内容区域 -->
    <div class="success-content">
      <!-- 成功图标和提示 -->
      <div class="success-section">
        <div class="success-icon">
          <div class="checkmark">✓</div>
        </div>
        <h1 class="success-title">发布成功！</h1>
        <p class="success-desc">
          您的宠物寻找信息已成功发布，我们会帮您尽快找到宠物
        </p>
      </div>

      <!-- 发布信息卡片 -->
      <div class="info-card" v-if="publishedPet">
        <div class="pet-image">
          <img
            v-if="publishedPet.image"
            :src="publishedPet.image"
            alt="宠物照片"
          />
          <div v-else class="no-image">📷</div>
        </div>
        <div class="pet-info">
          <h3 class="pet-name">{{ publishedPet.petName }}</h3>
          <p class="pet-details">
            {{ publishedPet.petType }} | {{ publishedPet.lostLocation }}
          </p>
          <p class="pet-reward">悬赏金额：¥{{ publishedPet.reward }}</p>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <button class="btn-detail" @click="viewDetail">
          <span class="btn-text">查看详情</span>
        </button>

        <button class="btn-home" @click="goHome">
          <span class="btn-text">返回首页</span>
        </button>
      </div>

      <!-- 温馨提示 -->
      <div class="tips-section">
        <div class="tips-title">📋 温馨提示</div>
        <ul class="tips-list">
          <li>您的信息已发布到平台，寻宠团队将陆续接单</li>
          <li>我们建议您同时在朋友圈、社交媒体等平台分享</li>
          <li>如有新的线索，可随时更新宠物信息</li>
          <li>请保持手机畅通，方便寻宠团队联系您</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, onMounted } from 'vue'
  import { useRouter, useRoute } from 'vue-router'

  const router = useRouter()
  const route = useRoute()

  // 发布的宠物信息
  const publishedPet = ref(null)

  // 查看详情
  const viewDetail = () => {
    if (publishedPet.value && publishedPet.value.id) {
      router.push(`/mobile/pet/${publishedPet.value.id}`)
    } else {
      // 如果没有详情ID，直接查看刚发布的内容（使用临时数据）
      router.push('/mobile/pet/temp')
    }
  }

  // 返回首页
  const goHome = () => {
    router.push('/')
  }

  // 页面初始化
  onMounted(() => {
    // 从路由参数或缓存中获取发布的宠物信息
    const petData = route.query.petData
    if (petData) {
      try {
        publishedPet.value = JSON.parse(decodeURIComponent(petData))
      } catch (error) {
        console.error('解析宠物数据失败:', error)
      }
    }

    // 如果没有传递数据，从 localStorage 获取临时数据
    if (!publishedPet.value) {
      const tempPetData = localStorage.getItem('temp_published_pet')
      if (tempPetData) {
        try {
          publishedPet.value = JSON.parse(tempPetData)
          // 使用后清除临时数据
          localStorage.removeItem('temp_published_pet')
        } catch (error) {
          console.error('解析临时宠物数据失败:', error)
        }
      }
    }
  })
</script>

<style scoped>
  .publish-success-page {
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    display: flex;
    flex-direction: column;
    position: relative;
  }

  .status-bar-placeholder {
    height: env(safe-area-inset-top, 20px);
    background: transparent;
  }

  /* 主要内容区域 */
  .success-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 40px 24px;
    padding-bottom: max(40px, env(safe-area-inset-bottom));
  }

  /* 成功提示区域 */
  .success-section {
    text-align: center;
    margin-bottom: 40px;
  }

  .success-icon {
    width: 80px;
    height: 80px;
    margin: 0 auto 24px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(10px);
    border: 2px solid rgba(255, 255, 255, 0.3);
  }

  .checkmark {
    font-size: 36px;
    color: #ffffff;
    font-weight: bold;
    line-height: 1;
  }

  .success-title {
    font-size: 28px;
    font-weight: 700;
    color: #ffffff;
    margin: 0 0 12px;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  }

  .success-desc {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.9);
    line-height: 1.5;
    margin: 0;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  }

  /* 信息卡片 */
  .info-card {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border-radius: 16px;
    padding: 20px;
    margin-bottom: 32px;
    display: flex;
    align-items: center;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.3);
  }

  .pet-image {
    width: 80px;
    height: 80px;
    border-radius: 12px;
    overflow: hidden;
    margin-right: 16px;
    flex-shrink: 0;
  }

  .pet-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .no-image {
    width: 100%;
    height: 100%;
    background: #f0f0f0;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32px;
    color: #ccc;
  }

  .pet-info {
    flex: 1;
  }

  .pet-name {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin: 0 0 8px;
  }

  .pet-details {
    font-size: 14px;
    color: #666;
    margin: 0 0 8px;
  }

  .pet-reward {
    font-size: 16px;
    font-weight: 600;
    color: #ff6b35;
    margin: 0;
  }

  /* 操作按钮 */
  .action-buttons {
    display: flex;
    flex-direction: column;
    gap: 16px;
    margin-bottom: 32px;
  }

  .btn-detail,
  .btn-home {
    height: 50px;
    border-radius: 25px;
    border: none;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .btn-detail {
    background: rgba(255, 255, 255, 0.2);
    color: #ffffff;
    border: 2px solid rgba(255, 255, 255, 0.5);
    backdrop-filter: blur(10px);
  }

  .btn-detail:active {
    transform: scale(0.98);
    background: rgba(255, 255, 255, 0.3);
  }

  .btn-home {
    background: rgba(255, 255, 255, 0.9);
    color: #667eea;
    backdrop-filter: blur(20px);
  }

  .btn-home:active {
    transform: scale(0.98);
    background: rgba(255, 255, 255, 1);
  }

  .btn-text {
    font-size: 16px;
    font-weight: 600;
  }

  /* 温馨提示 */
  .tips-section {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(20px);
    border-radius: 16px;
    padding: 20px;
    border: 1px solid rgba(255, 255, 255, 0.2);
  }

  .tips-title {
    font-size: 16px;
    font-weight: 600;
    color: #ffffff;
    margin-bottom: 12px;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  }

  .tips-list {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .tips-list li {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.9);
    line-height: 1.6;
    margin-bottom: 8px;
    padding-left: 16px;
    position: relative;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  }

  .tips-list li:before {
    content: '•';
    position: absolute;
    left: 0;
    color: rgba(255, 255, 255, 0.7);
  }

  .tips-list li:last-child {
    margin-bottom: 0;
  }

  /* 响应式适配 */
  @media (max-width: 375px) {
    .success-content {
      padding: 32px 20px;
    }

    .success-icon {
      width: 70px;
      height: 70px;
    }

    .checkmark {
      font-size: 32px;
    }

    .success-title {
      font-size: 24px;
    }

    .info-card {
      padding: 16px;
    }

    .pet-image {
      width: 70px;
      height: 70px;
    }
  }

  /* 动画效果 */
  @keyframes fadeInUp {
    from {
      opacity: 0;
      transform: translateY(30px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  @keyframes scaleIn {
    from {
      opacity: 0;
      transform: scale(0.8);
    }
    to {
      opacity: 1;
      transform: scale(1);
    }
  }

  .success-section {
    animation: scaleIn 0.6s ease-out;
  }

  .info-card {
    animation: fadeInUp 0.6s ease-out 0.2s both;
  }

  .action-buttons {
    animation: fadeInUp 0.6s ease-out 0.4s both;
  }

  .tips-section {
    animation: fadeInUp 0.6s ease-out 0.6s both;
  }
</style>
