import { getStorage, setStorage } from './storage.js'
import { routerToLogin } from "@/router/navigation.js";
import { DATE_FORMATS } from '@/types/date.ts';
import { t } from '@/language/index.js';
import { DeviceDetector } from '@/utils/device-detector';
import {ElMessage} from "element-plus"

const { isMobile } = DeviceDetector.getInstance().getDeviceInfo();


const months = [
  "Jan", "Feb", "Mar", "Apr", "May", "Jun",
  "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
];

export function empty (value){
  return (typeof value == 'undefined' || value == null || value == '');
}

/**
 * 将日期字符串格式化为简洁的日期格式（月份缩写、日期、年份）
 * @param {string} dateString - 需要格式化的日期字符串
 * @returns {string} 格式化后的日期字符串
 */
export function formatDateMDY_MonthAbbreviation(dateString) {
  if (!dateString) return "";

  const date = new Date(dateString);

  return `${months[date.getMonth()]} ${date.getDate()}, ${date.getFullYear()}`;
};

/**
 * 将UTC时间转换为用户本地时间
 * @param {string|number|Date} utcTime - UTC时间（支持ISO字符串、时间戳、Date对象）
 * @param {string} format - 返回格式，默认为美式日期时间格式
 *   支持自定义格式：YYYY（年）、MM（月）、DD（日）、HH（时）、mm（分）、ss（秒）占位符
 * @returns {string|null} 转换后的本地时间字符串，转换失败返回null
 */
export const convertUtcToLocal = (utcTime, format = DATE_FORMATS.US_DATETIME_WITHOUT_SECONDS) => {
    try {
        // 输入验证
        if (utcTime === null || utcTime === undefined || utcTime === '') {
            return null;
        }

        let date;

        // 处理不同输入类型，转换为Date对象
        if (utcTime instanceof Date) {
            date = new Date(utcTime);
        } else if (typeof utcTime === 'string') {
            // 处理字符串格式的UTC时间
            let timeString = utcTime.trim();

            // 如果没有时区标识，假设是UTC时间并添加Z
            if (!/[+-]\d{2}:?\d{2}|Z$/i.test(timeString)) {
                timeString += 'Z';
            }

            date = new Date(timeString);
        } else if (typeof utcTime === 'number') {
            // 处理时间戳（自动识别秒级或毫秒级）
            const timestamp = utcTime < 10000000000 ? utcTime * 1000 : utcTime;
            date = new Date(timestamp);
        } else {
            return null;
        }

        // 验证日期是否有效
        if (isNaN(date.getTime())) {
            return null;
        }

        // 获取本地时间的各个部分并格式化
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');

        // 使用模板替换生成最终格式
        return format
            .replace(/YYYY/g, year)
            .replace(/MM/g, month)
            .replace(/DD/g, day)
            .replace(/HH/g, hours)
            .replace(/mm/g, minutes)
            .replace(/ss/g, seconds);

    } catch (error) {
        console.error('Error converting UTC time to local:', error);
        return null;
    }
}

/**
 * 从UTC时间格式化为相对时间描述，自动处理时区转换
 * @param {string|number|Date} utcTime - UTC时间（支持ISO字符串、时间戳、Date对象）
 * @returns {string} 相对时间描述字符串，转换失败时返回默认值
 * @description
 *   - 24小时内：显示"X小时前"
 *   - 1-2天：显示"昨天"
 *   - 2-7天：显示"X天前"
 *   - 超过7天：显示月份和日期（如"Jan 15"）
 */
export const formatRelativeTimeFromUtc = (utcTime) => {
    try {
        // 输入验证
        if (utcTime === null || utcTime === undefined || utcTime === '') {
            return t('unknown');
        }

        // 转换为本地时间戳
        let localTimestamp;
        if (utcTime instanceof Date) {
            localTimestamp = utcTime.getTime();
        } else if (typeof utcTime === 'string') {
            let timeString = utcTime.trim();
            if (!/[+-]\d{2}:?\d{2}|Z$/i.test(timeString)) {
                timeString += 'Z';
            }
            const date = new Date(timeString);
            if (isNaN(date.getTime())) return t('unknown');
            localTimestamp = date.getTime();
        } else if (typeof utcTime === 'number') {
            localTimestamp = utcTime < 10000000000 ? utcTime * 1000 : utcTime;
        } else {
            return t('unknown');
        }

        // 格式化为相对时间
        const now = Date.now();
        const timeDiff = now - localTimestamp;
        const DAY_MS = 24 * 60 * 60 * 1000;
        const HOUR_MS = 60 * 60 * 1000;

        if (timeDiff < DAY_MS) {
            const hoursAgo = Math.floor(timeDiff / HOUR_MS);
            return hoursAgo < 1 ? t('hourAgo', { count: 1 }) : t('hoursAgo', { count: hoursAgo });
        }

        if (timeDiff < 2 * DAY_MS) return t('yesterday');

        if (timeDiff < 7 * DAY_MS) {
            const daysAgo = Math.floor(timeDiff / DAY_MS);
            return t('daysAgo', { count: daysAgo });
        }

        const date = new Date(localTimestamp);
        return `${months[date.getMonth()]} ${date.getDate()}`;

    } catch (error) {
        console.error('Error formatting relative time from UTC:', error);
        return t('unknown');
    }
}

/**
 * 检查UTC时间是否在指定天数范围内
 * @param {string|number|Date} utcTime - UTC时间
 * @param {number} days - 天数范围
 * @returns {boolean} 是否在指定天数范围内
 */
export const isWithinDaysFromUtc = (utcTime, days) => {
    try {
        // 输入验证
        if (utcTime === null || utcTime === undefined || utcTime === '' || !days) {
            return false;
        }

        // 转换为本地时间戳
        let localTimestamp;
        if (utcTime instanceof Date) {
            localTimestamp = utcTime.getTime();
        } else if (typeof utcTime === 'string') {
            let timeString = utcTime.trim();
            if (!/[+-]\d{2}:?\d{2}|Z$/i.test(timeString)) {
                timeString += 'Z';
            }
            const date = new Date(timeString);
            if (isNaN(date.getTime())) return false;
            localTimestamp = date.getTime();
        } else if (typeof utcTime === 'number') {
            localTimestamp = utcTime < 10000000000 ? utcTime * 1000 : utcTime;
        } else {
            return false;
        }

        const now = Date.now();
        const timeDiff = now - localTimestamp;
        const dayInMs = 24 * 60 * 60 * 1000;

        return timeDiff >= 0 && timeDiff <= (days * dayInMs);

    } catch (error) {
        console.error('Error checking time range from UTC:', error);
        return false;
    }
}

/**
 * 通用防抖函数
 * 在事件触发后的延迟时间内，如果再次触发事件，则重新计时
 * @param {Function} fn - 要防抖的函数
 * @param {number} delay - 延迟时间（毫秒）
 * @returns {Function} 防抖后的函数
 */
export function debounce(fn, delay = 300) {
  let timeoutId = null;
  return function (...args) {
    // 清除之前的定时器
    if (timeoutId) {
      clearTimeout(timeoutId);
    }
    // 设置新的定时器
    timeoutId = setTimeout(() => {
      fn.apply(this, args);
    }, delay);
  };
}

/**
 * 通用节流函数
 * 在指定时间间隔内，函数最多只能执行一次
 * @param {Function} fn - 要节流的函数
 * @param {number} interval - 时间间隔（毫秒）
 * @returns {Function} 节流后的函数
 */
export function throttle(fn, interval = 300) {
  let lastTime = 0;
  return function (...args) {
    const now = Date.now();
    if (now - lastTime >= interval) {
      lastTime = now;
      fn.apply(this, args);
    }
  };
}

/**
 * 防抖节流组合函数
 * 结合防抖和节流的优点，既能防止频繁触发，又能保证在较长时间内至少执行一次
 * @param {Function} fn - 要处理的函数
 * @param {number} debounceDelay - 防抖延迟时间（毫秒）
 * @param {number} throttleInterval - 节流间隔时间（毫秒）
 * @returns {Function} 处理后的函数
 */
export function debounceThrottle(fn, debounceDelay = 300, throttleInterval = 1000) {
  let debounceTimeoutId = null;
  let lastThrottleTime = 0;
  
  return function (...args) {
    const now = Date.now();
    
    // 节流：如果距离上次执行时间小于间隔，启用防抖
    if (now - lastThrottleTime < throttleInterval) {
      // 清除之前的防抖定时器
      if (debounceTimeoutId) {
        clearTimeout(debounceTimeoutId);
      }
      // 设置防抖
      debounceTimeoutId = setTimeout(() => {
        lastThrottleTime = Date.now();
        fn.apply(this, args);
      }, debounceDelay);
    } else {
      // 节流时间已过，立即执行
      lastThrottleTime = now;
      fn.apply(this, args);
    }
  };
}

// 访客消息限制管理工具
export const guestMessageLimit = {
  // 消息数量的存储键名
  GUEST_MESSAGE_KEY: 'guest_message_count',

  // 访客可发送的最大消息数
  MAX_GUEST_MESSAGES: 3,

  /**
   * 获取未登录用户已发送的消息数量
   * @returns {number} 消息数量
   */
  getMessageCount() {
    return getStorage(this.GUEST_MESSAGE_KEY) || 0;
  },

  /**
   * 增加未登录用户已发送的消息数量
   * @returns {number} 增加后的消息数量
   */
  increaseMessageCount() {
    const count = this.getMessageCount();
    const newCount = count + 1;
    setStorage(this.GUEST_MESSAGE_KEY, newCount);
    return newCount;
  },

  /**
   * 检查是否达到消息发送限制
   * @returns {boolean} 是否达到限制
   */
  hasReachedLimit() {
    return this.getMessageCount() >= this.MAX_GUEST_MESSAGES;
  },

  /**
   * 获取剩余可发送消息数量
   * @returns {number} 剩余消息数量
   */
  getRemainingCount() {
    const remaining = this.MAX_GUEST_MESSAGES - this.getMessageCount();
    return remaining > 0 ? remaining : 0;
  },

  /**
   * 重置访客消息计数
   */
  resetMessageCount() {
    setStorage(this.GUEST_MESSAGE_KEY, 0);
  },

  /**
   * 显示限制警告并跳转到登录页
   */
  showLimitWarning(message) {
    const remaining = this.getRemainingCount();
    if (remaining <= 1) {
      showToastFun(message,'warning');
      if (remaining === 0) {
        routerToLogin();
      }
    }
  },

  /**
   * 处理消息发送前的限制检查
   */
  handleMessageLimit({ isChat, isLoggedIn, willSend, message }) {
    if (!isChat || isLoggedIn) {
      return true;
    }
    const remaining = this.getRemainingCount();
    this.showLimitWarning(message);
    if (willSend && remaining > 0) {
      this.increaseMessageCount();
    }

    return remaining > 0;
  }
};

export function useSmoothScroll() {
  // 平滑滚动到指定 ref
  const smoothScrollToRef = (targetRef, options = {}) => {
    if (!targetRef.value) return
    const {
      container = window,
      duration = 800,
      offset = 0,
      easing = 'easeInOutCubic'
    } = options

    // 获取目标元素位置
    const targetPosition = targetRef.value.getBoundingClientRect().top +
                          (container.scrollY || container.scrollTop) -
                          offset

    // 获取容器的当前滚动位置
    const startPosition = container.scrollY || container.scrollTop
    const distance = targetPosition - startPosition

    // 缓动函数集合
    const easingFunctions = {
      linear: (t) => t,
      easeInOutCubic: (t) => t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2,
      easeOutQuart: (t) => 1 - Math.pow(1 - t, 4),
      easeOutExpo: (t) => t === 1 ? 1 : 1 - Math.pow(2, -10 * t)
    }

    const easingFn = easingFunctions[easing] || easingFunctions.easeInOutCubic
    const startTime = performance.now()

    // 动画函数
    const animateScroll = (currentTime) => {
      const elapsedTime = currentTime - startTime
      const progress = Math.min(elapsedTime / duration, 1)
      const easeProgress = easingFn(progress)

      // 更新滚动位置
      if (container === window) {
        window.scrollTo(0, startPosition + distance * easeProgress)
      } else {
        container.scrollTop = startPosition + distance * easeProgress
      }

      // 继续动画循环
      if (progress < 1) {
        requestAnimationFrame(animateScroll)
      }
    }

    // 开始动画
    requestAnimationFrame(animateScroll)
  }

  return {
    smoothScrollToRef
  }
}

  // 复制到剪贴板
export function copy(text) {
  // 尝试使用现代Clipboard API
  if (navigator.clipboard && window.isSecureContext) {
    navigator.clipboard.writeText(text).catch((err) => {
      console.error("Clipboard API error:", err);
      fallbackCopyTextToClipboard(text);
    });
  } else {
    // 回退到传统的复制方法
    fallbackCopyTextToClipboard(text);
  }

}

// 传统复制方法
const fallbackCopyTextToClipboard = (text) => {
  try {
    const textArea = document.createElement("textarea");
    textArea.value = text;

    // 使文本框不可见
    textArea.style.position = "fixed";
    textArea.style.top = "0";
    textArea.style.left = "0";
    textArea.style.width = "2em";
    textArea.style.height = "2em";
    textArea.style.padding = "0";
    textArea.style.border = "none";
    textArea.style.outline = "none";
    textArea.style.boxShadow = "none";
    textArea.style.background = "transparent";

    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();

    const successful = document.execCommand("copy");
    document.body.removeChild(textArea);

    if (!successful) {
      showToastFun(t('clipboardError'));
    }
  } catch (err) {
    console.error("Fallback clipboard error:", err);
    showToastFun(t('clipboardError'));
  }
};

/**
 * 根据生日计算年龄
 * @param {string|Date} birthday - 生日，支持多种格式：YYYY-MM-DD、YYYY/MM/DD、MM/DD/YYYY、Date对象等
 * @returns {number|null} 年龄数字，转换失败返回null
 */
export function calculateAge(birthday) {
  if (!birthday) {
    return null;
  }

  let birthDate;

  try {
    // 如果已经是Date对象
    if (birthday instanceof Date) {
      birthDate = birthday;
    } else if (typeof birthday === 'string') {
      // 处理字符串格式
      const trimmedBirthday = birthday.trim();

      // 支持的日期格式正则表达式
      const dateFormats = [
        /^\d{4}-\d{1,2}-\d{1,2}$/, // YYYY-MM-DD 或 YYYY-M-D
        /^\d{4}\/\d{1,2}\/\d{1,2}$/, // YYYY/MM/DD 或 YYYY/M/D
        /^\d{1,2}\/\d{1,2}\/\d{4}$/, // MM/DD/YYYY 或 M/D/YYYY
        /^\d{1,2}-\d{1,2}-\d{4}$/, // MM-DD-YYYY 或 M-D-YYYY
        /^\d{4}\.\d{1,2}\.\d{1,2}$/, // YYYY.MM.DD 或 YYYY.M.D
        /^\d{1,2}\.\d{1,2}\.\d{4}$/, // MM.DD.YYYY 或 M.D.YYYY
      ];

      // 检查是否匹配支持的格式
      const isValidFormat = dateFormats.some(format => format.test(trimmedBirthday));
      if (!isValidFormat) {
        return null;
      }

      // 尝试解析日期
      birthDate = new Date(trimmedBirthday);
    } else {
      return null;
    }

    // 检查日期是否有效
    if (isNaN(birthDate.getTime())) {
      return null;
    }

    // 检查日期是否合理（不能是未来日期，不能太久远）
    const now = new Date();
    const currentYear = now.getFullYear();
    const birthYear = birthDate.getFullYear();

    // 生日不能是未来日期
    if (birthDate > now) {
      return null;
    }

    // 生日年份不能太久远（假设不超过150岁）
    if (currentYear - birthYear > 150) {
      return null;
    }

    // 计算年龄
    let age = currentYear - birthYear;
    const currentMonth = now.getMonth();
    const birthMonth = birthDate.getMonth();
    const currentDay = now.getDate();
    const birthDay = birthDate.getDate();

    // 如果今年的生日还没到，年龄减1
    if (currentMonth < birthMonth || (currentMonth === birthMonth && currentDay < birthDay)) {
      age--;
    }

    // 年龄不能小于0
    if (age < 0) {
      return null;
    }

    return age;

  } catch (error) {
    console.error('Error calculating age:', error);
    return null;
  }
}


/**
 * 数字保留小数位
 * @param {number} number 数字
 * @param {number} digits 小数位数
 * @returns {number} 格式化后的数字
 */
export function numberFixed(number, digits) {
  return parseFloat(number.toFixed(digits));
}

/**
 * 获取图片尺寸
 * @param {string} url 图片URL
 * @returns {Promise<{width: number, height: number} | null>} 图片尺寸
 */
export function getImageDimensions(url) {
  return new Promise((resolve) => {
    const img = new Image();
    img.onload = () => {
      resolve({
        width: img.naturalWidth,
        height: img.naturalHeight
      });
    };
    img.onerror = () => {
      resolve(null);
    };
    img.src = url;
  });
}
/**
 * 检查表单数据是否与原始数据完全一致
 * @param {Object} currentData - 当前表单数据
 * @param {Object} originalData - 原始快速填写数据
 * @param {Array} fieldMappings - 字段映射配置
 * @returns {boolean} - 如果数据完全一致返回true，否则返回false
 */
export const isFormDataIdentical = (currentData, originalData, fieldMappings) => {
  if (!originalData) return false;

  for (const mapping of fieldMappings) {
    const { current, original, defaultValue = "" } = mapping;

    // 获取当前表单值
    const currentValue = getNestedValue(currentData, current) || defaultValue;

    // 获取原始数据值，支持多个字段映射
    let originalValue = defaultValue;
    if (Array.isArray(original)) {
      // 尝试从多个字段中获取值
      for (const field of original) {
        const value = getNestedValue(originalData, field);
        if (value !== undefined && value !== null && value !== "") {
          originalValue = value;
          break;
        }
      }
    } else {
      originalValue = getNestedValue(originalData, original) || defaultValue;
    }

    // 比较值
    if (currentValue !== originalValue) {
      return false;
    }
  }

  return true;
};

/**
 * 获取嵌套对象中的值
 * @param {Object} obj - 目标对象
 * @param {string} path - 属性路径，支持点号分隔
 * @returns {any} - 属性值
 */
const getNestedValue = (obj, path) => {
  return path.split('.').reduce((current, key) => current?.[key], obj);
};

/**
 * 处理未完成功能提示
 */
export const handleNotYet = () => { 
    showToastFun('This function is not yet available','warning');
};

/**
 * 获取元素的客户端矩形宽度（像素字符串格式）
 * @param {HTMLElement|Ref<HTMLElement>} element - DOM元素或Vue ref
 * @return {string} 宽度的像素字符串，如 "300px"
 */
export function getElementClientWidth(element) {
    const el = element?.value || element;
    if (!el || typeof el.getBoundingClientRect !== 'function') {
        return '0px';
    }
    const { width } = el.getBoundingClientRect();
    return numberFixed(width, 2) + 'px';
}

/**
 * 根据不同方法获取网站图标URL
 * @param {string} url - 网站URL
 * @param {number} methodIndex - 方法索引 (0-3)
 * @returns {string} 图标URL
 * @description 提供4种获取网站图标的方法：
 *   0: Google Favicon API (最可靠，支持所有浏览器)
 *   1: icon.horse服务（备用方案1）
 *   2: 直接从网站获取favicon（可能被CORS阻止）
 *   3: GitHub的favicon服务（备用方案2）
 */
export const getFaviconUrlByMethod = (url, methodIndex = 0) => {
  try {
    const domain = new URL(url).hostname;
    const methods = [
      // 方法1: Google Favicon API (最可靠，支持所有浏览器)
      `https://www.google.com/s2/favicons?sz=64&domain_url=${encodeURIComponent(url)}`,
      // 方法2: 使用icon.horse服务（备用方案1）
      `https://icon.horse/icon/${domain}`,
      // 方法3: 直接从网站获取favicon（可能被CORS阻止）
      `https://${domain}/favicon.ico`,
      // 方法4: 使用GitHub的favicon服务（备用方案2）
      `https://favicons.githubusercontent.com/${domain}`,
    ];

    return methods[methodIndex] || getDefaultFaviconUrl();
  } catch (error) {
    console.warn('Invalid URL for favicon:', url);
    return getDefaultFaviconUrl();
  }
};

/**
 * 获取默认图标URL
 * @returns {string} 默认图标路径
 * @description 当所有获取方法都失败时使用的默认图标
 */
export const getDefaultFaviconUrl = () => {
  return '/static/images/chat_icon.png';
};

/**
 * 从URL中提取域名
 * @param {string} url - 完整URL
 * @returns {string} 域名
 * @description 安全地从URL中提取域名，失败时返回默认值
 */
export const getDomainFromUrl = (url) => {
  try {
    return new URL(url).hostname;
  } catch {
    return 'Unknown domain';
  }
};

/**
 * 网站图标管理器类
 * @description 用于管理多个图标的加载状态和重试逻辑
 */
export class FaviconManager {
  constructor() {
    /** @type {Map<number, {retryCount: number}>} 记录加载失败的图标索引和尝试次数 */
    this.errorMap = new Map();
    /** @type {number} 最多尝试4种获取方法 */
    this.maxRetryMethods = 4;
  }

  /**
   * 获取图标URL，支持失败重试
   * @param {string} url - 网站URL
   * @param {number} originalIndex - 原始索引
   * @returns {string} 图标URL
   * @description 根据失败次数自动选择不同的获取方法
   */
  getFaviconUrl(url, originalIndex) {
    const errorInfo = this.errorMap.get(originalIndex);

    if (errorInfo && errorInfo.retryCount >= this.maxRetryMethods) {
      // 所有方法都失败了，返回默认图标
      return getDefaultFaviconUrl();
    }

    const methodIndex = errorInfo ? errorInfo.retryCount : 0;
    return getFaviconUrlByMethod(url, methodIndex);
  }

  /**
   * 处理图标加载失败
   * @param {number} originalIndex - 原始索引
   * @param {string} url - 失败的URL
   * @description 记录失败次数，用于下次重试时选择不同方法
   */
  handleFaviconError(originalIndex, url) {
    const currentError = this.errorMap.get(originalIndex) || { retryCount: 0 };

    // 增加重试次数
    this.errorMap.set(originalIndex, {
      retryCount: currentError.retryCount + 1
    });

    console.log(`Favicon load failed for ${url}, retry count: ${currentError.retryCount + 1}`);
  }

  /**
   * 重置错误状态
   * @description 清空所有错误记录，通常在新的图标加载会话开始时调用
   */
  reset() {
    this.errorMap.clear();
  }

  /**
   * 检查是否应该显示图标
   * @param {number} originalIndex - 原始索引
   * @returns {boolean} 是否应该显示
   * @description 判断图标是否还有重试机会，用于决定是否继续显示
   */
  shouldShowIcon(originalIndex) {
    const errorInfo = this.errorMap.get(originalIndex);
    return !errorInfo || errorInfo.retryCount < this.maxRetryMethods;
  }
}

/**
 * 从URL中提取文件名
 * @param {string} url - 文件URL
 * @returns {string} 提取的文件名
 */
function extractFilenameFromUrl(url) {
    try {
        // 移除URL参数
        const urlWithoutParams = url.split('?')[0];
        // 提取文件名
        const filename = urlWithoutParams.split('/').pop();
        // 如果没有扩展名，根据URL推断类型并添加默认扩展名
        if (filename && !filename.includes('.')) {
            const ext = url.split('.').pop().toLowerCase();
            const defaultExt = ['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(ext) ? 'png' : 
                              ['mp4', 'webm', 'mov', 'avi'].includes(ext) ? 'mp4' : 'file';
            return `${filename}.${defaultExt}`;
        }
        return filename || 'download';
    } catch (error) {
        return 'download';
    }
}

/**
 * 测试资源加载（仅用于图片）
 * @param {string} url - 资源URL
 * @param {string} type - 资源类型
 * @returns {Promise} 加载结果
 */
function testResourceLoad(url, type) {
    return new Promise((resolve, reject) => {
        const element = type === 'image' ? new Image() : document.createElement('video');
        element.onload = resolve;
        element.onerror = reject;
        element.src = url;
        
        // 视频需要手动触发加载
        if (type === 'video') {
            element.preload = 'metadata';
            element.load();
        }
        
        // 设置超时
        setTimeout(() => reject(new Error('资源加载超时')), 5000);
    });
}

/**
 * 检查URL是否跨域
 * @param {string} url - 要检查的URL
 * @returns {boolean} 是否跨域
 */
function isCrossOrigin(url) {
    try {
        const urlObj = new URL(url, window.location.origin);
        return urlObj.origin !== window.location.origin;
    } catch (error) {
        return true; // 无效URL视为跨域
    }
}

/**
 * 尝试使用download属性下载（仅用于非跨域资源）
 * @param {string} url - 文件URL
 * @param {string} filename - 文件名
 * @returns {Object} 下载结果
 */
function tryDownloadWithAttribute(url, filename) {
    try {
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        
        return { 
            success: true, 
            method: 'downloadAttribute',
            message: '下载已开始'
        };
        
    } catch (error) {
        throw new Error(`下载失败: ${error.message}`);
    }
}

/**
 * 尝试使用Blob下载
 * @param {string} url - 文件URL
 * @param {string} filename - 文件名
 * @returns {Promise<Object>} 下载结果
 */
async function tryDownloadWithBlob(url, filename) {
    try {
        // 获取文件数据
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`网络请求失败: ${response.status} ${response.statusText}`);
        }
        
        const blob = await response.blob();
        const blobUrl = URL.createObjectURL(blob);
        
        try {
            // 创建下载链接
            const a = document.createElement('a');
            a.href = blobUrl;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            
            return { 
                success: true, 
                method: 'blobDownload',
                message: '下载已开始',
                fileSize: blob.size
            };
            
        } finally {
            // 释放Blob URL
            URL.revokeObjectURL(blobUrl);
        }
        
    } catch (error) {
        throw new Error(`Blob下载失败: ${error.message}`);
    }
}

/**
 * 使用window.open作为最后的降级方案
 * @param {string} url - 文件URL
 * @returns {Object} 下载结果
 */
function tryDownloadWithWindowOpen(url) {
    try {
        const opened = window.open(url, '_blank');
        if (opened === null) {
            // 弹窗被阻止
            throw new Error('弹窗被浏览器阻止，请允许弹窗后重试');
        } else {
            return { 
                success: true, 
                method: 'windowOpen',
                message: '已在新窗口中打开文件'
            };
        }
    } catch (error) {
        throw new Error(`打开文件失败: ${error.message}`);
    }
}

/**
 * 下载文件的主函数
 * @param {string} url - 文件URL
 * @param {string} filename - 文件名，不传则从URL中自动获取
 * @param {string} type - 文件类型，可选值：'image'、'video'、null（自动推断）
 * @returns {Promise<Object>} 下载结果，包含 {success, method, message, error?}
 */
export async function downloadFile(url, filename = null, type = null) {
    try {
        // 输入验证
        if (!url || typeof url !== 'string') {
            throw new Error('无效的下载链接');
        }
        
        // 如果没有传入文件名，从URL中提取
        if (!filename) {
            filename = extractFilenameFromUrl(url);
        }
        
        // 自动推断资源类型
        if (!type) {
            const ext = url.split('.').pop().toLowerCase();
            type = ['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(ext) ? 'image' : 
                   ['mp4', 'webm', 'mov', 'avi'].includes(ext) ? 'video' : 'unknown';
        }

        // 优先检查是否跨域，如果是跨域直接使用 Blob 下载
        if (isCrossOrigin(url)) {
            console.log('检测到跨域资源，直接使用 Blob 下载方式');
            return await tryDownloadWithBlob(url, filename);
        }

        // 非跨域资源，根据文件类型选择下载策略
        if (type === 'image') {
            // 图片：先检测资源是否可用，然后使用download属性下载
            try {
                await testResourceLoad(url, 'image');
                console.log('图片资源检测成功，使用 download 属性下载');
                return tryDownloadWithAttribute(url, filename);
            } catch (loadError) {
                console.warn('图片预加载失败，降级到 Blob 下载:', loadError.message);
                return await tryDownloadWithBlob(url, filename);
            }
        } else if (type === 'video') {
            // 视频：为了稳定性，优先使用Blob下载
            console.log('视频文件使用 Blob 下载方式');
            return await tryDownloadWithBlob(url, filename);
        } else {
            // 未知类型：先尝试download属性，失败后尝试Blob下载
            try {
                console.log('未知文件类型，尝试使用 download 属性下载');
                return tryDownloadWithAttribute(url, filename);
            } catch (attributeError) {
                console.warn('download 属性下载失败，降级到 Blob 下载:', attributeError.message);
                return await tryDownloadWithBlob(url, filename);
            }
        }
        
    } catch (error) {
        // 所有方法都失败，尝试最后的降级方案
        console.warn('常规下载方法失败，使用 window.open 降级:', error.message);
        try {
            return tryDownloadWithWindowOpen(url);
        } catch (fallbackError) {
            // 完全失败
            throw new Error(`下载失败: ${error.message}。降级方案也失败: ${fallbackError.message}`);
        }
    }
}

/**
 * 下载图片的便捷方法
 * @param {string} url - 图片URL
 * @param {string} filename - 可选的文件名，不传则从URL中自动获取
 * @returns {Promise<Object>} 下载结果，包含 {success, method, message, error?}
 * @description Promise状态说明：
 *   - resolve: 下载已成功开始或完成
 *   - reject: 下载失败，包含详细错误信息
 */
export async function downloadImage(url, filename = null) {
    return await downloadFile(url, filename, 'image');
}

/**
 * 下载视频的便捷方法
 * @param {string} url - 视频URL
 * @param {string} filename - 可选的文件名，不传则从URL中自动获取
 * @returns {Promise<Object>} 下载结果，包含 {success, method, message, error?}
 * @description Promise状态说明：
 *   - resolve: 下载已成功开始或完成
 *   - reject: 下载失败，包含详细错误信息
 */
export async function downloadVideo(url, filename = null) {
    return await downloadFile(url, filename, 'video');
}

/**
 * 显示Toast - 平台自适应版本
 * @param {string} text - 要显示的文本
 * @param {string} type - PC端要显示的文本类型 'primary' (2.9.11) | 'success' | 'warning' | 'info' | 'error'
 */
export const showToastFun = async (text, type = 'error') => {
  // 统一使用ElMessage组件
  if (ElMessage) {
    if (type == 'primary') {
      ElMessage.primary(text);
    } else if (type == 'success') {
      ElMessage.success(text);
    } else if (type == 'warning') {
      ElMessage.warning(text);
    } else if (type == 'info') {
      ElMessage.info(text);
    } else if (type == 'error') {
      ElMessage.error(text);
    }
  } else {
    console.warn('Message组件未加载，降级到console输出:', text);
  }
}
