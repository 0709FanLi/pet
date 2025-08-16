<template>
  <view class="page">
    <!-- 顶部筛选 -->
    <view class="page-header">
      <u-dropdown activeColor="#5B8FF9">
        <u-dropdown-item
          v-model="selectedType"
          :options="petTypeOptions"
          :title="typeTitle"
        />
        <u-dropdown-item
          v-model="selectedCity"
          :options="cityOptions"
          :title="cityTitle"
        />
      </u-dropdown>
    </view>

    <!-- 宠物列表 -->
    <u-loading-page :loading="loading" loadingText="🐾 正在加载宠物信息..." />
    <view v-if="!loading" class="page-content">
      <!-- 宠物卡片列表 -->
      <block v-for="pet in filteredList" :key="pet.id">
        <view class="pet-card" @click="goDetail(pet)">
          <image class="pet-card__image" :src="pet.image" mode="aspectFill" />
          <view class="pet-card__content">
            <view class="pet-card__meta">
              <text class="pet-card__location">{{
                pet.location || '未知位置'
              }}</text>
              <text class="pet-card__time">{{
                formatLostTime(pet.lostTime)
              }}</text>
            </view>
            <view class="flex items-center justify-between mt-sm">
              <text class="pet-card__reward"
                >悬赏 ¥{{ pet.amount || '0' }}</text
              >
              <view class="tag" :class="getStatusTagClass(pet.status)">
                {{ statusText(pet.status) }}
              </view>
            </view>
          </view>
        </view>
      </block>

      <!-- 空状态 -->
      <view v-if="filteredList.length === 0" class="empty-state">
        <text class="empty-state__icon">🐾</text>
        <text class="empty-state__title">暂无宠物信息</text>
        <text class="empty-state__text"
          >目前没有符合条件的寻宠启事<br />您可以调整筛选条件或稍后再来看看</text
        >
      </view>
    </view>
  </view>
</template>

<script setup>
  import { ref, computed, onMounted } from 'vue'
  import { onPullDownRefresh } from '@dcloudio/uni-app'
  import { BASE_URL } from '@/common/config'
  import { request } from '@/common/request'

  const loading = ref(true)
  const list = ref([])

  // 下拉选项与当前值
  const petTypeOptions = ref([{ label: '全部种类', value: 'all' }])
  const cityOptions = ref([{ label: '全部地区', value: 'all' }])
  const selectedType = ref('all')
  const selectedCity = ref('all')

  const typeTitle = computed(() => {
    const match = petTypeOptions.value.find(o => o.value === selectedType.value)
    return match?.label || '全部种类'
  })
  const cityTitle = computed(() => {
    const match = cityOptions.value.find(o => o.value === selectedCity.value)
    return match?.label || '全部地区'
  })

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

  const loadList = async (opts = {}) => {
    const silent = !!opts.silent
    if (!silent) loading.value = true
    try {
      const res = await request({ url: '/api/lost-pets' })
      const arr = Array.isArray(res) ? res : res?.data || []
      const mapped = arr.map(pet => ({
        id: pet.id,
        petType: pet.petType,
        rawLostLocation: pet.lostLocation,
        location: pet.lostLocation,
        amount: pet.reward,
        lostTime: pet.lostTime,
        createdAt: pet.createdAt,
        approvedAt: pet.updatedAt,
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
      // 按通过审核时间倒序（最后通过在最上）
      mapped.sort((a, b) => {
        const taRaw = Date.parse(a.approvedAt || a.createdAt || a.lostTime || 0)
        const tbRaw = Date.parse(b.approvedAt || b.createdAt || b.lostTime || 0)
        const ta = isNaN(taRaw) ? 0 : taRaw
        const tb = isNaN(tbRaw) ? 0 : tbRaw
        if (ta !== tb) return tb - ta
        return (b.id || 0) - (a.id || 0)
      })
      list.value = mapped
    } finally {
      if (!silent) loading.value = false
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

  const getStatusTagClass = status => {
    const classMap = {
      finding: 'tag--primary',
      found: 'tag--success',
      closed: 'tag--warning',
    }
    return classMap[status] || 'tag--primary'
  }

  onMounted(async () => {
    await loadOptions()
    await loadList()
  })

  // 下拉刷新
  onPullDownRefresh(async () => {
    try {
      await loadList({ silent: true })
    } finally {
      uni.stopPullDownRefresh()
    }
  })
</script>

<style lang="scss" scoped>
  /* 页面特定样式，通用样式已在公共样式库中定义 */

  /* 宠物卡片悬停效果 */
  .pet-card {
    transition: all 0.3s ease;
    cursor: pointer;

    &:active {
      transform: scale(0.98);
      box-shadow: var(--shadow-medium);
    }
  }

  /* 宠物卡片动画入场效果 */
  .pet-card:nth-child(1) {
    animation: paw-animation 0.6s ease-out 0.1s both;
  }
  .pet-card:nth-child(2) {
    animation: paw-animation 0.6s ease-out 0.2s both;
  }
  .pet-card:nth-child(3) {
    animation: paw-animation 0.6s ease-out 0.3s both;
  }
  .pet-card:nth-child(4) {
    animation: paw-animation 0.6s ease-out 0.4s both;
  }
  .pet-card:nth-child(n + 5) {
    animation: paw-animation 0.6s ease-out 0.5s both;
  }

  /* 悬赏金额特殊样式 */
  .pet-card__reward {
    position: relative;

    &::before {
      content: '💰';
      margin-right: 4px;
      font-size: 12px;
    }
  }

  /* 时间标签样式优化 */
  .pet-card__time {
    position: relative;

    &::before {
      content: '⏰';
      margin-right: 4px;
      font-size: 10px;
    }
  }

  /* 位置标签样式优化 */
  .pet-card__location {
    position: relative;

    &::before {
      content: '📍';
      margin-right: 4px;
      font-size: 12px;
    }
  }

  /* 响应式优化 */
  @media (max-width: 375px) {
    .pet-card__content {
      padding: var(--spacing-sm);
    }

    .pet-card__location {
      font-size: var(--font-base);
    }
  }
</style>
