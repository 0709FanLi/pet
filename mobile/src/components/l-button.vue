<template>
  <button
    class="l-button"
    :class="[type, { disabled: disabled, loading: loading }]"
    :disabled="disabled || loading"
    @click="handleClick"
  >
    <span v-if="loading" class="loading-spinner"></span>
    <slot>{{ text }}</slot>
  </button>
</template>

<script setup>
import { ref } from 'vue'
const props = defineProps({
  text: String,
  type: { type: String, default: 'primary' },
  disabled: Boolean,
  loading: Boolean
})
const emit = defineEmits(['click'])
const handleClick = (e) => {
  if (!props.disabled && !props.loading) {
    emit('click', e)
  }
}
</script>

<style scoped>
.l-button {
  height: 44px;
  padding: 0 16px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 100px;
}

.primary {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: #FFFFFF;
}

.primary:hover:not(.disabled) {
  opacity: 0.9;
  transform: translateY(-1px);
}

.secondary {
  background: #f5f7fa;
  color: #667eea;
  border: 1px solid #667eea;
}

.secondary:hover:not(.disabled) {
  background: #e6e9f0;
}

.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.loading {
  cursor: wait;
}

.loading-spinner {
  margin-right: 8px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top: 2px solid #fff;
  border-radius: 50%;
  width: 16px;
  height: 16px;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>