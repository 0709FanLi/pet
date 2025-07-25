<template>
  <button 
    class="l-button"
    :class="{ 'is-disabled': disabled, 'is-loading': loading }"
    :disabled="disabled || loading"
    :style="buttonStyle"
    @click="handleClick"
  >
    <span v-if="loading" class="loading-icon">
      <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" width="16" height="16">
        <path 
          d="M512 64c-247.4 0-448 200.6-448 448s200.6 448 448 448 448-200.6 448-448-200.6-448-448-448zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z" 
          fill="#ffffff" 
          opacity="0.5" 
        />
        <path 
          d="M512 140c-205.4 0-372 166.6-372 372s166.6 372 372 372 372-166.6 372-372-166.6-372-372-372zm0 64c170.5 0 308 137.5 308 308s-137.5 308-308 308-308-137.5-308-308 137.5-308 308-308z" 
          fill="#ffffff" 
          class="loading-circle" 
        />
      </svg>
    </span>
    <span>{{ loading && loadingText ? loadingText : text }}</span>
  </button>
</template>

<script setup>
import { computed } from 'vue';
import { pxToRem } from '@/utils/px-to-rem';

// 明确声明props
const props = defineProps({
  /**
   * 按钮文本
   */
  text: {
    type: String,
    required: true
  },
  /**
   * 加载状态下的按钮文本
   */
  loadingText: {
    type: String,
    default: ''
  },
  /**
   * 按钮宽度，支持像素值或百分比
   */
  width: {
    type: String,
    default: '100%'
  },
  /**
   * 按钮高度，支持像素值
   */
  height: {
    type: String,
    default: '42px'
  },
  /**
   * 按钮圆角大小
   */
  borderRadius: {
    type: String,
    default: '12px'
  },
  /**
   * 按钮禁用状态
   */
  disabled: {
    type: Boolean,
    default: false
  },
  /**
   * 按钮加载状态
   */
  loading: {
    type: Boolean,
    default: false
  },
  /**
   * 按钮背景颜色
   */
  backgroundColor: {
    type: String,
    default: '#000000'
  },
  /**
   * 按钮文字颜色
   */
  textColor: {
    type: String,
    default: '#FFFFFF'
  }
});

// 明确声明emits
const emits = defineEmits(['onClick']);

// 计算按钮样式
const buttonStyle = computed(() => {
  // 自动处理px到rem的转换
  const convertValue = (value) => {
    if (typeof value === 'string' && value.endsWith('px')) {
      return pxToRem(value);
    }
    return value;
  };

  return {
    width: convertValue(props.width),
    height: convertValue(props.height),
    borderRadius: convertValue(props.borderRadius),
    backgroundColor: props.backgroundColor,
    color: props.textColor
  };
});

// 点击事件处理
const handleClick = (event) => {
  if (!props.disabled && !props.loading) {
    emits('onClick', event);
  }
};
</script>

<style scoped>
.l-button {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  border: none;
  cursor: pointer;
  font-size: 16px;
  transition: opacity 0.3s;
  box-sizing: border-box;
  padding: 0 16px;
  margin-bottom: 16px;
}

.l-button.is-disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.l-button.is-loading {
  opacity: 0.8;
  cursor: wait;
}

.loading-icon {
  margin-right: 8px;
  animation: spin 1.5s linear infinite;
  display: inline-block;
  display: flex;
  justify-content: center;
  align-items: center;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.loading-circle {
  transform-origin: center;
}
</style> 