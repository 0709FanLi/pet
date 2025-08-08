<template>
  <div class="mobile-home-page">
    <!-- 顶部状态栏占位 -->
    <div class="status-bar-placeholder"></div>

    <!-- 顶部筛选 Header -->
    <div class="home-header">
      <van-dropdown-menu active-color="#667eea">
        <van-dropdown-item v-model="selectedType" :options="petTypeOptions" />
        <van-dropdown-item v-model="selectedCity" :options="cityOptions" />
      </van-dropdown-menu>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 宠物列表 -->
      <div class="pet-list">
        <div
          class="pet-card"
          v-for="pet in displayedPetList"
          :key="pet.id"
          @click="viewPetDetail(pet)"
        >
          <div class="pet-image">
            <l-img :src="pet.image" w="80px" h="80px" />
          </div>
          <div class="pet-info">
            <h3 class="pet-location">{{ pet.location }}</h3>
            <p class="pet-amount">悬赏金额：¥{{ pet.amount }}</p>
            <p class="pet-time">{{ formatLostTime(pet.lostTime) }}</p>
          </div>
          <div class="pet-status">
            <span class="status-badge" :class="pet.status">{{
              getStatusText(pet.status)
            }}</span>
          </div>
        </div>

        <!-- 空状态 -->
        <div class="empty-state" v-if="petList.length === 0">
          <div class="empty-icon">🐾</div>
          <p class="empty-text">暂无宠物信息</p>
          <p class="empty-desc">快来发布第一条寻宠信息吧</p>
        </div>
      </div>
    </div>

    <!-- 底部导航栏 -->
    <div class="bottom-nav">
      <div
        class="nav-item"
        :class="{ active: currentTab === 'home' }"
        @click="switchTab('home')"
      >
        <div class="nav-icon">🏠</div>
        <span class="nav-text">首页</span>
      </div>
      <div class="nav-item center-publish" @click="handlePublish">
        <div class="plus-icon">+</div>
      </div>
      <div
        class="nav-item"
        :class="{ active: currentTab === 'my' }"
        @click="switchTab('my')"
      >
        <div class="nav-icon">👤</div>
        <span class="nav-text">我的</span>
      </div>
    </div>

    <!-- 悬浮发布按钮 -->
  </div>
</template>

<script setup>
  import { ref, reactive, onMounted, computed } from 'vue'
  import { useRouter } from 'vue-router'
  import LImg from '@/components/l-img.vue'
  import axios from 'axios'

  const router = useRouter()

  // 当前选中的标签
  const currentTab = ref('home')

  // 宠物列表数据
  const petList = ref([])

  // 筛选选项与选择
  const petTypeOptions = ref([{ text: '全部种类', value: 'all' }])
  const cityOptions = ref([{ text: '全部地区', value: 'all' }])
  const selectedType = ref('all')
  const selectedCity = ref('all')

  // 计算后的展示列表（根据筛选项）
  const displayedPetList = computed(() => {
    return petList.value.filter(pet => {
      const typeOk =
        selectedType.value === 'all' ||
        (pet.petType &&
          (pet.petType.includes(selectedType.value) ||
            // 兼容中英文
            (selectedType.value === 'cat' && pet.petType.includes('猫')) ||
            (selectedType.value === 'dog' && pet.petType.includes('狗')) ||
            (selectedType.value === 'other' && pet.petType.includes('其它'))))
      const cityOk =
        selectedCity.value === 'all' ||
        (pet.rawLostLocation &&
          pet.rawLostLocation.includes(
            selectedCity.valueLabel || selectedCity.value
          ))
      return typeOk && cityOk
    })
  })

  // 切换标签
  const switchTab = tab => {
    currentTab.value = tab
    if (tab === 'my') {
      router.push('/profile')
    }
  }

  // 发布按钮点击
  const handlePublish = () => {
    router.push('/publish')
  }

  // 查看宠物详情
  const viewPetDetail = pet => {
    router.push(`/mobile/pet/${pet.id}`)
  }

  // 格式化丢失时长 - 只显示小时数
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

  // 获取状态文本
  const getStatusText = status => {
    const statusMap = {
      finding: '寻找中',
      found: '已找到',
      closed: '已关闭',
    }
    return statusMap[status] || '未知'
  }
  // 加载宠物列表
  const loadPetList = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/lost-pets')
      petList.value = response.data.map(pet => ({
        id: pet.id,
        petType: pet.petType,
        rawLostLocation: pet.lostLocation,
        location: pet.lostLocation,
        amount: pet.reward,
        lostTime: pet.lostTime, // 改为丢失时间
        image: pet.images
          ? (() => {
              try {
                const images = JSON.parse(pet.images)
                const firstImage = images[0]
                return firstImage && firstImage.startsWith('/uploads/')
                  ? `http://localhost:8080${firstImage}`
                  : firstImage || '/static/images/default-pet.jpg'
              } catch (e) {
                return '/static/images/default-pet.jpg'
              }
            })()
          : '/static/images/default-pet.jpg',
        status: pet.status === 'lost' ? 'finding' : pet.status,
      }))
    } catch (error) {
      console.error('Failed to load pet list:', error)
    }
  }

  // 加载筛选选项（宠物种类与城市）
  const loadFilterOptions = async () => {
    try {
      const [typesRes, citiesRes] = await Promise.all([
        axios.get('http://localhost:8080/api/config/pet-types'),
        axios.get('http://localhost:8080/api/config/cities'),
      ])
      const types = Array.isArray(typesRes.data?.data) ? typesRes.data.data : []
      const cities = Array.isArray(citiesRes.data?.data)
        ? citiesRes.data.data
        : []

      petTypeOptions.value = [
        { text: '全部种类', value: 'all' },
        ...types.map(t => ({
          text: t,
          value: t === '猫' ? 'cat' : t === '狗' ? 'dog' : 'other',
        })),
      ]
      cityOptions.value = [
        { text: '全部地区', value: 'all' },
        ...cities.map(c => ({ text: c, value: c })),
      ]
    } catch (e) {
      // 失败时提供默认选项
      petTypeOptions.value = [
        { text: '全部种类', value: 'all' },
        { text: '猫', value: 'cat' },
        { text: '狗', value: 'dog' },
        { text: '其它', value: 'other' },
      ]
      cityOptions.value = [
        { text: '全部地区', value: 'all' },
        { text: '北京', value: '北京' },
        { text: '上海', value: '上海' },
        { text: '广州', value: '广州' },
        { text: '深圳', value: '深圳' },
        { text: '杭州', value: '杭州' },
        { text: '厦门', value: '厦门' },
        { text: '郑州', value: '郑州' },
      ]
    }
  }
  // 页面初始化
  onMounted(() => {
    loadFilterOptions()
    loadPetList()
  })
</script>

<style scoped>
  .mobile-home-page {
    min-height: 100vh;
    background: #f5f7fa;
    display: flex;
    flex-direction: column;
    position: relative;
  }

  .status-bar-placeholder {
    height: env(safe-area-inset-top, 20px);
    background: #ffffff;
  }

  /* 顶部筛选 Header */
  .home-header {
    background: #ffffff;
    padding: 4px 0 6px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  /* 调整 Vant 下拉菜单样式以贴合主题 */
  :deep(.van-dropdown-menu__bar) {
    box-shadow: none;
    padding: 0 8px;
  }
  :deep(.van-dropdown-menu__item) {
    margin: 0 6px;
    border-radius: 10px;
    background: #f7f8fa;
    padding: 2px 8px;
  }

  /* 主内容区域 */
  .main-content {
    flex: 1;
    padding-bottom: 80px; /* 为底部导航栏留出空间 */
    overflow-y: auto;
  }

  /* 宠物列表 */
  .pet-list {
    padding: 0;
  }

  .pet-card {
    background: #ffffff;
    margin-bottom: 1px;
    padding: 16px;
    display: flex;
    gap: 12px;
    align-items: center;
    cursor: pointer;
    transition: background-color 0.3s ease;
    position: relative;
  }

  .pet-card:active {
    background: #f8f9fa;
  }

  .pet-image {
    width: 80px;
    height: 80px;
    border-radius: 8px;
    overflow: hidden;
    flex-shrink: 0;
  }

  .pet-info {
    flex: 1;
    min-width: 0;
  }

  .pet-location {
    font-size: 16px;
    font-weight: 500;
    color: #333333;
    margin: 0 0 8px 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .pet-amount {
    font-size: 14px;
    color: #ff5252;
    font-weight: 500;
    margin: 0 0 4px 0;
  }

  .pet-time {
    font-size: 12px;
    color: #999999;
    margin: 0;
  }

  .pet-status {
    flex-shrink: 0;
  }

  .status-badge {
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;
  }

  .status-badge.finding {
    background: #e3f2fd;
    color: #1976d2;
  }

  .status-badge.found {
    background: #e8f5e8;
    color: #388e3c;
  }

  .status-badge.closed {
    background: #fafafa;
    color: #757575;
  }

  /* 空状态 */
  .empty-state {
    text-align: center;
    padding: 80px 20px;
    color: #999999;
  }

  .empty-icon {
    font-size: 48px;
    margin-bottom: 16px;
  }

  .empty-text {
    font-size: 16px;
    color: #666666;
    margin: 0 0 8px 0;
  }

  .empty-desc {
    font-size: 14px;
    color: #999999;
    margin: 0;
  }

  /* 底部导航栏 */
  .bottom-nav {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 60px;
    background: #ffffff;
    border-top: 1px solid #e0e0e0;
    display: flex;
    justify-content: space-around;
    align-items: center;
    z-index: 1000;
    padding-bottom: env(safe-area-inset-bottom, 0);
  }

  .nav-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 8px 16px;
    cursor: pointer;
    transition: color 0.3s ease;
    color: #999999;
  }

  .nav-item.active {
    color: #667eea;
  }

  .nav-icon {
    font-size: 20px;
    margin-bottom: 4px;
  }

  .nav-text {
    font-size: 12px;
    font-weight: 500;
  }

  /* 悬浮发布按钮 */
  .center-publish {
    position: relative;
    top: -20px;
    width: 60px;
    height: 60px;
    background: linear-gradient(45deg, #667eea, #764ba2);
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
  }
  .plus-icon {
    color: #ffffff;
    font-size: 28px;
    font-weight: bold;
  }

  .floating-publish {
    position: fixed;
    bottom: 80px;
    right: 16px;
    width: 56px;
    height: 56px;
    background: linear-gradient(45deg, #667eea, #764ba2);
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
    z-index: 999;
    transition: all 0.3s ease;
  }

  .floating-publish:active {
    transform: scale(0.95);
  }

  .plus-icon {
    color: #ffffff;
    font-size: 24px;
    font-weight: bold;
    line-height: 1;
  }

  /* 响应式调整 */
  @media (max-width: 375px) {
    .pet-card {
      padding: 12px;
    }

    .pet-image {
      width: 70px;
      height: 70px;
    }

    .pet-location {
      font-size: 15px;
    }

    .floating-publish {
      width: 50px;
      height: 50px;
      bottom: 75px;
      right: 12px;
    }

    .plus-icon {
      font-size: 20px;
    }
  }

  /* 滚动优化 */
  .main-content {
    -webkit-overflow-scrolling: touch;
    scroll-behavior: smooth;
  }

  /* 动画效果 */
  @keyframes slideInUp {
    from {
      opacity: 0;
      transform: translateY(20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .pet-card {
    animation: slideInUp 0.3s ease-out;
  }

  .floating-publish {
    animation: slideInUp 0.5s ease-out 0.3s both;
  }
</style>
