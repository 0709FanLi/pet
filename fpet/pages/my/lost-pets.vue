<template>
  <view class="my-page">
    <u-loading-page :loading="loading" loadingText="加载中..." />
    <view v-if="!loading" class="list">
      <block v-for="pet in list" :key="pet.id">
        <view class="card" @click="goDetail(pet)">
          <image class="cover" :src="pet.image" mode="aspectFill" />
          <view class="body">
            <view class="row between">
              <text class="title">{{ pet.petName || '未命名' }}</text>
              <text class="time">{{ formatLostTime(pet.lostTime) }}</text>
            </view>
            <view class="row">{{ pet.lostLocation }}</view>
            <view class="row">
              <u-button
                size="small"
                type="primary"
                plain
                @click.stop="goDetail(pet)"
                >查看详情</u-button
              >
              <u-button
                v-if="pet.status === 'lost'"
                size="small"
                type="success"
                plain
                @click.stop="markFound(pet)"
                >标记找到</u-button
              >
            </view>
          </view>
        </view>
      </block>
      <view v-if="list.length === 0" class="empty">暂无发布</view>
    </view>
  </view>
</template>

<script setup>
  import { ref, onMounted } from 'vue'
  import { BASE_URL, STORAGE_KEYS } from '@/common/config'
  import { request } from '@/common/request'

  const loading = ref(true)
  const list = ref([])

  const parseImg = str => {
    try {
      const a = JSON.parse(str || '[]')
      const f = a[0]
      if (!f) return '/static/images/default-pet.jpg'
      return f.startsWith('/uploads/') ? `${BASE_URL}${f}` : f
    } catch {
      return '/static/images/default-pet.jpg'
    }
  }
  const formatLostTime = t => {
    if (!t) return '时间未知'
    const d = Date.now() - new Date(t).getTime()
    const h = Math.floor(d / 3600000)
    if (h < 1) {
      const m = Math.floor(d / 60000)
      return m < 10 ? '刚刚丢失' : `${m}分钟前`
    }
    return `${h}小时前`
  }

  const load = async () => {
    loading.value = true
    try {
      // 从本地存储获取当前登录用户信息
      const userInfo = uni.getStorageSync(STORAGE_KEYS.userInfo)
      const token = uni.getStorageSync(STORAGE_KEYS.token)

      console.log('用户信息:', userInfo)
      console.log('Token:', token ? '存在' : '不存在')

      if (!userInfo || !userInfo.id) {
        console.error('用户未登录或用户信息不完整')
        uni.showToast({
          title: '请先登录',
          icon: 'none',
        })
        setTimeout(() => {
          uni.navigateTo({ url: '/pages/auth/login' })
        }, 1500)
        return
      }

      if (!token) {
        console.error('Token不存在，需要重新登录')
        uni.showToast({
          title: 'Token已过期，请重新登录',
          icon: 'none',
        })
        setTimeout(() => {
          uni.navigateTo({ url: '/pages/auth/login' })
        }, 1500)
        return
      }

      const userId = userInfo.id
      console.log('加载用户发布列表，用户ID:', userId)

      const res = await request({ url: `/api/lost-pets/user/${userId}` })
      console.log('API响应:', res)

      const arr = Array.isArray(res) ? res : res?.data || []
      list.value = arr.map(p => ({ ...p, image: parseImg(p.images) }))

      console.log('用户发布列表加载完成，数量:', list.value.length)
    } catch (error) {
      console.error('加载用户发布列表失败:', error)
      uni.showToast({
        title: '加载失败',
        icon: 'none',
      })
    } finally {
      loading.value = false
    }
  }

  const goDetail = pet =>
    uni.navigateTo({ url: `/pages/pet/detail?id=${pet.id}` })
  const markFound = pet => {
    pet.status = 'found'
  }

  onMounted(load)
</script>

<style lang="scss" scoped>
  .my-page {
    background: #f5f7fa;
    min-height: 100vh;
  }
  .list {
    padding: 8px 12px;
  }
  .card {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    margin-bottom: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  }
  .cover {
    width: 100%;
    height: 160px;
    display: block;
  }
  .body {
    padding: 12px;
  }
  .row {
    display: flex;
    align-items: center;
    gap: 10px;
    margin: 6px 0;
    &.between {
      justify-content: space-between;
    }
  }
  .title {
    font-size: 16px;
    font-weight: 600;
  }
  .time {
    font-size: 12px;
    color: #999;
  }
  .empty {
    text-align: center;
    color: #999;
    padding: 48px 0;
  }
</style>
