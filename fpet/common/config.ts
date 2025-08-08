declare const uni: any

function computeBaseUrl(): string {
  try {
    // 允许在运行时通过本地存储覆盖，适配真机调试
    // 可在控制台执行：uni.setStorageSync('base_url', 'http://192.168.1.11:8080')
    // @ts-ignore
    const stored =
      typeof uni !== 'undefined' && uni.getStorageSync
        ? uni.getStorageSync('base_url')
        : ''
    if (stored) return String(stored).replace(/\/+$/, '')
  } catch {}

  // 默认统一为你提供的局域网 IP
  return 'http://192.168.1.11:8080'
}

export const BASE_URL = computeBaseUrl()

export const API = {
  lostPets: '/api/lost-pets',
  petTypes: '/api/config/pet-types',
  cities: '/api/config/cities',
}

export const STORAGE_KEYS = {
  token: 'token',
  userInfo: 'userInfo',
}
