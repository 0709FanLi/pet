/**
 * 移动端UI反馈工具
 * 提供移动端专用的通知、提示等UI反馈功能
 */

/**
 * 显示基础通知
 * @param {Object} options - 通知选项
 * @param {string} options.message - 通知消息
 * @param {string} options.type - 通知类型 (success, error, warning, info)
 * @param {number} options.duration - 显示时长(毫秒)
 */
export function showNotificationBase(options = {}) {
    const {
        message = '',
        type = 'info',
        duration = 3000
    } = options;

    // 简单的移动端通知实现
    console.log(`[${type.toUpperCase()}] ${message}`);
    
    // 如果在浏览器环境中，可以使用更丰富的通知方式
    if (typeof window !== 'undefined') {
        // 创建简单的toast通知
        const toast = document.createElement('div');
        toast.textContent = message;
        toast.style.cssText = `
            position: fixed;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            background: ${getBackgroundColor(type)};
            color: white;
            padding: 12px 20px;
            border-radius: 6px;
            z-index: 9999;
            font-size: 14px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.2);
        `;
        
        document.body.appendChild(toast);
        
        // 自动移除
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, duration);
    }
}

/**
 * 根据类型获取背景颜色
 * @param {string} type - 通知类型
 * @returns {string} 背景颜色
 */
function getBackgroundColor(type) {
    const colors = {
        success: '#67C23A',
        error: '#F56C6C',
        warning: '#E6A23C',
        info: '#409EFF'
    };
    return colors[type] || colors.info;
}

/**
 * 显示成功通知
 * @param {string} message - 消息内容
 * @param {number} duration - 显示时长
 */
export function showSuccessNotification(message, duration = 3000) {
    showNotificationBase({ message, type: 'success', duration });
}

/**
 * 显示错误通知
 * @param {string} message - 消息内容
 * @param {number} duration - 显示时长
 */
export function showErrorNotification(message, duration = 3000) {
    showNotificationBase({ message, type: 'error', duration });
}

/**
 * 显示警告通知
 * @param {string} message - 消息内容
 * @param {number} duration - 显示时长
 */
export function showWarningNotification(message, duration = 3000) {
    showNotificationBase({ message, type: 'warning', duration });
}

/**
 * 显示信息通知
 * @param {string} message - 消息内容
 * @param {number} duration - 显示时长
 */
export function showInfoNotification(message, duration = 3000) {
    showNotificationBase({ message, type: 'info', duration });
}