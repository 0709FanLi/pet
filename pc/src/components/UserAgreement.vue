<template>
  <div class="modal" v-if="show">
    <div class="modal-content">
      <h2>用户协议</h2>
      <p>欢迎使用宠物找回平台PC端。本协议是您与平台之间达成的法律协议。请仔细阅读。</p>
      <p>1. 服务内容：提供宠物丢失发布、订单管理等服务。</p>
      <p>2. 用户义务：提供真实信息，遵守规则。</p>
      <p>3. 平台权利：审核内容，终止服务。</p>
      <p>4. 免责声明：不对结果负责。</p>
      <p>5. 协议变更：可修改，继续使用即接受。</p>
      <!-- 基于PC端需求调整 -->
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