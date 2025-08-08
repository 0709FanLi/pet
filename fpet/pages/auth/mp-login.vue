<template>
  <view class="mp-login-page">
    <view class="title">一键登录</view>

    <!-- 微信小程序 -->
    <!-- #ifdef MP-WEIXIN -->
    <button class="btn wechat" @click="wxOneTap">微信一键登录</button>
    <view class="tip"
      >将调用 wx.login 获取 code，后端换取 openid 并签发令牌</view
    >
    <!-- #endif -->

    <!-- 抖音小程序（头条系） -->
    <!-- #ifdef MP-TOUTIAO -->
    <button class="btn bytedance" @click="ttOneTap">抖音一键登录</button>
    <view class="tip"
      >将调用 tt.login 获取 code，后端换取 open_id 并签发令牌</view
    >
    <!-- #endif -->

    <!-- 其他平台占位 -->
    <!-- #ifndef MP-WEIXIN || MP-TOUTIAO -->
    <view class="tip">当前平台不支持一键登录示例</view>
    <!-- #endif -->
  </view>
</template>

<script setup>
  import { request } from '@/common/request'
  import { STORAGE_KEYS } from '@/common/config'

  // 微信一键登录示例
  const wxOneTap = async () => {
    // #ifdef MP-WEIXIN
    wx.login({
      success: async res => {
        try {
          const resp = await request({
            url: '/api/mp/wechat/login',
            method: 'POST',
            data: { code: res.code },
          })
          const token = resp?.data?.token
          if (token) {
            uni.setStorageSync(STORAGE_KEYS.token, token)
            uni.setStorageSync('userInfo', resp?.data?.user || {})
            uni.showToast({ title: '登录成功' })
            setTimeout(() => uni.switchTab({ url: '/pages/home/home' }), 300)
          } else throw new Error('no token')
        } catch (e) {
          uni.showToast({ title: '登录失败(后端未对接)', icon: 'none' })
        }
      },
      fail: () => uni.showToast({ title: 'wx.login 失败', icon: 'none' }),
    })
    // #endif
  }

  // 抖音一键登录示例
  const ttOneTap = async () => {
    // #ifdef MP-TOUTIAO
    tt.login({
      success: async res => {
        try {
          const resp = await request({
            url: '/api/mp/bytedance/login',
            method: 'POST',
            data: { code: res.code },
          })
          const token = resp?.data?.token
          if (token) {
            uni.setStorageSync(STORAGE_KEYS.token, token)
            uni.setStorageSync('userInfo', resp?.data?.user || {})
            uni.showToast({ title: '登录成功' })
            setTimeout(() => uni.switchTab({ url: '/pages/home/home' }), 300)
          } else throw new Error('no token')
        } catch (e) {
          uni.showToast({ title: '登录失败(后端未对接)', icon: 'none' })
        }
      },
      fail: () => uni.showToast({ title: 'tt.login 失败', icon: 'none' }),
    })
    // #endif
  }
</script>

<style scoped>
  .mp-login-page {
    padding: 24px;
  }
  .title {
    font-size: 20px;
    font-weight: 700;
    margin-bottom: 16px;
  }
  .btn {
    width: 100%;
    height: 44px;
    border-radius: 8px;
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 10px 0;
  }
  .wechat {
    background: #07c160;
  }
  .bytedance {
    background: #000;
  }
  .tip {
    color: #888;
    font-size: 12px;
    margin-top: 6px;
  }
</style>
