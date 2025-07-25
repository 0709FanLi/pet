<template>
    <el-tooltip
        :content="tooltipContent"
        :placement="placement"
        :effect="effect"
        :popper-class="popperClass"
        :visible="visible"
        :disabled="disabled"
        :offset="offset"
        :show-after="showAfter"
        :hide-after="hideAfter"
        :teleported="teleported"
        :transition="transition"
        :trigger="trigger"
        :virtual-ref="virtualRef"
        :virtual-triggering="virtualTriggering"
        :raw-content="rawContent"
        :persistent="persistent"
        :show-arrow="showArrow"
        :append-to="appendTo"
        :popper-options="popperOptions"
        :enterable="enterable"
        :z-index="zIndex"
    >
        <slot></slot>
    </el-tooltip>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps({
    // 提示内容
    content: {
        type: String,
        default: ''
    },
    // 位置
    placement: {
        type: String,
        default: 'bottom'
    },
    // 主题
    effect: {
        type: String,
        default: 'light'
    },
    // 自定义类名
    popperClass: {
        type: String,
        default: 'l-tooltip-popper-white'
    },
    // 是否显示
    visible: {
        type: Boolean,
        default: undefined
    },
    // 是否禁用
    disabled: {
        type: Boolean,
        default: false
    },
    // 偏移量
    offset: {
        type: Number,
        default: 8
    },
    // 显示延迟
    showAfter: {
        type: Number,
        default: 0
    },
    // 隐藏延迟
    hideAfter: {
        type: Number,
        default: 200
    },
    // 是否使用teleport
    teleported: {
        type: Boolean,
        default: true
    },
    // 过渡动画
    transition: {
        type: String,
        default: 'el-fade-in-linear'
    },
    // 触发方式
    trigger: {
        type: [String, Array],
        default: 'hover'
    },
    // 虚拟触发元素
    virtualRef: {
        type: Object,
        default: undefined
    },
    // 是否启用虚拟触发
    virtualTriggering: {
        type: Boolean,
        default: false
    },
    // 是否作为HTML插入
    rawContent: {
        type: Boolean,
        default: false
    },
    // 是否在鼠标离开时仍然保留
    persistent: {
        type: Boolean,
        default: false
    },
    // 是否显示箭头
    showArrow: {
        type: Boolean,
        default: false
    },
    // 挂载位置
    appendTo: {
        type: [String, Object],
        default: 'body'
    },
    // popper.js配置项
    popperOptions: {
        type: Object,
        default: () => ({})
    },
    // 鼠标是否可进入tooltip
    enterable: {
        type: Boolean,
        default: true
    },
    // z-index
    zIndex: {
        type: Number,
        default: undefined
    }
});

// 计算tooltip内容，如果没有提供content，则使用默认的多语言内容
const tooltipContent = computed(() => {
    return props.content || t('tooltip');
});
</script>
