<template>
  <view class="detail-page">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-state">
      <u-loading-icon mode="flower"></u-loading-icon>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 错误状态 -->
    <view v-else-if="error" class="error-state">
      <text class="error-icon">😞</text>
      <text class="error-text">{{ error }}</text>
      <u-button @click="retry" type="primary" size="small">重新加载</u-button>
    </view>

    <!-- 正常内容 -->
    <view v-else-if="pet" class="detail-content">
      <!-- 图片轮播区域 -->
      <view class="image-section">
        <swiper
          class="gallery"
          circular
          indicator-dots
          indicator-color="rgba(255,255,255,0.4)"
          indicator-active-color="#ffffff"
          autoplay
          interval="5000"
        >
          <swiper-item v-for="(img, idx) in images" :key="idx">
            <image
              :src="img"
              mode="aspectFill"
              class="gallery-img"
              @tap="previewImage(img)"
            />
          </swiper-item>
        </swiper>

        <!-- 图片计数器 -->
        <view class="image-counter" v-if="images.length > 1">
          <text class="counter-text"
            >{{ currentImageIndex + 1 }}/{{ images.length }}</text
          >
        </view>
      </view>

      <!-- 主要信息卡片 -->
      <view class="info-section">
        <!-- 宠物基本信息 -->
        <view class="pet-info-card">
          <view class="pet-header">
            <view class="pet-title">
              <text class="pet-name">{{ pet.petName }}</text>
              <view class="pet-badge">
                <text class="badge-icon">🐾</text>
                <text class="badge-text">{{ pet.petType }}</text>
              </view>
            </view>
            <view class="urgent-tag" v-if="isUrgent">
              <text class="urgent-icon">🚨</text>
              <text class="urgent-text">紧急寻找</text>
            </view>
          </view>

          <!-- 关键信息 -->
          <view class="key-info">
            <view class="info-item location">
              <view class="info-icon-wrapper location-icon">
                <text class="info-icon">📍</text>
              </view>
              <view class="info-content">
                <text class="info-label">丢失地点</text>
                <text class="info-value">{{ getLostLocation(pet) }}</text>
              </view>
            </view>

            <view class="info-item reward" v-if="pet.reward && pet.reward > 0">
              <view class="info-icon-wrapper reward-icon">
                <text class="info-icon">💰</text>
              </view>
              <view class="info-content">
                <text class="info-label">悬赏金额</text>
                <text class="info-value reward-amount">¥{{ pet.reward }}</text>
              </view>
            </view>

            <view class="info-item time">
              <view class="info-icon-wrapper time-icon">
                <text class="info-icon">⏰</text>
              </view>
              <view class="info-content">
                <text class="info-label">丢失时间</text>
                <text class="info-value">{{
                  formatLostTime(pet.lostTime)
                }}</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 详细描述卡片 -->
        <view class="description-card" v-if="pet.petDescription">
          <view class="card-header">
            <text class="card-icon">📝</text>
            <text class="card-title">详细描述</text>
          </view>
          <view class="description-content">
            <text class="description-text">{{ pet.petDescription }}</text>
          </view>
        </view>

        <!-- 联系信息卡片 -->
        <view class="contact-card">
          <view class="card-header">
            <text class="card-icon">📞</text>
            <text class="card-title">联系方式</text>
          </view>
          <view class="contact-options">
            <view class="contact-item" @tap="makePhoneCall">
              <view class="contact-icon-wrapper phone">
                <text class="contact-icon">📱</text>
              </view>
              <view class="contact-info">
                <text class="contact-label">拨打电话</text>
                <text class="contact-hint">快速联系失主</text>
              </view>
              <text class="contact-arrow">→</text>
            </view>

            <view class="contact-item" @tap="copyContact">
              <view class="contact-icon-wrapper wechat">
                <text class="contact-icon">💬</text>
              </view>
              <view class="contact-info">
                <text class="contact-label">复制联系方式</text>
                <text class="contact-hint">复制到剪贴板</text>
              </view>
              <text class="contact-arrow">→</text>
            </view>
          </view>
        </view>

        <!-- 侦探专用功能区 -->
        <view class="detective-section" v-if="isDetective">
          <view class="detective-card">
            <view class="card-header">
              <text class="card-icon">🕵️‍♂️</text>
              <text class="card-title">侦探专区</text>
            </view>
            
            <!-- 意向状态显示 -->
            <view class="intention-status">
              <view class="status-item" v-if="intentionStatus.status === 'none'">
                <text class="status-text">您可以表达接单意向</text>
                <view class="intention-count" v-if="intentionStatus.intentionCount > 0">
                  <text class="count-text">已有 {{ intentionStatus.intentionCount }} 位侦探表达意向</text>
                </view>
              </view>
              
              <view class="status-item" v-else-if="intentionStatus.status === 'intention'">
                <text class="status-text success">✅ 您已表达意向</text>
                <text class="status-detail">等待宠物主人确认</text>
              </view>
              
              <view class="status-item" v-else-if="intentionStatus.status === 'confirmed'">
                <text class="status-text confirmed">🎉 您已接单</text>
                <text class="status-detail">请联系宠物主人开始工作</text>
              </view>
              
              <view class="status-item" v-else-if="intentionStatus.status === 'withdrawn'">
                <text class="status-text withdrawn">❌ 已撤回意向</text>
                <text class="status-detail">无法再对该订单表达意向</text>
              </view>
              
              <view class="status-item" v-else-if="intentionStatus.isConfirmed">
                <text class="status-text unavailable">该订单已被其他侦探接单</text>
              </view>
            </view>
            
            <!-- 操作按钮 -->
            <view class="detective-actions">
              <!-- 意向接单按钮 -->
              <u-button 
                v-if="intentionStatus.status === 'none' && !intentionStatus.isConfirmed"
                @click="showIntentionModal = true"
                type="primary"
                shape="round"
                :custom-style="{ background: 'linear-gradient(135deg, #5b8ff9 0%, #36cfc9 100%)', border: 'none' }"
              >
                意向接单
              </u-button>
              
              <!-- 撤回意向按钮 -->
              <u-button 
                v-if="intentionStatus.status === 'intention'"
                @click="confirmWithdraw"
                type="warning"
                shape="round"
                plain
              >
                撤回意向
              </u-button>
              
              <!-- 联系主人按钮 -->
              <u-button 
                v-if="intentionStatus.status === 'confirmed'"
                @click="contactOwner"
                type="success"
                shape="round"
              >
                联系主人
              </u-button>
            </view>
          </view>
        </view>

        <!-- 温馨提示 -->
        <view class="tips-card">
          <view class="tip-item">
            <text class="tip-icon">💡</text>
            <text class="tip-text">见到走失宠物时，请耐心安抚，避免惊吓</text>
          </view>
          <view class="tip-item">
            <text class="tip-icon">📸</text>
            <text class="tip-text">发现线索时可拍照留证，便于失主确认</text>
          </view>
        </view>
      </view>

      <!-- 联系方式选择器 -->
      <u-action-sheet
        :show="showSheet"
        :actions="actions"
        title="选择联系方式"
        @close="showSheet = false"
        @select="onAction"
      />
      
      <!-- 意向接单模态框 -->
      <u-modal 
        v-model:show="showIntentionModal" 
        title="表达接单意向"
        :show-cancel-button="true"
        @confirm="submitIntention"
        @cancel="showIntentionModal = false"
      >
        <view class="intention-form">
          <view class="form-item">
            <text class="form-label">预计完成时间 *</text>
            <u-radio-group v-model="intentionForm.estimatedCompletion">
              <u-radio name="1天内">1天内</u-radio>
              <u-radio name="3天内">3天内</u-radio>
              <u-radio name="7天内">7天内</u-radio>
              <u-radio name="其他">其他</u-radio>
            </u-radio-group>
          </view>
          
          <view class="form-item">
            <text class="form-label">服务说明 *</text>
            <u-textarea 
              v-model="intentionForm.serviceDescription"
              placeholder="请描述您的优势，如：擅长该区域、有相关经验、团队优势等"
              :maxlength="200"
              count
            />
          </view>
          
          <view class="form-tip">
            <text class="tip-text">提示：表达意向后，宠物主人可查看您的信息并选择合适的侦探</text>
          </view>
        </view>
      </u-modal>
    </view>
  </view>
</template>

<script setup>
  import { ref, computed } from 'vue'
  import { onLoad } from '@dcloudio/uni-app'
  import { request } from '@/common/request'
  import { BASE_URL, API, STORAGE_KEYS } from '@/common/config'

  const pet = ref(null)
  const images = ref([])
  const showSheet = ref(false)
  const actions = ref([{ name: '拨打电话' }, { name: '复制微信' }])
  const loading = ref(true)
  const error = ref('')
  const currentId = ref('')
  const currentImageIndex = ref(0)
  
  // 侦探相关状态
  const isDetective = ref(false)
  const intentionStatus = ref({
    status: 'none', // none/intention/confirmed/withdrawn
    intentionCount: 0,
    isConfirmed: false
  })
  const showIntentionModal = ref(false)
  const intentionForm = ref({
    estimatedCompletion: '3天内',
    serviceDescription: ''
  })

  // 计算属性
  const isUrgent = computed(() => {
    if (!pet.value?.lostTime) return false
    const lostTime = new Date(pet.value.lostTime).getTime()
    const now = Date.now()
    const hoursDiff = (now - lostTime) / (1000 * 60 * 60)
    return hoursDiff <= 24 // 24小时内为紧急
  })

  const parseImages = imagesStr => {
    try {
      const arr = JSON.parse(imagesStr || '[]')
      return arr.map(x => (x.startsWith('/uploads/') ? `${BASE_URL}${x}` : x))
    } catch {
      return []
    }
  }

  const getLostLocation = pet => {
    if (!pet) return ''
    // 新格式：城市 + 具体地点
    if (pet.city && pet.address) {
      return `${pet.city} ${pet.address}`
    }
    // 兼容老格式
    return pet.lostLocation || pet.city || pet.address || '未知'
  }

  const formatLostTime = lostTime => {
    if (!lostTime) return '时间未知'
    const now = Date.now()
    const diff = now - new Date(lostTime).getTime()
    const h = Math.floor(diff / 3600000)
    if (h < 1) {
      const m = Math.floor(diff / 60000)
      return m < 10 ? '刚刚丢失' : `${m}分钟前`
    }
    return `${h}小时前`
  }

  // 图片预览
  const previewImage = current => {
    uni.previewImage({
      current,
      urls: images.value,
    })
  }

  // 拨打电话
  const makePhoneCall = () => {
    if (pet.value?.contactInfo) {
      uni.makePhoneCall({
        phoneNumber: pet.value.contactInfo,
        success: () => {
          console.log('[Detail] 拨打电话成功')
        },
        fail: err => {
          console.error('[Detail] 拨打电话失败:', err)
          uni.showToast({ title: '拨打失败', icon: 'none' })
        },
      })
    } else {
      uni.showToast({ title: '联系方式不可用', icon: 'none' })
    }
  }

  // 复制联系方式
  const copyContact = () => {
    if (pet.value?.contactInfo) {
      uni.setClipboardData({
        data: pet.value.contactInfo,
        success: () => {
          uni.showToast({ title: '已复制到剪贴板', icon: 'success' })
        },
        fail: () => {
          uni.showToast({ title: '复制失败', icon: 'none' })
        },
      })
    } else {
      uni.showToast({ title: '联系方式不可用', icon: 'none' })
    }
  }

  const contact = () => {
    showSheet.value = true
  }

  const onAction = e => {
    const name = e?.name
    if (name === '拨打电话') {
      makePhoneCall()
    } else if (name === '复制微信') {
      copyContact()
    }
    showSheet.value = false
  }

  const loadPetDetail = async id => {
    loading.value = true
    error.value = ''

    try {
      console.log('[Detail] 请求详情，ID:', id)
      const res = await request({ url: `/api/lost-pets/${id}` })
      console.log('[Detail] 详情响应:', res)

      const data = res?.data || res
      if (!data) {
        throw new Error('数据不存在')
      }

      pet.value = data
      images.value = parseImages(data.images)
      console.log('[Detail] 设置宠物数据:', data)
      console.log('[Detail] 设置图片数据:', images.value)
      
      // 检查侦探身份和意向状态
      await checkDetectiveStatus(id)
    } catch (e) {
      console.error('[Detail] 加载详情失败:', e)
      error.value = e.message || '加载失败，请重试'
    } finally {
      loading.value = false
    }
  }

  const retry = () => {
    if (currentId.value) {
      loadPetDetail(currentId.value)
    }
  }

  // 检查侦探身份和意向状态
  const checkDetectiveStatus = async (petId) => {
    try {
      const token = uni.getStorageSync(STORAGE_KEYS.token)
      if (!token) {
        console.log('[Detail] 用户未登录，跳过侦探检查')
        return
      }

      // 检查是否为认证侦探
      const detectiveRes = await request({
        url: API.detective.status,
        header: { Authorization: `Bearer ${token}` }
      })
      
      if (detectiveRes?.success && detectiveRes?.data?.status === 'approved') {
        isDetective.value = true
        console.log('[Detail] 用户是认证侦探')
        
        // 获取意向状态
        await loadIntentionStatus(petId)
      }
    } catch (error) {
      console.error('[Detail] 检查侦探身份失败:', error)
    }
  }

  // 加载意向状态
  const loadIntentionStatus = async (petId) => {
    try {
      const token = uni.getStorageSync(STORAGE_KEYS.token)
      const res = await request({
        url: `${API.detective.orders.intentionStatus}/${petId}`,
        header: { Authorization: `Bearer ${token}` }
      })
      
      if (res?.success) {
        intentionStatus.value = {
          status: res.status || 'none',
          intentionCount: res.intentionCount || 0,
          isConfirmed: res.isConfirmed || false
        }
        console.log('[Detail] 意向状态:', intentionStatus.value)
      }
    } catch (error) {
      console.error('[Detail] 获取意向状态失败:', error)
    }
  }

  // 提交意向接单
  const submitIntention = async () => {
    try {
      if (!intentionForm.value.serviceDescription?.trim()) {
        uni.showToast({ title: '请填写服务说明', icon: 'none' })
        return
      }

      uni.showLoading({ title: '提交中...' })
      
      const token = uni.getStorageSync(STORAGE_KEYS.token)
      const res = await request({
        url: API.detective.orders.intention,
        method: 'POST',
        data: {
          lostPetId: parseInt(currentId.value),
          estimatedCompletion: intentionForm.value.estimatedCompletion,
          serviceDescription: intentionForm.value.serviceDescription
        },
        header: { Authorization: `Bearer ${token}` }
      })

      uni.hideLoading()
      
      if (res?.success) {
        uni.showToast({ title: '意向提交成功', icon: 'success' })
        showIntentionModal.value = false
        
        // 重新加载意向状态
        await loadIntentionStatus(currentId.value)
        
        // 清空表单
        intentionForm.value = {
          estimatedCompletion: '3天内',
          serviceDescription: ''
        }
      } else {
        uni.showToast({ title: res?.message || '提交失败', icon: 'none' })
      }
    } catch (error) {
      uni.hideLoading()
      console.error('[Detail] 提交意向失败:', error)
      uni.showToast({ title: '提交失败，请重试', icon: 'none' })
    }
  }

  // 确认撤回意向
  const confirmWithdraw = () => {
    uni.showModal({
      title: '确认撤回意向',
      content: '撤回后将无法再对该寻宠启示表达意向，此操作不可撤销。确定要撤回吗？',
      success: (res) => {
        if (res.confirm) {
          withdrawIntention()
        }
      }
    })
  }

  // 撤回意向
  const withdrawIntention = async () => {
    try {
      uni.showLoading({ title: '撤回中...' })
      
      const token = uni.getStorageSync(STORAGE_KEYS.token)
      const res = await request({
        url: `${API.detective.orders.intention}/${currentId.value}`,
        method: 'DELETE',
        header: { Authorization: `Bearer ${token}` }
      })

      uni.hideLoading()
      
      if (res?.success) {
        uni.showToast({ title: '意向已撤回', icon: 'success' })
        
        // 重新加载意向状态
        await loadIntentionStatus(currentId.value)
      } else {
        uni.showToast({ title: res?.message || '撤回失败', icon: 'none' })
      }
    } catch (error) {
      uni.hideLoading()
      console.error('[Detail] 撤回意向失败:', error)
      uni.showToast({ title: '撤回失败，请重试', icon: 'none' })
    }
  }

  // 联系宠物主人
  const contactOwner = () => {
    if (pet.value?.contactInfo) {
      uni.makePhoneCall({
        phoneNumber: pet.value.contactInfo,
        success: () => {
          console.log('[Detail] 拨打电话成功')
        },
        fail: err => {
          console.error('[Detail] 拨打电话失败:', err)
          uni.showToast({ title: '拨打失败', icon: 'none' })
        },
      })
    } else {
      uni.showToast({ title: '联系方式不可用', icon: 'none' })
    }
  }

  onLoad(async options => {
    const id = options?.id
    console.log('[Detail] 获取ID:', id)

    if (!id) {
      console.error('[Detail] 没有获取到ID参数')
      error.value = '参数错误'
      loading.value = false
      return
    }

    currentId.value = id
    await loadPetDetail(id)
  })
</script>

<style lang="scss" scoped>
  .detail-page {
    background: #f8f8f8;
    min-height: 100vh;
  }

  .detail-content {
    position: relative;
  }

  /* 图片区域 */
  .image-section {
    position: relative;
    height: 300px;
    overflow: hidden;
  }

  .gallery {
    height: 100%;
    width: 100%;
  }

  .gallery-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }

  .image-counter {
    position: absolute;
    bottom: 16px;
    right: 16px;
    background: rgba(0, 0, 0, 0.6);
    color: #ffffff;
    padding: 6px 12px;
    border-radius: 20px;
    backdrop-filter: blur(8px);
  }

  .counter-text {
    font-size: 12px;
    font-weight: 500;
  }

  /* 信息区域 */
  .info-section {
    position: relative;
    margin-top: -20px;
    border-radius: 20px 20px 0 0;
    background: #f8f8f8;
    padding: 20px 16px 20px;
    z-index: 2;
  }

  /* 宠物信息卡片 */
  .pet-info-card {
    background: #ffffff;
    border-radius: 16px;
    padding: 20px;
    margin-bottom: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    border: 1px solid #f0f0f0;
  }

  .pet-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20px;
  }

  .pet-title {
    flex: 1;
  }

  .pet-name {
    font-size: 24px;
    font-weight: 700;
    color: #303133;
    margin-bottom: 8px;
    display: block;
  }

  .pet-badge {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    background: rgba(91, 143, 249, 0.1);
    color: #5b8ff9;
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;
  }

  .badge-icon {
    font-size: 12px;
    line-height: 1;
  }

  .badge-text {
    font-size: 12px;
  }

  .urgent-tag {
    display: flex;
    align-items: center;
    gap: 4px;
    background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
    color: #ffffff;
    padding: 6px 12px;
    border-radius: 20px;
    box-shadow: 0 2px 8px rgba(255, 77, 79, 0.3);
    animation: pulse 2s infinite;
  }

  .urgent-icon,
  .urgent-text {
    font-size: 12px;
    font-weight: 600;
    line-height: 1;
  }

  /* 关键信息 */
  .key-info {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .info-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    background: #f8f9fa;
    border-radius: 12px;
    border-left: 4px solid transparent;
  }

  .info-item.location {
    border-left-color: #36cfc9;
  }

  .info-item.reward {
    border-left-color: #ff6b9d;
  }

  .info-item.time {
    border-left-color: #faad14;
  }

  .info-icon-wrapper {
    width: 40px;
    height: 40px;
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .location-icon {
    background: rgba(54, 207, 201, 0.15);
  }

  .reward-icon {
    background: rgba(255, 107, 157, 0.15);
  }

  .time-icon {
    background: rgba(250, 173, 20, 0.15);
  }

  .info-icon {
    font-size: 16px;
    line-height: 1;
  }

  .info-content {
    flex: 1;
  }

  .info-label {
    display: block;
    font-size: 12px;
    color: #909399;
    margin-bottom: 4px;
    font-weight: 500;
  }

  .info-value {
    display: block;
    font-size: 14px;
    color: #303133;
    font-weight: 600;
    line-height: 1.4;
  }

  .reward-amount {
    color: #ff6b9d;
    font-size: 16px;
    font-weight: 700;
  }

  /* 卡片通用样式 */
  .description-card,
  .contact-card,
  .tips-card {
    background: #ffffff;
    border-radius: 16px;
    padding: 20px;
    margin-bottom: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    border: 1px solid #f0f0f0;
  }

  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #f0f0f0;
  }

  .card-icon {
    font-size: 16px;
    line-height: 1;
  }

  .card-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }

  /* 描述卡片 */
  .description-content {
    padding: 16px;
    background: #f8f9fa;
    border-radius: 12px;
    border-left: 4px solid #5b8ff9;
  }

  .description-text {
    font-size: 14px;
    color: #606266;
    line-height: 1.6;
  }

  /* 联系卡片 */
  .contact-options {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .contact-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    background: #f8f9fa;
    border-radius: 12px;
    transition: all 0.3s ease;
    border: 1px solid transparent;
  }

  .contact-item:active {
    background: #e8f4fd;
    border-color: #5b8ff9;
    transform: scale(0.98);
  }

  .contact-icon-wrapper {
    width: 40px;
    height: 40px;
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .contact-icon-wrapper.phone {
    background: rgba(82, 196, 26, 0.15);
  }

  .contact-icon-wrapper.wechat {
    background: rgba(54, 207, 201, 0.15);
  }

  .contact-icon {
    font-size: 16px;
    line-height: 1;
  }

  .contact-info {
    flex: 1;
  }

  .contact-label {
    display: block;
    font-size: 14px;
    color: #303133;
    font-weight: 600;
    margin-bottom: 2px;
  }

  .contact-hint {
    font-size: 12px;
    color: #909399;
  }

  .contact-arrow {
    font-size: 16px;
    color: #c0c4cc;
    font-weight: bold;
  }

  /* 提示卡片 */
  .tips-card {
    background: linear-gradient(135deg, #fff7e6 0%, #fff2cc 100%);
    border: 1px solid #ffe58f;
  }

  .tip-item {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    padding: 12px 0;
  }

  .tip-item:not(:last-child) {
    margin-bottom: 8px;
    padding-bottom: 16px;
    border-bottom: 1px solid rgba(250, 173, 20, 0.2);
  }

  .tip-icon {
    font-size: 14px;
    line-height: 1;
    margin-top: 2px;
  }

  .tip-text {
    flex: 1;
    font-size: 13px;
    color: #ad6800;
    line-height: 1.5;
    font-weight: 500;
  }

  /* 加载和错误状态样式 */
  .loading-state,
  .error-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    min-height: 50vh;
  }

  .loading-text {
    margin-top: 16px;
    color: #606266;
    font-size: 14px;
  }

  .error-icon {
    font-size: 48px;
    margin-bottom: 16px;
  }

  .error-text {
    color: #606266;
    font-size: 14px;
    margin-bottom: 20px;
    text-align: center;
  }

  /* 动画效果 */
  @keyframes pulse {
    0%,
    100% {
      transform: scale(1);
    }
    50% {
      transform: scale(1.05);
    }
  }

  /* 响应式适配 */
  @media (max-width: 375px) {
    .info-section {
      padding: 16px 12px 16px;
    }

    .pet-info-card,
    .description-card,
    .contact-card,
    .tips-card {
      padding: 16px;
    }

    .pet-name {
      font-size: 22px;
    }
  }

  @media (min-width: 415px) {
    .info-section {
      max-width: 600px;
      margin-left: auto;
      margin-right: auto;
    }
  }

  /* 侦探专用功能区 */
  .detective-section {
    margin-bottom: 16px;
  }

  .detective-card {
    background: linear-gradient(135deg, rgba(91, 143, 249, 0.05) 0%, rgba(54, 207, 201, 0.05) 100%);
    border-radius: 16px;
    padding: 20px;
    border: 2px solid rgba(91, 143, 249, 0.2);
    box-shadow: 0 4px 20px rgba(91, 143, 249, 0.1);
  }

  .intention-status {
    margin-bottom: 16px;
  }

  .status-item {
    text-align: center;
    padding: 12px;
    background: rgba(255, 255, 255, 0.8);
    border-radius: 12px;
    margin-bottom: 8px;
  }

  .status-text {
    display: block;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 4px;
  }

  .status-text.success {
    color: #52c41a;
  }

  .status-text.confirmed {
    color: #5b8ff9;
  }

  .status-text.withdrawn {
    color: #909399;
  }

  .status-text.unavailable {
    color: #ff6b9d;
  }

  .status-detail {
    font-size: 12px;
    color: #909399;
  }

  .intention-count {
    margin-top: 8px;
    padding: 8px 12px;
    background: rgba(91, 143, 249, 0.1);
    border-radius: 8px;
  }

  .count-text {
    font-size: 12px;
    color: #5b8ff9;
    font-weight: 500;
  }

  .detective-actions {
    display: flex;
    gap: 12px;
    justify-content: center;
  }

  /* 意向接单表单 */
  .intention-form {
    padding: 16px;
  }

  .form-item {
    margin-bottom: 20px;
  }

  .form-label {
    display: block;
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 8px;
  }

  .form-tip {
    margin-top: 16px;
    padding: 12px;
    background: #f8f9fa;
    border-radius: 8px;
    border-left: 4px solid #faad14;
  }

  .form-tip .tip-text {
    font-size: 12px;
    color: #909399;
    line-height: 1.4;
  }
</style>
