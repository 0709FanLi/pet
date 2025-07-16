<template>
    <div class="l-pagination">
        <el-pagination
            v-model:current-page="currentPageValue"
            v-model:page-size="pageSizeValue"
            :page-sizes="pageSizes"
            :layout="finalLayout"
            :total="total"
            :popper-class="popperClass"
            :goto-text="$t('goTo')"
            :page-label="$t('page')"
            :page-size-label="$t('itemsPerPage')"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
    </div>
</template>

<script setup>
import { ref, computed, watch,onMounted } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps({
    currentPage: {
        type: Number,
        default: 1
    },
    pageSize: {
        type: Number,
        default: 8
    },
    pageSizes: {
        type: Array,
        default: () => [8, 16, 32]
    },
    total: {
        type: Number,
        default: 0
    },
    showSizes: {
        type: Boolean,
        default: false
    },
    popperClass: {
        type: String,
        default: ''
    }
});

const emit = defineEmits(['update:currentPage', 'update:pageSize', 'size-change', 'current-change']);

const currentPageValue = ref(props.currentPage);
const pageSizeValue = ref(props.pageSize);

// 计算最终的布局
const finalLayout = computed(() => {
    return props.showSizes 
        ? 'prev, pager, next, sizes, jumper' 
        : 'prev, pager, next, jumper';
});

// 监听props变化
watch(() => props.currentPage, (newVal) => {
    currentPageValue.value = newVal;
});

watch(() => props.pageSize, (newVal) => {
    pageSizeValue.value = newVal;
});

// 监听内部值变化，向上传递
watch(currentPageValue, (newVal) => {
    emit('update:currentPage', newVal);
});

watch(pageSizeValue, (newVal) => {
    emit('update:pageSize', newVal);
});

// 处理事件
const handleSizeChange = (size) => {
    emit('size-change', size);
};

const handleCurrentChange = (page) => {
    emit('current-change', page);
};

onMounted(()=>{
    const paginationPageAll = document.querySelectorAll('.l-pagination .el-pagination__classifier');
    const paginationGotoAll = document.querySelectorAll('.l-pagination .el-pagination__goto');
    paginationPageAll.forEach(item=>{
        if(item.innerHTML === ''){
            item.innerHTML = t('page')
        }
    })
    paginationGotoAll.forEach(item=>{
        if(item.innerHTML){
            item.innerHTML = t('goTo')
        }
    })
})
</script>

<style scoped lang="scss">
.l-pagination {
    display: flex;
    justify-content: center;
    padding:24px;
    
    :deep(.el-pagination) {
        .el-pagination__jump {
            color: var(--text-description);
            margin-left: 12px;
        }
        
        .el-pager li {
            border: 1px solid var(--border-color);
            border-radius: 4px;
            font-weight: 400;
            color: var(--text-description);
            margin: 0 4px;
            
            &.is-active {
                border-color: var(--button-primary);
                background-color: var(--button-primary);
                color: white;
            }
        }
        
        .btn-prev, .btn-next {
            border: 1px solid var(--border-color);
            border-radius: 4px;
            margin: 0 4px;
            
            &:disabled {
                opacity: 0.5;
            }
        }
        
        .el-pagination__sizes {
            margin-left: 12px;
        }
    }
}
:deep(.el-pagination button:hover){
  color: var(--button-primary) !important;
}
</style> 