<template>
  <view class="page">
    <!-- 用户头部卡片 -->
    <view class="profile-header bg-gradient-primary">
      <!-- 消息图标 -->
      <view
        v-if="isLoggedIn"
        class="message-icon-wrapper"
        @click="goToMessages"
      >
        <text class="message-icon">📬</text>
        <view v-if="unreadCount > 0" class="message-badge">
          <text class="message-badge-text">{{
            unreadCount > 99 ? '99+' : unreadCount
          }}</text>
        </view>
      </view>

      <view class="profile-header__content">
        <view class="profile-avatar-container" @click="openAvatarOptions">
          <view
            v-if="!user.avatar"
            class="profile-avatar profile-avatar--default"
          >
            <view class="avatar-pet">
              <view class="pet-face">
                <view class="pet-ear pet-ear--left"></view>
                <view class="pet-ear pet-ear--right"></view>
                <view class="pet-head">
                  <view class="pet-eye pet-eye--left"></view>
                  <view class="pet-eye pet-eye--right"></view>
                  <view class="pet-nose"></view>
                  <view class="pet-mouth"></view>
                </view>
              </view>
            </view>
          </view>
          <image v-else class="profile-avatar" :src="avatarFullUrl" />
          <text class="profile-avatar__badge">🐾</text>
          <view class="profile-avatar__edit">
            <text class="edit-icon">📷</text>
          </view>
        </view>
        <view class="profile-info">
          <text class="profile-name">{{
            isLoggedIn ? user.username || '爱宠主人' : '未登录'
          }}</text>
          <text class="profile-phone">{{
            isLoggedIn
              ? user.phoneNumber || '未绑定手机号'
              : '点击登录查看更多功能'
          }}</text>
          <view v-if="isLoggedIn" class="profile-stats">
            <view class="profile-stat">
              <text class="profile-stat__number">{{ userStats.posts }}</text>
              <text class="profile-stat__label">发布</text>
            </view>
            <view class="profile-stat">
              <text class="profile-stat__number">{{ userStats.helped }}</text>
              <text class="profile-stat__label">帮助</text>
            </view>
            <view class="profile-stat">
              <text class="profile-stat__number">{{ userStats.days }}</text>
              <text class="profile-stat__label">天数</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 装饰元素 -->
      <text class="decoration decoration--1">🌟</text>
      <text class="decoration decoration--2">💝</text>
      <text class="decoration decoration--3">🎈</text>
    </view>

    <view class="page-content">
      <!-- 快捷功能区 -->
      <view v-if="isLoggedIn" class="quick-actions">
        <view class="quick-action" @click="goMyPosts">
          <view class="quick-action__icon">📝</view>
          <text class="quick-action__title">我的发布</text>
          <text class="quick-action__desc">查看发布历史</text>
        </view>

        <view class="quick-action" @click="handleDetectiveAction">
          <view class="quick-action__icon">{{ detectiveIcon }}</view>
          <text class="quick-action__title">{{ detectiveTitle }}</text>
          <text class="quick-action__desc">{{ detectiveDesc }}</text>
          <view
            v-if="detectiveStatus === 'pending'"
            class="quick-action__badge"
          >
            <text class="badge badge--warning">审核中</text>
          </view>
          <view
            v-else-if="detectiveStatus === 'approved'"
            class="quick-action__badge"
          >
            <text class="badge badge--success">已认证</text>
          </view>
        </view>
      </view>

      <!-- 未登录提示 -->
      <view v-else class="login-prompt">
        <view class="login-prompt__content">
          <text class="login-prompt__icon">🔐</text>
          <text class="login-prompt__title">登录后查看更多功能</text>
          <text class="login-prompt__desc"
            >发布寻宠信息、申请成为宠物侦探等</text
          >
        </view>
      </view>

      <!-- 消息和订单管理 -->
      <view v-if="isLoggedIn" class="menu-section">
        <view class="menu-section__title">
          <text class="menu-section__icon">📋</text>
          <text class="menu-section__text">消息与订单</text>
        </view>

        <view class="menu-list">
          <view class="menu-item" @click="goToMessages">
            <view class="menu-item__left">
              <text class="menu-item__icon">📬</text>
              <text class="menu-item__title">我的消息</text>
            </view>
            <view class="menu-item__right">
              <view v-if="unreadCount > 0" class="menu-badge">
                <text class="menu-badge-text">{{
                  unreadCount > 99 ? '99+' : unreadCount
                }}</text>
              </view>
              <text class="menu-item__arrow">›</text>
            </view>
          </view>

          <view
            v-if="detectiveStatus === 'approved'"
            class="menu-item"
            @click="goToMyOrders"
          >
            <view class="menu-item__left">
              <text class="menu-item__icon">🕵️‍♂️</text>
              <text class="menu-item__title">我的接单</text>
            </view>
            <text class="menu-item__arrow">›</text>
          </view>
        </view>
      </view>

      <!-- 功能菜单 -->
      <view class="menu-section">
        <view class="menu-section__title">
          <text class="menu-section__icon">⚙️</text>
          <text class="menu-section__text">更多功能</text>
        </view>

        <view class="menu-list">
          <view class="menu-item" @click="showHelp = true">
            <view class="menu-item__left">
              <text class="menu-item__icon">📞</text>
              <text class="menu-item__title">帮助与反馈</text>
            </view>
            <text class="menu-item__arrow">›</text>
          </view>

          <view class="menu-item" @click="showAbout = true">
            <view class="menu-item__left">
              <text class="menu-item__icon">ℹ️</text>
              <text class="menu-item__title">关于我们</text>
            </view>
            <text class="menu-item__arrow">›</text>
          </view>
        </view>
      </view>

      <!-- 登录/退出登录 -->
      <view class="auth-section">
        <view
          v-if="!isLoggedIn"
          class="btn btn--primary btn--block"
          @click="goLogin"
        >
          <text class="btn-text">🔑 登录</text>
        </view>
        <view v-else class="btn btn--secondary btn--block" @click="logout">
          <text class="btn-text">🚪 退出登录</text>
        </view>
      </view>
    </view>

    <!-- 弹窗 -->
    <u-popup :show="showHelp" @close="showHelp = false" round>
      <view class="popup-content">
        <view class="popup-header">
          <text class="popup-title">📞 联系客服</text>
        </view>
        <view class="popup-body">
          <view class="contact-item">
            <text class="contact-label">客服热线</text>
            <text class="contact-value">400-123-4567</text>
          </view>
          <view class="contact-item">
            <text class="contact-label">工作时间</text>
            <text class="contact-value">9:00-18:00</text>
          </view>
          <view class="contact-item">
            <text class="contact-label">微信客服</text>
            <text class="contact-value">pethelp2024</text>
          </view>
        </view>
      </view>
    </u-popup>

    <u-popup :show="showAbout" @close="showAbout = false" round>
      <view class="popup-content">
        <view class="popup-header">
          <text class="popup-title">🐾 关于我们</text>
        </view>
        <view class="popup-body">
          <text class="about-text">
            宠物找回平台致力于帮助每一只走失的毛孩子回家。
            <br /><br />
            我们相信，爱是连接彼此最好的纽带。
            <br /><br />
            版本: v1.0.0<br />
            © 2024 宠物找回平台
          </text>
        </view>
      </view>
    </u-popup>

    <!-- 头像操作弹窗 -->
    <u-action-sheet
      :show="showAvatarOptions"
      :actions="avatarActions"
      title="更换头像"
      @select="onAvatarAction"
      @close="showAvatarOptions = false"
      @cancel="showAvatarOptions = false"
    />
  </view>
</template>

<script setup>
  import { ref, computed, onMounted } from 'vue'
  import { onShow } from '@dcloudio/uni-app'
  import { STORAGE_KEYS, API, BASE_URL } from '@/common/config'
  import { request } from '@/common/request'

  const user = ref({ username: '', phoneNumber: '', avatar: '' })
  const showHelp = ref(false)
  const showAbout = ref(false)
  const detectiveStatus = ref('none')
  const showAvatarOptions = ref(false)
  const unreadCount = ref(0)

  // 用户统计数据
  const userStats = ref({
    posts: 0,
    helped: 2,
    days: 15,
  })

  // 登录状态 - 使用响应式数据
  const isLoggedIn = ref(false)

  // 检查登录状态
  const checkLoginStatus = () => {
    const token = uni.getStorageSync(STORAGE_KEYS.token)
    isLoggedIn.value = !!token
  }

  // 头像完整URL
  const avatarFullUrl = computed(() => {
    if (!user.value.avatar) return ''
    if (user.value.avatar.startsWith('http')) return user.value.avatar
    return `${BASE_URL}${user.value.avatar}`
  })

  // 头像操作选项
  const avatarActions = computed(() => {
    const actions = [
      { name: '从相册选择', value: 'album' },
      { name: '拍照', value: 'camera' },
    ]
    if (user.value.avatar) {
      actions.push({ name: '删除头像', value: 'delete' })
    }
    return actions
  })

  // 侦探功能相关计算属性
  const detectiveIcon = computed(() => {
    const iconMap = {
      none: '🕵️',
      pending: '⏳',
      approved: '⭐',
      rejected: '❌',
    }
    return iconMap[detectiveStatus.value] || '🕵️'
  })

  const detectiveTitle = computed(() => {
    const titleMap = {
      none: '成为宠物侦探',
      pending: '侦探申请中',
      approved: '宠物侦探',
      rejected: '重新申请',
    }
    return titleMap[detectiveStatus.value] || '成为宠物侦探'
  })

  const detectiveDesc = computed(() => {
    const descMap = {
      none: '帮助更多宠物回家',
      pending: '审核预计24小时',
      approved: '点击去接单',
      rejected: '点击重新提交',
    }
    return descMap[detectiveStatus.value] || '帮助更多宠物回家'
  })
  const goMyPosts = () => uni.navigateTo({ url: '/pages/my/lost-pets' })

  const goLogin = () => {
    uni.navigateTo({ url: '/pages/auth/login' })
  }

  // 头像操作相关函数
  const openAvatarOptions = () => {
    if (!isLoggedIn.value) {
      uni.showToast({ title: '请先登录', icon: 'none' })
      return
    }
    showAvatarOptions.value = true
  }

  const onAvatarAction = item => {
    showAvatarOptions.value = false
    switch (item.value) {
      case 'album':
        chooseImage('album')
        break
      case 'camera':
        chooseImage('camera')
        break
      case 'delete':
        deleteAvatar()
        break
    }
  }

  const chooseImage = sourceType => {
    uni.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: [sourceType],
      success: res => {
        const tempFilePath = res.tempFilePaths[0]
        uploadAvatar(tempFilePath)
      },
      fail: err => {
        console.error('选择图片失败:', err)
        uni.showToast({ title: '选择图片失败', icon: 'none' })
      },
    })
  }

  const uploadAvatar = filePath => {
    const token = uni.getStorageSync(STORAGE_KEYS.token)
    if (!token) {
      uni.showToast({ title: '请先登录', icon: 'none' })
      return
    }

    uni.showLoading({ title: '上传中...' })

    const uploadUrl = `${BASE_URL}${API.avatar.upload}`

    uni.uploadFile({
      url: uploadUrl,
      filePath: filePath,
      name: 'avatar',
      header: {
        Authorization: `Bearer ${token}`,
      },
      success: uploadRes => {
        try {
          const data = JSON.parse(uploadRes.data)
          if (data.success) {
            user.value.avatar = data.avatarUrl
            // 更新本地存储的用户信息
            const userInfo = uni.getStorageSync('userInfo')
            if (userInfo) {
              const parsedUserInfo =
                typeof userInfo === 'string' ? JSON.parse(userInfo) : userInfo
              parsedUserInfo.avatar = data.avatarUrl
              uni.setStorageSync('userInfo', JSON.stringify(parsedUserInfo))
            }
            uni.showToast({ title: '头像更新成功', icon: 'success' })
          } else {
            uni.showToast({ title: data.message || '上传失败', icon: 'none' })
          }
        } catch (e) {
          console.error('解析上传结果失败:', e)
          uni.showToast({ title: '上传失败', icon: 'none' })
        }
      },
      fail: err => {
        console.error('上传头像失败:', err)
        uni.showToast({ title: '上传失败', icon: 'none' })
      },
      complete: () => {
        uni.hideLoading()
      },
    })
  }

  const deleteAvatar = () => {
    uni.showModal({
      title: '确认删除',
      content: '确定要删除当前头像吗？',
      success: res => {
        if (res.confirm) {
          const token = uni.getStorageSync(STORAGE_KEYS.token)
          if (!token) {
            uni.showToast({ title: '请先登录', icon: 'none' })
            return
          }

          uni.showLoading({ title: '删除中...' })

          request({
            url: API.avatar.delete,
            method: 'DELETE',
          })
            .then(data => {
              if (data.success) {
                user.value.avatar = ''
                // 更新本地存储的用户信息
                const userInfo = uni.getStorageSync('userInfo')
                if (userInfo) {
                  const parsedUserInfo =
                    typeof userInfo === 'string'
                      ? JSON.parse(userInfo)
                      : userInfo
                  parsedUserInfo.avatar = ''
                  uni.setStorageSync('userInfo', JSON.stringify(parsedUserInfo))
                }
                uni.showToast({ title: '头像删除成功', icon: 'success' })
              } else {
                uni.showToast({
                  title: data.message || '删除失败',
                  icon: 'none',
                })
              }
            })
            .catch(err => {
              console.error('删除头像失败:', err)
              uni.showToast({ title: '删除失败', icon: 'none' })
            })
            .finally(() => {
              uni.hideLoading()
            })
        }
      },
    })
  }

  // 统一处理侦探相关功能
  const handleDetectiveAction = () => {
    const token = uni.getStorageSync(STORAGE_KEYS.token)
    if (!token) {
      uni.showToast({ title: '请先登录', icon: 'none' })
      setTimeout(() => uni.navigateTo({ url: '/pages/auth/login' }), 300)
      return
    }

    switch (detectiveStatus.value) {
      case 'none':
      case 'rejected':
        uni.navigateTo({ url: '/pages/detective/apply' })
        break
      case 'pending':
        uni.navigateTo({ url: '/pages/detective/pending' })
        break
      case 'approved':
        goToMyOrders()
        break
    }
  }

  // 消息相关方法
  const goToMessages = () => {
    uni.navigateTo({ url: '/pages/message/list' })
  }

  const goToMyOrders = () => {
    uni.navigateTo({ url: '/pages/detective/orders' })
  }

  const loadUnreadCount = async () => {
    try {
      const token = uni.getStorageSync(STORAGE_KEYS.token)
      if (!token) return

      const res = await request({
        url: API.notifications.unreadCount,
        header: { Authorization: `Bearer ${token}` },
      })

      if (res?.success) {
        unreadCount.value = res.data?.total || 0
      }
    } catch (error) {
      console.error('[Profile] 加载未读数量失败:', error)
    }
  }
  const logout = () => {
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
    checkLoginStatus() // 立即刷新登录状态
    uni.showToast({ title: '已退出登录', icon: 'success' })
    // 不跳转页面，让用户在当前页面看到状态变化
  }
  const fetchStatus = () => {
    const token = uni.getStorageSync(STORAGE_KEYS.token)
    if (!token) return
    request({ url: API.detective.status, method: 'GET' })
      .then(res => {
        const st = res && res.data ? res.data.status : 'none'
        detectiveStatus.value = st || 'none'
      })
      .catch(() => {})
  }

  onMounted(() => {
    checkLoginStatus()
    const u = uni.getStorageSync('userInfo')
    if (u) user.value = typeof u === 'string' ? JSON.parse(u) : u
    fetchStatus()
    loadUnreadCount()
  })

  // 返回我的页面时也刷新一次，避免提交申请后状态不更新
  onShow(() => {
    checkLoginStatus()
    fetchStatus()
    loadUnreadCount()
  })
</script>

<style lang="scss" scoped>
  /* 基于设计规范的我的页面样式 */

  /* 头部卡片样式 */
  .profile-header {
    position: relative;
    padding: var(--spacing-xl) var(--spacing-md) var(--spacing-lg);
    color: white;
    overflow: hidden;
    min-height: 200px;
  }

  .profile-header__content {
    display: flex;
    align-items: flex-start;
    gap: var(--spacing-md);
    position: relative;
    z-index: 2;
  }

  /* 消息图标 */
  .message-icon-wrapper {
    position: absolute;
    top: 16px;
    right: 16px;
    width: 40px;
    height: 40px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 10;
  }

  .message-icon {
    font-size: 18px;
    line-height: 1;
  }

  .message-badge {
    position: absolute;
    top: -2px;
    right: -2px;
    background: #ff6b9d;
    border-radius: 8px;
    min-width: 16px;
    height: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 4px;
    border: 2px solid #ffffff;
  }

  .message-badge-text {
    font-size: 10px;
    color: #ffffff;
    font-weight: 600;
  }

  /* 头像样式 */
  .profile-avatar-container {
    position: relative;
  }

  .profile-avatar {
    width: 80px;
    height: 80px;
    border-radius: var(--radius-round);
    border: 3px solid rgba(255, 255, 255, 0.3);
    object-fit: cover;

    &--default {
      background: linear-gradient(135deg, #ffe0b2 0%, #ffcc80 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;
      overflow: hidden;
    }
  }

  /* 默认头像 - 可爱宠物设计 */
  .avatar-pet {
    width: 60px;
    height: 60px;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .pet-face {
    position: relative;
    width: 100%;
    height: 100%;
  }

  .pet-ear {
    position: absolute;
    width: 16px;
    height: 20px;
    background: #8d6e63;
    border-radius: 50% 50% 50% 50% / 60% 60% 40% 40%;

    &--left {
      top: 8px;
      left: 8px;
      transform: rotate(-30deg);
    }

    &--right {
      top: 8px;
      right: 8px;
      transform: rotate(30deg);
    }

    &::after {
      content: '';
      position: absolute;
      top: 4px;
      left: 50%;
      transform: translateX(-50%);
      width: 8px;
      height: 10px;
      background: #ffab91;
      border-radius: 50%;
    }
  }

  .pet-head {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 45px;
    height: 40px;
    background: #a1887f;
    border-radius: 50% 50% 45% 45%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }

  .pet-eye {
    position: absolute;
    width: 6px;
    height: 8px;
    background: #2e2e2e;
    border-radius: 50%;
    top: 12px;
    animation: pet-blink 4s ease-in-out infinite;

    &--left {
      left: 12px;
    }

    &--right {
      right: 12px;
    }

    &::after {
      content: '';
      position: absolute;
      top: 1px;
      left: 1px;
      width: 2px;
      height: 2px;
      background: white;
      border-radius: 50%;
    }
  }

  .pet-nose {
    position: absolute;
    top: 20px;
    left: 50%;
    transform: translateX(-50%);
    width: 4px;
    height: 3px;
    background: #4e342e;
    border-radius: 50% 50% 50% 50% / 40% 40% 60% 60%;
  }

  .pet-mouth {
    position: absolute;
    top: 24px;
    left: 50%;
    transform: translateX(-50%);
    width: 8px;
    height: 4px;
    border: 1px solid #4e342e;
    border-top: none;
    border-radius: 0 0 50% 50%;
    background: transparent;

    &::before {
      content: '';
      position: absolute;
      top: -1px;
      left: 3px;
      width: 1px;
      height: 3px;
      background: #4e342e;
    }
  }

  .profile-avatar__badge {
    position: absolute;
    bottom: -2px;
    right: -2px;
    background: white;
    border-radius: var(--radius-round);
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    box-shadow: var(--shadow-light);
  }

  .profile-avatar__edit {
    position: absolute;
    bottom: -2px;
    left: -2px;
    background: var(--brand-primary);
    border-radius: var(--radius-round);
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: var(--shadow-light);
    transition: all 0.2s ease;

    &:active {
      transform: scale(0.9);
    }

    .edit-icon {
      font-size: 10px;
      color: white;
    }
  }

  /* 用户信息 */
  .profile-info {
    flex: 1;
  }

  .profile-name {
    font-size: var(--font-xl);
    font-weight: 700;
    display: block;
    margin-bottom: var(--spacing-xs);
  }

  .profile-phone {
    font-size: var(--font-sm);
    opacity: 0.9;
    display: block;
    margin-bottom: var(--spacing-md);
  }

  /* 统计数据 */
  .profile-stats {
    display: flex;
    gap: var(--spacing-lg);
  }

  .profile-stat {
    text-align: center;
  }

  .profile-stat__number {
    font-size: var(--font-xl);
    font-weight: 700;
    display: block;
    margin-bottom: var(--spacing-xs);
  }

  .profile-stat__label {
    font-size: var(--font-xs);
    opacity: 0.8;
    display: block;
  }

  /* 装饰元素 */
  .decoration {
    position: absolute;
    font-size: 24px;
    opacity: 0.6;

    &--1 {
      top: 20px;
      right: 20px;
      animation: twinkle 2s ease-in-out infinite;
    }

    &--2 {
      bottom: 30px;
      right: 40px;
      animation: float 3s ease-in-out infinite 0.5s;
    }

    &--3 {
      top: 60px;
      left: 20px;
      animation: bounce 2s ease-in-out infinite 1s;
    }
  }

  /* 快捷功能区 */
  .quick-actions {
    display: flex;
    gap: var(--spacing-md);
    margin-bottom: var(--spacing-lg);
  }

  .quick-action {
    flex: 1;
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: var(--spacing-md);
    box-shadow: var(--shadow-light);
    text-align: center;
    position: relative;
    transition: all 0.2s ease;

    &:active {
      transform: scale(0.98);
      box-shadow: var(--shadow-medium);
    }
  }

  .quick-action__icon {
    font-size: 32px;
    margin-bottom: var(--spacing-sm);
    display: block;
  }

  .quick-action__title {
    font-size: var(--font-lg);
    font-weight: 600;
    color: var(--text-primary);
    display: block;
    margin-bottom: var(--spacing-xs);
  }

  .quick-action__desc {
    font-size: var(--font-sm);
    color: var(--text-tertiary);
    display: block;
  }

  .quick-action__badge {
    position: absolute;
    top: -8px;
    right: -8px;
  }

  .badge {
    padding: 4px 8px;
    border-radius: var(--radius-xl);
    font-size: var(--font-xs);
    font-weight: 500;

    &--warning {
      background: var(--color-warning);
      color: white;
    }

    &--success {
      background: var(--color-success);
      color: white;
    }
  }

  /* 登录提示区域 */
  .login-prompt {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: var(--spacing-xl);
    margin-bottom: var(--spacing-lg);
    text-align: center;
    box-shadow: var(--shadow-light);
  }

  .login-prompt__content {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .login-prompt__icon {
    font-size: 48px;
    display: block;
    margin-bottom: var(--spacing-md);
    opacity: 0.8;
  }

  .login-prompt__title {
    font-size: var(--font-lg);
    font-weight: 600;
    color: var(--text-primary);
    display: block;
    margin-bottom: var(--spacing-sm);
  }

  .login-prompt__desc {
    font-size: var(--font-sm);
    color: var(--text-secondary);
    display: block;
  }

  /* 菜单区域 */
  .menu-section {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-light);
    overflow: hidden;
    margin-bottom: var(--spacing-lg);
  }

  .menu-section__title {
    display: flex;
    align-items: center;
    padding: var(--spacing-md);
    background: var(--bg-tertiary);
    border-bottom: 1px solid var(--border-light);
  }

  .menu-section__icon {
    font-size: var(--font-lg);
    margin-right: var(--spacing-sm);
  }

  .menu-section__text {
    font-size: var(--font-lg);
    font-weight: 600;
    color: var(--text-primary);
  }

  .menu-list {
    background: var(--bg-primary);
  }

  .menu-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: var(--spacing-md);
    border-bottom: 1px solid var(--border-light);
    transition: background 0.2s ease;

    &:last-child {
      border-bottom: none;
    }

    &:active {
      background: var(--bg-tertiary);
    }
  }

  .menu-item__left {
    display: flex;
    align-items: center;
  }

  .menu-item__icon {
    font-size: var(--font-lg);
    margin-right: var(--spacing-md);
  }

  .menu-item__title {
    font-size: var(--font-base);
    color: var(--text-primary);
  }

  .menu-item__arrow {
    font-size: var(--font-xl);
    color: var(--text-tertiary);
    transform: rotate(0deg);
    transition: transform 0.2s ease;
  }

  .menu-item__right {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .menu-badge {
    background: #ff6b9d;
    border-radius: 8px;
    min-width: 16px;
    height: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 4px;
  }

  .menu-badge-text {
    font-size: 10px;
    color: #ffffff;
    font-weight: 600;
  }

  /* 认证操作区域 */
  .auth-section {
    margin-bottom: var(--spacing-xl);
  }

  /* 弹窗样式 */
  .popup-content {
    width: 300px;
    max-width: 90vw;
  }

  .popup-header {
    padding: var(--spacing-lg) var(--spacing-md) var(--spacing-md);
    text-align: center;
    border-bottom: 1px solid var(--border-light);
  }

  .popup-title {
    font-size: var(--font-xl);
    font-weight: 600;
    color: var(--text-primary);
  }

  .popup-body {
    padding: var(--spacing-md);
  }

  /* 联系信息样式 */
  .contact-item {
    display: flex;
    justify-content: space-between;
    padding: var(--spacing-sm) 0;
    border-bottom: 1px solid var(--border-light);

    &:last-child {
      border-bottom: none;
    }
  }

  .contact-label {
    font-size: var(--font-base);
    color: var(--text-secondary);
  }

  .contact-value {
    font-size: var(--font-base);
    color: var(--text-primary);
    font-weight: 500;
  }

  /* 关于我们文本 */
  .about-text {
    font-size: var(--font-base);
    color: var(--text-secondary);
    line-height: var(--line-height-loose);
    text-align: center;
  }

  /* 动画效果 */
  @keyframes twinkle {
    0%,
    100% {
      opacity: 0.6;
      transform: scale(1);
    }
    50% {
      opacity: 1;
      transform: scale(1.2);
    }
  }

  @keyframes float {
    0%,
    100% {
      transform: translateY(0px);
    }
    50% {
      transform: translateY(-8px);
    }
  }

  @keyframes bounce {
    0%,
    20%,
    50%,
    80%,
    100% {
      transform: translateY(0);
    }
    40% {
      transform: translateY(-10px);
    }
    60% {
      transform: translateY(-5px);
    }
  }

  @keyframes pet-blink {
    0%,
    90%,
    100% {
      height: 8px;
    }
    95% {
      height: 1px;
    }
  }

  /* 响应式适配 */
  @media (max-width: 375px) {
    .profile-header {
      padding: var(--spacing-lg) var(--spacing-sm);
    }

    .profile-avatar {
      width: 64px;
      height: 64px;
    }

    .quick-actions {
      flex-direction: column;
    }

    .profile-stats {
      gap: var(--spacing-md);
    }
  }
</style>
