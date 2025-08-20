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
    <view v-else-if="pet">
      <swiper class="gallery" circular>
        <swiper-item v-for="(img, idx) in images" :key="idx">
          <image :src="img" mode="aspectFill" class="gallery-img" />
        </swiper-item>
      </swiper>
      <view class="body">
        <view class="title">{{ pet.petName }} · {{ pet.petType }}</view>
        <view class="row">丢失地点：{{ getLostLocation(pet) }}</view>
        <view class="row">悬赏金额：¥{{ pet.reward || '0' }}</view>
        <view class="row">丢失时间：{{ formatLostTime(pet.lostTime) }}</view>
        <view class="row" v-if="pet.petDescription"
          >宠物描述：{{ pet.petDescription }}</view
        >
        <view class="btns">
          <u-button type="primary" @click="contact">联系Ta</u-button>
        </view>
      </view>
      <u-action-sheet
        :show="showSheet"
        :actions="actions"
        @close="showSheet = false"
        @select="onAction"
      />
    </view>
  </view>
</template>

<script setup>
  import { ref } from 'vue'
  import { onLoad } from '@dcloudio/uni-app'
  import { request } from '@/common/request'
  import { BASE_URL } from '@/common/config'

  const pet = ref(null)
  const images = ref([])
  const showSheet = ref(false)
  const actions = ref([{ name: '拨打电话' }, { name: '复制微信' }])
  const loading = ref(true)
  const error = ref('')
  const currentId = ref('')

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

  const contact = () => {
    showSheet.value = true
  }
  const onAction = e => {
    const name = e?.name
    if (name === '拨打电话' && pet.value?.contactInfo) {
      uni.makePhoneCall({ phoneNumber: pet.value.contactInfo })
    } else if (name === '复制微信' && pet.value?.contactInfo) {
      uni.setClipboardData({ data: pet.value.contactInfo })
    }
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
    background: #f5f7fa;
    min-height: 100vh;
  }
  .gallery {
    height: 240px;
  }
  .gallery-img {
    width: 100%;
    height: 100%;
    display: block;
  }
  .body {
    background: #fff;
    border-radius: 12px 12px 0 0;
    margin-top: -12px;
    padding: 16px;
  }
  .title {
    font-size: 18px;
    font-weight: 700;
    margin-bottom: 8px;
  }
  .row {
    color: #666;
    margin: 6px 0;
  }
  .btns {
    margin-top: 12px;
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
</style>
