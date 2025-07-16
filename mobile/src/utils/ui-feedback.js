/**
 * 移动端UI反馈工具类 - 基于Vant组件封装
 * 参考文档: https://vant-ui.github.io/vant/#/zh-CN/
 * 与PC端保持相同的API接口，底层实现使用Vant组件
 */

/*
    不同：无 showMessageBoxPrompt showNotificationPrimary
    添加：fullScreenLoading  showMessageLoading
    showMessageLoading 和 fullScreenLoading 表现一致
*/

import { 
    showToast, 
    showDialog, 
    showConfirmDialog, 
    showNotify, 
    showLoadingToast, 
    closeToast,
    allowMultipleToast
} from 'vant';

// 允许同时显示多个 Toast
allowMultipleToast();

// Element Plus 到 Vant 的类型映射
const typeMap = {
    'primary': 'text',
    'success': 'success',
    'warning': 'text', 
    'info': 'text',
    'error': 'fail'
};

// ===================== Toast 消息提示封装 =====================

/**
 * 通用消息提示函数 (基于 Vant Toast)
 * 完整参数支持，参考: https://vant-ui.github.io/vant/#/zh-CN/toast
 * @param {string|Object} options - 消息配置选项
 * @param {string} [options.message=''] - 文字内容，支持通过\n换行
 * @param {'text'|'loading'|'success'|'fail'|'html'} [options.type='text'] - 提示类型
 * @param {string|Component} [options.icon] - 自定义图标，支持传入图标名称或图片链接，等同于Icon组件的name属性
 * @param {string} [options.iconSize] - 图标大小，如 20px、2em，默认单位为px
 * @param {string} [options.iconPrefix='van-icon'] - 图标类名前缀，等同于Icon组件的class-prefix属性
 * @param {'top'|'middle'|'bottom'} [options.position='middle'] - 位置
 * @param {string} [options.className=''] - 自定义类名
 * @param {boolean} [options.overlay=false] - 是否显示背景遮罩层
 * @param {boolean} [options.forbidClick=false] - 是否禁止背景点击
 * @param {boolean} [options.closeOnClick=false] - 是否在点击后关闭
 * @param {boolean} [options.closeOnClickOverlay=false] - 是否在点击遮罩层后关闭
 * @param {string} [options.loadingType='circular'] - 加载图标类型，可选值为spinner
 * @param {number} [options.duration=2000] - 展示时长(ms)，值为0时，toast不会消失
 * @param {number|string} [options.zIndex=2000] - 将组件的z-index层级设置为一个固定值
 * @param {string} [options.teleport] - 指定挂载的节点，等同于Teleport组件的to属性
 * @param {boolean} [options.wordWrap=false] - 是否在长单词内换行
 * @param {Function} [options.onClose] - 关闭时的回调函数
 * @param {Function} [options.onOpened] - 完全展示后的回调函数
 * @returns {Object} Toast实例，包含close方法用于手动关闭
 */
export const showMessageBase = (options) => {
    // 如果传入的是字符串，转换为配置对象
    if (typeof options === 'string') {
        options = { message: options };
    }
    
    // 构建完整的Vant Toast选项
    const vantOptions = {
        message: options.message || '',
        type: options.type || typeMap[options.originalType] || 'text',
        position: options.position || 'middle',
        duration: options.duration !== undefined ? options.duration : 2000,
        className: `${options.className} mobile-toast`|| 'mobile-toast',
        overlay: options.overlay || false,
        forbidClick: options.forbidClick || false,
        closeOnClick: options.closeOnClick || false,
        closeOnClickOverlay: options.closeOnClickOverlay || false,
        loadingType: options.loadingType || 'circular',
        wordWrap: options.wordWrap || false,
        onClose: options.onClose,
        onOpened: options.onOpened
    };

    // 处理图标相关参数
    if (options.icon !== undefined) {
        vantOptions.icon = options.icon;
    }
    if (options.iconSize) {
        vantOptions.iconSize = options.iconSize;
    }
    if (options.iconPrefix) {
        vantOptions.iconPrefix = options.iconPrefix;
    }

    // 处理层级
    if (options.zIndex !== undefined) {
        vantOptions.zIndex = options.zIndex;
    }

    // 处理挂载节点
    if (options.teleport) {
        vantOptions.teleport = options.teleport;
    }
    
    return showToast(vantOptions);
};

/**
 * 显示成功消息
 * @param {string|Object} options - 消息内容或配置对象
 */
export const showMessageSuccess = (options) => {
    if (typeof options === 'string') {
        return showMessageBase({
            type: 'success',
            originalType: 'success',
            message: options
        });
    }
    
    return showMessageBase({
        type: 'success',
        originalType: 'success',
        ...options
    }); 
};


/**
 * 显示信息消息
 * @param {string|Object} options - 消息内容或配置对象 
 */
export const showMessageInfo = (options) => {
    if (typeof options === 'string') {
        return showMessageBase({
            type: 'text',
            originalType: 'info',
            message: options
        });
    }
    
    return showMessageBase({
        type: 'text',
        originalType: 'info',
        ...options
    });
};

/**
 * 显示警告消息
 * @param {string|Object} options - 消息内容或配置对象
 */
export const showMessageWarning = (options) => {
    if (typeof options === 'string') {
        return showMessageBase({
            type: 'text',
            originalType: 'warning',
            icon: 'warning-o',
            message: options
        });
    }
    
    return showMessageBase({
        type: 'text',
        originalType: 'warning',
        icon: 'warning-o',
        ...options
    });
};

/**
 * 显示错误消息
 * @param {string|Object} options - 消息内容或配置对象  
 */
export const showMessageError = (options) => {
    if (typeof options === 'string') {
        return showMessageBase({
            type: 'fail',
            originalType: 'error',
            message: options
        });
    }
    
    return showMessageBase({
        type: 'fail',
        originalType: 'error',
        ...options
    });
};

/**
 * 显示主要消息
 * @param {string|Object} options - 消息内容或配置对象
 */
export const showMessagePrimary = (options) => {
    if (typeof options === 'string') {
        return showMessageBase({
            type: 'text',
            originalType: 'primary',
            message: options
        });
    }
    
    return showMessageBase({
        type: 'text',
        originalType: 'primary',
        ...options
    });
};

/**
 * 关闭所有消息提示
 */
export const closeAllMessages = () => {
    closeToast(true); // true 表示关闭所有 Toast
};

// ===================== Vant 原生类型便捷方法 =====================

/**
 * 显示文本消息 (Vant原生类型)
 * @param {string|Object} options - 消息内容或配置对象
 */
export const showMessageText = (options) => {
    if (typeof options === 'string') {
        return showMessageBase({
            type: 'text',
            message: options
        });
    }
    
    return showMessageBase({
        type: 'text',
        ...options
    });
};

/**
 * 显示加载消息 (Vant原生类型)
 * @param {string|Object} options - 消息内容或配置对象
 */
export const showMessageLoading = (options) => {
    if (typeof options === 'string') {
        return showMessageBase({
            type: 'loading',
            message: options
        });
    }
    
    return showMessageBase({
        type: 'loading',
        ...options
    });
};

/**
 * 显示HTML消息 (Vant原生类型)
 * @param {string|Object} options - 消息内容或配置对象
 */
export const showMessageHtml = (options) => {
    if (typeof options === 'string') {
        return showMessageBase({
            type: 'html',
            message: options
        });
    }
    
    return showMessageBase({
        type: 'html',
        ...options
    });
};

// ===================== Dialog 消息弹框封装 =====================

/**
 * 通用消息弹框函数 (基于 Vant Dialog)
 * @param {string|Object} options - 弹框配置选项
 * @param {string} [options.title=''] - 弹框标题
 * @param {string} [options.message=''] - 弹框内容
 * @param {string} [options.messageAlign='center'] - 内容对齐方式，可选值为 left center right
 * @param {'primary'|'success'|'warning'|'info'|'error'} [options.type=''] - 消息类型(在移动端主要影响样式)
 * @param {string} [options.className=''] - 自定义CSS类名
 * @param {boolean} [options.showConfirmButton=true] - 是否显示确认按钮
 * @param {boolean} [options.showCancelButton=false] - 是否显示取消按钮
 * @param {string} [options.confirmButtonText='OK'] - 确认按钮文字
 * @param {string} [options.cancelButtonText='Cancel'] - 取消按钮文字
 * @param {string} [options.confirmButtonColor=''] - 确认按钮颜色
 * @param {string} [options.cancelButtonColor=''] - 取消按钮颜色
 * @param {boolean} [options.closeOnClickOverlay=false] - 是否在点击遮罩层后关闭弹窗
 * @param {Function} [options.beforeClose] - 关闭前的回调函数
 * @returns {Promise} Promise对象，用于处理用户操作结果
 */
export const showMessageBox = (options) => {
    // 如果传入的是字符串，转换为配置对象
    if (typeof options === 'string') {
        options = { message: options };
    }
    
    const vantOptions = {
        title: options.title || '',
        message: options.message || '',
        messageAlign: options.messageAlign || 'center',
        className: options.className || '',
        showConfirmButton: options.showConfirmButton !== false,
        showCancelButton: options.showCancelButton || false,
        confirmButtonText: options.confirmButtonText || 'OK',
        cancelButtonText: options.cancelButtonText || 'Cancel',
        closeOnClickOverlay: options.closeOnClickOverlay || false,
        beforeClose: options.beforeClose
    };

    // 处理按钮颜色
    if (options.confirmButtonColor) {
        vantOptions.confirmButtonColor = options.confirmButtonColor;
    }
    if (options.cancelButtonColor) {
        vantOptions.cancelButtonColor = options.cancelButtonColor;
    }
    
    return showDialog(vantOptions);
};

/**
 * 显示警告提示弹框 (Alert)
 * @param {string} message - 消息内容
 * @param {string} [title=''] - 弹框标题
 * @param {Object} [options={}] - 其他配置选项
 * @returns {Promise} Promise对象
 */
export const showMessageBoxAlert = (message, title = '', options = {}) => {
    return showMessageBox({
        title,
        message,
        showConfirmButton: true,
        showCancelButton: false,
        confirmButtonText: 'OK',
        ...options
    });
};

/**
 * 显示确认弹框 (Confirm)
 * @param {string} message - 消息内容
 * @param {string} [title='Warning'] - 弹框标题
 * @param {Object} [options={}] - 其他配置选项
 * @returns {Promise} Promise对象
 */
export const showMessageBoxConfirm = (message, title = 'Warning', options = {}) => {
    return showConfirmDialog({
        title,
        message,
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        ...options
    });
};

// ===================== Notify 通知封装 =====================

/**
 * 通用通知函数 (基于 Vant Notify)
 * @param {string|Object} options - 通知配置选项
 * @param {string} [options.type='primary'] - 通知类型
 * @param {string} [options.message=''] - 通知内容
 * @param {string} [options.color=''] - 字体颜色
 * @param {string} [options.background=''] - 背景颜色
 * @param {string} [options.className=''] - 自定义CSS类名
 * @param {number} [options.duration=3000] - 显示时间(毫秒)，设为0则不自动关闭
 * @param {Function} [options.onClick] - 点击时的回调函数
 * @param {Function} [options.onClose] - 关闭时的回调函数
 * @param {Function} [options.onOpened] - 完全展示后的回调函数
 * @returns {Object} Notify实例，包含close方法用于手动关闭
 */
export const showNotificationBase = (options) => {
    // 如果传入的是字符串，转换为配置对象
    if (typeof options === 'string') {
        options = { message: options };
    }
    
    // 映射类型
    let vantType = 'primary';
    if (options.type) {
        const notifyTypeMap = {
            'primary': 'primary',
            'success': 'success',
            'warning': 'warning',
            'info': 'primary',
            'error': 'danger'
        };
        vantType = notifyTypeMap[options.type] || 'primary';
    }
    
    const vantOptions = {
        type: vantType,
        message: options.message || '',
        color: options.color,
        background: options.background,
        className: options.className || '',
        duration: options.duration || 3000,
        onClick: options.onClick,
        onClose: options.onClose,
        onOpened: options.onOpened
    };
    
    return showNotify(vantOptions);
};

/**
 * 显示成功通知
 * @param {string|Object} options - 通知内容或配置对象
 * @returns {Object} Notify实例
 */
export const showNotificationSuccess = (options) => {
    if (typeof options === 'string') {
        return showNotificationBase({
            type: 'success',
            message: options
        });
    }
    
    return showNotificationBase({
        type: 'success',
        ...options
    });
};

/**
 * 显示信息通知
 * @param {string|Object} options - 通知内容或配置对象
 * @returns {Object} Notify实例
 */
export const showNotificationInfo = (options) => {
    if (typeof options === 'string') {
        return showNotificationBase({
            type: 'info',
            message: options
        });
    }
    
    return showNotificationBase({
        type: 'info',
        ...options
    });
};

/**
 * 显示警告通知
 * @param {string|Object} options - 通知内容或配置对象
 * @returns {Object} Notify实例
 */
export const showNotificationWarning = (options) => {
    if (typeof options === 'string') {
        return showNotificationBase({
            type: 'warning',
            message: options
        });
    }
    
    return showNotificationBase({
        type: 'warning',
        ...options
    });
};

/**
 * 显示错误通知
 * @param {string|Object} options - 通知内容或配置对象
 * @returns {Object} Notify实例
 */
export const showNotificationError = (options) => {
    if (typeof options === 'string') {
        return showNotificationBase({
            type: 'error',
            message: options
        });
    }
    
    return showNotificationBase({
        type: 'error',
        ...options
    });
};

/**
 * 关闭所有通知
 */
export const closeAllNotifications = () => {
    showNotify.clear();
};

// ===================== Loading 加载提示封装 =====================

/**
 * 全屏加载提示 (基于 Vant LoadingToast)
 * @param {Object} [options={}] - 配置选项
 * @param {string} [options.message='Loading...'] - 加载文字
 * @param {boolean} [options.forbidClick=true] - 是否禁止背景点击
 * @param {number} [options.duration=0] - 显示时间，0为手动关闭
 * @param {string} [options.type='circular'] - 加载图标类型
 * @param {string} [options.loadingType='circular'] - 加载动画类型
 * @returns {Object} LoadingToast实例，包含close方法
 */
export const fullScreenLoading = (options = {}) => {
    const loadingOptions = {
        message: options.message || 'Loading...',
        forbidClick: options.forbidClick !== false,
        duration: options.duration || 0,
        type: 'loading',
        loadingType: options.loadingType || 'circular'
    };
    
    const loadingInstance = showLoadingToast(loadingOptions);
    
    // 5秒后保底关闭（与PC端保持一致）
    if (loadingOptions.duration === 0) {
        setTimeout(() => {
            try {
                loadingInstance.close();
            } catch (e) {
                console.warn('Loading already closed');
            }
        }, 5000);
    }
    
    return loadingInstance;
};

// 统一的UI反馈工具对象 - 提供便捷的访问方式
export const showUIFeedback = {
    success: showMessageSuccess,
    info: showMessageInfo,
    warning: showMessageWarning,
    error: showMessageError,
    primary: showMessagePrimary,
    loading: showMessageLoading,
    
    alert: showMessageBoxAlert,
    confirm: showMessageBoxConfirm,
    
    message: {
        success: showMessageSuccess,
        info: showMessageInfo,
        warning: showMessageWarning,
        error: showMessageError,
        primary: showMessagePrimary,
        loading: showMessageLoading
    },
    notification: {
        success: showNotificationSuccess,
        info: showNotificationInfo,
        warning: showNotificationWarning,
        error: showNotificationError
    },
    fullScreenLoading: fullScreenLoading,
    closeAllMessages,
    closeAllNotifications,
}