<!--
  通用弹框组件 - Common Dialog
  
  实现思路：
  1. 自定义弹框，不使用Element Plus
  2. 关闭按钮在弹框左上角
  3. 支持自定义内容插槽
  4. 支持多种关闭方式（按钮、ESC、背景点击）
  5. 基于Figma设计图的样式实现
  6. 支持基于屏幕宽度的自适应缩放功能
-->

<template>
  <teleport to="body">
    <transition name="dialog-fade">
      <div 
        v-if="visible" 
        class="common-dialog-overlay"
        @click="handleOverlayClick"
          @keydown.esc="handleEscapeKey"
          @keyup.esc="handleEscapeKey"
          @keydown="handleOverlayKeydown"
          @keyup="handleOverlayKeyup"
          tabindex="0"
          ref="overlayRef"
      >
        <div 
          class="common-dialog-container"
          :style="containerStyle"
          @click.stop
        >
          <!-- 关闭按钮 - 左上角 -->
          <l-img 
              @click="handleClose"
              :src="'/static/images/modal-close.png'" 
              :w="closeButtonSize"
              :h="closeButtonSize"
              class="dialog-close-btn"
              :style="closeButtonStyle"
              :title="t('close')"
            />
          <!-- 弹框内容区域 -->
          <div class="dialog-content">
            <slot></slot>
          </div>
        </div>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import { ref, watch, nextTick, onMounted, onUnmounted, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import LImg from '@/components/l-img.vue';
import { pxToRem, convertStyleObject } from '@/utils/px-to-rem';

// 多语言支持
const { t } = useI18n();

// Props
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  closeOnClickModal: {
    type: Boolean,
    default: true
  },
  closeOnPressEscape: {
    type: Boolean,
    default: true
  },
  isScale: {
    type: Boolean,
    default: false
  }
});

// Emits
const emit = defineEmits(['update:modelValue', 'close']);

// 响应式数据
const visible = ref(props.modelValue);
const screenWidth = ref(window.innerWidth);
const overlayRef = ref(null);

// 设计基准宽度
const DESIGN_WIDTH = 1920;

// 计算缩放比例
const scaleRatio = computed(() => {
  if (!props.isScale) {
    return 1; // 不启用缩放时，比例为1
  }
  
  if (screenWidth.value >= DESIGN_WIDTH) {
    return 1; // 屏幕宽度大于等于设计宽度时，不缩放
  }
  
  // 计算缩放比例，最小不低于0.5
  const ratio = screenWidth.value / DESIGN_WIDTH;
  return Math.max(ratio, 0.5);
});

// 弹框容器样式
const containerStyle = computed(() => {
  if (!props.isScale || scaleRatio.value === 1) {
    return {};
  }
  
  return {
    transform: `scale(${scaleRatio.value})`,
    transformOrigin: 'center center'
  };
});

// 关闭按钮尺寸
const closeButtonSize = computed(() => {
  const baseSize = 67;
  if (!props.isScale || scaleRatio.value === 1) {
    return pxToRem(baseSize + 'px');
  }
  return pxToRem(`${Math.round(baseSize * scaleRatio.value)}px`);
});

// 关闭按钮样式
const closeButtonStyle = computed(() => {
  if (!props.isScale || scaleRatio.value === 1) {
    return convertStyleObject({
      top: '-9px',
      left: '-15px'
    });
  }
  
  const baseOffset = 28;
  const scaledOffset = Math.round(baseOffset * scaleRatio.value);
  
  return convertStyleObject({
    top: `-${scaledOffset}px`,
    left: `-${scaledOffset}px`
  });
});

// 监听窗口尺寸变化
const handleResize = () => {
  screenWidth.value = window.innerWidth;
};

// 监听modelValue变化
watch(() => props.modelValue, (newVal) => {
  visible.value = newVal;
  if (newVal) {
    // 弹框打开时更新屏幕宽度
    screenWidth.value = window.innerWidth;
    nextTick(() => {
      // 弹框打开时聚焦到容器，以便ESC键生效（Mac兼容）
      if (overlayRef.value) {
        // 多次尝试聚焦，确保Mac系统正确响应
        overlayRef.value.focus();
        setTimeout(() => {
          if (overlayRef.value) {
            overlayRef.value.focus();
          }
        }, 100);
      }
    });
  } else {
    console.log('Dialog closed');
      }
    });

// 监听visible变化，同步到父组件
watch(visible, (newVal) => {
  if (newVal !== props.modelValue) {
    emit('update:modelValue', newVal);
  }
});

// 事件处理函数
const handleClose = () => {
    visible.value = false;
    emit('update:modelValue', false);
    emit('close');
};

const handleOverlayClick = () => {
  if (props.closeOnClickModal) {
    handleClose();
  }
};

// ESC键处理函数
const handleEscapeKey = () => {
  if (props.closeOnPressEscape && visible.value) {
    handleClose();
  }
};

// Overlay键盘事件处理（Mac兼容）
const handleOverlayKeydown = (event) => {
  if (event.key === 'Escape' || event.keyCode === 27 || event.code === 'Escape') {
    event.preventDefault();
    event.stopPropagation();
    handleEscapeKey();
  }
};

const handleOverlayKeyup = (event) => {
  if (event.key === 'Escape' || event.keyCode === 27 || event.code === 'Escape') {
    event.preventDefault();
    event.stopPropagation();
    handleEscapeKey();
  }
};

// 键盘事件处理
const handleKeydown = (event) => {
  // 支持多种ESC键识别方式，兼容Mac系统
  if (event.key === 'Escape' || event.keyCode === 27 || event.code === 'Escape') {
    event.preventDefault();
    event.stopPropagation();
    handleEscapeKey();
  }
};

// 生命周期
onMounted(() => {
  // 添加全局键盘事件监听，使用多种方式确保Mac兼容性
  document.addEventListener('keydown', handleKeydown, true);
  document.addEventListener('keyup', handleKeydown, true);
  window.addEventListener('keydown', handleKeydown, true);
  window.addEventListener('resize', handleResize);
  // 初始化屏幕宽度
  screenWidth.value = window.innerWidth;
});

onUnmounted(() => {
  // 清理事件监听
  document.removeEventListener('keydown', handleKeydown, true);
  document.removeEventListener('keyup', handleKeydown, true);
  window.removeEventListener('keydown', handleKeydown, true);
  window.removeEventListener('resize', handleResize);
});
</script>

<style scoped lang="scss">
/* 弹框过渡动画 */
.dialog-fade-enter-active,
.dialog-fade-leave-active {
  transition: opacity 0.3s ease;
}

.dialog-fade-enter-from,
.dialog-fade-leave-to {
  opacity: 0;
}

.dialog-fade-enter-active .common-dialog-container,
.dialog-fade-leave-active .common-dialog-container {
  transition: transform 0.3s ease;
}

.dialog-fade-enter-from .common-dialog-container,
.dialog-fade-leave-to .common-dialog-container {
  transform: scale(0.9);
}

/* 弹框覆盖层 */
.common-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.22); // 参考设计图的透明度
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  outline: none;
}

/* 弹框容器 */
.common-dialog-container {
  position: relative;
  background: var(--bg-primary);
  border-radius: 48px; // 参考设计图的圆角
  box-shadow: 0px 3.11px 12.44px 0px rgba(0, 0, 0, 0.1); // 参考设计图的阴影
  border: 1px solid rgba(0, 0, 0, 0.22);
  box-sizing: border-box;
  transition: transform 0.3s ease; // 添加缩放过渡动画
}

/* 关闭按钮 - 左上角位置 */
.dialog-close-btn {
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
  transition: all 0.3s ease; // 添加过渡动画
}

.close-icon {
  width: 16px;
  height: 16px;
}

/* 弹框内容区域 */
.dialog-content {
  width: 100%;
  height: 100%;
  position: relative;
}
</style> 