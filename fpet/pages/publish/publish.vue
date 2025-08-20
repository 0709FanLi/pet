<template>
  <view class="page">
    <!-- 温馨的头部 -->
    <view class="publish-header bg-gradient-primary">
      <view class="publish-header__content">
        <text class="publish-header__emoji">🐾</text>
        <view class="publish-header__text">
          <text class="publish-header__title">发布寻宠信息</text>
          <text class="publish-header__subtitle">每个细节都可能帮助Ta回家</text>
        </view>
      </view>
    </view>

    <view class="page-content">
      <u-form
        :model="form"
        ref="formRef"
        class="publish-form"
        :labelWidth="90"
        labelPosition="left"
      >
        <!-- 基本信息部分 -->
        <view class="form-section">
          <view class="form-section__title">
            <text class="form-section__icon">🐶</text>
            <text class="form-section__text">基本信息</text>
          </view>
          <u-form-item label="宠物名称" prop="petName" :labelWidth="90">
            <u-input
              v-model="form.petName"
              placeholder="请输入宠物名称"
              :maxlength="20"
              @input="onPetNameInput"
              @focus="onInputFocus"
              border="surround"
              clearable
            />
          </u-form-item>
          <u-form-item label="宠物种类" prop="petType" :labelWidth="90">
            <u-radio-group v-model="form.petType" shape="circle">
              <u-radio name="猫" label="猫" />
              <u-radio name="狗" label="狗" />
              <u-radio name="其它" label="其它" />
            </u-radio-group>
          </u-form-item>
          <u-form-item label="宠物品种" prop="petBreed" :labelWidth="90">
            <u-input
              v-model="form.petBreed"
              placeholder="可选，如 英短/金毛"
              border="surround"
              clearable
            />
          </u-form-item>
        </view>

        <!-- 丢失信息部分 -->
        <view class="form-section">
          <view class="form-section__title">
            <text class="form-section__icon">📍</text>
            <text class="form-section__text">丢失信息</text>
          </view>
          <u-form-item label="丢失城市" prop="city" :labelWidth="90">
            <view class="fake-input" @tap="openCitySheet">{{
              cityDisplay || '请选择丢失城市'
            }}</view>
            <u-action-sheet
              :show="showCity"
              :actions="cityActions"
              title="选择城市"
              @select="onSelectCity"
              @close="showCity = false"
              @cancel="showCity = false"
            />
          </u-form-item>
          <u-form-item label="具体地点" prop="address" :labelWidth="90">
            <u-input
              v-model="form.address"
              placeholder="道路、小区、门牌号"
              border="surround"
              clearable
            />
          </u-form-item>
          <u-form-item label="丢失时间" prop="lostTime" :labelWidth="90">
            <view class="fake-input" @tap="openTimePicker">{{
              lostTimeText
            }}</view>
            <u-datetime-picker
              :show="showTimePicker"
              v-model="pickerTime"
              :minDate="minDate"
              :maxDate="maxDate"
              mode="datetime"
              @confirm="onConfirmTime"
              @change="onChangeTime"
              @cancel="showTimePicker = false"
              @close="showTimePicker = false"
            />
          </u-form-item>
          <u-form-item label="情况描述" prop="petDescription" :labelWidth="90">
            <u-textarea
              v-model="form.petDescription"
              placeholder="如毛色、特征、项圈等（可选）"
              count
              border="surround"
            />
          </u-form-item>
        </view>

        <!-- 联系与悬赏部分 -->
        <view class="form-section">
          <view class="form-section__title">
            <text class="form-section__icon">☎️</text>
            <text class="form-section__text">联系与悬赏</text>
          </view>
          <u-form-item label="联系方式" prop="contactInfo" :labelWidth="90">
            <u-input
              v-model="form.contactInfo"
              placeholder="手机号或微信号"
              type="number"
              @focus="onContactFocus"
              border="surround"
              clearable
            />
          </u-form-item>
          <u-form-item label="悬赏金额" prop="reward" :labelWidth="90">
            <u-input
              v-model="form.reward"
              type="number"
              placeholder="如 500"
              border="surround"
              clearable
            />
          </u-form-item>
        </view>

        <!-- 图片上传部分 -->
        <view class="form-section">
          <view class="form-section__title">
            <text class="form-section__icon">📷</text>
            <text class="form-section__text">宠物照片</text>
          </view>
          <view class="upload-grid">
            <view v-for="(img, idx) in imageList" :key="idx" class="img-item">
              <image :src="img.url" mode="aspectFill" />
            </view>
            <view class="img-item add" @click="chooseAndUpload">
              <view class="inner">
                <text class="plus">＋</text>
                <text class="hint">添加图片</text>
              </view>
            </view>
          </view>
          <view class="upload-tips"
            >💡 最多上传6张，优先选择正脸、清晰照片</view
          >
        </view>
      </u-form>
    </view>

    <!-- 底部提交按钮 -->
    <view class="publish-footer">
      <view
        class="btn btn--primary btn--large btn--block"
        :class="{ 'btn--disabled': !canSubmit }"
        @click="submit"
      >
        <text class="btn-text">🎯 提交发布</text>
      </view>
    </view>

    <u-toast ref="toast" />
  </view>
</template>

<script setup>
  import { ref, computed, onMounted } from 'vue'
  import dayjs from 'dayjs'
  import { request, upload } from '@/common/request'
  import { BASE_URL, STORAGE_KEYS } from '@/common/config'

  const form = ref({
    petName: '',
    petType: '猫',
    city: '',
    address: '',
    // 默认当前时间（包含分钟）
    lostTime: dayjs().valueOf(),
    contactInfo: '',
    reward: '',
    petBreed: '',
    petDescription: '',
  })
  // 城市列表
  const showCity = ref(false)
  const cityActions = ref([])
  const cityDisplay = ref('')
  const openCitySheet = () => {
    showCity.value = true
  }
  const onSelectCity = e => {
    const name = e?.name || e?.text || ''
    form.value.city = name
    cityDisplay.value = name
    showCity.value = false
  }
  const imageList = ref([]) // { url }
  // 宠物名称不允许换行与回车
  const onPetNameInput = val => {
    if (typeof val === 'string') {
      const cleaned = val.replace(/[\r\n]+/g, ' ')
      if (cleaned !== form.value.petName) form.value.petName = cleaned
    }
  }

  // 输入框聚焦事件
  const onInputFocus = e => {
    console.log('[Publish] 输入框聚焦:', e)
  }

  // 联系方式聚焦事件
  const onContactFocus = e => {
    console.log('[Publish] 联系方式聚焦:', e)
    // 如果联系方式为空且有用户信息，尝试重新填充
    if (!form.value.contactInfo) {
      initUserInfo()
    }
  }

  const minDate = Date.now() - 3600 * 24 * 365 * 1000 // 一年前
  const maxDate = Date.now()
  const showTimePicker = ref(false)
  const pickerTime = ref(dayjs().valueOf())
  const openTimePicker = () => {
    pickerTime.value =
      form.value.lostTime || dayjs().minute(0).second(0).valueOf()
    showTimePicker.value = true
  }
  const onConfirmTime = e => {
    let ts = pickerTime.value
    if (typeof e === 'number') ts = e
    else if (typeof e?.value === 'number') ts = e.value
    else if (typeof e === 'string') ts = dayjs(e).valueOf()
    else if (typeof e?.value === 'string') ts = dayjs(e.value).valueOf()
    form.value.lostTime = ts
    pickerTime.value = ts
    showTimePicker.value = false
  }

  const onChangeTime = e => {
    // 实时回显选中的时间到分钟
    let ts = pickerTime.value
    if (typeof e === 'number') ts = e
    else if (typeof e?.value === 'number') ts = e.value
    else if (typeof e === 'string') ts = dayjs(e).valueOf()
    else if (typeof e?.value === 'string') ts = dayjs(e.value).valueOf()
    const rounded = dayjs(ts).second(0).valueOf()
    pickerTime.value = rounded
    form.value.lostTime = rounded
  }

  // 允许选择分钟，不再强制为 00

  // 回显到分钟：YYYY-MM-DD HH:mm
  const lostTimeText = computed(() =>
    dayjs(form.value.lostTime || dayjs().valueOf()).format('YYYY-MM-DD HH:mm')
  )

  const canSubmit = computed(() => {
    const f = form.value
    return (
      f.petName &&
      f.petType &&
      f.city &&
      f.address &&
      f.lostTime &&
      f.contactInfo
    )
  })

  const chooseAndUpload = () => {
    uni.chooseImage({
      count: Math.max(0, 6 - imageList.value.length),
      success: async res => {
        console.log('[chooseImage] success:', res)
        const files = res.tempFilePaths || []
        for (const [idx, localPath] of files.entries()) {
          try {
            console.log('[upload] start:', { idx, localPath })
            const resp = await upload(localPath, { type: 'pet' })
            console.log('[upload] response:', resp)
            const url = resolveUploadUrl(resp)
            if (url) {
              const displayUrl =
                typeof url === 'string' && url.startsWith('/uploads/')
                  ? `${BASE_URL}${url}`
                  : url
              imageList.value.push({ url: displayUrl, raw: url })
              console.log('[upload] push url to list:', {
                displayUrl,
                raw: url,
              })
            } else {
              console.warn('[upload] no url resolved from response:', resp)
              uni.showToast({ title: '上传返回无图片地址', icon: 'none' })
            }
          } catch (e) {
            console.error('[upload] error:', e)
            uni.showToast({ title: '图片上传失败', icon: 'none' })
          }
        }
      },
      fail: err => {
        console.error('[chooseImage] fail:', err)
        uni.showToast({ title: '选择图片失败', icon: 'none' })
      },
    })
  }

  function resolveUploadUrl(resp) {
    try {
      if (!resp) return ''
      if (typeof resp === 'string') {
        try {
          const obj = JSON.parse(resp)
          return resolveUploadUrl(obj)
        } catch {
          return ''
        }
      }
      if (resp.url && typeof resp.url === 'string') return resp.url
      if (resp.data) {
        if (typeof resp.data === 'string') {
          try {
            const dataObj = JSON.parse(resp.data)
            // 常见后端返回结构 { url }, { data: { url } }
            if (dataObj.url) return dataObj.url
            if (dataObj.data?.url) return dataObj.data.url
            if (dataObj.path) return dataObj.path
          } catch (e) {
            // 不是 JSON 字符串
          }
        } else if (typeof resp.data === 'object') {
          if (resp.data.url) return resp.data.url
          if (resp.data.data?.url) return resp.data.data.url
          if (resp.data.path) return resp.data.path
        }
      }
      if (resp.path) return resp.path
      return ''
    } catch (e) {
      console.error('[resolveUploadUrl] error:', e)
      return ''
    }
  }

  const submit = async () => {
    const lostTimeStr = dayjs(form.value.lostTime || Date.now()).format(
      'YYYY-MM-DD HH:mm:ss'
    )
    const imgs = imageList.value.map(i => i.raw || i.url)
    const payload = {
      userId: 1,
      petName: form.value.petName,
      petType: form.value.petType,
      petBreed: form.value.petBreed,
      petDescription: form.value.petDescription,
      city: form.value.city,
      address: form.value.address,
      lostTime: lostTimeStr,
      contactInfo: form.value.contactInfo,
      reward: form.value.reward,
      images: imgs,
    }
    try {
      const response = await request({
        url: '/api/lost-pets/json',
        method: 'POST',
        data: payload,
      })
      console.log('[Publish] 发布成功响应:', response)

      // 获取返回的宠物数据（包含真实ID）
      const createdPet = response?.data || response
      const temp = {
        id: createdPet?.id || Date.now(),
        ...payload,
        ...createdPet,
      }

      console.log('[Publish] 保存临时数据:', temp)
      uni.setStorageSync('temp_published_pet', temp)
      uni.redirectTo({ url: '/pages/publish/success' })
    } catch (e) {
      console.error('[Publish] 发布失败:', e)
      uni.showToast({ title: '发布失败', icon: 'none' })
    }
  }

  // 初始化用户信息
  const initUserInfo = () => {
    try {
      const userInfo = uni.getStorageSync(STORAGE_KEYS.userInfo)
      console.log('[Publish] 获取用户信息:', userInfo)

      if (userInfo) {
        const userObj =
          typeof userInfo === 'string' ? JSON.parse(userInfo) : userInfo
        console.log('[Publish] 解析后的用户信息:', userObj)

        // 自动填充联系方式（手机号）
        const phoneNumber =
          userObj?.phoneNumber || userObj?.phone || userObj?.username
        if (phoneNumber && !form.value.contactInfo) {
          form.value.contactInfo = phoneNumber
          console.log('[Publish] 联系方式已自动填充:', phoneNumber)
        }
      } else {
        console.log('[Publish] 未找到用户信息')
      }
    } catch (error) {
      console.error('[Publish] 用户信息解析失败:', error)
    }
  }

  // 加载城市
  ;(async function loadCities() {
    try {
      const res = await request({ url: '/api/config/cities' })
      const arr = Array.isArray(res?.data)
        ? res.data
        : Array.isArray(res)
        ? res
        : []
      cityActions.value = arr.map(name => ({ name }))
    } catch (e) {
      console.error('[publish] load cities error:', e)
    }
  })()

  // 页面加载时初始化
  onMounted(() => {
    initUserInfo()
  })
</script>

<style lang="scss" scoped>
  .page {
    min-height: 100vh;
    background-color: #f7f8fa;
  }

  .page-content {
    padding: 16px;
    padding-bottom: 100px; /* 为底部按钮预留空间 */
  }
  .publish-header {
    background: linear-gradient(135deg, #5b8ff9 0%, #36cfc9 100%);
    color: white;
    padding: 24px 16px 16px;
  }

  .publish-header__content {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .publish-header__emoji {
    font-size: 32px;
    line-height: 1;
  }

  .publish-header__text {
    flex: 1;
  }

  .publish-header__title {
    display: block;
    font-size: 18px;
    font-weight: 700;
    margin-bottom: 4px;
  }

  .publish-header__subtitle {
    display: block;
    font-size: 12px;
    opacity: 0.9;
  }
  /* 表单分组样式 */
  .form-section {
    background: #ffffff;
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  .form-section__title {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #f0f0f0;
  }

  .form-section__icon {
    font-size: 16px;
    line-height: 1;
  }

  .form-section__text {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }

  .upload-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 10px;
    padding: 6px 0 12px;
  }
  .img-item {
    width: 100%;
    padding-top: 100%;
    position: relative;
    border-radius: 12px;
    overflow: hidden;
    background: #f2f3f5;
    border: 1px dashed #e5e6eb;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .img-item image {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
  }
  .img-item.add {
    color: #999;
  }
  .img-item.add .inner {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: absolute;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
  }
  .img-item .plus {
    font-size: 28px;
    line-height: 1;
  }
  .img-item .hint {
    font-size: 12px;
  }
  .tips {
    color: #999;
    font-size: 12px;
    padding: 0 2px 10px;
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

  .fake-input {
    height: 36px;
    line-height: 36px;
    background: #fff; /* 与 u-input 一致 */
    border: 1px solid #e5e6eb;
    border-radius: 8px;
    padding: 0 12px;
    color: #333;
  }

  /* 表单项样式优化 */
  :deep(.u-form-item) {
    margin-bottom: 16px;
  }

  :deep(.u-form-item__label) {
    font-weight: 500;
    color: #606266;
    margin-bottom: 8px;
  }

  :deep(.u-form-item__body__content) {
    width: 100%;
  }

  /* uView输入框样式调整 */
  :deep(.u-input) {
    width: 100% !important;
    pointer-events: auto !important;
  }

  :deep(.u-input__content) {
    width: 100% !important;
    pointer-events: auto !important;
  }

  :deep(.u-input__inner) {
    width: 100% !important;
    pointer-events: auto !important;
    background: #fff !important;
    border: 1px solid #e5e6eb !important;
    border-radius: 8px !important;
    padding: 12px 16px !important;
    font-size: 14px !important;
    line-height: 1.5 !important;
  }

  :deep(.u-textarea) {
    width: 100% !important;
    pointer-events: auto !important;
  }

  :deep(.u-textarea__inner) {
    width: 100% !important;
    pointer-events: auto !important;
    background: #fff !important;
    border: 1px solid #e5e6eb !important;
    border-radius: 8px !important;
    padding: 12px 16px !important;
    font-size: 14px !important;
    line-height: 1.5 !important;
    min-height: 80px !important;
  }

  /* 单选框组样式 */
  :deep(.u-radio-group) {
    display: flex;
    gap: 16px;
    flex-wrap: wrap;
  }

  :deep(.u-radio) {
    margin-right: 0;
  }
</style>
