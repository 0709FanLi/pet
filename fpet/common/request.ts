import { BASE_URL, STORAGE_KEYS } from './config'

type ReqOptions = {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: Record<string, string>
}

export function request(options: ReqOptions): Promise<any> {
  const token = uni.getStorageSync(STORAGE_KEYS.token)
  return new Promise((resolve, reject) => {
    uni.request({
      url: options.url.startsWith('http')
        ? options.url
        : BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...(options.header || {}),
      },
      success: res => {
        // 兼容我们返回 {code, message, data} 与纯数组
        const data: any = res.data
        if (
          data &&
          typeof data === 'object' &&
          'code' in data &&
          'data' in data
        ) {
          if (data.code === 200) resolve(data)
          else reject(data)
        } else {
          resolve(data)
        }
      },
      fail: err => reject(err),
    })
  })
}

export function upload(
  filePath: string,
  formData?: any,
  name = 'file',
  url = '/api/upload/image'
): Promise<any> {
  const token = uni.getStorageSync(STORAGE_KEYS.token)
  return new Promise((resolve, reject) => {
    const fullUrl = url.startsWith('http') ? url : BASE_URL + url
    try {
      // 记录更详细的上下文
      console.log('[upload] using url:', fullUrl)
      console.log('[upload] file:', { filePath, name })
      console.log('[upload] formData:', formData)
      console.log('[upload] hasToken:', !!token)
      uni.getNetworkType({
        success: (r: any) =>
          console.log('[upload] networkType:', r?.networkType),
      })
    } catch {}
    uni.uploadFile({
      url: fullUrl,
      filePath,
      name,
      formData,
      header: {
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      success: res => {
        console.log('[upload] success raw:', res)
        try {
          const parsed = JSON.parse(res.data)
          resolve(parsed)
        } catch {
          resolve(res.data)
        }
      },
      fail: err => {
        console.error('[upload] fail:', err)
        console.error(
          '[upload] hint: 确认 BASE_URL 是否正确:',
          BASE_URL,
          '，以及服务端是否可达（同一局域网/IP未变更）。可在控制台执行 uni.setStorageSync("base_url", "http://192.168.1.18:8080") 后重试。'
        )
        reject(err)
      },
    })
  })
}
