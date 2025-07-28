<template>
  <div class="modal" v-if="show">
    <div class="modal-content">
      <h2>隐私政策</h2>
      <p>本隐私政策说明我们如何收集、使用和保护您的个人信息。请仔细阅读。</p>
      <p>1. 信息收集：我们收集注册信息、位置数据、宠物信息等必要数据。</p>
      <p>2. 信息使用：用于提供服务、匹配订单、改进平台。</p>
      <p>3. 信息共享：仅在必要时与合作伙伴共享，不出售个人信息。</p>
      <p>4. 数据安全：采用加密等措施保护数据。</p>
      <p>5. 用户权利：您可访问、修改或删除个人信息。</p>
      <p>6. 政策变更：我们可更新政策，通知用户。</p>
      <!-- 更多细节基于需求 -->
      <button @click="close">同意并关闭</button>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    show: Boolean
  },
  methods: {
    close() {
      this.$emit('close');
    }
  }
}
</script>

<style>
.modal { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; }
.modal-content { background: white; padding: 20px; border-radius: 8px; max-width: 80%; }
</style>