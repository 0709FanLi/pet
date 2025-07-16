<template>
    <el-dropdown ref="dropdownRef" popper-class="l-dropdown-popper" :placement="placement" :trigger="trigger" :max-height="pxToRem(maxHeight)">
        <div class="l-dropdown-trigger">
            <slot></slot>
            <el-icon><arrow-down /></el-icon>
        </div>
        <template #dropdown>
            <div class="l-dropdown-menu">
                <div class="l-dropdown-item" v-for="(item, index) in computedItems" :key="item.key" :command="item.key"
                    :class="{ 'is-selected': item.is_selected }" @click="handleItemClick(index)">
                    <div class="def-content">
                        <div class="def-check">
                            <el-icon v-if="item.is_selected"><Select /></el-icon>
                        </div>
                        <span>{{ item.title }}</span>
                    </div>
                    <div v-if="item.is_new" class="def-new-chat">{{ t('newChat') }}</div>
                </div>
            </div>
        </template>
    </el-dropdown>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { ArrowDown } from '@element-plus/icons-vue';
import { useI18n } from 'vue-i18n';
import { useChatModelStore } from '@/store/modules/chat-model';
import { MODEL_CAPABILITIES } from '@/models/chat-model-models';
import { pxToRem } from '@/utils/px-to-rem';

const { t } = useI18n();
const chatModelStore = useChatModelStore();

const props = defineProps({
  // 传统方式：直接传递items数组
  items: {
    type: Array,
    default: () => []
  },
  // 新方式：通过type自动获取数据
  type: {
    type: String,
    default: null,

  },
  // 初始选择的项目（用于模型同步）
  initialSelected: {
    type: String,
    default: null
  },
  trigger: {
    type: String,
    default: 'click'
  },
  placement: {
    type: String,
    default: 'top'
  },
  maxHeight: {
    type: String,
    default: '280px'
  }

});

const dropdownRef = ref(null);
const emit = defineEmits(['change', 'update:selected']);

// 内部数据状态
const internalItems = ref([]);
const currentSelected = ref(null);

// 计算属性：优先使用传入的items，否则使用内部获取的数据
const computedItems = computed(() => {
  if (props.items && props.items.length > 0) {
    return props.items;
  }
  return internalItems.value;
});

// 根据type自动获取数据
const fetchDataByType = async () => {
  if (!props.type) return;
  try {
    let dataList = [];

    switch (props.type) {
      case MODEL_CAPABILITIES.TEXT:
        dataList = await chatModelStore.getTextModels();
        break;
      case MODEL_CAPABILITIES.REASONING:
        dataList = await chatModelStore.getReasoningModels();
        break;
      case MODEL_CAPABILITIES.IMAGE_ANALYSIS:
        dataList = await chatModelStore.getImageAnalysisModels();
        break;
      case MODEL_CAPABILITIES.VISION:
        dataList = await chatModelStore.getVisionModels();
        break;
      case MODEL_CAPABILITIES.IMAGE_GEN:
        dataList = await chatModelStore.getImageGenModels();
        break;
      case MODEL_CAPABILITIES.DEEP_SEARCH:
        dataList = await chatModelStore.getDeepSearchModels();
        break;
      default:
        console.warn(`Unsupported type: ${props.type}`);
        dataList = [];
      }
    // 转换数据格式为dropdown需要的格式
    if (dataList && dataList.length > 0) {
      // 查找选择的项目，优先级：当前已选择的模型 > initialSelected > 第一个
      let selectedIndex = 0;

      // 1. 优先保持当前已选择的模型（如果在新列表中存在）
      if (currentSelected.value) {
        const foundIndex = dataList.findIndex(item => item.name === currentSelected.value);
        if (foundIndex !== -1) {
          selectedIndex = foundIndex;
        }
      }
      // 2. 如果当前模型不在新列表中，尝试使用initialSelected
      else if (props.initialSelected) {
        const foundIndex = dataList.findIndex(item => item.name === props.initialSelected);
        if (foundIndex !== -1) {
          selectedIndex = foundIndex;
        }
      }
      // 3. 否则默认选择第一个

      // 格式化数据
      const formattedData = dataList.map((item, index) => ({
        key: item.id || index + 1,
        title: item.name || item.title,
        name: item.name, // 保留原始name用于同步
        is_selected: index === selectedIndex,
        ...item // 保留其他属性
      }));

      internalItems.value = formattedData;

      // 设置当前选择
      if (formattedData[selectedIndex]) {
        const selectedModel = formattedData[selectedIndex].name;
        // 只有当选择的模型发生变化时才更新和发出事件
        if (currentSelected.value !== selectedModel) {
          currentSelected.value = selectedModel;
          emit('update:selected', selectedModel);
        }
      }
    }
  } catch (error) {
    console.error(`Failed to fetch data for type ${props.type}:`, error);
    internalItems.value = [];
  }
};

watch(() => props.type, async (newType) => {
  if (newType) {
    await fetchDataByType();
  }
}, { immediate: true });

const handleItemClick = (index) => {
  const selectedItem = computedItems.value[index];
  if (selectedItem.is_selected) {
    dropdownRef.value.handleClose();
    return;
  }

  // 更新选择状态
  computedItems.value.forEach((item, i) => {
    item.is_selected = i === index;
  });

  // 更新当前选择
  currentSelected.value = selectedItem.name || selectedItem.title;
  // 发出事件
  emit('change', index);
  emit('update:selected', selectedItem.name || selectedItem.title);

  dropdownRef.value.handleClose();
};

const handleSelectItem = async (modelName) => {
  console.log('modelName', modelName);

  if (!props.initialSelected && !modelName) return;

  if (internalItems.value.length == 0) {
    await fetchDataByType();
  }
  let name = modelName || props.initialSelected;
  const foundIndex = internalItems.value.findIndex(item => item.name === name);
  if (foundIndex !== -1) {
    internalItems.value.map(item => {
      item.is_selected = false;
    });
    internalItems.value[foundIndex].is_selected = true;
    // handleItemClick(foundIndex);
  }
}

// 暴露方法供外部调用
defineExpose({
  refresh: fetchDataByType,
  getCurrentSelected: () => currentSelected.value,
  handleSelectItem
});
</script>


<style scoped lang="scss">

.l-dropdown-trigger {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 14px;
  color: var(--text-title);
  gap: 4px;
}

.l-dropdown-menu {
    display: flex;
    flex-direction: column;
    width: 320px;
    box-sizing: border-box;
    border-radius: 12px;
    background: #fff;
    padding: 4px;
}

.l-dropdown-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    color: #1B2432;
    font-size: 15px;
    font-weight: 500;
    padding: 12px 8px;
    border-radius: 12px;
    cursor: pointer;

    &:hover {
        background: #F3F4F6;
        border-radius: 4px;
    }
}

.def-content {
    display: flex;
    align-items: center;
    gap: 2px;
}

.def-check {
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.def-new-chat {
    color: #9CA3AF;
    font-size: 12px;
    font-weight: 400;
    border-radius: 4px;
    border: 1px solid #E5E7EB;
    padding: 4px;
    box-sizing: border-box;
}
</style>

<style lang="scss">
.l-dropdown-popper {
  border-radius: 12px;
  .el-scrollbar {
    border-radius: 12px !important;
  }
}
</style>