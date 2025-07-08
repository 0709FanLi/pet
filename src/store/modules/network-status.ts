import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useNetworkStatusStore = defineStore('networkStatus', () => {
  // 网络状态
  const isOnline = ref(navigator.onLine);
  
  // 消息回调函数，由外部传入
  let onNetworkStatusChange: ((isOnline: boolean) => void) | null = null;
  
  // 更新网络状态
  const updateOnlineStatus = () => {
    const wasOnline = isOnline.value;
    isOnline.value = navigator.onLine;
    
    // 只在状态变化时触发回调
    if (wasOnline !== isOnline.value && onNetworkStatusChange) {
      onNetworkStatusChange(isOnline.value);
    }
  };
  
  // 设置网络状态变化回调
  const setNetworkStatusChangeCallback = (callback: (isOnline: boolean) => void) => {
    onNetworkStatusChange = callback;
  };
  
  // 初始化网络状态监听
  const initNetworkListener = () => {
    window.addEventListener('online', updateOnlineStatus);
    window.addEventListener('offline', updateOnlineStatus);
  };
  
  // 清理网络状态监听
  const removeNetworkListener = () => {
    window.removeEventListener('online', updateOnlineStatus);
    window.removeEventListener('offline', updateOnlineStatus);
    onNetworkStatusChange = null;
  };
  
  // 手动检查网络状态
  const checkNetworkStatus = () => {
    updateOnlineStatus();
  };
  
  return {
    // 状态
    isOnline,
    
    // 方法
    updateOnlineStatus,
    setNetworkStatusChangeCallback,
    initNetworkListener,
    removeNetworkListener,
    checkNetworkStatus
  };
}); 