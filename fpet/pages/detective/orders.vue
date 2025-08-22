<template>
  <view class="orders-page">
    <!-- 导航栏 -->
    <view class="custom-navbar">
      <view class="navbar-content">
        <text class="navbar-title">我的接单</text>
      </view>
    </view>

    <!-- 订单状态标签页 -->
    <view class="tabs-container">
      <view class="tabs">
        <view
          v-for="tab in tabs"
          :key="tab.key"
          class="tab-item"
          :class="{ active: activeTab === tab.key }"
          @click="switchTab(tab.key)"
        >
          <text class="tab-text">{{ tab.label }}</text>
          <view v-if="tab.badge > 0" class="tab-badge">
            <text class="tab-badge-text">{{
              tab.badge > 99 ? '99+' : tab.badge
            }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 订单列表 -->
    <view class="orders-list">
      <u-empty
        v-if="orders.length === 0 && !loading"
        mode="list"
        text="暂无订单"
      />

      <view
        v-for="order in orders"
        :key="order.id"
        class="order-item"
        @click="goToOrderDetail(order)"
      >
        <!-- 订单头部 -->
        <view class="order-header">
          <view class="order-status">
            <view
              class="status-dot"
              :class="getStatusClass(order.status)"
            ></view>
            <text class="status-text">{{ getStatusLabel(order.status) }}</text>
          </view>
          <text class="order-time">{{ formatTime(order.createdAt) }}</text>
        </view>

        <!-- 宠物信息 -->
        <view class="pet-info">
          <view class="pet-avatar">
            <image
              :src="
                order.lostPet.images && order.lostPet.images.length > 0
                  ? order.lostPet.images[0]
                  : '/static/images/default-pet.png'
              "
              class="pet-image"
              mode="aspectFill"
            />
          </view>
          <view class="pet-details">
            <text class="pet-name">{{ order.lostPet.petName }}</text>
            <text class="pet-breed">{{
              order.lostPet.petBreed || '未知品种'
            }}</text>
            <text class="lost-location"
              >{{ order.lostPet.lostCity }}
              {{ order.lostPet.specificLocation }}</text
            >
          </view>
        </view>

        <!-- 订单信息 -->
        <view class="order-info">
          <view class="info-row">
            <text class="info-label">联系人：</text>
            <text class="info-value">{{
              order.lostPet.user.nickname || order.lostPet.user.phoneNumber
            }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">联系电话：</text>
            <text class="info-value">{{ order.lostPet.contactPhone }}</text>
          </view>
          <view class="info-row" v-if="order.estimatedCompletion">
            <text class="info-label">预计完成：</text>
            <text class="info-value">{{ order.estimatedCompletion }}</text>
          </view>
        </view>

        <!-- 服务描述 -->
        <view class="service-description" v-if="order.serviceDescription">
          <text class="description-label">服务说明：</text>
          <text class="description-text">{{ order.serviceDescription }}</text>
        </view>

        <!-- 订单操作 -->
        <view class="order-actions">
          <template v-if="order.status === 'intention'">
            <u-button
              type="text"
              size="small"
              @click.stop="withdrawOrder(order)"
            >
              <text class="action-text withdraw">撤回意向</text>
            </u-button>
            <u-button
              type="text"
              size="small"
              @click.stop="contactOwner(order)"
            >
              <text class="action-text">联系主人</text>
            </u-button>
          </template>

          <template v-else-if="order.status === 'confirmed'">
            <u-button type="text" size="small" @click.stop="startWork(order)">
              <text class="action-text primary">开始工作</text>
            </u-button>
            <u-button
              type="text"
              size="small"
              @click.stop="contactOwner(order)"
            >
              <text class="action-text">联系主人</text>
            </u-button>
          </template>

          <template v-else-if="order.status === 'in_progress'">
            <u-button
              type="text"
              size="small"
              @click.stop="updateProgress(order)"
            >
              <text class="action-text primary">更新进度</text>
            </u-button>
            <u-button
              type="text"
              size="small"
              @click.stop="markCompleted(order)"
            >
              <text class="action-text success">完成订单</text>
            </u-button>
          </template>

          <template v-else>
            <u-button
              type="text"
              size="small"
              @click.stop="goToOrderDetail(order)"
            >
              <text class="action-text">查看详情</text>
            </u-button>
          </template>
        </view>
      </view>
    </view>

    <!-- 加载更多 -->
    <view class="load-more" v-if="hasMore">
      <u-loadmore :status="loadStatus" @loadmore="loadMore" />
    </view>

    <!-- 进度更新弹窗 -->
    <u-modal
      :show="showProgressModal"
      title="更新进度"
      showCancelButton
      @confirm="submitProgress"
      @cancel="showProgressModal = false"
    >
      <view class="progress-modal">
        <u-textarea
          v-model="progressForm.update"
          placeholder="请描述当前工作进度..."
          autoHeight
          maxlength="500"
        />
      </view>
    </u-modal>
  </view>
</template>

<script setup lang="ts">
  import { ref, reactive, computed, onMounted, onShow } from 'vue'
  import { request } from '@/common/request'
  import config from '@/common/config'

  // 标签页配置
  const tabs = ref([
    { key: 'all', label: '全部', badge: 0 },
    { key: 'intention', label: '意向中', badge: 0 },
    { key: 'confirmed', label: '已确认', badge: 0 },
    { key: 'in_progress', label: '进行中', badge: 0 },
    { key: 'completed', label: '已完成', badge: 0 },
  ])

  // 响应式数据
  const activeTab = ref('all')
  const orders = ref([])
  const loading = ref(false)
  const loadStatus = ref('loadmore')
  const hasMore = ref(true)
  const currentPage = ref(1)

  // 进度更新
  const showProgressModal = ref(false)
  const currentOrder = ref(null)
  const progressForm = reactive({
    update: '',
  })

  // 页面加载
  onMounted(() => {
    loadOrders()
    loadOrderCounts()
  })

  onShow(() => {
    loadOrderCounts()
  })

  // 加载订单列表
  const loadOrders = async (loadMore = false) => {
    try {
      loading.value = true
      const page = loadMore ? currentPage.value + 1 : 1

      const response = await request({
        url: config.API.detective.orders.my,
        method: 'GET',
        data: {
          page,
          pageSize: 20,
          status: activeTab.value === 'all' ? undefined : activeTab.value,
        },
      })

      if (response.code === 200) {
        const newOrders = response.data.list || []

        if (loadMore) {
          orders.value.push(...newOrders)
        } else {
          orders.value = newOrders
        }

        currentPage.value = page
        hasMore.value = newOrders.length >= 20
        loadStatus.value = hasMore.value ? 'loadmore' : 'nomore'
      }
    } catch (error) {
      console.error('加载订单失败:', error)
      uni.showToast({
        title: '加载失败',
        icon: 'none',
      })
    } finally {
      loading.value = false
    }
  }

  // 加载订单数量
  const loadOrderCounts = async () => {
    try {
      const response = await request({
        url: config.API.detective.orders.counts,
        method: 'GET',
      })

      if (response.code === 200) {
        const counts = response.data || {}
        tabs.value.forEach(tab => {
          tab.badge = counts[tab.key] || 0
        })
      }
    } catch (error) {
      console.error('加载订单数量失败:', error)
    }
  }

  // 切换标签页
  const switchTab = (tabKey: string) => {
    if (activeTab.value === tabKey) return

    activeTab.value = tabKey
    currentPage.value = 1
    hasMore.value = true
    loadOrders()
  }

  // 加载更多
  const loadMore = () => {
    if (loading.value || !hasMore.value) return
    loadOrders(true)
  }

  // 跳转到订单详情
  const goToOrderDetail = (order: any) => {
    uni.navigateTo({
      url: `/pages/order/detail?id=${order.id}`,
    })
  }

  // 撤回订单
  const withdrawOrder = (order: any) => {
    uni.showModal({
      title: '确认撤回',
      content: '撤回后将无法再次对此宠物表达接单意向，确定要撤回吗？',
      success: async res => {
        if (res.confirm) {
          try {
            await request({
              url: config.API.detective.orders.intention.replace(
                '{lostPetId}',
                order.lostPet.id.toString()
              ),
              method: 'DELETE',
            })

            // 从列表中移除
            const index = orders.value.findIndex(o => o.id === order.id)
            if (index > -1) {
              orders.value.splice(index, 1)
            }

            loadOrderCounts()

            uni.showToast({
              title: '撤回成功',
              icon: 'success',
            })
          } catch (error) {
            console.error('撤回失败:', error)
            uni.showToast({
              title: '撤回失败',
              icon: 'none',
            })
          }
        }
      },
    })
  }

  // 联系主人
  const contactOwner = (order: any) => {
    uni.showActionSheet({
      itemList: ['拨打电话', '复制号码'],
      success: res => {
        if (res.tapIndex === 0) {
          uni.makePhoneCall({
            phoneNumber: order.lostPet.contactPhone,
          })
        } else if (res.tapIndex === 1) {
          uni.setClipboardData({
            data: order.lostPet.contactPhone,
            success: () => {
              uni.showToast({
                title: '号码已复制',
                icon: 'success',
              })
            },
          })
        }
      },
    })
  }

  // 开始工作
  const startWork = async (order: any) => {
    try {
      await request({
        url: config.API.detective.orders.start.replace(
          '{id}',
          order.id.toString()
        ),
        method: 'PUT',
      })

      // 更新本地状态
      order.status = 'in_progress'
      loadOrderCounts()

      uni.showToast({
        title: '已开始工作',
        icon: 'success',
      })
    } catch (error) {
      console.error('开始工作失败:', error)
      uni.showToast({
        title: '操作失败',
        icon: 'none',
      })
    }
  }

  // 更新进度
  const updateProgress = (order: any) => {
    currentOrder.value = order
    progressForm.update = ''
    showProgressModal.value = true
  }

  // 提交进度更新
  const submitProgress = async () => {
    if (!progressForm.update.trim()) {
      uni.showToast({
        title: '请输入进度描述',
        icon: 'none',
      })
      return
    }

    try {
      await request({
        url: config.API.detective.orders.progress.replace(
          '{id}',
          currentOrder.value.id.toString()
        ),
        method: 'PUT',
        data: {
          progressUpdate: progressForm.update,
        },
      })

      showProgressModal.value = false
      progressForm.update = ''

      uni.showToast({
        title: '进度更新成功',
        icon: 'success',
      })
    } catch (error) {
      console.error('进度更新失败:', error)
      uni.showToast({
        title: '更新失败',
        icon: 'none',
      })
    }
  }

  // 完成订单
  const markCompleted = (order: any) => {
    uni.showModal({
      title: '确认完成',
      content: '确定已完成此订单吗？完成后将无法撤销。',
      success: async res => {
        if (res.confirm) {
          try {
            await request({
              url: config.API.detective.orders.complete.replace(
                '{id}',
                order.id.toString()
              ),
              method: 'PUT',
            })

            // 更新本地状态
            order.status = 'completed'
            loadOrderCounts()

            uni.showToast({
              title: '订单已完成',
              icon: 'success',
            })
          } catch (error) {
            console.error('完成订单失败:', error)
            uni.showToast({
              title: '操作失败',
              icon: 'none',
            })
          }
        }
      },
    })
  }

  // 工具方法
  const getStatusClass = (status: string) => {
    const classMap = {
      intention: 'status-intention',
      confirmed: 'status-confirmed',
      in_progress: 'status-progress',
      completed: 'status-completed',
      closed: 'status-closed',
      withdrawn: 'status-withdrawn',
    }
    return classMap[status] || 'status-default'
  }

  const getStatusLabel = (status: string) => {
    const labelMap = {
      intention: '意向中',
      confirmed: '已确认',
      in_progress: '进行中',
      completed: '已完成',
      closed: '已关闭',
      withdrawn: '已撤回',
    }
    return labelMap[status] || '未知状态'
  }

  const formatTime = (time: string) => {
    const date = new Date(time)
    const now = new Date()
    const diff = now.getTime() - date.getTime()

    if (diff < 24 * 60 * 60 * 1000) {
      return date.toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit',
      })
    } else {
      return date.toLocaleDateString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
      })
    }
  }
</script>

<style scoped>
  .orders-page {
    min-height: 100vh;
    background-color: #f8f9fa;
  }

  /* 导航栏 */
  .custom-navbar {
    background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
    padding: 44px 20px 20px;
    position: sticky;
    top: 0;
    z-index: 100;
  }

  .navbar-content {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .navbar-title {
    font-size: 18px;
    font-weight: 600;
    color: #ffffff;
  }

  /* 标签页 */
  .tabs-container {
    background-color: #ffffff;
    border-bottom: 1px solid #f0f0f0;
  }

  .tabs {
    display: flex;
    padding: 0 20px;
  }

  .tab-item {
    flex: 1;
    position: relative;
    padding: 15px 0;
    display: flex;
    align-items: center;
    justify-content: center;
    border-bottom: 3px solid transparent;
    transition: all 0.3s ease;
  }

  .tab-item.active {
    border-bottom-color: #ff6b35;
  }

  .tab-text {
    font-size: 14px;
    color: #666666;
    font-weight: 500;
    margin-right: 5px;
  }

  .tab-item.active .tab-text {
    color: #ff6b35;
    font-weight: 600;
  }

  .tab-badge {
    background-color: #ff6b35;
    border-radius: 10px;
    min-width: 16px;
    height: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 4px;
  }

  .tab-badge-text {
    font-size: 10px;
    color: #ffffff;
    font-weight: 600;
  }

  /* 订单列表 */
  .orders-list {
    padding: 20px;
  }

  .order-item {
    background-color: #ffffff;
    border-radius: 16px;
    margin-bottom: 16px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
  }

  .order-item:active {
    transform: scale(0.98);
  }

  /* 订单头部 */
  .order-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
  }

  .order-status {
    display: flex;
    align-items: center;
  }

  .status-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    margin-right: 8px;
  }

  .status-dot.status-intention {
    background-color: #ff6b35;
  }

  .status-dot.status-confirmed {
    background-color: #27ae60;
  }

  .status-dot.status-progress {
    background-color: #3498db;
  }

  .status-dot.status-completed {
    background-color: #2ecc71;
  }

  .status-dot.status-closed,
  .status-dot.status-withdrawn {
    background-color: #95a5a6;
  }

  .status-text {
    font-size: 14px;
    color: #333333;
    font-weight: 600;
  }

  .order-time {
    font-size: 12px;
    color: #999999;
  }

  /* 宠物信息 */
  .pet-info {
    display: flex;
    align-items: center;
    margin-bottom: 16px;
  }

  .pet-avatar {
    width: 60px;
    height: 60px;
    border-radius: 12px;
    overflow: hidden;
    margin-right: 16px;
    background-color: #f5f5f5;
  }

  .pet-image {
    width: 100%;
    height: 100%;
  }

  .pet-details {
    flex: 1;
    min-width: 0;
  }

  .pet-name {
    font-size: 16px;
    font-weight: 600;
    color: #333333;
    display: block;
    margin-bottom: 4px;
  }

  .pet-breed {
    font-size: 14px;
    color: #666666;
    display: block;
    margin-bottom: 4px;
  }

  .lost-location {
    font-size: 14px;
    color: #999999;
    display: block;
  }

  /* 订单信息 */
  .order-info {
    margin-bottom: 16px;
  }

  .info-row {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
  }

  .info-row:last-child {
    margin-bottom: 0;
  }

  .info-label {
    font-size: 14px;
    color: #666666;
    min-width: 80px;
  }

  .info-value {
    font-size: 14px;
    color: #333333;
    font-weight: 500;
  }

  /* 服务描述 */
  .service-description {
    margin-bottom: 16px;
    padding: 12px;
    background-color: #f8f9fa;
    border-radius: 8px;
  }

  .description-label {
    font-size: 12px;
    color: #666666;
    display: block;
    margin-bottom: 6px;
  }

  .description-text {
    font-size: 14px;
    color: #333333;
    line-height: 1.5;
  }

  /* 订单操作 */
  .order-actions {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 16px;
    border-top: 1px solid #f0f0f0;
    padding-top: 16px;
  }

  .action-text {
    font-size: 14px;
    color: #ff6b35;
    font-weight: 500;
  }

  .action-text.withdraw {
    color: #e74c3c;
  }

  .action-text.primary {
    color: #3498db;
  }

  .action-text.success {
    color: #27ae60;
  }

  /* 进度更新弹窗 */
  .progress-modal {
    padding: 20px 0;
  }

  /* 加载更多 */
  .load-more {
    padding: 20px;
  }
</style>
