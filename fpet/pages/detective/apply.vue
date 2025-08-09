<template>
  <view class="apply-page">
    <view class="header">
      <text class="emoji">🐾</text>
      <view class="texts">
        <text class="title">申请成为宠物侦探</text>
        <text class="sub">完善资料，审核通过即可接单帮助更多小动物回家</text>
      </view>
    </view>

    <u-form :model="form" ref="formRef" class="form-wrap" :labelWidth="90">
      <view class="section">
        <view class="section-title">🧑 基础信息</view>
        <u-form-item label="真实姓名" prop="realName" required>
          <u-input v-model="form.realName" placeholder="请输入真实姓名" />
        </u-form-item>
        <u-form-item label="手机号" prop="phone" required>
          <u-input v-model="form.phone" placeholder="请输入手机号" disabled />
        </u-form-item>
        <u-form-item label="所在城市" prop="city" required>
          <u-input v-model="form.city" placeholder="如：上海市" />
        </u-form-item>
        <u-form-item label="公司名称" prop="companyName">
          <u-input v-model="form.companyName" placeholder="选填" />
        </u-form-item>
        <u-form-item label="常驻地址" prop="address">
          <u-input v-model="form.address" placeholder="选填" />
        </u-form-item>
      </view>

      <view class="section">
        <view class="section-title">🤝 团队与设备</view>
        <u-form-item label="团队人数" prop="teamSize" required>
          <u-input
            v-model.number="form.teamSize"
            type="number"
            placeholder="请输入人数"
          />
        </u-form-item>
        <u-form-item label="设备清单" prop="devices" required>
          <u-checkbox-group v-model="form.devices">
            <u-checkbox
              v-for="d in deviceOptions"
              :key="d"
              :name="d"
              :label="d"
            />
          </u-checkbox-group>
        </u-form-item>
        <u-form-item label="设备照片" prop="devicePhotos" required>
          <view class="upload-grid">
            <view
              v-for="(img, idx) in deviceImages"
              :key="idx"
              class="img-item"
            >
              <image :src="img.url" mode="aspectFill" />
            </view>
            <view
              v-if="deviceImages.length < 6"
              class="img-item add"
              @click="chooseDeviceImage"
            >
              <view class="inner"
                ><text class="plus">＋</text
                ><text class="hint">添加照片</text></view
              >
            </view>
          </view>
        </u-form-item>
      </view>

      <view class="section">
        <view class="section-title">🧭 能力与时间</view>
        <u-form-item label="经验年限" prop="experienceYears" required>
          <u-input
            v-model.number="form.experienceYears"
            type="number"
            placeholder="如：3"
          />
        </u-form-item>
        <u-form-item label="服务范围" prop="serviceAreas" required>
          <u-input
            v-model="serviceAreasText"
            placeholder="多城市用顿号分隔，如：上海、苏州"
            @blur="onAreasBlur"
          />
        </u-form-item>
        <u-form-item label="可服务时间" prop="availableTimes" required>
          <u-checkbox-group v-model="form.availableTimes">
            <u-checkbox
              v-for="t in timeOptions"
              :key="t"
              :name="t"
              :label="t"
            />
          </u-checkbox-group>
        </u-form-item>
        <u-form-item label="个人简介" prop="bio">
          <u-textarea
            v-model="form.bio"
            placeholder="选填：擅长领域、经验介绍"
            autoHeight
          />
        </u-form-item>
      </view>

      <view class="section">
        <view class="section-title">🪪 认证与资质</view>
        <u-form-item label="身份证正面" prop="idCardFront" required>
          <u-button size="small" @click="pickId('front')">上传图片</u-button>
          <image
            v-if="form.idCardFront"
            class="thumb"
            :src="display(form.idCardFront)"
          />
        </u-form-item>
        <u-form-item label="身份证反面" prop="idCardBack" required>
          <u-button size="small" @click="pickId('back')">上传图片</u-button>
          <image
            v-if="form.idCardBack"
            class="thumb"
            :src="display(form.idCardBack)"
          />
        </u-form-item>
        <u-form-item label="资质/证书" prop="certificates">
          <u-button size="small" @click="pickCert">上传图片</u-button>
          <view class="upload-grid small">
            <view v-for="(img, idx) in certImages" :key="idx" class="img-item">
              <image :src="img.url" mode="aspectFill" />
            </view>
          </view>
        </u-form-item>
      </view>

      <view class="section">
        <view class="section-title">💳 收款与确认</view>
        <u-form-item label="收款方式" prop="payout.type" required>
          <u-radio-group v-model="form.payout.type" shape="circle">
            <u-radio name="wechat" label="微信" />
            <u-radio name="alipay" label="支付宝" />
          </u-radio-group>
        </u-form-item>
        <u-form-item label="收款账号" prop="payout.account" required>
          <u-input
            v-model="form.payout.account"
            placeholder="请输入微信号或支付宝账号"
          />
        </u-form-item>
        <u-form-item label="同意协议" prop="agree" required>
          <u-checkbox
            v-model="form.agree"
            :name="'agree'"
            label="我已阅读并同意《侦探服务条款》《隐私政策》"
          />
        </u-form-item>
      </view>
    </u-form>

    <view class="bottom">
      <u-button
        type="primary"
        :disabled="!canSubmit"
        @click="submit"
        :customStyle="primaryStyle"
        >提交申请</u-button
      >
    </view>

    <u-toast ref="toast" />
  </view>
</template>

<script setup>
  import { ref, reactive, computed, onMounted } from 'vue'
  import { API, BASE_URL, STORAGE_KEYS } from '@/common/config'
  import { request, upload } from '@/common/request'

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
  const onAreasBlur = () => {
    form.serviceAreas = serviceAreasText.value
      .split(/[、,，\s]+/)
      .map(s => s.trim())
      .filter(Boolean)
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

  const submit = async () => {
    try {
      const payload = JSON.parse(JSON.stringify(form))
      const res = await request({
        url: API.detective.apply,
        method: 'POST',
        data: payload,
      })
      uni.showToast({ title: '提交成功', icon: 'success' })
      setTimeout(() => uni.navigateBack(), 1000)
    } catch (e) {
      uni.showToast({ title: '提交失败', icon: 'none' })
    }
  }

  onMounted(() => {
    const u = uni.getStorageSync(STORAGE_KEYS.userInfo)
    if (u) {
      const obj = typeof u === 'string' ? JSON.parse(u) : u
      form.phone = obj?.phone || obj?.phoneNumber || ''
    }
  })
</script>

<style lang="scss" scoped>
  .apply-page {
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
  .section-title {
    font-weight: 700;
    color: #333;
    padding: 8px 2px 10px;
  }
  .upload-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 10px;
    padding: 6px 0 12px;
  }
  .upload-grid.small {
    grid-template-columns: repeat(5, 1fr);
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
    inset: 0;
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
    inset: 0;
  }
  .plus {
    font-size: 28px;
    line-height: 1;
  }
  .hint {
    font-size: 12px;
  }
  .thumb {
    width: 80px;
    height: 60px;
    margin-left: 10px;
    border-radius: 6px;
    border: 1px solid #eee;
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
