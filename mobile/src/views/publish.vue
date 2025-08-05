<template>
  <div class="publish-page">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="nav-left" @click="goBack">
        <van-icon name="arrow-left" size="20" color="#333" />
      </div>
      <div class="nav-title">发布寻宠信息</div>
      <div class="nav-right"></div>
    </div>

    <!-- 主体内容 -->
    <div class="content-wrapper">
      <van-form @submit="submitForm" ref="formRef">
        <!-- 宠物照片上传区域 -->
        <div class="photo-section">
          <div class="section-title">
            <span class="title-text">宠物照片</span>
            <span class="title-tips">最多9张，第一张为封面</span>
          </div>
          <div class="photo-upload-area">
            <van-uploader
              v-model="form.images"
              multiple
              :max-count="9"
              :after-read="afterRead"
              :before-read="beforeRead"
              :max-size="10 * 1024 * 1024"
              @oversize="onOversize"
              upload-text="添加照片"
              class="custom-uploader"
            >
              <template #default>
                <div class="upload-placeholder">
                  <div class="upload-icon">📷</div>
                  <div class="upload-text">添加宠物照片</div>
                </div>
              </template>
            </van-uploader>
          </div>
        </div>

        <!-- 基本信息 -->
        <div class="form-section">
          <div class="section-title">
            <span class="title-text">基本信息</span>
          </div>
          <div class="form-content">
            <van-field
              v-model="form.petName"
              label="宠物名称"
              placeholder="请输入宠物的名字"
              required
              class="custom-field"
              left-icon="pet"
            />
            <van-field
              v-model="form.petType"
              label="宠物类型"
              placeholder="如：狗狗、猫咪等"
              required
              class="custom-field"
              left-icon="category"
            />
            <van-field
              v-model="form.petBreed"
              label="宠物品种"
              placeholder="如：金毛、英短等（选填）"
              class="custom-field"
              left-icon="more"
            />
            <van-field
              v-model="form.petDescription"
              label="外貌特征"
              placeholder="请详细描述宠物的外貌特征，如毛色、体型、特殊标记等"
              required
              type="textarea"
              rows="3"
              class="custom-field"
              left-icon="description"
            />
          </div>
        </div>

        <!-- 丢失信息 -->
        <div class="form-section">
          <div class="section-title">
            <span class="title-text">丢失信息</span>
          </div>
          <div class="form-content">
            <van-field
              v-model="form.lostLocation"
              label="丢失地点"
              placeholder="请输入详细的丢失地点"
              required
              class="custom-field"
              left-icon="location"
            />
            <div class="time-input-group">
              <div class="time-input-row">
                <van-field
                  v-model="form.lostHours"
                  label="丢失多久了"
                  placeholder="请输入小时数"
                  type="number"
                  min="1"
                  max="8760"
                  required
                  class="custom-field time-hours-field"
                  left-icon="clock"
                />
                <span class="time-unit">小时前</span>
              </div>
              <div class="time-tips">
                例如：5小时前输入 5，2天前输入 48，一周前输入 168
              </div>
            </div>
          </div>
        </div>

        <!-- 联系信息 -->
        <div class="form-section">
          <div class="section-title">
            <span class="title-text">联系方式</span>
          </div>
          <div class="form-content">
            <van-field
              v-model="form.contactInfo"
              label="联系方式"
              placeholder="请输入您的手机号或微信号"
              required
              class="custom-field"
              left-icon="phone"
            />
          </div>
        </div>

        <!-- 悬赏信息 -->
        <div class="form-section">
          <div class="section-title">
            <span class="title-text">悬赏金额</span>
          </div>
          <div class="form-content">
            <van-field
              v-model="form.reward"
              label="悬赏金额"
              placeholder="最低300元"
              type="number"
              required
              class="custom-field reward-field"
              left-icon="gold-coin"
              :rules="rewardRules"
            >
              <template #input>
                <div class="reward-input-wrapper">
                  <span class="reward-symbol">¥</span>
                  <input
                    v-model="form.reward"
                    type="number"
                    placeholder="最低300元"
                    class="reward-input"
                  />
                  <span class="reward-unit">元</span>
                </div>
              </template>
            </van-field>
            <div class="reward-tips">
              <p>💡 合理的悬赏金额能更快找到宠物</p>
              <p>🔒 找到宠物确认后才会支付悬赏</p>
            </div>
          </div>
        </div>

        <!-- 发布按钮 -->
        <div class="submit-section">
          <van-button
            round
            block
            type="primary"
            native-type="submit"
            class="submit-btn"
            :loading="submitting"
            :disabled="!canSubmit"
          >
            {{ submitting ? '发布中...' : '立即发布' }}
          </van-button>
          <div class="submit-tips">
            发布即表示同意<span class="link-text" @click="showUserAgreement"
              >《用户协议》</span
            >和<span class="link-text" @click="showPrivacyPolicy"
              >《隐私政策》</span
            >
          </div>
        </div>
      </van-form>
    </div>

    <!-- 加载遮罩 -->
    <van-overlay :show="uploading" class="loading-overlay">
      <div class="loading-content">
        <van-loading size="24px" color="#667eea" />
        <div class="loading-text">上传图片中...</div>
      </div>
    </van-overlay>

    <!-- 用户协议弹框 -->
    <UserAgreement
      :show="showAgreementDialog"
      @close="showAgreementDialog = false"
    />

    <!-- 隐私政策弹框 -->
    <PrivacyPolicy
      :show="showPrivacyDialog"
      @close="showPrivacyDialog = false"
    />
  </div>
</template>

<script setup>
  import { ref, computed, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import {
    Form,
    Field,
    Button,
    Uploader,
    DatePicker,
    Popup,
    Icon,
    Loading,
    Overlay,
    showToast,
    showSuccessToast,
    showFailToast,
  } from 'vant'
  import axios from 'axios'
  import UserAgreement from '@/components/UserAgreement.vue'
  import PrivacyPolicy from '@/components/PrivacyPolicy.vue'

  const router = useRouter()

  // 表单数据
  const form = ref({
    petName: '',
    petType: '',
    petBreed: '',
    petDescription: '',
    lostLocation: '',
    lostHours: 1, // 只保留小时数，默认为1
    contactInfo: '',
    reward: '',
    images: [],
  })

  // 状态
  const submitting = ref(false)
  const uploading = ref(false)
  const formRef = ref(null)

  // 弹框状态
  const showAgreementDialog = ref(false)
  const showPrivacyDialog = ref(false)

  // 悬赏验证规则
  const rewardRules = [
    { required: true, message: '请输入悬赏金额' },
    {
      validator: val => {
        const amount = parseFloat(val)
        return amount >= 300
      },
      message: '悬赏金额至少300元',
    },
  ]

  // 计算属性：是否可以提交
  const canSubmit = computed(() => {
    return (
      form.value.petName &&
      form.value.petType &&
      form.value.petDescription &&
      form.value.lostLocation &&
      form.value.lostHours > 0 && // 小时数必须大于0
      form.value.contactInfo &&
      form.value.reward &&
      form.value.images.length > 0
    )
  })

  // 返回上一页
  const goBack = () => {
    router.go(-1)
  }

  // 显示用户协议
  const showUserAgreement = () => {
    showAgreementDialog.value = true
  }

  // 显示隐私政策
  const showPrivacyPolicy = () => {
    showPrivacyDialog.value = true
  }

  // 文件上传前验证
  const beforeRead = file => {
    const allowedTypes = [
      'image/jpeg',
      'image/jpg',
      'image/png',
      'image/gif',
      'image/webp',
    ]

    if (!allowedTypes.includes(file.type)) {
      showFailToast('只支持 JPG、PNG、GIF、WEBP 格式的图片')
      return false
    }

    return true
  }

  // 文件大小超出限制
  const onOversize = () => {
    showFailToast('图片大小不能超过 10MB')
  }

  // 文件读取完成
  const afterRead = async file => {
    uploading.value = true

    try {
      const formData = new FormData()
      formData.append('file', file.file)
      formData.append('type', 'pet')

      const response = await axios.post(
        'http://localhost:8080/api/upload/image',
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        }
      )

      if (response.data.code === 200) {
        // 更新文件对象，添加服务器返回的URL
        file.url = response.data.data.url
        file.serverUrl = response.data.data.url
        showSuccessToast('图片上传成功')
      } else {
        showFailToast(response.data.message || '图片上传失败')
        // 从列表中移除失败的文件
        const index = form.value.images.findIndex(img => img === file)
        if (index > -1) {
          form.value.images.splice(index, 1)
        }
      }
    } catch (error) {
      console.error('Upload error:', error)
      showFailToast('图片上传失败')
      // 从列表中移除失败的文件
      const index = form.value.images.findIndex(img => img === file)
      if (index > -1) {
        form.value.images.splice(index, 1)
      }
    } finally {
      uploading.value = false
    }
  }

  // 提交表单
  const submitForm = async () => {
    if (submitting.value) return

    try {
      submitting.value = true

      // 验证必填字段
      if (!canSubmit.value) {
        showFailToast('请完善所有必填信息')
        return
      }

      // 准备提交数据
      const userId = 1 // TODO: 从用户状态获取真实用户ID
      const imageUrls = form.value.images
        .filter(img => img.serverUrl)
        .map(img => img.serverUrl)

      if (imageUrls.length === 0) {
        showFailToast('请至少上传一张宠物照片')
        return
      }

      // 计算实际的丢失时间（只用小时数向前推算）
      const now = new Date()
      const lostTimeMs = now.getTime() - form.value.lostHours * 60 * 60 * 1000
      const lostTime = new Date(lostTimeMs)
      const lostTimeStr =
        lostTime.getFullYear() +
        '-' +
        (lostTime.getMonth() + 1).toString().padStart(2, '0') +
        '-' +
        lostTime.getDate().toString().padStart(2, '0') +
        ' ' +
        lostTime.getHours().toString().padStart(2, '0') +
        ':' +
        lostTime.getMinutes().toString().padStart(2, '0') +
        ':00'

      const formData = new FormData()
      formData.append('userId', userId)
      formData.append('petName', form.value.petName)
      formData.append('petType', form.value.petType)
      formData.append('petBreed', form.value.petBreed || '')
      formData.append('petDescription', form.value.petDescription)
      formData.append('lostLocation', form.value.lostLocation)
      formData.append('lostTime', lostTimeStr)
      formData.append('contactInfo', form.value.contactInfo)
      formData.append('reward', form.value.reward)

      // 添加图片URL（以JSON字符串形式）
      imageUrls.forEach((url, index) => {
        formData.append('images', url)
      })

      const response = await axios.post(
        'http://localhost:8080/api/lost-pets',
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        }
      )

      // 准备跳转到成功页面的数据
      const petData = {
        id: response.data?.id,
        petName: form.value.petName,
        petType: form.value.petType,
        lostLocation: form.value.lostLocation,
        reward: form.value.reward,
        image: imageUrls[0] || null, // 第一张图片作为封面
      }

      // 将数据保存到临时存储，供成功页面使用
      localStorage.setItem('temp_published_pet', JSON.stringify(petData))

      showSuccessToast('发布成功！')

      // 延迟跳转到发布成功页面
      setTimeout(() => {
        router.push('/publish-success')
      }, 1500)
    } catch (error) {
      console.error('Submit error:', error)
      const errorMessage =
        error.response?.data?.message || error.message || '发布失败，请重试'
      showFailToast(errorMessage)
    } finally {
      submitting.value = false
    }
  }

  onMounted(() => {
    // 页面初始化
  })
</script>

<style scoped>
  .publish-page {
    min-height: 100vh;
    background: #f8fafc;
    position: relative;
  }

  /* 顶部导航栏 */
  .top-nav {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 44px;
    background: #ffffff;
    display: flex;
    align-items: center;
    padding: 0 16px;
    border-bottom: 1px solid #f0f0f0;
    z-index: 1000;
  }

  .nav-left {
    width: 44px;
    height: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }

  .nav-title {
    flex: 1;
    text-align: center;
    font-size: 17px;
    font-weight: 600;
    color: #333;
  }

  .nav-right {
    width: 44px;
  }

  /* 内容区域 */
  .content-wrapper {
    padding-top: 44px;
    padding-bottom: 20px;
  }

  /* 区块样式 */
  .photo-section,
  .form-section {
    background: #ffffff;
    margin-bottom: 12px;
    border-radius: 12px;
    margin: 0 16px 12px;
    overflow: hidden;
  }

  .section-title {
    padding: 16px 16px 8px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .title-text {
    font-size: 16px;
    font-weight: 600;
    color: #333;
  }

  .title-tips {
    font-size: 12px;
    color: #999;
  }

  /* 照片上传区域 */
  .photo-upload-area {
    padding: 0 16px 16px;
  }

  .custom-uploader {
    --van-uploader-upload-background-color: #f8fafc;
    --van-uploader-upload-border-color: #e2e8f0;
  }

  .upload-placeholder {
    width: 80px;
    height: 80px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #ffffff;
    cursor: pointer;
    transition: all 0.3s ease;
  }

  .upload-placeholder:active {
    transform: scale(0.95);
  }

  .upload-icon {
    font-size: 24px;
    margin-bottom: 4px;
  }

  .upload-text {
    font-size: 11px;
    text-align: center;
  }

  /* 表单区域 */
  .form-content {
    padding: 0 16px 16px;
  }

  .custom-field {
    margin-bottom: 12px;
    --van-field-border-color: #f0f0f0;
    --van-field-background-color: #f8fafc;
    border-radius: 8px;
    overflow: hidden;
  }

  .custom-field:last-child {
    margin-bottom: 0;
  }

  /* 悬赏金额特殊样式 */
  .reward-field {
    --van-field-background-color: #fff8e1;
    border: 1px solid #ffd54f;
  }

  .reward-input-wrapper {
    display: flex;
    align-items: center;
    width: 100%;
  }

  .reward-symbol {
    font-size: 16px;
    font-weight: bold;
    color: #ff6b35;
    margin-right: 4px;
  }

  .reward-input {
    flex: 1;
    border: none;
    outline: none;
    background: transparent;
    font-size: 16px;
    font-weight: 600;
    color: #333;
  }

  .reward-unit {
    font-size: 14px;
    color: #666;
    margin-left: 4px;
  }

  .reward-tips {
    margin-top: 8px;
    padding: 12px;
    background: #f0f9ff;
    border-radius: 8px;
    border-left: 3px solid #3b82f6;
  }

  .reward-tips p {
    margin: 0;
    font-size: 12px;
    color: #64748b;
    line-height: 1.5;
  }

  .reward-tips p:first-child {
    margin-bottom: 4px;
  }

  /* 时间输入样式 */
  .time-input-group {
    padding: 0 16px 16px;
  }

  .time-input-row {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .time-hours-field {
    flex: 1;
    max-width: 200px;
  }

  .time-unit {
    font-size: 14px;
    color: #666;
    white-space: nowrap;
    margin: 0 4px;
  }

  .time-tips {
    margin-top: 8px;
    font-size: 12px;
    color: #999;
    line-height: 1.4;
    text-align: center;
  }

  /* 提交区域 */
  .submit-section {
    padding: 20px 16px;
  }

  .submit-btn {
    height: 50px;
    font-size: 16px;
    font-weight: 600;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
    transition: all 0.3s ease;
  }

  .submit-btn:active {
    transform: translateY(1px);
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  }

  .submit-btn:disabled {
    background: #cbd5e1;
    box-shadow: none;
    transform: none;
  }

  .submit-tips {
    text-align: center;
    font-size: 12px;
    color: #94a3b8;
    margin-top: 12px;
    line-height: 1.5;
  }

  .link-text {
    color: #667eea;
    cursor: pointer;
    text-decoration: underline;
    transition: color 0.3s ease;
  }

  .link-text:hover {
    color: #5a67d8;
  }

  .link-text:active {
    color: #4c51bf;
  }

  /* 弹窗样式 */
  .date-popup {
    border-radius: 16px 16px 0 0;
  }

  /* 加载遮罩 */
  .loading-overlay {
    background: rgba(0, 0, 0, 0.3);
  }

  .loading-content {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 24px;
    background: #ffffff;
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  }

  .loading-text {
    margin-top: 12px;
    font-size: 14px;
    color: #64748b;
  }

  /* 响应式适配 */
  @media (max-width: 375px) {
    .photo-section,
    .form-section {
      margin: 0 12px 10px;
    }

    .upload-placeholder {
      width: 70px;
      height: 70px;
    }

    .upload-icon {
      font-size: 20px;
    }

    .upload-text {
      font-size: 10px;
    }
  }

  /* 滚动优化 */
  .content-wrapper {
    -webkit-overflow-scrolling: touch;
    scroll-behavior: smooth;
  }

  /* 动画效果 */
  @keyframes fadeInUp {
    from {
      opacity: 0;
      transform: translateY(20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .photo-section,
  .form-section {
    animation: fadeInUp 0.4s ease-out;
  }

  .form-section:nth-child(2) {
    animation-delay: 0.1s;
  }
  .form-section:nth-child(3) {
    animation-delay: 0.2s;
  }
  .form-section:nth-child(4) {
    animation-delay: 0.3s;
  }
  .form-section:nth-child(5) {
    animation-delay: 0.4s;
  }
</style>
