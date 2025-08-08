<template>
  <view class="profile-page">
    <view class="header">
      <image
        class="avatar"
        :src="user.avatar || '/static/images/default-avatar.png'"
      />
      <view class="info">
        <text class="name">{{ user.username || '未设置用户名' }}</text>
        <text class="phone">{{ user.phoneNumber || '未绑定手机号' }}</text>
      </view>
    </view>
    <u-cell-group>
      <u-cell title="我的发布" isLink @click="goMyPosts" />
      <u-cell title="帮助与反馈" isLink @click="showHelp = true" />
      <u-cell title="关于我们" isLink @click="showAbout = true" />
    </u-cell-group>
    <view class="logout">
      <u-button type="error" plain @click="logout">退出登录</u-button>
    </view>
    <u-popup :show="showHelp" @close="showHelp = false"
      ><view class="popup"><text>客服：400-123-4567</text></view></u-popup
    >
    <u-popup :show="showAbout" @close="showAbout = false"
      ><view class="popup"><text>© 宠物找回平台</text></view></u-popup
    >
  </view>
</template>

<script setup>
  import { ref, onMounted } from 'vue'
  const user = ref({ username: '', phoneNumber: '', avatar: '' })
  const showHelp = ref(false)
  const showAbout = ref(false)
  const goMyPosts = () => uni.navigateTo({ url: '/pages/my/lost-pets' })
  const logout = () => {
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
    uni.reLaunch({ url: '/pages/home/home' })
  }
  onMounted(() => {
    const u = uni.getStorageSync('userInfo')
    if (u) user.value = typeof u === 'string' ? JSON.parse(u) : u
  })
</script>

<style lang="scss" scoped>
  .profile-page {
    min-height: 100vh;
    background: #f5f7fa;
  }
  .header {
    display: flex;
    align-items: center;
    padding: 16px;
    background: #fff;
    margin-bottom: 10px;
  }
  .avatar {
    width: 64px;
    height: 64px;
    border-radius: 50%;
    margin-right: 12px;
  }
  .name {
    font-size: 18px;
    font-weight: 600;
    display: block;
  }
  .phone {
    font-size: 14px;
    color: #999;
  }
  .logout {
    padding: 16px;
  }
  .popup {
    padding: 24px;
  }
</style>
