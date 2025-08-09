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

  // H5 场景：随当前主机自动拼接端口，避免每次改IP
  try {
    // @ts-ignore
    const loc = typeof window !== 'undefined' ? window.location : null
    if (loc && loc.hostname) {
      return `${loc.protocol}//${loc.hostname}:8080`
    }
  } catch {}

  // 默认回退为你的局域网 IP（也可用 base_url 覆盖）
  return 'http://192.168.1.11:8080'
}

export const BASE_URL = computeBaseUrl()

export const API = {
  lostPets: '/api/lost-pets',
  petTypes: '/api/config/pet-types',
  cities: '/api/config/cities',
  detective: {
    apply: '/api/detective/apply',
    application: '/api/detective/application',
    reviewLog: '/api/detective/application/review-log',
  },
}

export const STORAGE_KEYS = {
  token: 'token',
  userInfo: 'userInfo',
}
