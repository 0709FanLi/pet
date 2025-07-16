<template>
  <van-image 
    :src="isLoading ? loadingImagePath : path" 
    :fit="fit" 
    :lazy-load="lazy"
    :show-error="true"
    :show-loading="true"
    :error-icon="'photo-fail'"
    :loading-icon="'photo'"
    @load="onLoad" 
    @error="onError"
    @click="handleImageClick"
    :style="[borderStyle, imgStyle]"
    class="mobile-l-img"
  >
    <!-- 自定义加载状态 -->
    <template #loading>
      <div class="loading-container">
        <van-loading type="spinner" size="20px" />
      </div>
    </template>
    
    <!-- 自定义错误状态 -->
    <template #error>
      <div class="error-container">
        <van-icon name="photo-fail" size="20px" />
      </div>
    </template>
  </van-image>
</template>

<script setup>
// 引入区域
import { ref, computed, watch, onMounted } from 'vue';
import { showImagePreview } from 'vant';
import { empty } from '@/utils/util';

// props区域
const props = defineProps({
  src: {
    type: String,
    default: '',
  },
  w: {
    type: [String, Number],
    default: '0',
  },
  h: {
    type: [String, Number],
    default: '0',
  },
  fit: {
    type: String,
    default: 'cover',
    validator: (value) => ['contain', 'cover', 'fill', 'none', 'scale-down'].includes(value)
  },
  lazy: {
    type: Boolean,
    default: false,
  },
  previewList: {
    type: Array,
    default: () => [],
  },
  border: {
    type: String,
    default: '',
  },
  radius: {
    type: String,
    default: '0',
  },
  isGif: {
    type: Boolean,
    default: false,
  },
  otherStyle: {
    type: Object,
    default: () => ({})
  },
  previewIndex: {
    type: Number,
    default: 0,
  },
  // 是否启用预览功能
  enablePreview: {
    type: Boolean,
    default: false,
  },
  // 自定义加载图片路径
  customLoadingPath: {
    type: String,
    default: '',
  }
});

// emits区域
const emit = defineEmits(['click', 'onError', 'onLoad']);

// 变量区域
const path = ref('');
const previewShowList = ref([]);
const isLoading = ref(true);
const loadingImagePath = '/static/images/img_loading.png';

// 计算属性区域
const borderStyle = computed(() => {
  if (props.border == '') return {};
  let obj = {};
  obj['border'] = props.border;
  obj['box-sizing'] = 'border-box';
  return obj;
});


const setRadius = (obj) => {
  let radiusArr = props.radius.split(' ');
  if (radiusArr.length <= 1) {
    obj['borderRadius'] = props.radius;
  } else {
    obj['borderTopLeftRadius'] = radiusArr[0];
    obj['borderTopRightRadius'] = radiusArr[1];
    obj['borderBottomRightRadius'] = radiusArr[2];
    obj['borderBottomLeftRadius'] = radiusArr[3];
  }
  return obj;
};

const imgStyle = computed(() => {
  let obj = {
    width: props.w,
    minWidth: props.w,
    height: props.h,
    minHeight: props.h
  };
  obj = Object.assign(obj, props.otherStyle);
  return setRadius(obj);
});

const ossPathOriginal = computed(() => {
  if (props.isGif) {
    return import.meta.env.VITE_APP_OSS_PATH;
  } else {
    return import.meta.env.VITE_APP_OSS_PIC_PATH;
  }
});

// 点击事件区域
const handleImageClick = (e) => {
  // 先触发点击事件
  emit('click', e);
  
  // 如果启用预览且有预览列表，显示预览
  if (props.enablePreview && previewShowList.value.length > 0) {
    showImagePreview({
      images: previewShowList.value,
      startPosition: props.previewIndex,
      showIndex: true,
      closeable: true,
    });
  }
};

// 自定义事件区域
const onError = (error) => {
  console.log('移动端图片加载错误:', error);
  isLoading.value = false;
  emit('onError', error);
};

const onLoad = () => {
  isLoading.value = false;
  emit('onLoad');
};

// 业务逻辑区域

// 数据解析区域
const parsePreviewList = () => {
  if (empty(props.previewList)) return;
  previewShowList.value = [];
  for (let i = 0, len = props.previewList.length; i < len; i++) {
    let item = props.previewList[i];
    if(Object.prototype.toString.call(item) === '[object Object]'){
      previewShowList.value.push(parse(item.url || item.previewUrl));
    }else{
      previewShowList.value.push(parse(item));
    }
  }
};

const parse = (src) => {
  if (src == '') return '';
  if(!src) return '';

  if (src.startsWith('/static')){
    return src;
  }
  if (src.substring(0, 4) == 'http') return src;
  if (src.substring(0, 5) == 'data:') return src;
  if (src.substring(0, 4) == 'blob') return src;
  return ossPathOriginal.value + src;
};

// watch区域
watch(() => props.src, () => {
  isLoading.value = true;
  path.value = parse(props.src);
});

watch(() => props.previewList, () => {
  parsePreviewList();
}, {
  deep: true
});

// 周期函数区域
onMounted(() => {
  parsePreviewList();
  path.value = parse(props.src);
  isLoading.value = true;
});
</script>

<style scoped lang="scss">
.mobile-l-img {
  display: inline-block;
  
  // 自定义加载状态样式
  .loading-container {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    border-radius: inherit;
  }
  
  // 自定义错误状态样式
  .error-container {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    background-color: var(--bg-secondary, #f5f5f5);
    border-radius: inherit;
    color: var(--text-secondary, #999);
  }
}

// Vant 组件样式覆盖
:deep(.van-image) {
  border-radius: inherit;
}

:deep(.van-image__img) {
  border-radius: inherit;
}

// 图片预览样式定制
:deep(.van-image-preview) {
  .van-image-preview__image {
    border-radius: 0;
  }
}
</style> 