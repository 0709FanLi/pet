<template>
  <view class="message-detail-page">
    <!-- 导航栏 -->
    <view class="custom-navbar">
      <view class="navbar-content">
        <view class="navbar-left" @click="goBack">
          <text class="back-icon">‹</text>
        </view>
        <text class="navbar-title">消息详情</text>
        <view class="navbar-right">
          <view class="more-btn" @click="showMoreActions">
            <text class="more-icon">⋯</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <u-loading-icon mode="semicircle" color="#FF6B35" size="24" />
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 消息内容 -->
    <view v-else-if="message" class="message-container">
      <!-- 消息头部 -->
      <view class="message-header">
        <view class="message-type">
          <view class="type-icon" :class="getTypeClass(message.type)">
            <text class="type-icon-text">{{ getTypeIcon(message.type) }}</text>
          </view>
          <text class="type-text">{{ getTypeLabel(message.type) }}</text>
        </view>
        <view class="message-status" v-if="message.status === 'unread'">
          <text class="status-text">未读</text>
        </view>
      </view>

      <!-- 消息标题 -->
      <view class="message-title">
        <text class="title-text">{{ message.title }}</text>
      </view>

      <!-- 消息时间 -->
      <view class="message-time">
        <text class="time-text">{{ formatDateTime(message.createdAt) }}</text>
      </view>

      <!-- 消息内容 -->
      <view class="message-content">
        <rich-text
          class="content-text"
          :nodes="formatContent(message.content)"
        />
      </view>

      <!-- 相关操作按钮 -->
      <view class="action-buttons" v-if="hasActions">
        <!-- 订单相关操作 -->
        <template v-if="message.type === 'order' && message.extraData">
          <u-button
            v-if="message.extraData.actionType === 'intention_confirmed'"
            type="primary"
            size="large"
            @click="goToOrderDetail"
          >
            查看订单详情
          </u-button>

          <u-button
            v-if="message.extraData.actionType === 'new_recommendation'"
            type="primary"
            size="large"
            @click="goToPetDetail"
          >
            查看推荐详情
          </u-button>
        </template>

        <!-- 系统公告操作 -->
        <template v-if="message.type === 'announcement'">
          <u-button
            type="primary"
            size="large"
            @click="handleAnnouncementAction"
          >
            了解详情
          </u-button>
        </template>
      </view>

      <!-- 消息元数据 -->
      <view class="message-meta" v-if="message.extraData">
        <view
          class="meta-item"
          v-for="(value, key) in getMetaData(message.extraData)"
          :key="key"
        >
          <text class="meta-label">{{ key }}：</text>
          <text class="meta-value">{{ value }}</text>
        </view>
      </view>
    </view>

    <!-- 错误状态 -->
    <view v-else class="error-container">
      <u-empty mode="error" text="消息加载失败" />
      <u-button
        type="primary"
        size="large"
        @click="loadMessage"
        style="margin-top: 20px"
      >
        重新加载
      </u-button>
    </view>

    <!-- 更多操作弹窗 -->
    <u-action-sheet
      :show="showActions"
      :actions="actionList"
      @close="showActions = false"
      @select="handleAction"
    />
  </view>
</template>

<script setup lang="ts">
  import { ref, reactive, computed, onMounted } from 'vue'
  import { onLoad } from '@dcloudio/uni-app'
  import { request } from '@/common/request'
  import config from '@/common/config'

  // 页面参数
  const messageId = ref('')

  // 响应式数据
  const message = ref(null)
  const loading = ref(true)
  const showActions = ref(false)

  // 操作列表
  const actionList = ref([
    { name: '标记为已读', value: 'mark_read', disabled: false },
    { name: '删除消息', value: 'delete', color: '#e74c3c' },
  ])

  // 计算属性
  const hasActions = computed(() => {
    if (!message.value) return false

    const { type, extraData } = message.value
    return (
      (type === 'order' &&
        extraData &&
        ['intention_confirmed', 'new_recommendation'].includes(
          extraData.actionType
        )) ||
      type === 'announcement'
    )
  })

  // 页面加载
  onLoad(options => {
    if (options.id) {
      messageId.value = options.id
      loadMessage()
    }
  })

  // 加载消息详情
  const loadMessage = async () => {
    try {
      loading.value = true

      const response = await request({
        url: config.API.notifications.detail.replace('{id}', messageId.value),
        method: 'GET',
      })

      if (response.code === 200) {
        message.value = response.data

        // 自动标记为已读
        if (message.value.status === 'unread') {
          await markAsRead()
          message.value.status = 'read'
        }

        // 更新操作列表状态
        updateActionList()
      } else {
        throw new Error(response.message || '加载失败')
      }
    } catch (error) {
      console.error('加载消息详情失败:', error)
      uni.showToast({
        title: '加载失败',
        icon: 'none',
      })
    } finally {
      loading.value = false
    }
  }

  // 标记为已读
  const markAsRead = async () => {
    try {
      await request({
        url: config.API.notifications.markRead.replace('{id}', messageId.value),
        method: 'PUT',
      })
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }

  // 更新操作列表
  const updateActionList = () => {
    if (message.value) {
      actionList.value[0].disabled = message.value.status === 'read'
    }
  }

  // 返回上一页
  const goBack = () => {
    uni.navigateBack()
  }

  // 显示更多操作
  const showMoreActions = () => {
    showActions.value = true
  }

  // 处理操作
  const handleAction = async action => {
    showActions.value = false

    switch (action.value) {
      case 'mark_read':
        if (message.value.status === 'unread') {
          await markAsRead()
          message.value.status = 'read'
          updateActionList()
          uni.showToast({
            title: '已标记为已读',
            icon: 'success',
          })
        }
        break

      case 'delete':
        deleteMessage()
        break
    }
  }

  // 删除消息
  const deleteMessage = () => {
    uni.showModal({
      title: '确认删除',
      content: '确定要删除这条消息吗？',
      success: async res => {
        if (res.confirm) {
          try {
            await request({
              url: config.API.notifications.delete.replace(
                '{id}',
                messageId.value
              ),
              method: 'DELETE',
            })

            uni.showToast({
              title: '删除成功',
              icon: 'success',
            })

            setTimeout(() => {
              uni.navigateBack()
            }, 1500)
          } catch (error) {
            console.error('删除消息失败:', error)
            uni.showToast({
              title: '删除失败',
              icon: 'none',
            })
          }
        }
      },
    })
  }

  // 跳转到订单详情
  const goToOrderDetail = () => {
    if (message.value?.extraData?.orderId) {
      uni.navigateTo({
        url: `/pages/order/detail?id=${message.value.extraData.orderId}`,
      })
    }
  }

  // 跳转到宠物详情
  const goToPetDetail = () => {
    if (message.value?.extraData?.petId) {
      uni.navigateTo({
        url: `/pages/pet/detail?id=${message.value.extraData.petId}`,
      })
    }
  }

  // 处理公告操作
  const handleAnnouncementAction = () => {
    if (message.value?.extraData?.actionUrl) {
      uni.navigateTo({
        url: message.value.extraData.actionUrl,
      })
    }
  }

  // 工具方法
  const getTypeIcon = (type: string) => {
    const icons = {
      order: '📋',
      system: '🔔',
      recommend: '💡',
      announcement: '📢',
    }
    return icons[type] || '📧'
  }

  const getTypeClass = (type: string) => {
    return `type-${type}`
  }

  const getTypeLabel = (type: string) => {
    const labels = {
      order: '订单消息',
      system: '系统消息',
      recommend: '推荐消息',
      announcement: '公告消息',
    }
    return labels[type] || '未知消息'
  }

  const formatDateTime = (time: string) => {
    const date = new Date(time)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    })
  }

  const formatContent = (content: string) => {
    // 将换行符转换为 <br> 标签
    return content.replace(/\n/g, '<br>')
  }

  const getMetaData = (extraData: any) => {
    if (!extraData) return {}

    const metaData = {}

    // 根据消息类型显示不同的元数据
    if (extraData.petName) {
      metaData['宠物名称'] = extraData.petName
    }

    if (extraData.detectiveName) {
      metaData['侦探姓名'] = extraData.detectiveName
    }

    if (extraData.location) {
      metaData['相关地点'] = extraData.location
    }

    if (extraData.estimatedCompletion) {
      metaData['预计完成时间'] = extraData.estimatedCompletion
    }

    return metaData
  }
</script>

<style scoped>
  .message-detail-page {
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
    justify-content: space-between;
  }

  .navbar-left {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .back-icon {
    font-size: 24px;
    color: #ffffff;
    font-weight: 600;
  }

  .navbar-title {
    font-size: 18px;
    font-weight: 600;
    color: #ffffff;
  }

  .navbar-right {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .more-btn {
    width: 32px;
    height: 32px;
    border-radius: 16px;
    background-color: rgba(255, 255, 255, 0.2);
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .more-icon {
    font-size: 18px;
    color: #ffffff;
    font-weight: 600;
  }

  /* 加载状态 */
  .loading-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
  }

  .loading-text {
    font-size: 14px;
    color: #999999;
    margin-top: 12px;
  }

  /* 错误状态 */
  .error-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
  }

  /* 消息容器 */
  .message-container {
    margin: 20px;
    background-color: #ffffff;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }

  /* 消息头部 */
  .message-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
  }

  .message-type {
    display: flex;
    align-items: center;
  }

  .type-icon {
    width: 32px;
    height: 32px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 12px;
  }

  .type-icon.type-order {
    background-color: #e3f2fd;
  }

  .type-icon.type-system {
    background-color: #f3e5f5;
  }

  .type-icon.type-recommend {
    background-color: #e8f5e8;
  }

  .type-icon.type-announcement {
    background-color: #fff3e0;
  }

  .type-icon-text {
    font-size: 16px;
  }

  .type-text {
    font-size: 14px;
    color: #666666;
    font-weight: 500;
  }

  .message-status {
    background-color: #ff6b35;
    border-radius: 12px;
    padding: 4px 12px;
  }

  .status-text {
    font-size: 12px;
    color: #ffffff;
    font-weight: 600;
  }

  /* 消息标题 */
  .message-title {
    margin-bottom: 12px;
  }

  .title-text {
    font-size: 20px;
    font-weight: 600;
    color: #333333;
    line-height: 1.4;
  }

  /* 消息时间 */
  .message-time {
    margin-bottom: 24px;
  }

  .time-text {
    font-size: 14px;
    color: #999999;
  }

  /* 消息内容 */
  .message-content {
    margin-bottom: 24px;
  }

  .content-text {
    font-size: 16px;
    color: #333333;
    line-height: 1.6;
    word-wrap: break-word;
  }

  /* 操作按钮 */
  .action-buttons {
    margin-bottom: 24px;
  }

  /* 消息元数据 */
  .message-meta {
    border-top: 1px solid #f0f0f0;
    padding-top: 20px;
  }

  .meta-item {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
  }

  .meta-item:last-child {
    margin-bottom: 0;
  }

  .meta-label {
    font-size: 14px;
    color: #666666;
    min-width: 100px;
  }

  .meta-value {
    font-size: 14px;
    color: #333333;
    font-weight: 500;
  }
</style>
