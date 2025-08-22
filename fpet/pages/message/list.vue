<template>
  <view class="message-list-page">
    <!-- 导航栏 -->
    <view class="custom-navbar">
      <view class="navbar-content">
        <text class="navbar-title">我的消息</text>
      </view>
    </view>

    <!-- 消息类型标签页 -->
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

    <!-- 操作栏 -->
    <view class="action-bar" v-if="messages.length > 0">
      <view class="action-left">
        <u-checkbox
          v-model="allSelected"
          @change="toggleSelectAll"
          color="#FF6B35"
        >
          <text class="action-text">全选</text>
        </u-checkbox>
      </view>
      <view class="action-right">
        <u-button
          type="text"
          size="mini"
          @click="markAllRead"
          :disabled="!hasUnreadSelected"
        >
          <text class="action-button-text">标记已读</text>
        </u-button>
        <u-button
          type="text"
          size="mini"
          @click="deleteSelected"
          :disabled="!hasSelected"
        >
          <text class="action-button-text delete">删除</text>
        </u-button>
      </view>
    </view>

    <!-- 消息列表 -->
    <view class="message-list">
      <u-empty
        v-if="messages.length === 0 && !loading"
        mode="message"
        text="暂无消息"
      />

      <view
        v-for="message in messages"
        :key="message.id"
        class="message-item"
        :class="{
          unread: message.status === 'unread',
          selected: selectedIds.includes(message.id),
        }"
        @click="handleMessageClick(message)"
      >
        <view class="message-checkbox">
          <u-checkbox
            :value="selectedIds.includes(message.id)"
            @change="toggleSelect(message.id)"
            color="#FF6B35"
            @click.stop
          />
        </view>

        <view class="message-content">
          <view class="message-header">
            <view class="message-type">
              <view class="type-icon" :class="getTypeClass(message.type)">
                <text class="type-icon-text">{{
                  getTypeIcon(message.type)
                }}</text>
              </view>
              <text class="type-text">{{ getTypeLabel(message.type) }}</text>
            </view>
            <text class="message-time">{{
              formatTime(message.createdAt)
            }}</text>
          </view>

          <view class="message-title">
            <text class="title-text">{{ message.title }}</text>
            <view v-if="message.status === 'unread'" class="unread-dot"></view>
          </view>

          <view class="message-preview">
            <text class="preview-text">{{ message.content }}</text>
          </view>
        </view>

        <view class="message-arrow">
          <text class="arrow-icon">></text>
        </view>
      </view>
    </view>

    <!-- 加载更多 -->
    <view class="load-more" v-if="hasMore">
      <u-loadmore :status="loadStatus" @loadmore="loadMore" />
    </view>
  </view>
</template>

<script setup lang="ts">
  import { ref, reactive, computed, onMounted, onShow } from 'vue'
  import { onLoad } from '@dcloudio/uni-app'
  import { request } from '@/common/request'
  import { API } from '@/common/config'

  // 标签页配置
  const tabs = ref([
    { key: 'all', label: '全部', badge: 0 },
    { key: 'system', label: '系统', badge: 0 },
    { key: 'order', label: '订单', badge: 0 },
    { key: 'recommend', label: '推荐', badge: 0 },
  ])

  // 响应式数据
  const activeTab = ref('all')
  const messages = ref([])
  const loading = ref(false)
  const loadStatus = ref('loadmore')
  const hasMore = ref(true)
  const currentPage = ref(1)

  // 选择相关
  const selectedIds = ref([])
  const allSelected = ref(false)

  // 计算属性
  const hasSelected = computed(() => selectedIds.value.length > 0)
  const hasUnreadSelected = computed(() => {
    return selectedIds.value.some(id => {
      const message = messages.value.find(m => m.id === id)
      return message && message.status === 'unread'
    })
  })

  // 页面加载
  onMounted(() => {
    loadMessages()
    loadUnreadCounts()
  })

  onShow(() => {
    loadUnreadCounts()
  })

  // 加载消息列表
  const loadMessages = async (loadMore = false) => {
    try {
      loading.value = true
      const page = loadMore ? currentPage.value + 1 : 1

      const response = await request({
        url: API.notifications.list,
        method: 'GET',
        data: {
          page,
          pageSize: 20,
          type: activeTab.value === 'all' ? undefined : activeTab.value,
        },
      })

      if (response.code === 200) {
        const newMessages = response.data.list || []

        if (loadMore) {
          messages.value.push(...newMessages)
        } else {
          messages.value = newMessages
        }

        currentPage.value = page
        hasMore.value = newMessages.length >= 20
        loadStatus.value = hasMore.value ? 'loadmore' : 'nomore'
      }
    } catch (error) {
      console.error('加载消息失败:', error)
      uni.showToast({
        title: '加载失败',
        icon: 'none',
      })
    } finally {
      loading.value = false
    }
  }

  // 加载未读数量
  const loadUnreadCounts = async () => {
    try {
      const response = await request({
        url: API.notifications.unreadCount,
        method: 'GET',
      })

      if (response.code === 200) {
        const counts = response.data || {}
        tabs.value.forEach(tab => {
          tab.badge = counts[tab.key] || 0
        })
      }
    } catch (error) {
      console.error('加载未读数量失败:', error)
    }
  }

  // 切换标签页
  const switchTab = (tabKey: string) => {
    if (activeTab.value === tabKey) return

    activeTab.value = tabKey
    currentPage.value = 1
    hasMore.value = true
    selectedIds.value = []
    allSelected.value = false
    loadMessages()
  }

  // 加载更多
  const loadMore = () => {
    if (loading.value || !hasMore.value) return
    loadMessages(true)
  }

  // 消息点击处理
  const handleMessageClick = async (message: any) => {
    // 标记为已读
    if (message.status === 'unread') {
      await markMessageRead(message.id)
      message.status = 'read'
      loadUnreadCounts()
    }

    // 跳转到详情页
    uni.navigateTo({
      url: `/pages/message/detail?id=${message.id}`,
    })
  }

  // 标记消息已读
  const markMessageRead = async (messageId: number) => {
    try {
      await request({
        url: API.notifications.markRead.replace('{id}', messageId.toString()),
        method: 'PUT',
      })
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }

  // 选择相关方法
  const toggleSelect = (messageId: number) => {
    const index = selectedIds.value.indexOf(messageId)
    if (index > -1) {
      selectedIds.value.splice(index, 1)
    } else {
      selectedIds.value.push(messageId)
    }

    allSelected.value = selectedIds.value.length === messages.value.length
  }

  const toggleSelectAll = () => {
    if (allSelected.value) {
      selectedIds.value = messages.value.map(m => m.id)
    } else {
      selectedIds.value = []
    }
  }

  // 批量标记已读
  const markAllRead = async () => {
    try {
      await request({
        url: API.notifications.markAllRead,
        method: 'PUT',
      })

      // 更新本地状态
      messages.value.forEach(message => {
        if (selectedIds.value.includes(message.id)) {
          message.status = 'read'
        }
      })

      selectedIds.value = []
      allSelected.value = false
      loadUnreadCounts()

      uni.showToast({
        title: '已标记为已读',
        icon: 'success',
      })
    } catch (error) {
      console.error('批量标记已读失败:', error)
      uni.showToast({
        title: '操作失败',
        icon: 'none',
      })
    }
  }

  // 批量删除
  const deleteSelected = async () => {
    uni.showModal({
      title: '确认删除',
      content: `确定要删除选中的 ${selectedIds.value.length} 条消息吗？`,
      success: async res => {
        if (res.confirm) {
          try {
            // 逐个删除消息
            for (const id of selectedIds.value) {
              await request({
                url: API.notifications.delete.replace('{id}', id.toString()),
                method: 'DELETE',
              })
            }

            // 从列表中移除已删除的消息
            messages.value = messages.value.filter(
              m => !selectedIds.value.includes(m.id)
            )
            selectedIds.value = []
            allSelected.value = false
            loadUnreadCounts()

            uni.showToast({
              title: '删除成功',
              icon: 'success',
            })
          } catch (error) {
            console.error('批量删除失败:', error)
            uni.showToast({
              title: '删除失败',
              icon: 'none',
            })
          }
        }
      },
    })
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

  const formatTime = (time: string) => {
    const date = new Date(time)
    const now = new Date()
    const diff = now.getTime() - date.getTime()

    if (diff < 60 * 1000) {
      return '刚刚'
    } else if (diff < 60 * 60 * 1000) {
      return `${Math.floor(diff / (60 * 1000))}分钟前`
    } else if (diff < 24 * 60 * 60 * 1000) {
      return `${Math.floor(diff / (60 * 60 * 1000))}小时前`
    } else if (diff < 7 * 24 * 60 * 60 * 1000) {
      return `${Math.floor(diff / (24 * 60 * 60 * 1000))}天前`
    } else {
      return date.toLocaleDateString()
    }
  }
</script>

<style scoped>
  .message-list-page {
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
    font-size: 16px;
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
    min-width: 18px;
    height: 18px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 5px;
  }

  .tab-badge-text {
    font-size: 11px;
    color: #ffffff;
    font-weight: 600;
  }

  /* 操作栏 */
  .action-bar {
    background-color: #ffffff;
    padding: 12px 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid #f0f0f0;
  }

  .action-left {
    display: flex;
    align-items: center;
  }

  .action-text {
    font-size: 14px;
    color: #666666;
    margin-left: 8px;
  }

  .action-right {
    display: flex;
    align-items: center;
    gap: 15px;
  }

  .action-button-text {
    font-size: 14px;
    color: #ff6b35;
  }

  .action-button-text.delete {
    color: #e74c3c;
  }

  /* 消息列表 */
  .message-list {
    padding: 0 20px;
  }

  .message-item {
    background-color: #ffffff;
    border-radius: 12px;
    margin-bottom: 12px;
    padding: 16px;
    display: flex;
    align-items: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    transition: all 0.3s ease;
    position: relative;
  }

  .message-item.unread {
    background-color: #fefefe;
    border-left: 4px solid #ff6b35;
  }

  .message-item.selected {
    background-color: #fff8f5;
    border: 1px solid #ff6b35;
  }

  .message-checkbox {
    margin-right: 12px;
  }

  .message-content {
    flex: 1;
    min-width: 0;
  }

  .message-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8px;
  }

  .message-type {
    display: flex;
    align-items: center;
  }

  .type-icon {
    width: 24px;
    height: 24px;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 8px;
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
    font-size: 12px;
  }

  .type-text {
    font-size: 12px;
    color: #999999;
  }

  .message-time {
    font-size: 12px;
    color: #999999;
  }

  .message-title {
    display: flex;
    align-items: center;
    margin-bottom: 6px;
  }

  .title-text {
    font-size: 16px;
    font-weight: 600;
    color: #333333;
    margin-right: 8px;
    line-height: 1.4;
  }

  .unread-dot {
    width: 8px;
    height: 8px;
    background-color: #ff6b35;
    border-radius: 50%;
  }

  .message-preview {
    margin-bottom: 4px;
  }

  .preview-text {
    font-size: 14px;
    color: #666666;
    line-height: 1.5;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
  }

  .message-arrow {
    margin-left: 12px;
  }

  .arrow-icon {
    font-size: 16px;
    color: #cccccc;
  }

  /* 加载更多 */
  .load-more {
    padding: 20px;
  }
</style>
