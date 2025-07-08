<template>
    <div class="auth-page">
        <!-- 登录/注册表单 -->
        <div class="auth-container">
            <div class="center-container">
                <div class="logo-large">
                  <l-img src="/static/logo3.png" w="60px" h="60px" />
                  <span class="logo-text">{{ $t('appName') }}</span>
                </div>

                <h1 class="page-title">登录/注册</h1>

                <form class="auth-form" @submit.prevent="handleAuth">
                    <!-- 手机号输入 -->
                    <div class="form-item">
                        <div class="form-row">
                            <label class="form-label">手机号</label>
                            <div class="input-container" :class="{ 'input-error': phoneError }">
                                <input
                                    type="tel"
                                    v-model="authForm.phone"
                                    placeholder="请输入手机号"
                                    class="form-input"
                                    @blur="validatePhone"
                                    @input="clearPhoneError"
                                    maxlength="11"
                                />
                            </div>
                        </div>
                        <div class="error-message" v-if="phoneError">{{ phoneError }}</div>
                    </div>

                    <!-- 验证码输入 -->
                    <div class="form-item">
                        <div class="form-row">
                            <label class="form-label">验证码</label>
                            <div class="verification-container">
                                <div class="input-container" :class="{ 'input-error': codeError }">
                                    <input
                                        type="text"
                                        v-model="authForm.verificationCode"
                                        placeholder="请输入验证码"
                                        class="form-input verification-input"
                                        @blur="validateCode"
                                        @input="clearCodeError"
                                        maxlength="4"
                                    />
                                </div>
                                <button 
                                    type="button" 
                                    class="send-code-btn"
                                    :class="{ disabled: !canSendCode || countdown > 0 }"
                                    @click="sendVerificationCode"
                                    :disabled="!canSendCode || countdown > 0"
                                >
                                    {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
                                </button>
                            </div>
                        </div>
                        <div class="error-message" v-if="codeError">{{ codeError }}</div>
                    </div>

                    <!-- 登录/注册按钮 -->
                    <div class="form-item">
                        <l-button
                            text="登录/注册"
                            :loading="loading"
                            :disabled="loading"
                            @onClick="handleAuth"
                            type="submit"
                            class="auth-button"
                        />
                    </div>

                    <!-- 协议提示 -->
                    <div class="agreement-notice">
                        <span class="notice-text">
                            登录即表示同意
                            <a href="#" class="agreement-link" @click.prevent="viewTerms">《用户协议》</a>
                            和
                            <a href="#" class="agreement-link" @click.prevent="viewPrivacy">《隐私政策》</a>
                        </span>
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
import { ref, reactive, computed, onUnmounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Close } from '@element-plus/icons-vue';
import LImg from '@/components/l-img.vue';
import LButton from '@/components/l-button.vue';
import { UserAuthApi, PhoneLoginRequest, SendCodeRequest } from '@/api/user-auth-api';

const { t } = useI18n();
const router = useRouter();

// 表单数据
const authForm = reactive({
    phone: '',
    verificationCode: ''
});

// 状态管理
const loading = ref(false);
const countdown = ref(0);
let countdownTimer = null;

// 错误信息
const phoneError = ref('');
const codeError = ref('');

// 计算属性
const canSendCode = computed(() => {
    return authForm.phone && /^1[3-9]\d{9}$/.test(authForm.phone);
});

// 验证手机号
const validatePhone = () => {
    if (!authForm.phone) {
        phoneError.value = '请输入手机号';
        return false;
    }
    if (!/^1[3-9]\d{9}$/.test(authForm.phone)) {
        phoneError.value = '请输入正确的手机号格式';
        return false;
    }
    phoneError.value = '';
    return true;
};

// 验证验证码
const validateCode = () => {
    if (!authForm.verificationCode) {
        codeError.value = '请输入验证码';
        return false;
    }
    if (!/^\d{4}$/.test(authForm.verificationCode)) {
        codeError.value = '请输入4位数字验证码';
        return false;
    }
    codeError.value = '';
    return true;
};

// 清除错误信息
const clearPhoneError = () => {
    phoneError.value = '';
};

const clearCodeError = () => {
    codeError.value = '';
};

// 发送验证码
const sendVerificationCode = async () => {
    if (!validatePhone()) {
        return;
    }

    try {
        const request = {
            phone: authForm.phone,
            type: 'auth'
        };
        
        const response = await UserAuthApi.sendCode(request);
        
        if (response.code === 200) {
            ElMessage.success('验证码发送成功');
            
            // 开始倒计时
            countdown.value = 60;
            countdownTimer = setInterval(() => {
                countdown.value--;
                if (countdown.value <= 0) {
                    clearInterval(countdownTimer);
                    countdownTimer = null;
                }
            }, 1000);
        } else {
            ElMessage.error(response.message || '验证码发送失败');
        }
    } catch (error) {
        console.error('发送验证码失败:', error);
        ElMessage.error('验证码发送失败，请稍后重试');
    }
};

// 登录/注册处理
const handleAuth = async () => {
    // 验证所有字段
    const isPhoneValid = validatePhone();
    const isCodeValid = validateCode();

    if (!isPhoneValid || !isCodeValid) {
        return;
    }

    try {
        loading.value = true;
        
        // 调用手机验证码登录API
        const request = {
            phone: authForm.phone,
            verificationCode: authForm.verificationCode
        };
        
        const response = await UserAuthApi.phoneLogin(request);
        
        if (response.code === 200) {
            ElMessage.success('登录成功');
            
            // 保存用户信息和token到本地存储
            if (response.data?.token) {
                localStorage.setItem('token', response.data.token);
                localStorage.setItem('userInfo', JSON.stringify(response.data));
            }
            
            // 登录成功后跳转到首页
            router.push('/');
        } else {
            ElMessage.error(response.message || '登录失败');
        }
    } catch (error) {
        console.error('登录失败:', error);
        ElMessage.error('登录失败，请稍后重试');
    } finally {
        loading.value = false;
    }
};

// 查看用户协议
const viewTerms = () => {
    // 跳转到用户协议页面
    router.push('/terms');
};

// 查看隐私政策
const viewPrivacy = () => {
    // 跳转到隐私政策页面
    router.push('/privacy');
};

// 返回上一页
const goBack = () => {
    router.back();
};

// 组件卸载时清理定时器
onUnmounted(() => {
    if (countdownTimer) {
        clearInterval(countdownTimer);
    }
});
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
  background: #FFFFFF;
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
  border: 1px solid #B0BEC5;
  border-radius: 6px;
  background-color: #FFFFFF;
  overflow: hidden;
  transition: border-color 0.3s ease;
  flex: 1;
}

.input-container:focus-within {
  border-color: #87CEEB;
}

.send-code-btn {
  background: #87CEEB;
}

.agreement-link {
  color: #87CEEB;
}

.input-error {
  border-color: #FF5252;
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
  background: #87CEEB;
  border: none;
  border-radius: 6px;
  color: #FFFFFF;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  min-width: 120px;
}

.send-code-btn:hover:not(.disabled) {
  background: #87CEEB;
}

.send-code-btn.disabled {
  background: #E0E0E0;
  color: #999999;
  cursor: not-allowed;
}

/* 错误信息 */
.error-message {
  margin-top: 6px;
  font-size: 12px;
  color: #FF5252;
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
  color: #87CEEB;
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