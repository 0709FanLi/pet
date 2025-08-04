/**
 * 移动端本地存储工具类 - 简化版
 */

/**
 * 设置localStorage
 * @param {string} key 键名
 * @param {any} value 值
 */
export function setStorage(key, value) {
  try {
    if (typeof value === 'object' && value !== null) {
      localStorage.setItem(key, JSON.stringify(value))
    } else {
      localStorage.setItem(key, String(value))
    }
  } catch (error) {
    console.error('存储设置失败:', error)
  }
}

/**
 * 获取localStorage
 * @param {string} key 键名
 * @returns {any} 存储的值
 */
export function getStorage(key) {
  try {
    const value = localStorage.getItem(key)

    if (value === null) return null

    // 尝试解析JSON
    try {
      return JSON.parse(value)
    } catch (error) {
      // 不是JSON格式，返回原始值并尝试转换类型
      if (value === 'true') return true
      if (value === 'false') return false
      if (!isNaN(value) && value.trim() !== '') return Number(value)
      return value
    }
  } catch (error) {
    console.error('存储获取失败:', error)
    return null
  }
}

/**
 * 移除localStorage
 * @param {string} key 键名
 */
export function removeStorage(key) {
  try {
    localStorage.removeItem(key)
  } catch (error) {
    console.error('存储移除失败:', error)
  }
}

/**
 * 清空所有localStorage
 */
export function clearStorage() {
  try {
    localStorage.clear()
  } catch (error) {
    console.error('存储清空失败:', error)
  }
}

/**
 * 获取所有localStorage键
 * @returns {Array} 所有键的数组
 */
export function getStorageKeys() {
  try {
    return Object.keys(localStorage)
  } catch (error) {
    console.error('获取存储键失败:', error)
    return []
  }
}

/**
 * 检查key是否存在
 * @param {string} key 键名
 * @returns {boolean} 是否存在
 */
export function hasStorage(key) {
  try {
    return localStorage.getItem(key) !== null
  } catch (error) {
    console.error('检查存储存在性失败:', error)
    return false
  }
}
