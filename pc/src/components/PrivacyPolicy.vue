<template>
  <div class="modal" v-if="show">
    <div class="modal-content">
      <h2>隐私政策</h2>
      <p>本隐私政策说明我们如何收集、使用和保护您的个人信息。请仔细阅读。</p>
      <p>1. 信息收集：收集注册、宠物信息等。</p>
      <p>2. 信息使用：用于服务提供、订单匹配。</p>
      <p>3. 信息共享：必要时共享。</p>
      <p>4. 数据安全：保护措施。</p>
      <p>5. 用户权利：访问修改。</p>
      <p>6. 政策变更：更新通知。</p>
      <!-- 基于PC端调整 -->
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