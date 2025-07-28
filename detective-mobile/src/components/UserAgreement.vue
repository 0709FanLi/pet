<template>
  <div class="modal" v-if="show">
    <div class="modal-content">
      <h2>用户协议</h2>
      <p>欢迎使用宠物侦探接单平台。本协议是您与平台之间达成的法律协议。请仔细阅读。</p>
      <p>1. 服务内容：平台提供宠物找寻接单服务，您作为宠物侦探同意遵守平台规则。</p>
      <p>2. 用户义务：您需提供真实信息，进行实名认证，并诚实履行找寻义务。</p>
      <p>3. 平台权利：平台有权审核、暂停或终止您的账户。</p>
      <p>4. 免责声明：平台不对找寻结果承担责任。</p>
      <p>5. 协议变更：平台可随时修改协议， continued use constitutes acceptance。</p>
      <!-- 更多条款基于需求文档 -->
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