<template>
  <view class="home-page">
    <!-- 顶部筛选 -->
    <view class="header">
      <u-dropdown activeColor="#5B8FF9">
        <u-dropdown-item v-model="selectedType" :options="petTypeOptions" />
        <u-dropdown-item v-model="selectedCity" :options="cityOptions" />
      </u-dropdown>
    </view>

    <!-- 列表 -->
    <u-loading-page :loading="loading" loadingText="加载中..." />
    <view v-if="!loading" class="list">
      <block v-for="pet in filteredList" :key="pet.id">
        <view class="card" @click="goDetail(pet)">
          <image class="card-image" :src="pet.image" mode="aspectFill" />
          <view class="card-body">
            <view class="row between">
              <text class="title">{{ pet.location || '未知位置' }}</text>
              <text class="time">{{ formatLostTime(pet.lostTime) }}</text>
            </view>
            <view class="row">
              <text class="reward">悬赏 ¥{{ pet.amount || '0' }}</text>
            </view>
            <view class="row">
              <u-tag
                :text="statusText(pet.status)"
                :type="pet.status === 'finding' ? 'primary' : 'success'"
                plain
                size="mini"
              />
            </view>
          </view>
        </view>
      </block>

      <view v-if="filteredList.length === 0" class="empty">
        <text class="empty-icon">🐾</text>
        <text class="empty-text">暂无符合条件的宠物信息</text>
      </view>
    </view>
  </view>
</template>

<script setup>
  import { ref, computed, onMounted } from 'vue'
  import { BASE_URL } from '@/common/config'
  import { request } from '@/common/request'

  const loading = ref(true)
  const list = ref([])

  // 下拉选项与当前值
  const petTypeOptions = ref([{ label: '全部种类', value: 'all' }])
  const cityOptions = ref([{ label: '全部地区', value: 'all' }])
  const selectedType = ref('all')
  const selectedCity = ref('all')

  const filteredList = computed(() => {
    return list.value.filter(p => {
      const typeOk =
        selectedType.value === 'all' ||
        (p.petType &&
          (p.petType.includes(selectedType.value) ||
            (selectedType.value === 'cat' && p.petType.includes('猫')) ||
            (selectedType.value === 'dog' && p.petType.includes('狗')) ||
            (selectedType.value === 'other' && p.petType.includes('其它'))))
      const cityOk =
        selectedCity.value === 'all' ||
        (p.rawLostLocation && p.rawLostLocation.includes(selectedCity.value))
      return typeOk && cityOk
    })
  })

  const loadOptions = async () => {
    try {
      const [types, cities] = await Promise.all([
        request({ url: '/api/config/pet-types' }),
        request({ url: '/api/config/cities' }),
      ])
      const t = Array.isArray(types?.data) ? types.data : []
      const c = Array.isArray(cities?.data) ? cities.data : []
      petTypeOptions.value = [
        { label: '全部种类', value: 'all' },
        ...t.map(x => ({
          label: x,
          value: x === '猫' ? 'cat' : x === '狗' ? 'dog' : 'other',
        })),
      ]
      cityOptions.value = [
        { label: '全部地区', value: 'all' },
        ...c.map(x => ({ label: x, value: x })),
      ]
    } catch (e) {
      petTypeOptions.value = [
        { label: '全部种类', value: 'all' },
        { label: '猫', value: 'cat' },
        { label: '狗', value: 'dog' },
        { label: '其它', value: 'other' },
      ]
      cityOptions.value = [
        { label: '全部地区', value: 'all' },
        { label: '北京', value: '北京' },
        { label: '上海', value: '上海' },
        { label: '广州', value: '广州' },
        { label: '深圳', value: '深圳' },
        { label: '杭州', value: '杭州' },
        { label: '厦门', value: '厦门' },
        { label: '郑州', value: '郑州' },
      ]
    }
  }

  const loadList = async () => {
    loading.value = true
    try {
      const res = await request({ url: '/api/lost-pets' })
      const arr = Array.isArray(res) ? res : res?.data || []
      list.value = arr.map(pet => ({
        id: pet.id,
        petType: pet.petType,
        rawLostLocation: pet.lostLocation,
        location: pet.lostLocation,
        amount: pet.reward,
        lostTime: pet.lostTime,
        image: (() => {
          try {
            const imgs = JSON.parse(pet.images || '[]')
            const first = imgs[0]
            if (!first) return '/static/images/default-pet.jpg'
            return first.startsWith('/uploads/') ? `${BASE_URL}${first}` : first
          } catch {
            return '/static/images/default-pet.jpg'
          }
        })(),
        status: pet.status === 'lost' ? 'finding' : pet.status,
      }))
    } finally {
      loading.value = false
    }
  }

  const goDetail = pet => {
    uni.navigateTo({ url: `/pages/pet/detail?id=${pet.id}` })
  }

  const formatLostTime = lostTime => {
    if (!lostTime) return '时间未知'
    const now = Date.now()
    const diff = now - new Date(lostTime).getTime()
    const hours = Math.floor(diff / 3600000)
    if (hours < 1) {
      const m = Math.floor(diff / 60000)
      return m < 10 ? '刚刚丢失' : `${m}分钟前`
    }
    return `${hours}小时前`
  }

  const statusText = s =>
    ({ finding: '寻找中', found: '已找到', closed: '已关闭' }[s] || '未知')

  onMounted(async () => {
    await loadOptions()
    await loadList()
  })
</script>

<style lang="scss" scoped>
  .home-page {
    background: #f5f7fa;
    min-height: 100vh;
  }
  .header {
    background: #fff;
    padding: 8px 0;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }
  .list {
    padding: 8px 12px 80px;
  }
  .card {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    margin-bottom: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  }
  .card-image {
    width: 100%;
    height: 180px;
    display: block;
  }
  .card-body {
    padding: 12px;
  }
  .row {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 6px;
    &.between {
      justify-content: space-between;
    }
  }
  .title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
  }
  .time {
    font-size: 12px;
    color: #999;
  }
  .reward {
    font-size: 14px;
    color: #ff6b6b;
    font-weight: 600;
  }
  .empty {
    text-align: center;
    color: #999;
    padding: 60px 0;
    .empty-icon {
      font-size: 42px;
      display: block;
      margin-bottom: 8px;
    }
  }
</style>
