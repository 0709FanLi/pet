<!--
  全局视频组件 - L-Video Component
  
  功能说明：
  1. 支持本地和远程视频源自动识别和处理
  2. 提供完整的HTML5 video属性支持
  3. 支持自定义样式和事件处理
  4. 加载状态管理和错误处理
  5. 支持视频预加载和懒加载
  6. 自定义视频控制器（替代浏览器默认控制器）
  
  使用示例：
  <l-video 
    src="/static/videos/demo.mp4" 
    w="640px" 
    h="360px" 
    :show-controls="true"
    @play="handlePlay"
  />
-->

<template>
  <div class="l-video-container" :style="containerStyle">
    <!-- 加载状态显示 -->
    <div v-if="isLoading" class="video-loading" :style="videoStyle">
      <div class="loading-content">
        <l-img src="/static/images/video-loading.png" w="40px" h="40px" />
        <span class="loading-text">{{ t('videoLoading') || 'Loading video...' }}</span>
      </div>
    </div>
    
    <!-- 视频元素 -->
    <video
      v-show="!isLoading"
      ref="videoRef"
      :src="parsedSrc"
      :poster="parsedPoster"
      :controls="false"
      :controlslist="isFullscreen ? 'nodownload nofullscreen noremoteplayback' : ''"
      :autoplay="autoplay"
      :loop="loop"
      :muted="muted"
      :preload="preload"
      :playsinline="playsinline"
      class="l-video-player"
      :style="videoStyle"
      :class="videoClass"
      @loadstart="handleLoadStart"
      @loadedmetadata="handleLoadedMetadata"
      @loadeddata="handleLoadedData"
      @canplay="handleCanPlay"
      @canplaythrough="handleCanPlayThrough"
      @play="handlePlay"
      @pause="handlePause"
      @ended="handleEnded"
      @timeupdate="handleTimeUpdate"
      @volumechange="handleVolumeChange"
      @error="handleError"
      @click="handleVideoClick"
      @mouseenter="handleMouseEnter"
      @mouseleave="handleMouseLeave"
    >
      <!-- 不支持视频的浏览器显示文本 -->
      <p class="video-not-supported">
        {{ t('videoNotSupported') }}
      </p>
    </video>
    
    <!-- 自定义视频控制覆盖层 当视频加载完成时显示 -->
    <div 
      v-if="showControls && !isLoading" 
      class="video-overlay" 
      :class="{ 'fullscreen-overlay': isFullscreen }"
      @click.stop
    >
      <div class="video-controls">
        <div class="control-left">
          <!-- 播放/暂停按钮 -->
          <l-img 
            @click="togglePlayPause"
            v-show="isPlaying"
            src="/static/images/pause-icon.png" 
            w="24px" 
            h="24px" 
            class="video-control-icon"
          />
          <l-img 
            @click="togglePlayPause"
            v-show="!isPlaying"
            src="/static/images/videos/play_icon.png"
            w="24px" 
            h="24px" 
            class="video-control-icon"
          />
          
          <!-- 时间显示 -->
          <span class="video-time">{{ formattedCurrentTime }} / {{ formattedDuration }}</span>
        </div>
        
        <div class="control-right">
          <!-- 声音按钮 -->
          <template v-if="isShowVoice">
            <l-img 
              v-show="!isMuted"
              @click="toggleMute"
              src="/static/images/voice-icon.png" 
              w="20px" 
              h="19px" 
              class="video-control-icon"
            />
            <l-img 
              v-show="isMuted"
              @click="toggleMute"
              src="/static/images/voice-icon-close.png" 
              w="20px" 
              h="19px" 
              class="video-control-icon"
            />
          </template>
          <!-- 全屏按钮 -->
          <l-img 
            :src="isFullscreen ? '/static/images/shrink-icon.png' : '/static/images/expand-icon.png'" 
            w="20px" 
            h="20px" 
            @click="requestFullscreen" 
            class="video-control-icon" 
          />
        </div>
      </div>
    </div>
    
    <!-- 错误状态显示 -->
    <div v-if="hasError" class="video-error" :style="videoStyle">
      <div class="error-content">
        <l-img src="/static/images/format-error.png" w="25px" h="25px" />
        <span class="error-text">{{ errorMessage }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue';
import { useI18n } from 'vue-i18n';
import LImg from '@/components/l-img.vue';
import { convertStyleObject } from '@/utils/px-to-rem';

// 多语言支持
const { t } = useI18n();

// Props定义
const props = defineProps({
  // ==================== 视频源相关 ====================
  /**
   * 视频源地址（必填）
   * @description 支持多种格式：
   *   - 静态文件：'/static/videos/demo.mp4'
   *   - HTTP/HTTPS URL：'https://example.com/video.mp4'
   *   - Data URL：'data:video/mp4;base64,...'
   *   - Blob URL：'blob:...'
   *   - 相对路径：自动添加OSS前缀
   */
  src: {
    type: String,
    required: true,
    default: ''
  },
  
  /**
   * 视频封面图片地址
   * @description 在视频加载前或暂停时显示的封面图
   */
  poster: {
    type: String,
    default: ''
  },
  
  // ==================== 尺寸相关 ====================
  /**
   * 视频宽度
   * @description 支持字符串（如'640px', '100%'）或数字（自动添加px）
   */
  w: {
    type: [String, Number],
    default: '100%'
  },
  
  /**
   * 视频高度
   * @description 支持字符串（如'360px', 'auto'）或数字（自动添加px）
   */
  h: {
    type: [String, Number],
    default: '100%'
  },
  
  // ==================== HTML5 Video 属性 ====================
  /**
   * 是否显示自定义控制器
   * @description true-显示自定义控制器，false-隐藏控制器
   */
  showControls: {
    type: Boolean,
    default: true
  },
  
  /**
   * 是否显示声音控制按钮
   * @description true-显示声音按钮，false-隐藏声音按钮
   */
  isShowVoice: {
    type: Boolean,
    default: false
  },
  
  /**
   * 是否自动播放
   * @description 现代浏览器通常需要用户交互或静音才能自动播放
   */
  autoplay: {
    type: Boolean,
    default: false
  },
  
  /**
   * 是否循环播放
   * @description 视频播放结束后是否自动重新开始
   */
  loop: {
    type: Boolean,
    default: false
  },
  
  /**
   * 是否静音
   * @description 设置为true有助于自动播放功能
   */
  muted: {
    type: Boolean,
    default: false
  },
  
  /**
   * 预加载策略
   * @description 
   *   - 'none': 不预加载任何数据
   *   - 'metadata': 只预加载元数据（时长、尺寸等）
   *   - 'auto': 预加载整个视频
   */
  preload: {
    type: String,
    default: 'metadata',
    validator: (value) => ['none', 'metadata', 'auto'].includes(value)
  },
  
  /**
   * 移动端内联播放
   * @description 在iOS Safari中防止视频全屏播放
   */
  playsinline: {
    type: Boolean,
    default: true
  },
  
  // ==================== 样式相关 ====================
  /**
   * 视频适应方式
   * @description 控制视频如何适应容器尺寸
   *   - 'fill': 拉伸填满容器（可能变形）
   *   - 'contain': 保持比例，完整显示（可能有黑边）
   *   - 'cover': 保持比例，裁剪填满（可能被裁剪）
   *   - 'none': 不缩放
   *   - 'scale-down': 等同于none或contain中较小的一个
   */
  fit: {
    type: String,
    default: 'contain',
    validator: (value) => ['fill', 'contain', 'cover', 'none', 'scale-down'].includes(value)
  },
  
  /**
   * 圆角大小
   * @description 支持单个值或四个值（CSS border-radius格式）
   *   - 单个值：'8px'
   *   - 四个值：'8px 12px 8px 12px'（上左 上右 下右 下左）
   */
  radius: {
    type: String,
    default: '0'
  },
  
  /**
   * 边框样式
   * @description CSS border属性值，如：'1px solid #ddd'
   */
  border: {
    type: String,
    default: ''
  },
  
  /**
   * 自定义样式对象
   * @description 额外的CSS样式，会与组件样式合并
   */
  otherStyle: {
    type: Object,
    default: () => ({})
  },
  
  /**
   * 自定义CSS类名
   * @description 支持字符串、数组或对象格式
   *   - 字符串：'custom-class'
   *   - 数组：['class1', 'class2']
   *   - 对象：{ 'class1': true, 'class2': false }
   */
  customClass: {
    type: [String, Array, Object],
    default: ''
  },
  
  // ==================== 功能控制 ====================
  /**
   * 是否懒加载
   * @description 延迟加载视频资源，用于性能优化
   */
  lazy: {
    type: Boolean,
    default: false
  },
  
  /**
   * 是否显示加载状态
   * @description 控制是否显示加载动画和文字
   */
  showLoading: {
    type: Boolean,
    default: true
  }
});

// Emits定义 - 组件向父组件发送的事件
const emit = defineEmits([
  // ==================== 交互事件 ====================
  /**
   * 点击视频时触发
   * @param {Event} event - 原生点击事件对象
   */
  'click',
  
  // ==================== 加载相关事件 ====================
  /**
   * 开始加载视频时触发
   * @param {Event} event - 原生loadstart事件对象
   */
  'loadstart',
  
  /**
   * 视频元数据加载完成时触发
   * @param {Event} event - 原生loadedmetadata事件对象
   * @description 此时可获取视频时长、尺寸等信息
   */
  'loadedmetadata',
  
  /**
   * 视频数据加载完成时触发
   * @param {Event} event - 原生loadeddata事件对象
   */
  'loadeddata',
  
  /**
   * 视频可以开始播放时触发（缓冲足够）
   * @param {Event} event - 原生canplay事件对象
   */
  'canplay',
  
  /**
   * 视频可以流畅播放时触发（无需停顿缓冲）
   * @param {Event} event - 原生canplaythrough事件对象
   */
  'canplaythrough',
  
  /**
   * 视频加载成功时触发（自定义事件）
   * @param {Event} event - 原生事件对象
   */
  'load-success',
  
  /**
   * 视频加载失败时触发（自定义事件）
   * @param {Event} event - 原生事件对象
   */
  'load-error',
  
  // ==================== 播放控制事件 ====================
  /**
   * 视频开始播放时触发
   * @param {Event} event - 原生play事件对象
   */
  'play',
  
  /**
   * 视频暂停时触发
   * @param {Event} event - 原生pause事件对象
   */
  'pause',
  
  /**
   * 视频播放结束时触发
   * @param {Event} event - 原生ended事件对象
   */
  'ended',
  
  /**
   * 播放时间更新时触发（播放过程中持续触发）
   * @param {Event} event - 原生timeupdate事件对象
   * @description 可用于更新进度条、显示当前时间等
   */
  'timeupdate',
  
  /**
   * 音量变化时触发
   * @param {Event} event - 原生volumechange事件对象
   */
  'volumechange',
  
  // ==================== 错误事件 ====================
  /**
   * 视频发生错误时触发
   * @param {Event} event - 原生error事件对象
   * @description 包含错误类型和详细信息
   */
  'error',
  /**
   * 鼠标进入视频时触发
   * @param {Event} event - 原生mouseenter事件对象
   */
  'mouseenter',
  /**
   * 鼠标离开视频时触发
   * @param {Event} event - 原生mouseleave事件对象
   */
  'mouseleave'
]);

// 响应式数据
const videoRef = ref(null);
const isLoading = ref(true);
const hasError = ref(false);
const errorMessage = ref('');
const isPlaying = ref(false);
const currentTime = ref(0);
const duration = ref(0);
const isFullscreen = ref(false);
const isMuted = ref(props.muted);

// 计算属性 - 解析视频源路径
const parsedSrc = computed(() => {
  return parsePath(props.src);
});

// 计算属性 - 解析海报路径
const parsedPoster = computed(() => {
  return props.poster ? parsePath(props.poster) : '';
});

// 计算属性 - 容器样式
const containerStyle = computed(() => {
  return convertStyleObject({
    width: typeof props.w === 'number' ? `${props.w}px` : props.w,
    height: typeof props.h === 'number' ? `${props.h}px` : props.h,
    position: 'relative',
    display: 'inline-block',
  });
});

// 计算属性 - 视频样式
const videoStyle = computed(() => {
  let style = convertStyleObject({
    width: '100%',
    height: '100%',
    objectFit: props.fit
  });
  
  // 添加边框
  if (props.border) {
    style = convertStyleObject({
      border: props.border,
      boxSizing: 'border-box'
    });
  }
  
  // 添加圆角
  style = setRadius(style);
  
  // 合并自定义样式
  return convertStyleObject(Object.assign(style, props.otherStyle));
});

// 计算属性 - 视频CSS类名
const videoClass = computed(() => {
  let classes = ['l-video'];
  
  if (props.customClass) {
    if (typeof props.customClass === 'string') {
      classes.push(props.customClass);
    } else if (Array.isArray(props.customClass)) {
      classes.push(...props.customClass);
    } else if (typeof props.customClass === 'object') {
      Object.entries(props.customClass).forEach(([key, value]) => {
        if (value) classes.push(key);
      });
    }
  }
  
  return classes;
});

// 计算属性 - 格式化当前时间
const formattedCurrentTime = computed(() => {
  return formatTime(currentTime.value);
});

// 计算属性 - 格式化总时长
const formattedDuration = computed(() => {
  return formatTime(duration.value);
});

// 计算属性 - OSS路径前缀
const ossPathOriginal = computed(() => {
  return process.env.VITE_APP_OSS_PATH || '';
});

// 工具函数 - 路径解析（参考l-img.vue）
const parsePath = (src) => {
  if (!src || src === '') return '';
  
  // 静态文件路径（以/static开头）
  if (src.startsWith('/static')) {
    return src;
  }
  
  // HTTP/HTTPS 完整URL
  if (src.startsWith('http://') || src.startsWith('https://')) {
    return src;
  }
  
  // Data URL
  if (src.startsWith('data:')) {
    return src;
  }
  
  // Blob URL
  if (src.startsWith('blob:')) {
    return src;
  }
  
  // 相对路径，添加OSS前缀
  return ossPathOriginal.value + src;
};

// 工具函数 - 设置圆角样式
const setRadius = (styleObj) => {
  if (!props.radius || props.radius === '0') return styleObj;
  
  const radiusArr = props.radius.split(' ');
  if (radiusArr.length <= 1) {
    styleObj.borderRadius = props.radius;
  } else {
    // 支持四个角分别设置：top-left top-right bottom-right bottom-left
    styleObj.borderTopLeftRadius = radiusArr[0];
    styleObj.borderTopRightRadius = radiusArr[1] || radiusArr[0];
    styleObj.borderBottomRightRadius = radiusArr[2] || radiusArr[0];
    styleObj.borderBottomLeftRadius = radiusArr[3] || radiusArr[1] || radiusArr[0];
  }
  
  return styleObj;
};

// 工具函数 - 格式化时间
const formatTime = (seconds) => {
  if (!seconds || isNaN(seconds)) return '00:00';
  const mins = Math.floor(seconds / 60);
  // 使用Math.round进行四舍五入，确保时间显示更准确
  const secs = Math.round(seconds % 60);
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
};

// 事件处理函数
const handleLoadStart = (event) => {
  if (props.showLoading) {
    isLoading.value = true;
  }
  hasError.value = false;
  emit('loadstart', event);
};

const handleLoadedMetadata = (event) => {
  if (videoRef.value) {
    duration.value = videoRef.value.duration || 0;
  }
  emit('loadedmetadata', event);
};

const handleLoadedData = (event) => {
  emit('loadeddata', event);
};

const handleCanPlay = (event) => {
  if (props.showLoading) {
    isLoading.value = false;
  }
  hasError.value = false;
  emit('canplay', event);
  emit('load-success', event);
};

const handleCanPlayThrough = (event) => {
  if (props.showLoading) {
    isLoading.value = false;
  }
  emit('canplaythrough', event);
};

const handlePlay = (event) => {
  isPlaying.value = true;
  startTimeUpdate();
  emit('play', event);
};

const handlePause = (event) => {
  isPlaying.value = false;
  emit('pause', event);
};

const handleEnded = (event) => {
  isPlaying.value = false;
  // 视频结束时显示完整时长，而不是重置为0
  if (videoRef.value) {
    currentTime.value = videoRef.value.duration || 0;
  }
  emit('ended', event);
};

const handleTimeUpdate = (event) => {
  if (videoRef.value) {
    currentTime.value = videoRef.value.currentTime || 0;
    // 确保在接近结束时显示完整时间
    if (videoRef.value.duration && 
        (videoRef.value.duration - videoRef.value.currentTime) < 0.1) {
      currentTime.value = videoRef.value.duration;
    }
  }
  emit('timeupdate', event);
};

const handleVolumeChange = (event) => {
  emit('volumechange', event);
};

const handleMouseEnter = (event) => {
  console.log('mouseenter');
  emit('mouseenter', event);
};
const handleMouseLeave = (event) => {
  console.log('mouseleave');
  emit('mouseleave', event);
};
const handleError = (event) => {
  isLoading.value = false;
  hasError.value = true;
  
  // 根据错误类型设置错误信息
  const error = event.target.error;
  if (error) {
    switch (error.code) {
      case error.MEDIA_ERR_ABORTED:
        errorMessage.value = t('videoErrorAborted');
        break;
      case error.MEDIA_ERR_NETWORK:
        errorMessage.value = t('videoErrorNetwork');
        break;
      case error.MEDIA_ERR_DECODE:
        errorMessage.value = t('videoErrorDecode');
        break;
      case error.MEDIA_ERR_SRC_NOT_SUPPORTED:
        errorMessage.value = t('videoErrorNotSupported');
        break;
      default:
        errorMessage.value = t('videoErrorUnknown');
    }
  } else {
    errorMessage.value = t('videoErrorLoad');
  }
  
  emit('error', event);
  emit('load-error', event);
};

const handleVideoClick = (event) => {
  // 如果显示控制器，点击视频区域切换播放/暂停
  if (props.showControls) {
    togglePlayPause();
  }
  emit('click', event);
};

// 自定义控制器相关方法
const togglePlayPause = () => {
  if (!videoRef.value) return;
  
  if (isPlaying.value) {
    videoRef.value.pause();
  } else {
    videoRef.value.play();
  }
};

const toggleMute = () => {
  if (!videoRef.value) return;
  
  isMuted.value = !isMuted.value;
  videoRef.value.muted = isMuted.value;
};

const requestFullscreen = () => {
  if (videoRef.value) {
    // 请求全屏时确保使用视频容器而不是video元素
    if (isFullscreen.value) {
      document.exitFullscreen();
      isFullscreen.value = false;
      emit('fullscreen-change', false);
    } else {
      const container = videoRef.value.parentElement;
      if (container && container.requestFullscreen) {
        container.requestFullscreen();
      } else if (videoRef.value.requestFullscreen) {
          videoRef.value.requestFullscreen();
      }
    }
  }
};

// 全屏状态监听
const handleFullscreenChange = () => {
  const isCurrentlyFullscreen = !!(
    document.fullscreenElement ||
    document.webkitFullscreenElement ||
    document.mozFullScreenElement ||
    document.msFullscreenElement
  );
  
  isFullscreen.value = isCurrentlyFullscreen;
  
  // 在全屏模式下强制隐藏原生控制器
  if (videoRef.value) {
    videoRef.value.controls = false;
  }
};

const startTimeUpdate = () => {
  const updateTime = () => {
    if (videoRef.value && isPlaying.value) {
      currentTime.value = videoRef.value.currentTime || 0;
      requestAnimationFrame(updateTime);
    }
  };
  updateTime();
};

// 公共方法 - 播放控制
const play = () => {
  if (videoRef.value) {
    return videoRef.value.play();
  }
};

const pause = () => {
  if (videoRef.value) {
    videoRef.value.pause();
  }
};

const getCurrentTime = () => {
  return videoRef.value ? videoRef.value.currentTime : 0;
};

const setCurrentTime = (time) => {
  if (videoRef.value) {
    videoRef.value.currentTime = time;
  }
};

const getDuration = () => {
  return videoRef.value ? videoRef.value.duration : 0;
};

const getVolume = () => {
  return videoRef.value ? videoRef.value.volume : 0;
};

const setVolume = (volume) => {
  if (videoRef.value) {
    videoRef.value.volume = Math.max(0, Math.min(1, volume));
  }
};

// 监听src变化
watch(() => props.src, (newSrc) => {
  console.log('newSrc', newSrc);
  if (newSrc) {
    isLoading.value = props.showLoading;
    hasError.value = false;
    isPlaying.value = false;
    currentTime.value = 0;
    duration.value = 0;
  }
});

// 监听muted属性变化
watch(() => props.muted, (newMuted) => {
  isMuted.value = newMuted;
  if (videoRef.value) {
    videoRef.value.muted = newMuted;
  }
});

// 生命周期 - 添加全屏监听
onMounted(() => {
  // 添加全屏状态变化监听
  document.addEventListener('fullscreenchange', handleFullscreenChange);
  document.addEventListener('webkitfullscreenchange', handleFullscreenChange);
  document.addEventListener('mozfullscreenchange', handleFullscreenChange);
  document.addEventListener('MSFullscreenChange', handleFullscreenChange);
});

onUnmounted(() => {
  // 移除全屏状态变化监听
  document.removeEventListener('fullscreenchange', handleFullscreenChange);
  document.removeEventListener('webkitfullscreenchange', handleFullscreenChange);
  document.removeEventListener('mozfullscreenchange', handleFullscreenChange);
  document.removeEventListener('MSFullscreenChange', handleFullscreenChange);
});

// 暴露公共方法
defineExpose({
  videoRef,
  isFullscreen,
  play,
  pause,
  getCurrentTime,
  setCurrentTime,
  getDuration,
  getVolume,
  setVolume,
  togglePlayPause,
  toggleMute,
  requestFullscreen
});
</script>

<style scoped lang="scss">
.l-video-container {
  position: relative;
  display: inline-block;
  overflow: hidden;
  width: 100%;
  height: 100%;
}
.l-video-player{
  background-color: #000;
}
.l-video {
  width: 100%;
  height: 100%;
  display: block;
  
  /* 隐藏原生控制器 */
  &::-webkit-media-controls {
    display: none !important;
  }
  
  &::-webkit-media-controls-panel {
    display: none !important;
  }
  
  &::-webkit-media-controls-play-button {
    display: none !important;
  }
  
  &::-webkit-media-controls-start-playback-button {
    display: none !important;
  }
  
  &::-webkit-media-controls-fullscreen-button {
    display: none !important;
  }
  
  &::-webkit-media-controls-current-time-display {
    display: none !important;
  }
  
  &::-webkit-media-controls-time-remaining-display {
    display: none !important;
  }
  
  &::-webkit-media-controls-timeline {
    display: none !important;
  }
  
  &::-webkit-media-controls-volume-slider {
    display: none !important;
  }
  
  &::-webkit-media-controls-mute-button {
    display: none !important;
  }
  
  &::-webkit-media-controls-toggle-closed-captions-button {
    display: none !important;
  }
}

/* 加载状态样式 */
.video-loading {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: inherit;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.loading-text {
  font-size: 14px;
  color: #666;
  font-family: 'SF Pro Text', sans-serif;
}

/* 自定义视频控制覆盖层样式（参考upload-video.vue） */
.video-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  padding: 16px;
  border-radius: inherit;
  transition: opacity 0.3s ease;
  z-index: 2; /* 确保在全屏时也显示在最上层 */
}

/* 全屏时的控制器样式 */
.fullscreen-overlay {
  position: fixed !important;
  bottom: 0 !important;
  left: 0 !important;
  right: 0 !important;
  width: 100% !important;
  height: auto !important;
  border-radius: 0 !important;
  z-index: 2 !important;
}


.video-controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: white;
}

.control-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.control-right {
  display: flex;
  align-items: center;
  gap: 17px;
}

.video-time {
  font-size: 14px;
  font-family: monospace;
  color: white;
}

.video-control-icon {
  cursor: pointer;
  transition: opacity 0.2s;
  
  &:hover {
    opacity: 0.8;
  }
}

/* 错误状态样式 */
.video-error {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f8f8;
  border: 1px solid #e1e1e1;
  border-radius: inherit;
}

.error-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  text-align: center;
}

.error-icon {
  font-size: 24px;
}

.error-text {
  font-size: 14px;
  color: #ff4d4f;
  font-family: 'SF Pro Text', sans-serif;
}

/* 不支持视频的样式 */
.video-not-supported {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  margin: 0;
  font-size: 14px;
  color: #999;
  background: #f5f5f5;
}

/* 加载动画 */
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  
  .loading-text,
  .error-text {
    font-size: 12px;
  }
  
  .error-icon {
    font-size: 20px;
  }
  
  .video-overlay {
    padding: 12px;
  }
  
  .video-time {
    font-size: 12px;
  }
  
  .control-left {
    gap: 8px;
  }
}
</style>

