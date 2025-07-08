<template>
  <div class="mobile-login-page">
    <!-- 顶部状态栏占位 -->
    <div class="status-bar-placeholder"></div>
    
    <!-- 主要内容区域 -->
    <div class="login-content">
      <!-- Logo区域 -->
      <div class="logo-section">
        <l-img src="/static/logo3.png" w="80px" h="80px" />
        <h1 class="app-title">{{ $t('appName') }}</h1>
      </div>

      <!-- 登录表单 -->
      <div class="login-form">
        <!-- 手机号输入 -->
        <div class="form-group">
          <div class="input-row">
            <span class="input-label">手机号</span>
            <div class="input-wrapper" :class="{ 'error': phoneError }">
              <input
                type="tel"
                v-model="authForm.phone"
                placeholder="请输入手机号"
                class="mobile-input"
                @blur="validatePhone"
                @input="clearPhoneError"
                maxlength="11"
              />
            </div>
          </div>
          <div class="error-text" v-if="phoneError">{{ phoneError }}</div>
        </div>

        <!-- 验证码输入 -->
        <div class="form-group">
          <div class="input-row">
            <span class="input-label">验证码</span>
            <div class="code-input-wrapper">
              <div class="input-wrapper" :class="{ 'error': codeError }">
                <input
                  type="text"
                  v-model="authForm.verificationCode"
                  placeholder="请输入验证码"
                  class="mobile-input code-input"
                  @blur="validateCode"
                  @input="clearCodeError"
                  maxlength="4"
                />
              </div>
              <button 
                class="send-code-button"
                :class="{ 'disabled': !canSendCode || countdown > 0 }"
                @click="sendVerificationCode"
                :disabled="!canSendCode || countdown > 0"
              >
                {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
              </button>
            </div>
          </div>
          <div class="error-text" v-if="codeError">{{ codeError }}</div>
        </div>

        <!-- 登录按钮 -->
        <div class="form-group">
          <l-button
            text="登录/注册"
            :loading="loading"
            :disabled="loading"
            @onClick="handleAuth"
            class="login-button"
          />
        </div>

        <!-- 协议提示 -->
        <div class="agreement-section">
          <p class="agreement-text">
            登录即表示同意
            <a href="#" class="agreement-link" @click.prevent="viewTerms">《用户协议》</a>
            和
            <a href="#" class="agreement-link" @click.prevent="viewPrivacy">《隐私政策》</a>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import LImg from '@/components/l-img.vue'
import LButton from '@/components/l-button.vue'
import { UserAuthApi } from '@/api/user-auth-api'

const { t } = useI18n()
const router = useRouter()

// 表单数据
const authForm = reactive({
  phone: '',
  verificationCode: ''
})

// 状态管理
const loading = ref(false)
const countdown = ref(0)
let countdownTimer = null

// 错误信息
const phoneError = ref('')
const codeError = ref('')

// 计算属性
const canSendCode = computed(() => {
  return authForm.phone && /^1[3-9]\d{9}$/.test(authForm.phone)
})

// 验证手机号
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

// 验证验证码
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

// 清除错误信息
const clearPhoneError = () => {
  phoneError.value = ''
}

const clearCodeError = () => {
  codeError.value = ''
}

// 发送验证码
const sendVerificationCode = async () => {
  if (!validatePhone()) {
    return
  }

  try {
    const request = {
      phone: authForm.phone,
      type: 'auth'
    }
    
    const response = await UserAuthApi.sendCode(request)
    
    if (response.code === 200) {
      ElMessage.success('验证码发送成功')
      
      // 开始倒计时
      countdown.value = 60
      countdownTimer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(countdownTimer)
          countdownTimer = null
        }
      }, 1000)
    } else {
      ElMessage.error(response.message || '验证码发送失败')
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
    ElMessage.error('验证码发送失败，请稍后重试')
  }
}

// 登录/注册处理
const handleAuth = async () => {
  // 验证所有字段
  const isPhoneValid = validatePhone()
  const isCodeValid = validateCode()

  if (!isPhoneValid || !isCodeValid) {
    return
  }

  try {
    loading.value = true
    
    // 调用手机验证码登录API
    const request = {
      phone: authForm.phone,
      verificationCode: authForm.verificationCode
    }
    
    const response = await UserAuthApi.phoneLogin(request)
    
    if (response.code === 200) {
      ElMessage.success('登录成功')
      
      // 保存用户信息和token到本地存储
      if (response.data?.token) {
        localStorage.setItem('token', response.data.token)
        localStorage.setItem('userInfo', JSON.stringify(response.data))
      }
      
      // 登录成功后跳转到首页
      router.push('/mobile/home')
    } else {
      ElMessage.error(response.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 查看用户协议
const viewTerms = () => {
  router.push('/mobile/terms')
}

// 查看隐私政策
const viewPrivacy = () => {
  router.push('/mobile/privacy')
}

// 组件卸载时清理定时器
onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style scoped>
.mobile-login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  position: relative;
}

.status-bar-placeholder {
  height: env(safe-area-inset-top, 20px);
  background: transparent;
}

.login-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 40px 24px 40px 24px;
  padding-bottom: max(40px, env(safe-area-inset-bottom));
}

/* Logo区域 */
.logo-section {
  text-align: center;
  margin-bottom: 60px;
  margin-top: 40px;
}

.app-title {
  font-size: 32px;
  font-weight: 700;
  color: #FFFFFF;
  margin: 20px 0 0 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

/* 登录表单 */
.login-form {
  flex: 1;
  max-width: 400px;
  margin: 0 auto;
  width: 100%;
}

.form-group {
  margin-bottom: 24px;
}

/* 输入行布局 */
.input-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.input-label {
  font-size: 16px;
  font-weight: 500;
  color: #FFFFFF;
  min-width: 60px;
  flex-shrink: 0;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

/* 输入框样式 */
.input-wrapper {
  flex: 1;
  position: relative;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.input-wrapper:focus-within {
  border-color: #87CEEB;
  box-shadow: 0 0 0 3px rgba(135, 206, 235, 0.1);
}

.send-code-button {
  color: #87CEEB;
}

.send-code-button:hover:not(.disabled) {
  border-color: #87CEEB;
  box-shadow: 0 4px 8px rgba(135, 206, 235, 0.2);
}

.input-wrapper.error {
  border-color: #FF5252;
  background: rgba(255, 255, 255, 0.95);
}

.mobile-input {
  width: 100%;
  height: 52px;
  padding: 0 16px;
  font-size: 16px;
  color: #333333;
  background: transparent;
  border: none;
  border-radius: 12px;
  box-sizing: border-box;
}

.mobile-input:focus {
  outline: none;
}

.mobile-input::placeholder {
  color: #999999;
}

/* 验证码输入特殊布局 */
.code-input-wrapper {
  flex: 1;
  display: flex;
  gap: 12px;
  align-items: center;
}

.code-input {
  flex: 1;
}

.send-code-button {
  height: 52px;
  padding: 0 20px;
  background: rgba(255, 255, 255, 0.9);
  border: 2px solid transparent;
  border-radius: 12px;
  color: #667eea;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  min-width: 100px;
}

.send-code-button:hover:not(.disabled) {
  background: #FFFFFF;
  border-color: #667eea;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(102, 126, 234, 0.2);
}

.send-code-button.disabled {
  background: rgba(255, 255, 255, 0.5);
  color: #999999;
  cursor: not-allowed;
}

/* 错误信息 */
.error-text {
  font-size: 14px;
  color: #FFE0E0;
  margin-left: 72px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

/* 登录按钮 */
.login-button {
  width: 100%;
  height: 56px;
  margin-top: 32px;
  background: linear-gradient(45deg, #FF6B6B, #FF8E8E);
  border: none;
  border-radius: 16px;
  font-size: 18px;
  font-weight: 600;
  color: #FFFFFF;
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.3);
  transition: all 0.3s ease;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 107, 107, 0.4);
}

.login-button:active {
  transform: translateY(0);
}

/* 协议区域 */
.agreement-section {
  margin-top: 40px;
  text-align: center;
}

.agreement-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.5;
  margin: 0;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.agreement-link {
  color: #FFE0E0;
  text-decoration: none;
  font-weight: 500;
}

.agreement-link:hover {
  text-decoration: underline;
}

/* 响应式调整 */
@media (max-width: 375px) {
  .login-content {
    padding: 30px 20px;
  }
  
  .input-label {
    min-width: 50px;
    font-size: 15px;
  }
  
  .mobile-input {
    height: 48px;
    font-size: 15px;
  }
  
  .send-code-button {
    height: 48px;
    min-width: 90px;
    font-size: 13px;
  }
  
  .error-text {
    margin-left: 62px;
  }
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-form {
  animation: fadeInUp 0.6s ease-out;
}

.logo-section {
  animation: fadeInUp 0.6s ease-out 0.2s both;
}
</style>