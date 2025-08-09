<template>
  <view class="pending-page">
    <view class="card">
      <view class="icon">⏳</view>
      <view class="title">申请已提交，等待审核</view>
      <view class="desc"
        >我们将在 24
        小时内完成审核，请保持电话畅通。如需补充资料可在此页面返回后再次进入申请页修改并重新提交。</view
      >
      <u-button type="primary" @click="goHome" :customStyle="primaryStyle"
        >返回首页</u-button
      >
    </view>
  </view>
</template>

<script setup>
  const primaryStyle = {
    background:
      'linear-gradient(135deg, var(--brand-gradient-start), var(--brand-gradient-end))',
    border: 'none',
  }
  const goHome = () => uni.switchTab({ url: '/pages/home/home' })
</script>

<style scoped>
  .pending-page {
    min-height: 100vh;
    background: #f7f8fa;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 24px;
  }
  .card {
    background: #fff;
    border-radius: 12px;
    padding: 24px;
    text-align: center;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
    max-width: 520px;
  }
  .icon {
    font-size: 48px;
    margin-bottom: 12px;
  }
  .title {
    font-size: 18px;
    font-weight: 700;
    margin-bottom: 8px;
  }
  .desc {
    color: #666;
    font-size: 14px;
    line-height: 1.6;
    margin-bottom: 16px;
  }
</style>
