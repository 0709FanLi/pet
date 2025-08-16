/**
 * 宠物侦探申请信息缓存工具类
 * 实现本地缓存和服务器缓存的同步管理
 */

import { API, STORAGE_KEYS } from './config'
import { request } from './request'

declare const uni: any

// 缓存接口定义
export interface DetectiveCacheData {
  // 基础信息
  realName?: string
  phone?: string
  city?: string
  companyName?: string
  address?: string

  // 团队与设备
  teamSize?: number
  devices?: string[]
  devicePhotos?: string[]

  // 能力与时间
  experienceYears?: number
  serviceAreas?: string[]
  availableTimes?: string[]
  bio?: string

  // 认证与资质
  idCardFront?: string
  idCardBack?: string
  certificates?: string[]

  // 收款信息
  payout?: {
    type?: string
    account?: string
  }

  // 紧急联系人
  emergencyContact?: {
    name?: string
    phone?: string
  }

  // 同意协议
  agree?: boolean

  // 缓存时间戳
  cachedAt?: string
  updatedAt?: string
}

// 图片缓存接口
export interface ImageCacheItem {
  url: string // 显示URL（可能是临时文件路径）
  raw?: string // 服务器路径
  localPath?: string // 本地临时文件路径
}

export class DetectiveCache {
  /**
   * 保存申请信息到本地缓存
   */
  static saveToLocal(data: DetectiveCacheData): void {
    try {
      const cacheData = {
        ...data,
        cachedAt: new Date().toISOString(),
      }
      uni.setStorageSync(
        STORAGE_KEYS.detectiveFormCache,
        JSON.stringify(cacheData)
      )
      console.log('[DetectiveCache] 本地缓存已保存')
    } catch (error) {
      console.error('[DetectiveCache] 本地缓存保存失败:', error)
    }
  }

  /**
   * 从本地缓存读取申请信息
   */
  static getFromLocal(): DetectiveCacheData | null {
    try {
      const cached = uni.getStorageSync(STORAGE_KEYS.detectiveFormCache)
      if (cached) {
        const data = JSON.parse(cached)
        console.log('[DetectiveCache] 本地缓存已读取')
        return data
      }
      return null
    } catch (error) {
      console.error('[DetectiveCache] 本地缓存读取失败:', error)
      return null
    }
  }

  /**
   * 清除本地缓存
   */
  static clearLocal(): void {
    try {
      uni.removeStorageSync(STORAGE_KEYS.detectiveFormCache)
      uni.removeStorageSync(STORAGE_KEYS.detectiveImageCache)
      console.log('[DetectiveCache] 本地缓存已清除')
    } catch (error) {
      console.error('[DetectiveCache] 本地缓存清除失败:', error)
    }
  }

  /**
   * 保存申请信息到服务器
   */
  static async saveToServer(data: DetectiveCacheData): Promise<boolean> {
    try {
      const token = uni.getStorageSync(STORAGE_KEYS.token)
      if (!token) {
        console.warn('[DetectiveCache] 未登录，无法保存到服务器')
        return false
      }

      const response = await request({
        url: API.detective.cache.save,
        method: 'POST',
        data,
        header: {
          Authorization: `Bearer ${token}`,
        },
      })

      if (response.success) {
        console.log('[DetectiveCache] 服务器缓存已保存')
        return true
      } else {
        console.error('[DetectiveCache] 服务器缓存保存失败:', response.message)
        return false
      }
    } catch (error) {
      console.error('[DetectiveCache] 服务器缓存保存异常:', error)
      return false
    }
  }

  /**
   * 从服务器获取申请信息
   */
  static async getFromServer(): Promise<DetectiveCacheData | null> {
    try {
      const token = uni.getStorageSync(STORAGE_KEYS.token)
      if (!token) {
        console.warn('[DetectiveCache] 未登录，无法从服务器获取')
        return null
      }

      const response = await request({
        url: API.detective.cache.get,
        method: 'GET',
        header: {
          Authorization: `Bearer ${token}`,
        },
      })

      if (response.success && response.data) {
        console.log('[DetectiveCache] 服务器缓存已获取')
        return response.data
      } else {
        console.log('[DetectiveCache] 服务器暂无缓存数据')
        return null
      }
    } catch (error) {
      console.error('[DetectiveCache] 服务器缓存获取异常:', error)
      return null
    }
  }

  /**
   * 检查服务器是否有缓存
   */
  static async checkServerCache(): Promise<boolean> {
    try {
      const token = uni.getStorageSync(STORAGE_KEYS.token)
      if (!token) {
        return false
      }

      const response = await request({
        url: API.detective.cache.check,
        method: 'GET',
        header: {
          Authorization: `Bearer ${token}`,
        },
      })

      return response.success && response.hasCache
    } catch (error) {
      console.error('[DetectiveCache] 服务器缓存检查异常:', error)
      return false
    }
  }

  /**
   * 清除服务器缓存
   */
  static async clearServer(): Promise<boolean> {
    try {
      const token = uni.getStorageSync(STORAGE_KEYS.token)
      if (!token) {
        return false
      }

      const response = await request({
        url: API.detective.cache.clear,
        method: 'DELETE',
        header: {
          Authorization: `Bearer ${token}`,
        },
      })

      if (response.success) {
        console.log('[DetectiveCache] 服务器缓存已清除')
        return true
      } else {
        console.error('[DetectiveCache] 服务器缓存清除失败:', response.message)
        return false
      }
    } catch (error) {
      console.error('[DetectiveCache] 服务器缓存清除异常:', error)
      return false
    }
  }

  /**
   * 智能保存：优先保存到服务器，失败则保存到本地
   */
  static async smartSave(data: DetectiveCacheData): Promise<void> {
    // 总是保存到本地作为备份
    this.saveToLocal(data)

    // 尝试保存到服务器
    const serverSuccess = await this.saveToServer(data)
    if (!serverSuccess) {
      console.warn('[DetectiveCache] 服务器保存失败，已保存到本地缓存')
    }
  }

  /**
   * 智能获取：优先从服务器获取，失败则从本地获取
   */
  static async smartGet(): Promise<DetectiveCacheData | null> {
    // 先尝试从服务器获取
    const serverData = await this.getFromServer()
    if (serverData) {
      // 同步到本地缓存
      this.saveToLocal(serverData)
      return serverData
    }

    // 服务器获取失败，从本地获取
    const localData = this.getFromLocal()
    if (localData) {
      console.log('[DetectiveCache] 从本地缓存获取数据')
      return localData
    }

    return null
  }

  /**
   * 完全清除：清除本地和服务器缓存
   */
  static async smartClear(): Promise<void> {
    this.clearLocal()
    await this.clearServer()
  }

  /**
   * 保存图片缓存信息
   */
  static saveImageCache(images: { [key: string]: ImageCacheItem[] }): void {
    try {
      uni.setStorageSync(
        STORAGE_KEYS.detectiveImageCache,
        JSON.stringify(images)
      )
      console.log('[DetectiveCache] 图片缓存已保存')
    } catch (error) {
      console.error('[DetectiveCache] 图片缓存保存失败:', error)
    }
  }

  /**
   * 获取图片缓存信息
   */
  static getImageCache(): { [key: string]: ImageCacheItem[] } | null {
    try {
      const cached = uni.getStorageSync(STORAGE_KEYS.detectiveImageCache)
      if (cached) {
        return JSON.parse(cached)
      }
      return null
    } catch (error) {
      console.error('[DetectiveCache] 图片缓存读取失败:', error)
      return null
    }
  }

  /**
   * 格式化缓存时间显示
   */
  static formatCacheTime(timeStr?: string): string {
    if (!timeStr) return ''

    try {
      const date = new Date(timeStr)
      const now = new Date()
      const diffMs = now.getTime() - date.getTime()
      const diffMins = Math.floor(diffMs / (1000 * 60))
      const diffHours = Math.floor(diffMins / 60)
      const diffDays = Math.floor(diffHours / 24)

      if (diffMins < 1) return '刚刚'
      if (diffMins < 60) return `${diffMins}分钟前`
      if (diffHours < 24) return `${diffHours}小时前`
      if (diffDays < 7) return `${diffDays}天前`

      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      })
    } catch (error) {
      return timeStr
    }
  }
}

export default DetectiveCache
