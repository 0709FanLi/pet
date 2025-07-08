<template>
  <el-image 
    @click="clickThis" 
    :src="isLoading ? loadingImagePath : path" 
    :fit="fit" 
    :lazy="lazy" 
    :preview-src-list="previewShowList"     
    :z-index="zIndex"
    :initial-index="previewIndex" 
    :scroll-container="scrollContainer" 
    @error="onError" 
    @load="onLoad" 
    :style="[borderStyle, imgStyle]"
    alt="img"
  >
    <template #error>
      <div class="image-slot" ref="errorRef">
        <l-img src="/static/images/placeholder-icon.png"  :style="imgStyle" fit="cover" alt="error" v-if="isShowErrorIcon" />
        <l-img src="/static/images/placeholder-image.jpg"  :style="imgStyle" fit="cover" alt="error" v-else />
      </div>
    </template>
  </el-image>
</template>

<script setup>
import { ref, computed, watch ,onMounted, nextTick} from 'vue';
import { empty } from '../utils/util';
import { pxToRem } from '@/utils/px-to-rem';
import LImg from '@/components/l-img.vue';

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
    default: 'cover'
  },
  lazy: {
    type: Boolean,
    default: false,
  },
  scrollContainer: {
    type: [Object, String],
    default: () => {
    },
  },
  zIndex: {
    type: Number,
    default: 2000
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
  showErrorType: { // 显示错误类型 icon 占位图标 image 占位图片
    type: String,
    default: 'icon',
  },
});

const emit = defineEmits(['click', 'onError', 'onLoad']);

const path = ref('');
const previewShowList = ref([]);
const isLoading = ref(true);
const loadingImagePath = '/static/images/img_loading.png';
const isError = ref(false);
const errorRef = ref(null);
const errorWidth = ref(0);
const errorHeight = ref(0);

const isShowErrorIcon = computed(() => {
  return errorWidth.value < 40 && errorHeight.value < 40;
});

const borderStyle = computed(() => {
  if (props.border == '') return {};
  let obj = {};
  obj['border'] = props.border;
  obj['box-sizing'] = 'border-box';
  return obj;
});

// 自动转换px值为rem的辅助函数
const convertPxToRem = (value) => {
  if (!value) return value;
  // 如果是数字，直接转换为px字符串再转rem
  if (typeof value === 'number') {
    return pxToRem(`${value}px`);
  }
  // 如果是字符串且包含px，转换为rem
  if (typeof value === 'string' && value.includes('px')) {
    return pxToRem(value);
  }
  // 其他情况直接返回
  return value;
};

const imgStyle = computed(() => {
  let obj = {
    width: convertPxToRem(props.w),
    minWidth: convertPxToRem(props.w),
    height: convertPxToRem(props.h),
    minHeight: convertPxToRem(props.h)
  };
  obj = Object.assign(obj, props.otherStyle);
  return setRadius(obj);
});

const ossPathOriginal = computed(() => {
  if (props.isGif) {
    return process.env.VITE_APP_OSS_PATH;
  } else {
    return process.env.VITE_APP_OSS_PIC_PATH;
  }
});

const clickThis = (e) => {
  emit('click', e);
};

const onError = (error) => {
  isError.value = true;
  isLoading.value = false;
  nextTick(() => {
    getImgWidthAndHeight();
  });
  emit('onError', error);
};

const onLoad = () => {
  isLoading.value = false;
  emit('onLoad');
};

const setRadius = (obj) => {
  let radiusArr = props.radius.split(' ');
  if (radiusArr.length <= 1) {
    obj['borderRadius'] = convertPxToRem(props.radius);
  } else {
    obj['borderTopLeftRadius'] = convertPxToRem(radiusArr[0]);
    obj['borderTopRightRadius'] = convertPxToRem(radiusArr[1]);
    obj['borderBottomRightRadius'] = convertPxToRem(radiusArr[2]);
    obj['borderBottomLeftRadius'] = convertPxToRem(radiusArr[3]);
  }
  return obj;
};

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


//  获取实际图片渲染的宽高
const getImgWidthAndHeight = () => {
  if (errorRef.value) {
    errorWidth.value = errorRef.value.offsetWidth;
    errorHeight.value = errorRef.value.offsetHeight;
  }
};

watch(() => props.src, () => {
  isLoading.value = true;
  path.value = parse(props.src);
});

watch(() => props.previewList, () => {
  parsePreviewList();
}, {
  deep: true
});

onMounted(()=>{
  parsePreviewList();
  path.value = parse(props.src);
  isLoading.value = true;
})
</script>

<style lang="scss" scoped>
.image-slot {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>