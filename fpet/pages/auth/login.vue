<template>
  <view class="login-page">
    <u-form :model="form">
      <u-form-item label="账号">
        <u-input v-model="form.username" placeholder="手机号/用户名" />
      </u-form-item>
      <u-form-item label="密码">
        <u-input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
        />
      </u-form-item>
    </u-form>
    <u-button type="primary" @click="login">登录</u-button>
  </view>
</template>

<script setup>
  import { ref } from 'vue'
  import { request } from '@/common/request'
  import { STORAGE_KEYS } from '@/common/config'
  const form = ref({ username: '', password: '' })
  const login = async () => {
    try {
      const res = await request({
        url: '/api/users/login',
        method: 'POST',
        data: form.value,
      })
      const token = res?.data?.token || res?.token
      if (token) {
        uni.setStorageSync(STORAGE_KEYS.token, token)
        uni.setStorageSync('userInfo', res?.data?.user || {})
        uni.showToast({ title: '登录成功' })
        setTimeout(() => uni.switchTab({ url: '/pages/home/home' }), 500)
      } else throw new Error('no token')
    } catch {
      uni.showToast({ title: '登录失败', icon: 'none' })
    }
  }
</script>

<style scoped>
  .login-page {
    padding: 16px;
  }
</style>
