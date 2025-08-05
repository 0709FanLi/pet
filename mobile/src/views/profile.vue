<template>
  <div class="profile-page">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="nav-left" @click="goBack">
        <van-icon name="arrow-left" size="20" color="#333" />
      </div>
      <div class="nav-title">个人中心</div>
      <div class="nav-right"></div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <van-loading size="24px" color="#667eea" />
      <div class="loading-text">加载中...</div>
    </div>

    <!-- 主要内容 -->
    <div v-else class="profile-content">
      <!-- 用户信息区域 -->
      <div class="user-section">
        <div class="user-info">
          <div class="avatar-container">
            <img
              :src="userInfo.avatar || '/static/images/default-avatar.png'"
              :alt="userInfo.username"
              class="user-avatar"
            />
          </div>
          <div class="user-details">
            <h3 class="username">{{ userInfo.username || '未设置用户名' }}</h3>
            <p class="phone-number">
              {{ userInfo.phoneNumber || '未绑定手机号' }}
            </p>
            <van-button
              size="small"
              type="primary"
              plain
              class="edit-btn"
              @click="editProfile"
            >
              编辑
            </van-button>
          </div>
        </div>
      </div>

      <!-- 功能菜单 -->
      <div class="menu-section">
        <van-cell-group class="menu-group">
          <van-cell
            title="我的发布"
            icon="orders-o"
            is-link
            @click="goToMyPosts"
            class="menu-item"
          >
            <template #right-icon>
              <span class="post-count">{{ postCount }}</span>
              <van-icon name="arrow" />
            </template>
          </van-cell>

          <van-cell
            title="帮助与反馈"
            icon="service-o"
            is-link
            @click="showHelp"
            class="menu-item"
          />

          <van-cell
            title="关于我们"
            icon="info-o"
            is-link
            @click="showAbout"
            class="menu-item"
          />
        </van-cell-group>
      </div>

      <!-- 退出登录 -->
      <div class="logout-section">
        <van-button block class="logout-btn" @click="handleLogout">
          退出登录
        </van-button>
      </div>
    </div>

    <!-- 底部导航栏 -->
    <div class="bottom-nav">
      <div class="nav-item" @click="goToHome">
        <van-icon name="home-o" size="20" />
        <span>首页</span>
      </div>
      <div class="nav-item" @click="goToMyPosts">
        <van-icon name="orders-o" size="20" />
        <span>我的发布</span>
      </div>
      <div class="nav-item active">
        <van-icon name="user-o" size="20" />
        <span>个人中心</span>
      </div>
    </div>

    <!-- 编辑个人信息弹窗 -->
    <van-popup
      v-model="showEditDialog"
      position="bottom"
      :style="{ height: '60%' }"
    >
      <div class="edit-dialog">
        <div class="dialog-header">
          <van-button
            type="default"
            size="small"
            @click="showEditDialog = false"
            >取消</van-button
          >
          <h3>编辑个人信息</h3>
          <van-button type="primary" size="small" @click="saveProfile"
            >保存</van-button
          >
        </div>

        <div class="dialog-content">
          <van-cell-group>
            <van-field
              v-model="editForm.username"
              label="用户名"
              placeholder="请输入用户名"
              clearable
            />
            <van-field
              v-model="editForm.email"
              label="邮箱"
              placeholder="请输入邮箱地址"
              clearable
            />
          </van-cell-group>
        </div>
      </div>
    </van-popup>

    <!-- 帮助弹窗 -->
    <van-popup
      v-model="showHelpDialog"
      position="center"
      :style="{ width: '90%', padding: '20px' }"
    >
      <div class="help-dialog">
        <h3>帮助与反馈</h3>
        <div class="help-content">
          <p><strong>如何发布丢失信息？</strong></p>
          <p>1. 点击首页"发布丢失信息"按钮</p>
          <p>2. 上传宠物照片，填写详细信息</p>
          <p>3. 设置悬赏金额，完成支付</p>

          <p><strong>需要帮助？</strong></p>
          <p>客服电话：400-123-4567</p>
          <p>客服微信：pet-rescue-help</p>
        </div>
        <van-button block type="primary" @click="showHelpDialog = false"
          >知道了</van-button
        >
      </div>
    </van-popup>

    <!-- 关于我们弹窗 -->
    <van-popup
      v-model="showAboutDialog"
      position="center"
      :style="{ width: '90%', padding: '20px' }"
    >
      <div class="about-dialog">
        <h3>关于我们</h3>
        <div class="about-content">
          <p>宠物找回平台致力于帮助走失的宠物回到主人身边。</p>
          <p>我们提供便捷的信息发布服务，让更多人能看到您的寻宠信息。</p>
          <p>版本：v1.0.0</p>
          <p>© 2023 宠物找回平台</p>
        </div>
        <van-button block type="primary" @click="showAboutDialog = false"
          >知道了</van-button
        >
      </div>
    </van-popup>
  </div>
</template>

<script setup>
  import { ref, reactive, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import {
    Icon,
    Loading,
    Button,
    CellGroup,
    Cell,
    Popup,
    Field,
    showToast,
    showSuccessToast,
    showConfirmDialog,
  } from 'vant'
  import axios from 'axios'
  import { removeToken, getAccessToken } from '@/utils/auth'

  const router = useRouter()

  // 数据状态
  const loading = ref(true)
  const userInfo = ref({
    username: '',
    phoneNumber: '',
    email: '',
    avatar: '',
  })
  const postCount = ref(0)

  // 弹窗状态
  const showEditDialog = ref(false)
  const showHelpDialog = ref(false)
  const showAboutDialog = ref(false)

  // 编辑表单
  const editForm = reactive({
    username: '',
    email: '',
  })

  // 返回上一页
  const goBack = () => {
    router.go(-1)
  }

  // 导航方法
  const goToHome = () => {
    router.push('/')
  }

  const goToMyPosts = () => {
    router.push('/my-lost-pets')
  }

  // 编辑个人信息
  const editProfile = () => {
    editForm.username = userInfo.value.username || ''
    editForm.email = userInfo.value.email || ''
    showEditDialog.value = true
  }

  // 保存个人信息
  const saveProfile = async () => {
    try {
      // TODO: 调用后端API更新用户信息
      // const response = await axios.put('/api/users/profile', editForm)

      // 暂时更新本地数据
      userInfo.value.username = editForm.username
      userInfo.value.email = editForm.email

      showSuccessToast('保存成功')
      showEditDialog.value = false
    } catch (error) {
      console.error('Failed to update profile:', error)
      showToast('保存失败，请重试')
    }
  }

  // 显示帮助
  const showHelp = () => {
    showHelpDialog.value = true
  }

  // 显示关于我们
  const showAbout = () => {
    showAboutDialog.value = true
  }

  // 退出登录
  const handleLogout = () => {
    showConfirmDialog({
      title: '确认退出',
      message: '您确定要退出登录吗？',
    })
      .then(() => {
        // 清除token
        removeToken()
        // 清除用户信息
        localStorage.removeItem('userInfo')

        showSuccessToast('退出成功')

        // 跳转到登录页
        setTimeout(() => {
          router.replace('/login')
        }, 1000)
      })
      .catch(() => {
        // 用户取消退出
      })
  }

  // 加载用户信息
  const loadUserInfo = async () => {
    try {
      // 从localStorage获取用户信息
      const savedUserInfo = localStorage.getItem('userInfo')
      if (savedUserInfo) {
        const userData = JSON.parse(savedUserInfo)
        userInfo.value = {
          username: userData.username || userData.phoneNumber,
          phoneNumber: userData.phoneNumber,
          email: userData.email,
          avatar: userData.avatar,
        }
      }

      // TODO: 从后端获取最新用户信息
      // const response = await axios.get('/api/users/profile')
      // userInfo.value = response.data
    } catch (error) {
      console.error('Failed to load user info:', error)
    }
  }

  // 加载发布数量
  const loadPostCount = async () => {
    try {
      // TODO: 调用后端API获取用户发布数量
      // const response = await axios.get('/api/lost-pets/my-count')
      // postCount.value = response.data.count

      // 暂时使用模拟数据
      postCount.value = 3
    } catch (error) {
      console.error('Failed to load post count:', error)
      postCount.value = 0
    }
  }

  // 页面初始化
  onMounted(async () => {
    loading.value = true

    try {
      await Promise.all([loadUserInfo(), loadPostCount()])
    } finally {
      loading.value = false
    }
  })
</script>

<style scoped>
  .profile-page {
    min-height: 100vh;
    background: #f5f7fa;
    display: flex;
    flex-direction: column;
  }

  /* 顶部导航 */
  .top-nav {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 1000;
    height: 50px;
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 16px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  }

  .nav-left,
  .nav-right {
    width: 40px;
    display: flex;
    justify-content: center;
  }

  .nav-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
  }

  /* 加载状态 */
  .loading-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding-top: 50px;
  }

  .loading-text {
    margin-top: 12px;
    color: #666;
    font-size: 14px;
  }

  /* 主要内容 */
  .profile-content {
    flex: 1;
    padding-top: 50px;
    padding-bottom: 80px;
  }

  /* 用户信息区域 */
  .user-section {
    background: #fff;
    margin-bottom: 10px;
    padding: 20px 16px;
  }

  .user-info {
    display: flex;
    align-items: center;
  }

  .avatar-container {
    margin-right: 16px;
  }

  .user-avatar {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #f0f0f0;
  }

  .user-details {
    flex: 1;
  }

  .username {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin: 0 0 4px 0;
  }

  .phone-number {
    font-size: 14px;
    color: #666;
    margin: 0 0 8px 0;
  }

  .edit-btn {
    width: 60px;
    height: 28px;
  }

  /* 功能菜单 */
  .menu-section {
    background: #fff;
    margin-bottom: 10px;
  }

  .menu-group {
    background: #fff;
  }

  .menu-item {
    padding: 16px;
    border-bottom: 1px solid #f0f0f0;
  }

  .menu-item:last-child {
    border-bottom: none;
  }

  .post-count {
    background: #ff6b6b;
    color: #fff;
    font-size: 12px;
    padding: 2px 6px;
    border-radius: 10px;
    margin-right: 8px;
    min-width: 20px;
    text-align: center;
  }

  /* 退出登录 */
  .logout-section {
    padding: 20px 16px;
  }

  .logout-btn {
    background: #fff;
    color: #ff4757;
    border: 1px solid #ff4757;
    height: 44px;
    font-size: 16px;
    border-radius: 8px;
  }

  .logout-btn:active {
    background: #ff4757;
    color: #fff;
  }

  /* 底部导航 */
  .bottom-nav {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 60px;
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: space-around;
    border-top: 1px solid #f0f0f0;
    z-index: 1000;
  }

  .nav-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #666;
    font-size: 12px;
    cursor: pointer;
    transition: color 0.3s;
  }

  .nav-item.active {
    color: #1989fa;
  }

  .nav-item span {
    margin-top: 4px;
  }

  /* 弹窗样式 */
  .edit-dialog,
  .help-dialog,
  .about-dialog {
    background: #fff;
    border-radius: 12px 12px 0 0;
  }

  .dialog-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px;
    border-bottom: 1px solid #f0f0f0;
  }

  .dialog-header h3 {
    margin: 0;
    font-size: 16px;
    font-weight: 600;
    color: #333;
  }

  .dialog-content {
    padding: 16px;
  }

  .help-dialog h3,
  .about-dialog h3 {
    text-align: center;
    margin-bottom: 20px;
    font-size: 18px;
    color: #333;
  }

  .help-content,
  .about-content {
    margin-bottom: 20px;
    line-height: 1.6;
    color: #666;
  }

  .help-content p,
  .about-content p {
    margin: 8px 0;
  }

  .help-content strong {
    color: #333;
  }
</style>
