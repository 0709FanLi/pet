<template>
  <view class="login-page">
    <view class="header">
      <text class="emoji">🐾</text>
      <view class="texts">
        <text class="title">登录 / 注册</text>
        <text class="sub">手机号 + 验证码，无需密码</text>
      </view>
    </view>

    <u-form :model="authForm" class="form-wrap" :labelWidth="80">
      <view class="section">
        <u-form-item label="手机号" prop="phone">
          <u-input
            v-model="authForm.phone"
            type="number"
            placeholder="请输入手机号"
            maxlength="11"
            @blur="validatePhone"
            @input="clearPhoneError"
          />
        </u-form-item>
        <view class="error" v-if="phoneError">{{ phoneError }}</view>

        <u-form-item label="验证码" prop="verificationCode">
          <view class="row">
            <u-input
              class="flex1"
              v-model="authForm.verificationCode"
              type="number"
              maxlength="4"
              placeholder="请输入4位验证码"
              @blur="validateCode"
              @input="clearCodeError"
            />
            <u-button
              class="send"
              size="small"
              type="primary"
              :plain="countdown > 0"
              :disabled="!canSendCode || countdown > 0"
              @click="sendVerificationCode"
            >
              {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
            </u-button>
          </view>
        </u-form-item>
        <view class="error" v-if="codeError">{{ codeError }}</view>
      </view>

      <view class="section">
        <u-form-item>
          <u-checkbox
            v-model="agreed"
            label="我已阅读并同意《用户协议》《隐私政策》"
          />
        </u-form-item>
      </view>
    </u-form>

    <view class="bottom">
      <u-button
        type="primary"
        :disabled="!canLogin"
        :loading="loading"
        @click="handleAuth"
        :customStyle="primaryStyle"
        >登录 / 注册</u-button
      >
    </view>
  </view>
</template>

<script setup>
  import { ref, reactive, computed, onUnmounted } from 'vue'
  import { request } from '@/common/request'
  import { STORAGE_KEYS } from '@/common/config'

  const authForm = reactive({ phone: '', verificationCode: '' })
  const loading = ref(false)
  const countdown = ref(0)
  let timer = null

  const phoneError = ref('')
  const codeError = ref('')

  const canSendCode = computed(
    () => !!authForm.phone && /^1[3-9]\d{9}$/.test(authForm.phone)
  )
  const canLogin = computed(() => agreed.value && !loading.value)
  const agreed = ref(true)

  const validatePhone = () => {
    if (!authForm.phone) {
      phoneError.value = '请输入手机号'
      return false
    }
    if (!/^1[3-9]\d{9}$/.test(authForm.phone)) {
      phoneError.value = '请输入正确的手机号格式'
      return false
    }
    phoneError.value = ''
    return true
  }
  const validateCode = () => {
    if (!authForm.verificationCode) {
      codeError.value = '请输入验证码'
      return false
    }
    if (!/^\d{4}$/.test(authForm.verificationCode)) {
      codeError.value = '请输入4位数字验证码'
      return false
    }
    codeError.value = ''
    return true
  }
  const clearPhoneError = () => (phoneError.value = '')
  const clearCodeError = () => (codeError.value = '')

  const sendVerificationCode = async () => {
    if (!validatePhone()) return
    try {
      const payload = { phone: authForm.phone, type: 'auth' }
      console.log('[auth] sendCode payload:', payload)
      const res = await request({
        url: '/api/users/send-code',
        method: 'POST',
        data: payload,
      })
      console.log('[auth] sendCode resp:', res)
      uni.showToast({ title: '验证码已发送', icon: 'success' })
      countdown.value = 10
      timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer)
          timer = null
        }
      }, 1000)
      // 便于联调：后端固定为 1111
      authForm.verificationCode = '1111'
    } catch (e) {
      console.error('[auth] sendCode error:', e)
      uni.showToast({ title: '发送失败', icon: 'none' })
    }
  }

  const handleAuth = async () => {
    if (!agreed.value) {
      uni.showToast({ title: '请先同意相关协议', icon: 'none' })
      return
    }
    if (!validatePhone() || !validateCode()) return
    try {
      loading.value = true
      const payload = {
        phone: authForm.phone,
        verificationCode: authForm.verificationCode,
      }
      console.log('[auth] phoneLogin payload:', payload)
      const res = await request({
        url: '/api/users/phone-login',
        method: 'POST',
        data: payload,
      })
      console.log('[auth] phoneLogin resp:', res)
      const token = res?.data?.token || res?.token
      const user = res?.data || {}
      if (token) {
        uni.setStorageSync(STORAGE_KEYS.token, token)
        uni.setStorageSync(STORAGE_KEYS.userInfo, user)
        uni.showToast({ title: '登录成功', icon: 'success' })
        setTimeout(() => uni.switchTab({ url: '/pages/home/home' }), 500)
      } else {
        throw new Error('no token')
      }
    } catch (e) {
      console.error('[auth] phoneLogin error:', e)
      const msg =
        (e && (e.message || e.msg || e.error || e.reason)) || '登录失败'
      uni.showToast({ title: String(msg), icon: 'none' })
    } finally {
      loading.value = false
    }
  }

  onUnmounted(() => {
    if (timer) clearInterval(timer)
  })

  const primaryStyle = {
    background:
      'linear-gradient(135deg, var(--brand-gradient-start), var(--brand-gradient-end))',
    border: 'none',
  }
</script>

<style scoped>
  .login-page {
    background: #f7f8fa;
    min-height: 100vh;
    padding-bottom: 80px;
  }
  .header {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px 16px 8px;
    color: #fff;
    background: linear-gradient(
      135deg,
      var(--brand-gradient-start),
      var(--brand-gradient-end)
    );
  }
  .emoji {
    font-size: 30px;
  }
  .title {
    font-size: 18px;
    font-weight: 700;
    display: block;
  }
  .sub {
    font-size: 12px;
    opacity: 0.9;
  }
  .form-wrap {
    padding: 8px 12px;
  }
  .section {
    background: #fff;
    border-radius: 12px;
    padding: 8px 12px 2px;
    margin: 10px 0;
    box-shadow: 0 4px 16px rgba(91, 143, 249, 0.08);
  }
  .row {
    display: flex;
    gap: 8px;
    align-items: center;
  }
  .flex1 {
    flex: 1;
  }
  .send {
    min-width: 96px;
    max-width: 100px;
  }
  .error {
    color: #fa5151;
    font-size: 12px;
    padding: 0 2px 8px;
  }
  .bottom {
    position: fixed;
    left: 0;
    right: 0;
    bottom: env(safe-area-inset-bottom, 0);
    padding: 10px 14px;
    background: #fff;
    box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.06);
  }
</style>
