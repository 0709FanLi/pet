<template>
  <view class="success-page">
    <view class="icon">✅</view>
    <view class="title">发布成功</view>
    <view class="card" v-if="pet">
      <image class="cover" :src="coverSrc" mode="aspectFill" />
      <view class="info"
        >{{ pet.petName }} · {{ pet.petType }} ·
        {{ getLostLocation(pet) }}</view
      >
    </view>
    <view class="btns">
      <u-button type="primary" @click="goDetail">查看发布详情</u-button>
      <u-button @click="goHome">返回首页</u-button>
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
      const imgs = Array.isArray(pet.value?.images) ? pet.value.images : []
      const first = imgs?.[0]
      if (!first) return '/static/images/default-pet.jpg'
      return typeof first === 'string' && first.startsWith('/uploads/')
        ? `${BASE_URL}${first}`
        : first
    } catch {
      return '/static/images/default-pet.jpg'
    }
  })
  const goHome = () => uni.switchTab({ url: '/pages/home/home' })
  const goDetail = () => {
    if (!pet.value?.id) return goHome()
    uni.redirectTo({ url: `/pages/pet/detail?id=${pet.value.id}` })
  }
</script>

<style lang="scss" scoped>
  .success-page {
    padding: 24px;
    text-align: center;
  }
  .icon {
    font-size: 54px;
    margin: 12px 0;
  }
  .title {
    font-size: 20px;
    font-weight: 700;
    margin-bottom: 16px;
  }
  .card {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    margin: 0 auto 16px;
    width: 86%;
  }
  .cover {
    width: 100%;
    height: 160px;
    display: block;
  }
  .info {
    padding: 12px;
    color: #666;
  }
  .btns {
    display: flex;
    flex-direction: column;
    gap: 10px;
    padding: 12px 24px;
  }
</style>
