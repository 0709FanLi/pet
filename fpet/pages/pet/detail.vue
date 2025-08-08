<template>
  <view class="detail-page" v-if="pet">
    <swiper class="gallery" circular>
      <swiper-item v-for="(img, idx) in images" :key="idx">
        <image :src="img" mode="aspectFill" class="gallery-img" />
      </swiper-item>
    </swiper>
    <view class="body">
      <view class="title">{{ pet.petName }} · {{ pet.petType }}</view>
      <view class="row">丢失地点：{{ pet.lostLocation }}</view>
      <view class="row">悬赏金额：¥{{ pet.reward || '0' }}</view>
      <view class="row">丢失时间：{{ formatLostTime(pet.lostTime) }}</view>
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

  const parseImages = imagesStr => {
    try {
      const arr = JSON.parse(imagesStr || '[]')
      return arr.map(x => (x.startsWith('/uploads/') ? `${BASE_URL}${x}` : x))
    } catch {
      return []
    }
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

  onLoad(async options => {
    try {
      const id = options?.id
      if (!id) return
      const res = await request({ url: `/api/lost-pets/${id}` })
      const data = res?.data || res
      pet.value = data
      images.value = parseImages(data.images)
    } catch (e) {
      console.error('[detail] load error:', e)
    }
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
</style>
