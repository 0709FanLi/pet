<template>
  <div class="auth-page">
    <!-- 登录/注册表单 -->
    <div class="auth-container">
      <div class="center-container">
        <div class="logo-large">
          <l-img src="/static/logo3.png" w="60px" h="60px" />
        </div>

        <h1 class="page-title">管理员登录</h1>

        <form class="auth-form" @submit.prevent="handleAuth">
          <!-- 手机号输入 -->
          <div class="form-item">
            <div class="form-row">
              <label class="form-label">用户名</label>
              <div
                class="input-container"
                :class="{ 'input-error': phoneError }"
              >
                <input
                  type="text"
                  v-model="authForm.username"
                  placeholder="请输入用户名"
                  class="form-input"
                  @blur="validatePhone"
                  @input="clearPhoneError"
                  maxlength="11"
                />
              </div>
            </div>
            <div class="error-message" v-if="phoneError">{{ phoneError }}</div>
          </div>

          <!-- 密码输入 -->
          <div class="form-item">
            <div class="form-row">
              <label class="form-label">密码</label>
              <div class="verification-container">
                <div
                  class="input-container"
                  :class="{ 'input-error': codeError }"
                >
                  <input
                    type="password"
                    v-model="authForm.password"
                    placeholder="请输入密码"
                    class="form-input verification-input"
                    @blur="validateCode"
                    @input="clearCodeError"
                    maxlength="50"
                  />
                </div>
              </div>
            </div>
            <div class="error-message" v-if="codeError">{{ codeError }}</div>
          </div>

          <!-- 登录/注册按钮 -->
          <div class="form-item">
            <l-button
              text="登录"
              :loading="loading"
              :disabled="loading"
              @onClick="handleAuth"
              type="submit"
              class="auth-button"
            />
          </div>
        </form>
      </div>
    </div>

    <!-- 关闭按钮 -->
    <div class="close-button" @click="goBack">
      <el-icon color="#333333" size="24"><Close /></el-icon>
    </div>
  </div>
</template>

<script setup>
  import { ref, reactive, computed, onUnmounted } from 'vue'
  import { useI18n } from 'vue-i18n'
  import { useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus'
  import { Close } from '@element-plus/icons-vue'
  import LImg from '@/components/l-img.vue'
  import LButton from '@/components/l-button.vue'
  // 移除未使用的短信登录API导入，管理员登录无需此依赖

  const { t } = useI18n()
  const router = useRouter()

  // 表单数据（管理员登录：用户名+密码）
  const authForm = reactive({
    username: '',
    password: '',
  })

  // 状态管理
  const loading = ref(false)
  const countdown = ref(0)
  let countdownTimer = null

  // 错误信息
  const phoneError = ref('')
  const codeError = ref('')

  // 计算属性
  const canSendCode = computed(() => false)

  // 验证手机号
  const validatePhone = () => {
    if (!authForm.username) {
      phoneError.value = '请输入用户名'
      return false
    }
    phoneError.value = ''
    return true
  }

  // 验证验证码
  const validateCode = () => {
    if (!authForm.password) {
      codeError.value = '请输入密码'
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

  // 发送验证码（管理员登录不使用，保留空实现避免错误）
  const sendVerificationCode = async () => {}

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

      // 管理后台用户名密码登录
      const request = {
        username: authForm.username,
        password: authForm.password,
      }
      const response = await fetch('http://192.168.1.11:8080/api/admin/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(request),
      }).then(r => r.json())

      if (response.code === 200) {
        ElMessage.success('登录成功')

        // 保存用户信息和token到本地存储
        if (response.data?.token) {
          localStorage.setItem('token', response.data.token)
          localStorage.setItem('userInfo', JSON.stringify(response.data))
        }

        // 登录成功后跳转到首页
        router.push('/')
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
    router.push('/terms')
  }

  // 查看隐私政策
  const viewPrivacy = () => {
    router.push('/privacy')
  }

  // 返回上一页
  const goBack = () => {
    router.back()
  }

  // 组件卸载时清理定时器
  onUnmounted(() => {
    if (countdownTimer) {
      clearInterval(countdownTimer)
    }
  })
</script>

<style scoped>
  .auth-page {
    min-height: 100vh;
    width: 100vw;
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    display: flex;
    flex-direction: column;
    position: relative;
    box-sizing: border-box;
  }

  /* 移动端样式 (默认) */
  .auth-container {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 0;
    min-height: 100vh;
  }

  .center-container {
    width: 90%;
    max-width: none;
    padding: 32px 24px;
    background: #ffffff;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    margin: 20px;
  }

  /* PC端样式 */
  @media (min-width: 769px) {
    .auth-container {
      padding: 40px 20px;
      min-height: calc(100vh - 80px);
    }

    .center-container {
      width: 100%;
      max-width: 500px;
      padding: 40px;
      margin: 0;
    }
  }

  .logo-large {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 24px;
  }

  .logo-text {
    font-weight: 600;
    font-size: 26px;
    color: #333333;
    margin-left: 8px;
  }

  /* 移动端标题样式 */
  .page-title {
    font-size: 28px;
    font-weight: 600;
    color: #333333;
    text-align: center;
    margin-bottom: 32px;
    margin-top: 0;
  }

  /* PC端标题样式 */
  @media (min-width: 769px) {
    .page-title {
      font-size: 32px;
    }
  }

  .auth-form {
    width: 100%;
  }

  /* 移动端表单项样式 */
  .form-item {
    margin-bottom: 20px;
    position: relative;
  }

  /* PC端表单项样式 */
  @media (min-width: 769px) {
    .form-item {
      margin-bottom: 24px;
    }
  }

  /* 表单行布局 */
  .form-row {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .form-label {
    font-weight: 500;
    font-size: 14px;
    color: #333333;
    white-space: nowrap;
    min-width: 60px;
    flex-shrink: 0;
  }

  /* 移动端调整 */
  @media (max-width: 768px) {
    .form-row {
      gap: 12px;
    }

    .form-label {
      min-width: 50px;
      font-size: 13px;
    }
  }

  /* 输入框样式 */
  .input-container {
    position: relative;
    display: flex;
    align-items: center;
    border: 1px solid #b0bec5;
    border-radius: 6px;
    background-color: #ffffff;
    overflow: hidden;
    transition: border-color 0.3s ease;
    flex: 1;
  }

  .input-container:focus-within {
    border-color: #87ceeb;
  }

  .send-code-btn {
    background: #87ceeb;
  }

  .agreement-link {
    color: #87ceeb;
  }

  .input-error {
    border-color: #ff5252;
  }

  .form-input {
    width: 100%;
    height: 48px;
    padding: 0 16px;
    font-size: 16px;
    color: #333333;
    box-sizing: border-box;
    border: none;
    background: transparent;
  }

  .form-input:focus {
    outline: none;
  }

  .form-input::placeholder {
    color: #999999;
  }

  /* 验证码容器 */
  .verification-container {
    display: flex;
    gap: 8px;
    align-items: flex-start;
    flex: 1;
  }

  .verification-input {
    flex: 1;
  }

  .send-code-btn {
    height: 48px;
    padding: 0 20px;
    background: #87ceeb;
    border: none;
    border-radius: 6px;
    color: #ffffff;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s ease;
    white-space: nowrap;
    min-width: 120px;
  }

  .send-code-btn:hover:not(.disabled) {
    background: #87ceeb;
  }

  .send-code-btn.disabled {
    background: #e0e0e0;
    color: #999999;
    cursor: not-allowed;
  }

  /* 错误信息 */
  .error-message {
    margin-top: 6px;
    font-size: 12px;
    color: #ff5252;
  }

  /* 登录/注册按钮 */
  .auth-button {
    width: 100%;
    margin-top: 8px;
  }

  /* 协议提示 */
  .agreement-notice {
    text-align: center;
    margin-top: 24px;
  }

  .notice-text {
    font-size: 12px;
    color: #666666;
    line-height: 1.4;
  }

  .agreement-link {
    color: #87ceeb;
    text-decoration: none;
  }

  .agreement-link:hover {
    text-decoration: underline;
  }

  /* 关闭按钮 - 移动端隐藏 */
  .close-button {
    display: none;
  }

  /* 关闭按钮 - PC端显示 */
  @media (min-width: 769px) {
    .close-button {
      position: absolute;
      top: 16px;
      right: 16px;
      width: 32px;
      height: 32px;
      display: flex;
      justify-content: center;
      align-items: center;
      cursor: pointer;
      border-radius: 50%;
      transition: background-color 0.3s ease;
    }

    .close-button:hover {
      background-color: rgba(0, 0, 0, 0.1);
    }
  }

  /* 响应式设计 */
  @media (max-width: 768px) {
    .auth-container {
      padding: 16px;
    }

    .center-container {
      max-width: 100%;
    }

    .verification-container {
      flex-direction: column;
      gap: 12px;
    }

    .send-code-btn {
      width: 100%;
    }
  }

  /* 动画效果 */
  .send-code-btn,
  .close-button {
    transition: all 0.3s ease;
  }

  .send-code-btn:active:not(.disabled),
  .close-button:active {
    transform: scale(0.98);
  }
</style>
