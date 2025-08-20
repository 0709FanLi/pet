<template>
  <view class="page">
    <!-- 温馨头部 -->
    <view class="apply-header bg-gradient-primary">
      <view class="apply-header__content">
        <view class="apply-header__icon">
          <text class="header-emoji">🕵️</text>
          <text class="badge-text">认证</text>
        </view>
        <view class="apply-header__info">
          <text class="apply-title">成为宠物侦探</text>
          <text class="apply-subtitle">每个小生命都值得被守护 🐾</text>
          <text class="apply-desc">完善资料，开启您的温暖守护之旅</text>
        </view>
      </view>

      <!-- 装饰元素 -->
      <text class="decoration decoration--1">⭐</text>
      <text class="decoration decoration--2">🌟</text>
      <text class="decoration decoration--3">💖</text>
    </view>

    <!-- 缓存恢复提示 -->
    <view v-if="showCacheRestore" class="cache-restore-banner">
      <view class="cache-restore-content">
        <view class="cache-restore-info">
          <text class="cache-icon">💾</text>
          <view class="cache-text">
            <text class="cache-title">发现缓存信息</text>
            <text class="cache-desc">{{ cacheTimeText }}</text>
          </view>
        </view>
        <view class="cache-actions">
          <view
            class="btn btn--secondary btn--small"
            @click="ignoreCacheRestore"
          >
            忽略
          </view>
          <view class="btn btn--primary btn--small" @click="restoreFromCache">
            恢复
          </view>
        </view>
      </view>
    </view>

    <!-- 申请表单 -->
    <view class="apply-content">
      <u-form :model="form" ref="formRef" class="apply-form">
        <!-- 基础信息 -->
        <view class="form-section">
          <view class="form-section__header">
            <text class="form-section__icon">👤</text>
            <text class="form-section__title">基础信息</text>
            <text class="form-section__desc">请填写您的真实信息</text>
          </view>

          <view class="form-fields">
            <view class="input-group">
              <text class="input-label">真实姓名 *</text>
              <view class="input-wrapper">
                <u-input
                  v-model="form.realName"
                  placeholder="请输入您的真实姓名"
                  border="none"
                  @focus="onInputFocus"
                />
              </view>
            </view>

            <view class="input-group">
              <text class="input-label">手机号码 *</text>
              <view class="input-wrapper input-wrapper--disabled">
                <u-input
                  v-model="form.phone"
                  placeholder="请输入手机号码"
                  type="number"
                  border="none"
                  disabled
                />
              </view>
            </view>

            <view class="input-group">
              <text class="input-label">所在城市 *</text>
              <view class="input-wrapper">
                <u-input
                  v-model="form.city"
                  placeholder="如：上海市"
                  border="none"
                  @focus="onInputFocus"
                />
              </view>
            </view>

            <view class="input-group">
              <text class="input-label">公司名称</text>
              <view class="input-wrapper">
                <u-input
                  v-model="form.companyName"
                  placeholder="公司或组织名称（选填）"
                  border="none"
                />
              </view>
            </view>

            <view class="input-group">
              <text class="input-label">常驻地址</text>
              <view class="input-wrapper">
                <u-input
                  v-model="form.address"
                  placeholder="详细地址（选填）"
                  border="none"
                />
              </view>
            </view>
          </view>
        </view>

        <!-- 团队与设备 -->
        <view class="form-section">
          <view class="form-section__header">
            <text class="form-section__icon">🔧</text>
            <text class="form-section__title">团队与设备</text>
            <text class="form-section__desc">展示您的专业实力</text>
          </view>

          <view class="form-fields">
            <view class="input-group">
              <text class="input-label">团队人数 *</text>
              <view class="input-wrapper">
                <u-input
                  v-model="form.teamSize"
                  type="number"
                  placeholder="请输入团队人数"
                  border="none"
                  @focus="onInputFocus"
                />
                <text class="input-suffix">人</text>
              </view>
            </view>

            <view class="input-group">
              <text class="input-label">设备清单 *</text>
              <view class="checkbox-grid">
                <view
                  v-for="device in deviceOptions"
                  :key="device"
                  class="checkbox-item"
                  :class="{
                    'checkbox-item--checked': form.devices.includes(device),
                  }"
                  @click="toggleDevice(device)"
                >
                  <text class="checkbox-item__icon">{{
                    getDeviceIcon(device)
                  }}</text>
                  <text class="checkbox-item__text">{{ device }}</text>
                  <text
                    v-if="form.devices.includes(device)"
                    class="checkbox-item__check"
                    >✓</text
                  >
                </view>
              </view>
            </view>

            <view class="input-group">
              <text class="input-label">设备照片 *</text>
              <text class="input-tip">请上传设备实拍照片，最多6张</text>
              <view class="upload-grid">
                <view
                  v-for="(img, idx) in deviceImages"
                  :key="idx"
                  class="upload-item"
                >
                  <image
                    :src="img.url"
                    mode="aspectFill"
                    class="upload-image"
                  />
                  <view
                    class="upload-item__remove"
                    @click="removeDeviceImage(idx)"
                  >
                    <text class="remove-icon">×</text>
                  </view>
                </view>
                <view
                  v-if="deviceImages.length < 6"
                  class="upload-item upload-item--add"
                  @click="chooseDeviceImage"
                >
                  <text class="upload-add__icon">📷</text>
                  <text class="upload-add__text">添加照片</text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 能力与时间 -->
        <view class="form-section">
          <view class="form-section__header">
            <text class="form-section__icon">⭐</text>
            <text class="form-section__title">能力与时间</text>
            <text class="form-section__desc">展示您的专业经验</text>
          </view>

          <view class="form-fields">
            <view class="input-group">
              <text class="input-label">经验年限 *</text>
              <view class="input-wrapper">
                <u-input
                  v-model="form.experienceYears"
                  type="number"
                  placeholder="请输入经验年限"
                  border="none"
                  @focus="onInputFocus"
                />
                <text class="input-suffix">年</text>
              </view>
            </view>

            <view class="input-group">
              <text class="input-label">服务范围 *</text>
              <view class="input-wrapper">
                <u-input
                  v-model="serviceAreasText"
                  placeholder="多城市用顿号分隔，如：上海、苏州"
                  border="none"
                  @blur="onAreasBlur"
                />
              </view>
            </view>

            <view class="input-group">
              <text class="input-label">可服务时间 *</text>
              <view class="checkbox-grid">
                <view
                  v-for="time in timeOptions"
                  :key="time"
                  class="checkbox-item"
                  :class="{
                    'checkbox-item--checked':
                      form.availableTimes.includes(time),
                  }"
                  @click="toggleTime(time)"
                >
                  <text class="checkbox-item__icon">{{
                    getTimeIcon(time)
                  }}</text>
                  <text class="checkbox-item__text">{{ time }}</text>
                  <text
                    v-if="form.availableTimes.includes(time)"
                    class="checkbox-item__check"
                    >✓</text
                  >
                </view>
              </view>
            </view>

            <view class="input-group">
              <text class="input-label">个人简介</text>
              <view class="textarea-wrapper">
                <u-textarea
                  v-model="form.bio"
                  placeholder="介绍您的擅长领域、从业经验、成功案例等（选填）"
                  maxlength="500"
                  autoHeight
                  count
                  border="none"
                />
              </view>
            </view>
          </view>
        </view>

        <!-- 认证与资质 -->
        <view class="form-section">
          <view class="form-section__header">
            <text class="form-section__icon">🛡️</text>
            <text class="form-section__title">认证与资质</text>
            <text class="form-section__desc">确保信息真实可靠</text>
          </view>

          <view class="form-fields">
            <view class="input-group">
              <text class="input-label">身份证正面 *</text>
              <text class="input-tip">请上传清晰的身份证正面照片</text>
              <view class="id-card-upload">
                <view
                  class="id-card-placeholder"
                  :class="{ 'id-card-placeholder--filled': form.idCardFront }"
                  @click="pickId('front')"
                >
                  <image
                    v-if="form.idCardFront"
                    :src="display(form.idCardFront)"
                    mode="aspectFill"
                    class="id-card-image"
                  />
                  <view v-else class="id-card-empty">
                    <text class="id-card-icon">📄</text>
                    <text class="id-card-text">点击上传身份证正面</text>
                  </view>
                </view>
              </view>
            </view>

            <view class="input-group">
              <text class="input-label">身份证反面 *</text>
              <text class="input-tip">请上传清晰的身份证反面照片</text>
              <view class="id-card-upload">
                <view
                  class="id-card-placeholder"
                  :class="{ 'id-card-placeholder--filled': form.idCardBack }"
                  @click="pickId('back')"
                >
                  <image
                    v-if="form.idCardBack"
                    :src="display(form.idCardBack)"
                    mode="aspectFill"
                    class="id-card-image"
                  />
                  <view v-else class="id-card-empty">
                    <text class="id-card-icon">📄</text>
                    <text class="id-card-text">点击上传身份证反面</text>
                  </view>
                </view>
              </view>
            </view>

            <view class="input-group">
              <text class="input-label">资质证书</text>
              <text class="input-tip">相关从业证书、培训证明等（选填）</text>
              <view class="upload-grid">
                <view
                  v-for="(img, idx) in certImages"
                  :key="idx"
                  class="upload-item"
                >
                  <image
                    :src="img.url"
                    mode="aspectFill"
                    class="upload-image"
                  />
                  <view
                    class="upload-item__remove"
                    @click="removeCertImage(idx)"
                  >
                    <text class="remove-icon">×</text>
                  </view>
                </view>
                <view
                  v-if="certImages.length < 6"
                  class="upload-item upload-item--add"
                  @click="pickCert"
                >
                  <text class="upload-add__icon">📜</text>
                  <text class="upload-add__text">添加证书</text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 收款与确认 -->
        <view class="form-section">
          <view class="form-section__header">
            <text class="form-section__icon">💰</text>
            <text class="form-section__title">收款与确认</text>
            <text class="form-section__desc">完成最后步骤</text>
          </view>

          <view class="form-fields">
            <view class="input-group">
              <text class="input-label">收款方式 *</text>
              <view class="payment-options">
                <view
                  class="payment-item"
                  :class="{
                    'payment-item--selected': form.payout.type === 'wechat',
                  }"
                  @click="form.payout.type = 'wechat'"
                >
                  <text class="payment-icon">💬</text>
                  <text class="payment-text">微信</text>
                  <text
                    v-if="form.payout.type === 'wechat'"
                    class="payment-check"
                    >✓</text
                  >
                </view>
                <view
                  class="payment-item"
                  :class="{
                    'payment-item--selected': form.payout.type === 'alipay',
                  }"
                  @click="form.payout.type = 'alipay'"
                >
                  <text class="payment-icon">🔷</text>
                  <text class="payment-text">支付宝</text>
                  <text
                    v-if="form.payout.type === 'alipay'"
                    class="payment-check"
                    >✓</text
                  >
                </view>
              </view>
            </view>

            <view class="input-group">
              <text class="input-label">收款账号 *</text>
              <view class="input-wrapper">
                <u-input
                  v-model="form.payout.account"
                  :placeholder="
                    form.payout.type === 'wechat'
                      ? '请输入微信号'
                      : '请输入支付宝账号'
                  "
                  border="none"
                  @focus="onInputFocus"
                />
              </view>
            </view>

            <view class="input-group">
              <view class="agreement-check" @click="form.agree = !form.agree">
                <view
                  class="agreement-checkbox"
                  :class="{ 'agreement-checkbox--checked': form.agree }"
                >
                  <text v-if="form.agree" class="checkbox-icon">✓</text>
                </view>
                <text class="agreement-text">
                  我已阅读并同意
                  <text class="agreement-link">《侦探服务条款》</text>
                  和
                  <text class="agreement-link">《隐私政策》</text>
                </text>
              </view>
            </view>
          </view>
        </view>
      </u-form>
    </view>

    <!-- 提交按钮 -->
    <view class="submit-footer">
      <view class="submit-content">
        <view class="submit-tips">
          <text class="tips-icon">💡</text>
          <text class="tips-text">提交后我们将在24小时内完成审核</text>
        </view>
        <view class="btn btn--primary btn--large" @click="submit">
          <text class="btn-icon">🚀</text>
          <text class="btn-text">提交申请</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
  import { ref, reactive, computed, onMounted, watch } from 'vue'
  import { API, BASE_URL, STORAGE_KEYS } from '@/common/config'
  import { request, upload } from '@/common/request'
  import DetectiveCache from '@/common/detective-cache'

  const deviceOptions = [
    '无人机',
    '热成像仪',
    '夜视仪',
    '望远镜',
    '对讲机',
    '寻宠犬',
    '车辆',
    'GPS 追踪器',
    '其他',
  ]
  const timeOptions = ['工作日', '周末', '夜间', '节假日']

  const form = reactive({
    realName: '',
    phone: '',
    city: '',
    companyName: '',
    address: '',
    teamSize: undefined,
    devices: [],
    devicePhotos: [],
    experienceYears: undefined,
    serviceAreas: [],
    availableTimes: [],
    bio: '',
    idCardFront: '',
    idCardBack: '',
    certificates: [],
    payout: { type: 'wechat', account: '' },
    emergencyContact: { name: '', phone: '' },
    agree: true,
  })

  const deviceImages = ref([])
  const certImages = ref([])
  const serviceAreasText = ref('')

  // 缓存相关状态
  const showCacheRestore = ref(false)
  const cacheTimeText = ref('')
  const cachedData = ref(null)
  const autoSaving = ref(false)

  // 服务范围处理
  const onAreasBlur = () => {
    form.serviceAreas = serviceAreasText.value
      .split(/[、,，\s]+/)
      .map(s => s.trim())
      .filter(Boolean)
  }

  // 设备选择切换
  const toggleDevice = device => {
    const index = form.devices.indexOf(device)
    if (index > -1) {
      form.devices.splice(index, 1)
    } else {
      form.devices.push(device)
    }
  }

  // 时间选择切换
  const toggleTime = time => {
    const index = form.availableTimes.indexOf(time)
    if (index > -1) {
      form.availableTimes.splice(index, 1)
    } else {
      form.availableTimes.push(time)
    }
  }

  // 获取设备图标
  const getDeviceIcon = device => {
    const iconMap = {
      无人机: '🚁',
      热成像仪: '🔍',
      夜视仪: '🌙',
      望远镜: '🔭',
      对讲机: '📻',
      寻宠犬: '🐕',
      车辆: '🚗',
      'GPS 追踪器': '📍',
      其他: '🔧',
    }
    return iconMap[device] || '🔧'
  }

  // 获取时间图标
  const getTimeIcon = time => {
    const iconMap = {
      工作日: '💼',
      周末: '🏖️',
      夜间: '🌙',
      节假日: '🎉',
    }
    return iconMap[time] || '⏰'
  }

  // 删除设备图片
  const removeDeviceImage = index => {
    deviceImages.value.splice(index, 1)
    form.devicePhotos = deviceImages.value.map(i => i.raw || i.url)
  }

  // 删除证书图片
  const removeCertImage = index => {
    certImages.value.splice(index, 1)
    form.certificates = certImages.value.map(i => i.raw || i.url)
  }

  // 缓存相关函数

  /**
   * 检查并显示缓存恢复提示
   */
  const checkCacheRestore = async () => {
    try {
      const cached = await DetectiveCache.smartGet()
      if (cached) {
        cachedData.value = cached
        cacheTimeText.value = `上次保存: ${DetectiveCache.formatCacheTime(
          cached.updatedAt || cached.cachedAt
        )}`
        showCacheRestore.value = true
      }
    } catch (error) {
      console.error('[Apply] 缓存检查失败:', error)
    }
  }

  /**
   * 从缓存恢复数据
   */
  const restoreFromCache = () => {
    if (!cachedData.value) return

    try {
      const cached = cachedData.value

      // 恢复基础信息
      if (cached.realName) form.realName = cached.realName
      if (cached.city) form.city = cached.city
      if (cached.companyName) form.companyName = cached.companyName
      if (cached.address) form.address = cached.address
      if (cached.teamSize) form.teamSize = cached.teamSize
      if (cached.experienceYears) form.experienceYears = cached.experienceYears
      if (cached.bio) form.bio = cached.bio

      // 恢复数组类型数据
      if (cached.devices && Array.isArray(cached.devices)) {
        form.devices = [...cached.devices]
      }
      if (cached.serviceAreas && Array.isArray(cached.serviceAreas)) {
        form.serviceAreas = [...cached.serviceAreas]
        serviceAreasText.value = cached.serviceAreas.join('、')
      }
      if (cached.availableTimes && Array.isArray(cached.availableTimes)) {
        form.availableTimes = [...cached.availableTimes]
      }

      // 恢复图片信息
      if (cached.idCardFront) form.idCardFront = cached.idCardFront
      if (cached.idCardBack) form.idCardBack = cached.idCardBack

      // 恢复图片数组
      if (cached.devicePhotos && Array.isArray(cached.devicePhotos)) {
        form.devicePhotos = [...cached.devicePhotos]
        deviceImages.value = cached.devicePhotos.map(url => ({
          url: display(url),
          raw: url,
        }))
      }
      if (cached.certificates && Array.isArray(cached.certificates)) {
        form.certificates = [...cached.certificates]
        certImages.value = cached.certificates.map(url => ({
          url: display(url),
          raw: url,
        }))
      }

      // 恢复收款信息
      if (cached.payout) {
        if (cached.payout.type) form.payout.type = cached.payout.type
        if (cached.payout.account) form.payout.account = cached.payout.account
      }

      // 恢复紧急联系人
      if (cached.emergencyContact) {
        if (cached.emergencyContact.name)
          form.emergencyContact.name = cached.emergencyContact.name
        if (cached.emergencyContact.phone)
          form.emergencyContact.phone = cached.emergencyContact.phone
      }

      // 恢复协议状态
      if (cached.agree !== undefined) form.agree = cached.agree

      // 恢复图片缓存
      const imageCache = DetectiveCache.getImageCache()
      if (imageCache) {
        if (imageCache.deviceImages) {
          deviceImages.value = imageCache.deviceImages
        }
        if (imageCache.certImages) {
          certImages.value = imageCache.certImages
        }
      }

      showCacheRestore.value = false
      uni.showToast({ title: '缓存信息已恢复', icon: 'success' })
    } catch (error) {
      console.error('[Apply] 缓存恢复失败:', error)
      uni.showToast({ title: '缓存恢复失败', icon: 'none' })
    }
  }

  /**
   * 忽略缓存恢复
   */
  const ignoreCacheRestore = () => {
    showCacheRestore.value = false
    cachedData.value = null
  }

  /**
   * 自动保存当前表单数据到缓存
   */
  const autoSaveToCache = async () => {
    if (autoSaving.value) return

    try {
      autoSaving.value = true

      const cacheData = {
        realName: form.realName,
        phone: form.phone,
        city: form.city,
        companyName: form.companyName,
        address: form.address,
        teamSize: form.teamSize,
        devices: form.devices,
        devicePhotos: form.devicePhotos,
        experienceYears: form.experienceYears,
        serviceAreas: form.serviceAreas,
        availableTimes: form.availableTimes,
        bio: form.bio,
        idCardFront: form.idCardFront,
        idCardBack: form.idCardBack,
        certificates: form.certificates,
        payout: {
          type: form.payout.type,
          account: form.payout.account,
        },
        emergencyContact: {
          name: form.emergencyContact.name,
          phone: form.emergencyContact.phone,
        },
        agree: form.agree,
      }

      // 保存到缓存
      await DetectiveCache.smartSave(cacheData)

      // 保存图片缓存
      DetectiveCache.saveImageCache({
        deviceImages: deviceImages.value,
        certImages: certImages.value,
      })
    } catch (error) {
      console.error('[Apply] 自动保存失败:', error)
    } finally {
      autoSaving.value = false
    }
  }

  /**
   * 清除所有缓存
   */
  const clearAllCache = async () => {
    try {
      await DetectiveCache.smartClear()
      console.log('[Apply] 缓存已清除')
    } catch (error) {
      console.error('[Apply] 缓存清除失败:', error)
    }
  }

  /**
   * 输入框聚焦事件
   */
  const onInputFocus = e => {
    console.log('[Apply] 输入框聚焦:', e)
    // 在移动端确保键盘弹出
    // uni-app 会自动处理键盘弹出，这里主要用于调试
  }

  const display = path =>
    typeof path === 'string' && path.startsWith('/uploads/')
      ? `${BASE_URL}${path}`
      : path

  const pickId = async side => {
    uni.chooseImage({
      count: 1,
      success: async res => {
        const filePath = res.tempFilePaths?.[0]
        if (!filePath) return
        const resp = await upload(filePath, { type: 'id_card' })
        const url = resolveUploadUrl(resp)
        if (url) {
          form[side === 'front' ? 'idCardFront' : 'idCardBack'] = url
        }
      },
    })
  }

  const pickCert = () => {
    uni.chooseImage({
      count: 6 - certImages.value.length,
      success: async res => {
        for (const p of res.tempFilePaths) {
          const r = await upload(p, { type: 'cert' })
          const url = resolveUploadUrl(r)
          if (url) certImages.value.push({ url: display(url), raw: url })
        }
        form.certificates = certImages.value.map(i => i.raw || i.url)
      },
    })
  }

  const chooseDeviceImage = () => {
    uni.chooseImage({
      count: 6 - deviceImages.value.length,
      success: async res => {
        for (const p of res.tempFilePaths) {
          const r = await upload(p, { type: 'device' })
          const url = resolveUploadUrl(r)
          if (url) deviceImages.value.push({ url: display(url), raw: url })
        }
        form.devicePhotos = deviceImages.value.map(i => i.raw || i.url)
      },
    })
  }

  function resolveUploadUrl(resp) {
    try {
      if (!resp) return ''
      if (typeof resp === 'string') {
        try {
          return resolveUploadUrl(JSON.parse(resp))
        } catch {
          return ''
        }
      }
      if (resp.url) return resp.url
      if (resp.data) {
        if (typeof resp.data === 'string') {
          try {
            const o = JSON.parse(resp.data)
            return o.url || o.data?.url || o.path || ''
          } catch {}
        } else if (typeof resp.data === 'object') {
          return resp.data.url || resp.data.data?.url || resp.data.path || ''
        }
      }
      return resp.path || ''
    } catch {
      return ''
    }
  }

  const primaryStyle = {
    background:
      'linear-gradient(135deg, var(--brand-gradient-start), var(--brand-gradient-end))',
    border: 'none',
  }

  const canSubmit = computed(() => {
    // 保留完整性判断（仅用于可能的UI提示），但按钮不再禁用
    const f = form
    return !!(
      f.realName &&
      f.phone &&
      f.city &&
      f.teamSize &&
      f.devices?.length &&
      f.devicePhotos?.length &&
      f.experienceYears &&
      f.serviceAreas?.length &&
      f.availableTimes?.length &&
      f.idCardFront &&
      f.idCardBack &&
      f.payout?.type &&
      f.payout?.account &&
      f.agree
    )
  })

  const getMissingFields = () => {
    // 提交前动态校验，返回缺失字段中文名称列表
    const f = form
    // 确保把服务范围输入框的文本同步到数组
    onAreasBlur()
    const missing = []
    if (!f.realName) missing.push('真实姓名')
    if (!f.phone) missing.push('手机号')
    if (!f.city) missing.push('所在城市')
    if (!f.teamSize || Number(f.teamSize) <= 0) missing.push('团队人数')
    if (!f.devices || f.devices.length === 0) missing.push('设备清单')
    if (!f.devicePhotos || f.devicePhotos.length === 0) missing.push('设备照片')
    if (!f.experienceYears || Number(f.experienceYears) <= 0)
      missing.push('经验年限')
    if (!f.serviceAreas || f.serviceAreas.length === 0) missing.push('服务范围')
    if (!f.availableTimes || f.availableTimes.length === 0)
      missing.push('可服务时间')
    if (!f.idCardFront) missing.push('身份证正面')
    if (!f.idCardBack) missing.push('身份证反面')
    if (!f.payout?.type) missing.push('收款方式')
    if (!f.payout?.account) missing.push('收款账号')
    if (!f.agree) missing.push('同意协议')
    return missing
  }

  const submit = async () => {
    console.log('[detective-apply] 🚀 开始提交申请...')
    console.log('[detective-apply] 当前表单数据:', JSON.stringify(form, null, 2))
    
    // 1. 验证必填字段
    const missing = getMissingFields()
    if (missing.length > 0) {
      console.warn('[detective-apply] ❌ 缺少必填字段:', missing)
      const brief =
        missing.slice(0, 4).join('、') + (missing.length > 4 ? ' 等' : '')
      uni.showToast({ title: `请完善：${brief}`, icon: 'none' })
      return
    }
    console.log('[detective-apply] ✅ 表单验证通过')

    // 2. 检查网络和配置
    console.log('[detective-apply] 🌐 API配置:')
    console.log('  - BASE_URL:', BASE_URL)
    console.log('  - API.detective.apply:', API.detective.apply)
    console.log('  - 完整URL:', BASE_URL + API.detective.apply)

    try {
      // 3. 准备提交数据
      const payload = JSON.parse(JSON.stringify(form))
      console.log('[detective-apply] 📤 准备提交的数据:', JSON.stringify(payload, null, 2))
      
      // 4. 显示提交状态
      uni.showLoading({ title: '提交中...' })
      console.log('[detective-apply] ⏳ 开始发送请求...')
      
      // 5. 发送请求
      const startTime = Date.now()
      const res = await request({
        url: API.detective.apply,
        method: 'POST',
        data: payload,
      })
      const endTime = Date.now()
      
      console.log('[detective-apply] 📥 收到响应:')
      console.log('  - 耗时:', (endTime - startTime), 'ms')
      console.log('  - 响应数据:', JSON.stringify(res, null, 2))
      
      uni.hideLoading()

      // 6. 检查响应结果
      if (res && (res.code === 200 || res.code === '200')) {
        console.log('[detective-apply] ✅ 提交成功!')
        console.log('  - 申请ID:', res.data?.applicationId)
        console.log('  - 状态:', res.data?.status)
        
        // 提交成功后清除缓存
        console.log('[detective-apply] 🧹 清除缓存...')
        await clearAllCache()
        console.log('[detective-apply] ✅ 缓存清除完成')

        uni.showToast({ title: '提交成功', icon: 'success' })
        console.log('[detective-apply] 🔄 准备跳转到pending页面...')
        setTimeout(() => {
          console.log('[detective-apply] 📍 执行页面跳转')
          uni.redirectTo({ url: '/pages/detective/pending' })
        }, 500)
      } else {
        console.error('[detective-apply] ❌ 服务器返回错误:')
        console.error('  - code:', res?.code)
        console.error('  - message:', res?.message)
        console.error('  - 完整响应:', res)
        
        const errorMsg = res?.message || '提交失败，请重试'
        uni.showToast({ title: errorMsg, icon: 'none', duration: 3000 })
      }
      
    } catch (e) {
      console.error('[detective-apply] 💥 提交异常:')
      console.error('  - 错误类型:', e.constructor.name)
      console.error('  - 错误消息:', e.message)
      console.error('  - 错误栈:', e.stack)
      console.error('  - 完整错误对象:', e)
      
      uni.hideLoading()
      
      // 根据错误类型提供更具体的错误信息
      let errorMessage = '提交失败'
      if (e.message && e.message.includes('Network')) {
        errorMessage = '网络连接失败，请检查网络'
      } else if (e.message && e.message.includes('timeout')) {
        errorMessage = '请求超时，请重试'
      } else if (e.message && e.message.includes('statusCode')) {
        errorMessage = '服务器响应错误'
      } else if (e.message) {
        errorMessage = `提交失败: ${e.message}`
      }
      
      uni.showToast({ title: errorMessage, icon: 'none', duration: 3000 })
    }
  }

  // 表单自动保存监听
  watch(
    () => [
      form.realName,
      form.city,
      form.companyName,
      form.address,
      form.teamSize,
      form.devices,
      form.experienceYears,
      form.serviceAreas,
      form.availableTimes,
      form.bio,
      form.payout.type,
      form.payout.account,
      form.agree,
    ],
    () => {
      // 防抖保存：用户停止输入1秒后保存
      clearTimeout(window.__detectiveAutoSaveTimer)
      window.__detectiveAutoSaveTimer = setTimeout(() => {
        autoSaveToCache()
      }, 1000)
    },
    { deep: true }
  )

  onMounted(async () => {
    // 初始化用户信息
    try {
      const userInfo = uni.getStorageSync(STORAGE_KEYS.userInfo)
      console.log('[Apply] 获取用户信息:', userInfo)

      if (userInfo) {
        const userObj =
          typeof userInfo === 'string' ? JSON.parse(userInfo) : userInfo
        console.log('[Apply] 解析后的用户信息:', userObj)

        // 尝试多种可能的手机号字段
        const phoneNumber =
          userObj?.phoneNumber || userObj?.phone || userObj?.username
        if (phoneNumber) {
          form.phone = phoneNumber
          console.log('[Apply] 手机号已自动填充:', phoneNumber)
        } else {
          console.log('[Apply] 未找到手机号信息, userObj:', userObj)
        }
      } else {
        console.log('[Apply] 未找到用户信息')
      }
    } catch (error) {
      console.error('[Apply] 用户信息解析失败:', error)
    }

    // 检查是否有缓存需要恢复
    await checkCacheRestore()
  })
</script>

<style lang="scss" scoped>
  /* 基于设计规范的申请页面样式 */

  /* 缓存恢复横幅 */
  .cache-restore-banner {
    background: var(--bg-primary);
    border-bottom: 1px solid var(--border-light);
    padding: var(--spacing-md);
    box-shadow: var(--shadow-light);
  }

  .cache-restore-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: var(--spacing-md);
  }

  .cache-restore-info {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    flex: 1;
  }

  .cache-icon {
    font-size: var(--font-xl);
  }

  .cache-text {
    display: flex;
    flex-direction: column;
    gap: var(--spacing-xs);
  }

  .cache-title {
    font-size: var(--font-base);
    font-weight: 600;
    color: var(--text-primary);
  }

  .cache-desc {
    font-size: var(--font-sm);
    color: var(--text-tertiary);
  }

  .cache-actions {
    display: flex;
    gap: var(--spacing-sm);
  }

  .btn--small {
    padding: 6px 12px;
    font-size: var(--font-sm);
    min-height: 32px;
  }

  .btn--secondary {
    background: var(--bg-tertiary);
    color: var(--text-secondary);
    border: 1px solid var(--border-base);

    &:active {
      background: var(--bg-quaternary);
    }
  }

  /* 头部样式 */
  .apply-header {
    position: relative;
    padding: var(--spacing-xl) var(--spacing-md) var(--spacing-lg);
    color: white;
    overflow: hidden;
    min-height: 180px;
  }

  .apply-header__content {
    display: flex;
    align-items: flex-start;
    gap: var(--spacing-md);
    position: relative;
    z-index: 2;
  }

  .apply-header__icon {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: var(--spacing-xs);
  }

  .header-emoji {
    font-size: 48px;
    display: block;
    animation: bounce 2s ease-in-out infinite;
  }

  .badge-text {
    background: rgba(255, 255, 255, 0.2);
    color: white;
    padding: 4px 8px;
    border-radius: var(--radius-xl);
    font-size: var(--font-xs);
    font-weight: 500;
  }

  .apply-header__info {
    flex: 1;
  }

  .apply-title {
    font-size: var(--font-2xl);
    font-weight: 700;
    display: block;
    margin-bottom: var(--spacing-xs);
  }

  .apply-subtitle {
    font-size: var(--font-lg);
    display: block;
    margin-bottom: var(--spacing-sm);
    opacity: 0.9;
  }

  .apply-desc {
    font-size: var(--font-sm);
    opacity: 0.8;
    display: block;
  }

  /* 装饰元素 */
  .decoration {
    position: absolute;
    font-size: 24px;
    opacity: 0.6;
    animation: twinkle 2s ease-in-out infinite;

    &--1 {
      top: 20px;
      right: 20px;
      animation-delay: 0s;
    }

    &--2 {
      bottom: 30px;
      right: 40px;
      animation-delay: 0.7s;
    }

    &--3 {
      top: 60px;
      left: 20px;
      animation-delay: 1.4s;
    }
  }

  /* 表单内容 */
  .apply-content {
    padding: var(--spacing-md);
    padding-bottom: 200px; /* 进一步增加底部间距，确保不遮挡任何内容 */
  }

  .apply-form {
    display: flex;
    flex-direction: column;
    gap: var(--spacing-lg);
  }

  /* 表单区块 */
  .form-section {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: var(--spacing-lg);
    box-shadow: var(--shadow-light);
    border: 1px solid var(--border-light);
  }

  .form-section__header {
    margin-bottom: var(--spacing-lg);
    padding-bottom: var(--spacing-md);
    border-bottom: 1px solid var(--border-light);
  }

  .form-section__icon {
    font-size: var(--font-xl);
    margin-right: var(--spacing-sm);
  }

  .form-section__title {
    font-size: var(--font-xl);
    font-weight: 600;
    color: var(--text-primary);
    display: inline;
  }

  .form-section__desc {
    font-size: var(--font-sm);
    color: var(--text-tertiary);
    display: block;
    margin-top: var(--spacing-xs);
  }

  .form-fields {
    display: flex;
    flex-direction: column;
    gap: var(--spacing-lg);
  }

  /* 输入组 */
  .input-group {
    display: flex;
    flex-direction: column;
    gap: var(--spacing-sm);
  }

  .input-label {
    font-size: var(--font-base);
    font-weight: 500;
    color: var(--text-primary);
  }

  .input-tip {
    font-size: var(--font-sm);
    color: var(--text-tertiary);
    margin-top: -4px;
  }

  .input-wrapper {
    position: relative;
    display: flex;
    align-items: center;
    border: 1px solid var(--border-base);
    border-radius: var(--radius-md);
    background: var(--bg-primary);
    transition: all 0.2s ease;
    min-height: 48px;

    &:focus-within {
      border-color: var(--brand-primary);
      box-shadow: 0 0 0 2px rgba(91, 143, 249, 0.15);
    }
  }

  /* uView组件样式重置 */
  :deep(.u-input) {
    flex: 1;
    padding: 0 !important;
    font-size: var(--font-base);
    color: var(--text-primary);
    background: transparent !important;
    border: none !important;
    border-radius: 0 !important;
    height: 46px;
    line-height: 46px;
    box-shadow: none !important;
  }

  :deep(.u-input__content) {
    padding: 0 var(--spacing-md) !important;
    width: 100% !important;
    height: 46px !important;
    line-height: 46px !important;
  }

  :deep(.u-input__inner) {
    background: transparent !important;
    border: none !important;
    padding: 0 !important;
    margin: 0 !important;
    height: 46px !important;
    line-height: 46px !important;
    font-size: var(--font-base) !important;
    color: var(--text-primary) !important;
    box-shadow: none !important;
    outline: none !important;
    text-indent: 0 !important;
    width: 100% !important;

    &::placeholder,
    &::-webkit-input-placeholder,
    &::-moz-placeholder {
      padding-left: 0 !important;
      text-indent: 0 !important;
      color: var(--text-placeholder) !important;
    }
  }

  :deep(input) {
    padding: 0 !important;
    margin: 0 !important;
    text-indent: 0 !important;
    width: 100% !important;
    height: 46px !important;
    line-height: 46px !important;
    border: none !important;
    outline: none !important;
    background: transparent !important;

    &::placeholder,
    &::-webkit-input-placeholder,
    &::-moz-placeholder {
      padding-left: 0 !important;
      text-indent: 0 !important;
      color: var(--text-placeholder) !important;
    }
  }

  :deep(.u-input--disabled) {
    .u-input__inner {
      background: transparent !important;
      color: var(--text-tertiary) !important;
    }
  }

  .input-wrapper--disabled {
    background: var(--bg-tertiary) !important;
    cursor: not-allowed;

    :deep(.u-input__content) {
      padding: 0 var(--spacing-md) !important;
    }

    :deep(.u-input__inner) {
      color: var(--text-tertiary) !important;
      cursor: not-allowed;
      padding: 0 !important;
    }

    :deep(input) {
      color: var(--text-tertiary) !important;
      cursor: not-allowed;

      &::placeholder,
      &::-webkit-input-placeholder,
      &::-moz-placeholder {
        color: var(--text-tertiary) !important;
      }
    }
  }

  .input-placeholder {
    color: var(--text-placeholder);
  }

  .input-suffix {
    margin-left: var(--spacing-sm);
    font-size: var(--font-base);
    color: var(--text-secondary);
  }

  /* 文本域 */
  .textarea-wrapper {
    position: relative;
    border: 1px solid var(--border-base);
    border-radius: var(--radius-md);
    background: var(--bg-primary);
    transition: all 0.2s ease;
    min-height: 100px;

    &:focus-within {
      border-color: var(--brand-primary);
      box-shadow: 0 0 0 2px rgba(91, 143, 249, 0.15);
    }
  }

  /* uView textarea样式重置 */
  :deep(.u-textarea) {
    background: transparent !important;
    border: none !important;
    border-radius: 0 !important;
    padding: var(--spacing-md);
    font-size: var(--font-base);
    color: var(--text-primary);
    min-height: 100px;
  }

  :deep(.u-textarea__inner) {
    background: transparent !important;
    border: none !important;
    padding: 0 !important;
    font-size: var(--font-base) !important;
    color: var(--text-primary) !important;
    min-height: 68px !important;
    line-height: 1.5 !important;
    box-shadow: none !important;
    outline: none !important;
    resize: none !important;
  }

  :deep(.u-textarea__count) {
    font-size: var(--font-xs);
    color: var(--text-tertiary);
    background: transparent;
    padding: 4px 0;
    position: absolute;
    bottom: 8px;
    right: 12px;
  }

  /* 复选框网格 */
  .checkbox-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: var(--spacing-sm);
  }

  .checkbox-item {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    padding: var(--spacing-sm) var(--spacing-md);
    border: 1px solid var(--border-base);
    border-radius: var(--radius-md);
    background: var(--bg-primary);
    cursor: pointer;
    transition: all 0.2s ease;
    position: relative;
    min-height: 44px;

    &:active {
      transform: scale(0.98);
    }

    &--checked {
      border-color: var(--brand-primary);
      background: rgba(91, 143, 249, 0.1);
    }
  }

  .checkbox-item__icon {
    font-size: var(--font-lg);
  }

  .checkbox-item__text {
    flex: 1;
    font-size: var(--font-base);
    color: var(--text-primary);
  }

  .checkbox-item__check {
    font-size: var(--font-base);
    color: var(--brand-primary);
    font-weight: 600;
  }

  /* 上传网格 */
  .upload-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: var(--spacing-md);
  }

  .upload-item {
    aspect-ratio: 1;
    border-radius: var(--radius-md);
    overflow: hidden;
    position: relative;
    border: 1px solid var(--border-base);
    background: var(--bg-tertiary);

    &--add {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.2s ease;
      border: 2px dashed var(--border-base);

      &:active {
        transform: scale(0.98);
        border-color: var(--brand-primary);
        background: rgba(91, 143, 249, 0.05);
      }
    }
  }

  .upload-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .upload-item__remove {
    position: absolute;
    top: 4px;
    right: 4px;
    width: 20px;
    height: 20px;
    background: rgba(0, 0, 0, 0.6);
    border-radius: var(--radius-round);
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }

  .remove-icon {
    color: white;
    font-size: 14px;
    font-weight: bold;
  }

  .upload-add__icon {
    font-size: 24px;
    margin-bottom: var(--spacing-xs);
    opacity: 0.6;
  }

  .upload-add__text {
    font-size: var(--font-sm);
    color: var(--text-tertiary);
  }

  /* 身份证上传 */
  .id-card-upload {
    margin-top: var(--spacing-sm);
  }

  .id-card-placeholder {
    width: 100%;
    height: 160px;
    border: 2px dashed var(--border-base);
    border-radius: var(--radius-md);
    background: var(--bg-tertiary);
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s ease;
    overflow: hidden;

    &:active {
      transform: scale(0.99);
      border-color: var(--brand-primary);
      background: rgba(91, 143, 249, 0.05);
    }

    &--filled {
      border-style: solid;
      border-color: var(--border-base);
    }
  }

  .id-card-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .id-card-empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: var(--spacing-sm);
  }

  .id-card-icon {
    font-size: 32px;
    opacity: 0.6;
  }

  .id-card-text {
    font-size: var(--font-base);
    color: var(--text-tertiary);
  }

  /* 支付方式选择 */
  .payment-options {
    display: flex;
    gap: var(--spacing-md);
  }

  .payment-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: var(--spacing-sm);
    padding: var(--spacing-md);
    border: 1px solid var(--border-base);
    border-radius: var(--radius-md);
    background: var(--bg-primary);
    cursor: pointer;
    transition: all 0.2s ease;
    position: relative;
    min-height: 80px;

    &:active {
      transform: scale(0.98);
    }

    &--selected {
      border-color: var(--brand-primary);
      background: rgba(91, 143, 249, 0.1);
    }
  }

  .payment-icon {
    font-size: var(--font-xl);
  }

  .payment-text {
    font-size: var(--font-base);
    color: var(--text-primary);
    font-weight: 500;
  }

  .payment-check {
    position: absolute;
    top: 4px;
    right: 4px;
    font-size: var(--font-sm);
    color: var(--brand-primary);
    font-weight: 600;
  }

  /* 协议确认 */
  .agreement-check {
    display: flex;
    align-items: flex-start;
    gap: var(--spacing-sm);
    cursor: pointer;
    padding: var(--spacing-sm);
    border-radius: var(--radius-md);
    transition: background 0.2s ease;

    &:active {
      background: var(--bg-tertiary);
    }
  }

  .agreement-checkbox {
    width: 20px;
    height: 20px;
    border: 2px solid var(--border-base);
    border-radius: var(--radius-sm);
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s ease;
    margin-top: 2px;

    &--checked {
      border-color: var(--brand-primary);
      background: var(--brand-primary);
    }
  }

  .checkbox-icon {
    color: white;
    font-size: 12px;
    font-weight: bold;
  }

  .agreement-text {
    flex: 1;
    font-size: var(--font-base);
    color: var(--text-secondary);
    line-height: var(--line-height-normal);
  }

  .agreement-link {
    color: var(--brand-primary);
    font-weight: 500;
  }

  /* 提交区域 */
  .submit-footer {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
    background: var(--bg-primary);
    padding: var(--spacing-lg) var(--spacing-md);
    padding-bottom: calc(var(--spacing-lg) + env(safe-area-inset-bottom));
    box-shadow: var(--shadow-medium);
    border-top: 1px solid var(--border-light);
    z-index: 10; /* 确保在最上层 */
  }

  .submit-content {
    display: flex;
    flex-direction: column;
    gap: var(--spacing-sm);
  }

  .submit-tips {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    justify-content: center;
  }

  .tips-icon {
    font-size: var(--font-base);
  }

  .tips-text {
    font-size: var(--font-sm);
    color: var(--text-tertiary);
  }

  .btn--large {
    padding: var(--spacing-md) var(--spacing-lg);
    font-size: var(--font-lg);
    min-height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: var(--spacing-sm);
  }

  .btn-icon {
    font-size: var(--font-lg);
  }

  .btn-text {
    font-weight: 600;
  }

  /* 动画效果 */
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

  /* 响应式适配 */
  @media (max-width: 375px) {
    .apply-header {
      padding: var(--spacing-lg) var(--spacing-sm);
    }

    .apply-content {
      padding: var(--spacing-sm);
      padding-bottom: 220px; /* 小屏幕需要更多底部间距 */
    }

    .form-section {
      padding: var(--spacing-md);
    }

    .checkbox-grid {
      grid-template-columns: 1fr;
    }

    .payment-options {
      flex-direction: column;
    }

    .submit-footer {
      padding: var(--spacing-md) var(--spacing-sm);
      padding-bottom: calc(var(--spacing-md) + env(safe-area-inset-bottom));
    }
  }
</style>
