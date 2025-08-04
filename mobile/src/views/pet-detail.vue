<template>
  <div class="pet-detail-page">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="nav-left" @click="goBack">
        <van-icon name="arrow-left" size="20" color="#333" />
      </div>
      <div class="nav-title">宠物详情</div>
      <div class="nav-right">
        <van-icon name="share" size="18" color="#333" @click="sharePet" />
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <van-loading size="24px" color="#667eea" />
      <div class="loading-text">加载中...</div>
    </div>

    <!-- 主要内容 -->
    <div v-else-if="petDetail" class="detail-content">
      <!-- 宠物照片展示（多张支持滑动） -->
      <div class="photo-section">
        <van-swipe class="pet-swipe" :autoplay="3000" indicator-color="white">
          <van-swipe-item v-for="(image, index) in petImages" :key="index">
            <img
              :src="image"
              :alt="`宠物照片 ${index + 1}`"
              class="pet-image"
            />
          </van-swipe-item>
        </van-swipe>
        <div class="photo-count">{{ petImages.length }} 张照片</div>
      </div>

      <!-- 信息展示区 -->
      <div class="info-section">
        <div class="basic-info">
          <h1 class="pet-name">{{ petDetail.petName }}</h1>
          <div class="pet-meta">
            <span class="pet-type">{{ petDetail.petType }}</span>
            <span class="separator">·</span>
            <span class="pet-breed">{{
              petDetail.petBreed || '未知品种'
            }}</span>
          </div>
          <div class="lost-info">
            <div class="info-item">
              <span class="info-label">丢失地点</span>
              <span class="info-value">{{ petDetail.lostLocation }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">丢失时间</span>
              <span class="info-value">{{
                formatLostTime(petDetail.lostTime)
              }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">联系方式</span>
              <span class="info-value">{{ petDetail.contactInfo }}</span>
            </div>
          </div>
        </div>

        <!-- 详细描述 -->
        <div class="description-section">
          <h3 class="section-title">详细描述</h3>
          <p class="description-text">{{ petDetail.petDescription }}</p>
        </div>

        <!-- 支付金额展示 -->
        <div class="reward-section">
          <div class="reward-card">
            <div class="reward-icon">💰</div>
            <div class="reward-info">
              <span class="reward-label">悬赏金额</span>
              <span class="reward-amount">¥{{ petDetail.reward }}</span>
            </div>
          </div>
        </div>

        <!-- 接单团队信息（若有） -->
        <div v-if="teamInfo" class="team-section">
          <h3 class="section-title">接单团队</h3>
          <div class="team-card">
            <div class="team-avatar">
              <img
                :src="teamInfo.avatar || '/static/default-avatar.png'"
                :alt="teamInfo.name"
              />
            </div>
            <div class="team-info">
              <div class="team-name">{{ teamInfo.name }}</div>
              <div class="team-rating">
                <van-rate
                  v-model="teamInfo.rating"
                  readonly
                  size="14"
                  color="#ffc107"
                />
                <span class="rating-text">({{ teamInfo.rating }}/5)</span>
              </div>
              <div class="team-contact">
                <span class="contact-item">微信：{{ teamInfo.wechat }}</span>
                <span class="contact-item">电话：{{ teamInfo.phone }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 状态显示 -->
        <div class="status-section">
          <div class="status-card" :class="statusClass">
            <div class="status-icon">{{ statusIcon }}</div>
            <div class="status-text">{{ statusText }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-else class="error-container">
      <div class="error-icon">😔</div>
      <div class="error-text">宠物信息加载失败</div>
      <van-button type="primary" @click="loadPetDetail">重新加载</van-button>
    </div>

    <!-- 底部操作按钮 -->
    <div v-if="petDetail" class="bottom-actions">
      <!-- 联系团队按钮（若已接单显示） -->
      <van-button
        v-if="teamInfo"
        type="primary"
        class="action-btn contact-btn"
        @click="contactTeam"
      >
        联系团队
      </van-button>

      <!-- 确认找到按钮（若团队提交找到显示） -->
      <van-button
        v-if="showConfirmButton"
        type="success"
        class="action-btn confirm-btn"
        @click="confirmFound"
      >
        确认找到
      </van-button>

      <!-- 如果没有接单，显示分享按钮 -->
      <van-button
        v-if="!teamInfo"
        type="default"
        class="action-btn share-btn"
        @click="sharePet"
      >
        分享寻宠信息
      </van-button>
    </div>

    <!-- 联系方式选择弹窗 -->
    <van-action-sheet v-model="showContactSheet" title="选择联系方式">
      <div class="contact-options">
        <div class="contact-option" @click="contactByWechat">
          <van-icon name="wechat" size="24" color="#07c160" />
          <span>微信联系</span>
        </div>
        <div class="contact-option" @click="contactByPhone">
          <van-icon name="phone" size="24" color="#1989fa" />
          <span>电话联系</span>
        </div>
      </div>
    </van-action-sheet>
  </div>
</template>

<script setup>
  import { ref, computed, onMounted } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  import {
    Icon,
    Loading,
    Button,
    Swipe,
    SwipeItem,
    Rate,
    ActionSheet,
    showToast,
    showSuccessToast,
    showConfirmDialog,
  } from 'vant'
  import axios from 'axios'

  const router = useRouter()
  const route = useRoute()

  // 数据状态
  const loading = ref(true)
  const petDetail = ref(null)
  const teamInfo = ref(null)
  const showContactSheet = ref(false)

  // 计算属性
  const petImages = computed(() => {
    if (!petDetail.value?.images) return ['/static/images/default-pet.jpg']

    try {
      const images = JSON.parse(petDetail.value.images)
      // 将相对路径转换为完整的URL
      const fullUrls = images.map(img => {
        if (img.startsWith('/uploads/')) {
          return `http://localhost:8080${img}`
        }
        return img
      })
      return fullUrls.length > 0 ? fullUrls : ['/static/images/default-pet.jpg']
    } catch (error) {
      console.error('解析图片JSON失败:', error)
      console.log('原始images数据:', petDetail.value.images)
      return ['/static/images/default-pet.jpg']
    }
  })

  const statusClass = computed(() => {
    if (!petDetail.value) return ''

    const status = petDetail.value.status
    return {
      'status-lost': status === 'lost',
      'status-finding': status === 'finding',
      'status-found': status === 'found',
    }
  })

  const statusIcon = computed(() => {
    if (!petDetail.value) return '❓'

    const statusIcons = {
      lost: '😢',
      finding: '🔍',
      found: '😊',
    }
    return statusIcons[petDetail.value.status] || '❓'
  })

  const statusText = computed(() => {
    if (!petDetail.value) return '未知状态'

    const statusTexts = {
      lost: '寻找中',
      finding: '团队找寻中',
      found: '已找到',
    }
    return statusTexts[petDetail.value.status] || '未知状态'
  })

  const showConfirmButton = computed(() => {
    return teamInfo.value && petDetail.value?.status === 'finding'
  })

  // 返回上一页
  const goBack = () => {
    router.go(-1)
  }

  // 格式化丢失时长
  const formatLostTime = lostTime => {
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

  // 加载宠物详情
  const loadPetDetail = async () => {
    try {
      loading.value = true
      const petId = route.params.id

      if (!petId) {
        // 如果没有ID，尝试从localStorage获取临时数据
        const tempPetData = localStorage.getItem('temp_published_pet')
        if (tempPetData) {
          petDetail.value = JSON.parse(tempPetData)
          // 模拟一些缺失的字段
          petDetail.value.status = 'lost'
          petDetail.value.lostTime = new Date().toISOString()
          loading.value = false
          return
        } else {
          throw new Error('未找到宠物ID')
        }
      }

      // 从API获取宠物详情
      const response = await axios.get(
        `http://localhost:8080/api/lost-pets/${petId}`
      )
      petDetail.value = response.data

      // 模拟加载团队信息（实际应该从API获取）
      if (Math.random() > 0.5) {
        // 50%概率有团队接单
        teamInfo.value = {
          name: '专业寻宠团队',
          avatar: '/static/team-avatar.png',
          rating: 4.8,
          wechat: 'search_pet_team',
          phone: '138-0013-8000',
        }
      }
    } catch (error) {
      console.error('加载宠物详情失败:', error)
      showToast('加载失败，请重试')
    } finally {
      loading.value = false
    }
  }

  // 分享宠物信息
  const sharePet = () => {
    // 生成分享内容
    const shareText =
      `【寻宠启事】${petDetail.value.petName}走失了！\n` +
      `品种：${petDetail.value.petType}\n` +
      `丢失地点：${petDetail.value.lostLocation}\n` +
      `悬赏：¥${petDetail.value.reward}\n` +
      `请大家帮忙留意，谢谢！🙏`

    if (navigator.share) {
      navigator.share({
        title: '寻宠启事',
        text: shareText,
        url: window.location.href,
      })
    } else {
      // 降级方案：复制到剪贴板
      navigator.clipboard.writeText(shareText).then(() => {
        showSuccessToast('分享内容已复制到剪贴板')
      })
    }
  }

  // 联系团队
  const contactTeam = () => {
    if (!teamInfo.value) return
    showContactSheet.value = true
  }

  // 微信联系
  const contactByWechat = () => {
    showContactSheet.value = false
    showToast(`微信号：${teamInfo.value.wechat}`)
    // 实际应该调用微信API或复制微信号
  }

  // 电话联系
  const contactByPhone = () => {
    showContactSheet.value = false
    const tel = teamInfo.value.phone.replace(/-/g, '')
    window.location.href = `tel:${tel}`
  }

  // 确认找到
  const confirmFound = async () => {
    const confirmed = await showConfirmDialog({
      title: '确认找到宠物',
      message: '确认找到宠物后，系统将自动向团队支付悬赏金额，此操作不可撤销。',
      confirmButtonText: '确认找到',
      cancelButtonText: '再想想',
    })

    if (confirmed) {
      try {
        // 调用API确认找到
        await axios.post(
          `http://localhost:8080/api/lost-pets/${petDetail.value.id}/confirm-found`
        )

        petDetail.value.status = 'found'
        showSuccessToast('确认成功，悬赏金额已支付给团队')

        // 延迟跳转回首页
        setTimeout(() => {
          router.push('/')
        }, 2000)
      } catch (error) {
        console.error('确认找到失败:', error)
        showToast('操作失败，请重试')
      }
    }
  }

  // 页面初始化
  onMounted(() => {
    loadPetDetail()
  })
</script>

<style scoped>
  .pet-detail-page {
    min-height: 100vh;
    background: #f8fafc;
    display: flex;
    flex-direction: column;
  }

  /* 顶部导航栏 */
  .top-nav {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 44px;
    background: #ffffff;
    display: flex;
    align-items: center;
    padding: 0 16px;
    border-bottom: 1px solid #f0f0f0;
    z-index: 1000;
  }

  .nav-left,
  .nav-right {
    width: 44px;
    height: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }

  .nav-title {
    flex: 1;
    text-align: center;
    font-size: 17px;
    font-weight: 600;
    color: #333;
  }

  /* 内容区域 */
  .detail-content {
    flex: 1;
    padding-top: 44px;
    padding-bottom: 80px;
  }

  /* 照片展示区 */
  .photo-section {
    position: relative;
    height: 300px;
    background: #f5f5f5;
  }

  .pet-swipe {
    width: 100%;
    height: 100%;
  }

  .pet-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .photo-count {
    position: absolute;
    bottom: 12px;
    right: 12px;
    background: rgba(0, 0, 0, 0.6);
    color: white;
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 12px;
  }

  /* 信息展示区 */
  .info-section {
    padding: 0 16px;
  }

  .basic-info {
    background: white;
    border-radius: 12px;
    padding: 20px;
    margin: 16px 0;
  }

  .pet-name {
    font-size: 24px;
    font-weight: 700;
    color: #333;
    margin: 0 0 8px;
  }

  .pet-meta {
    color: #666;
    font-size: 14px;
    margin-bottom: 16px;
  }

  .separator {
    margin: 0 8px;
  }

  .lost-info {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .info-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .info-label {
    font-size: 14px;
    color: #666;
  }

  .info-value {
    font-size: 14px;
    color: #333;
    font-weight: 500;
  }

  /* 描述区域 */
  .description-section {
    background: white;
    border-radius: 12px;
    padding: 20px;
    margin: 12px 0;
  }

  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin: 0 0 12px;
  }

  .description-text {
    font-size: 14px;
    color: #666;
    line-height: 1.6;
    margin: 0;
  }

  /* 悬赏区域 */
  .reward-section {
    margin: 12px 0;
  }

  .reward-card {
    background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
    border-radius: 12px;
    padding: 20px;
    display: flex;
    align-items: center;
    color: white;
  }

  .reward-icon {
    font-size: 24px;
    margin-right: 12px;
  }

  .reward-info {
    display: flex;
    flex-direction: column;
  }

  .reward-label {
    font-size: 14px;
    opacity: 0.9;
  }

  .reward-amount {
    font-size: 24px;
    font-weight: 700;
  }

  /* 团队信息区域 */
  .team-section {
    background: white;
    border-radius: 12px;
    padding: 20px;
    margin: 12px 0;
  }

  .team-card {
    display: flex;
    align-items: center;
  }

  .team-avatar {
    width: 50px;
    height: 50px;
    border-radius: 25px;
    overflow: hidden;
    margin-right: 12px;
  }

  .team-avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .team-info {
    flex: 1;
  }

  .team-name {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 4px;
  }

  .team-rating {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
  }

  .rating-text {
    margin-left: 4px;
    font-size: 12px;
    color: #666;
  }

  .team-contact {
    font-size: 12px;
    color: #666;
  }

  .contact-item {
    margin-right: 12px;
  }

  /* 状态区域 */
  .status-section {
    margin: 12px 0;
  }

  .status-card {
    background: white;
    border-radius: 12px;
    padding: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .status-lost {
    border-left: 4px solid #ff6b35;
  }

  .status-finding {
    border-left: 4px solid #1989fa;
  }

  .status-found {
    border-left: 4px solid #07c160;
  }

  .status-icon {
    font-size: 20px;
    margin-right: 8px;
  }

  .status-text {
    font-size: 16px;
    font-weight: 600;
    color: #333;
  }

  /* 底部操作区域 */
  .bottom-actions {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background: white;
    padding: 12px 16px;
    padding-bottom: max(12px, env(safe-area-inset-bottom));
    border-top: 1px solid #f0f0f0;
    display: flex;
    gap: 12px;
  }

  .action-btn {
    flex: 1;
    height: 44px;
    border-radius: 22px;
    font-size: 16px;
    font-weight: 600;
  }

  .contact-btn {
    background: #1989fa;
    border-color: #1989fa;
  }

  .confirm-btn {
    background: #07c160;
    border-color: #07c160;
  }

  .share-btn {
    background: #667eea;
    border-color: #667eea;
    color: white;
  }

  /* 联系方式选择 */
  .contact-options {
    padding: 20px;
  }

  .contact-option {
    display: flex;
    align-items: center;
    padding: 16px 0;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
  }

  .contact-option:last-child {
    border-bottom: none;
  }

  .contact-option span {
    margin-left: 12px;
    font-size: 16px;
    color: #333;
  }

  /* 加载和错误状态 */
  .loading-container,
  .error-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px;
  }

  .loading-text {
    margin-top: 12px;
    color: #666;
  }

  .error-icon {
    font-size: 48px;
    margin-bottom: 16px;
  }

  .error-text {
    font-size: 16px;
    color: #666;
    margin-bottom: 20px;
  }

  /* 响应式适配 */
  @media (max-width: 375px) {
    .photo-section {
      height: 250px;
    }

    .basic-info,
    .description-section,
    .team-section {
      padding: 16px;
    }

    .pet-name {
      font-size: 20px;
    }
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

  .basic-info,
  .description-section,
  .reward-section,
  .team-section,
  .status-section {
    animation: fadeInUp 0.4s ease-out;
  }

  .basic-info {
    animation-delay: 0.1s;
  }
  .description-section {
    animation-delay: 0.2s;
  }
  .reward-section {
    animation-delay: 0.3s;
  }
  .team-section {
    animation-delay: 0.4s;
  }
  .status-section {
    animation-delay: 0.5s;
  }
</style>
