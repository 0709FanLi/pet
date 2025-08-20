import { BASE_URL, STORAGE_KEYS } from './config'

type ReqOptions = {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: Record<string, string>
}

export function request(options: ReqOptions): Promise<any> {
  const token = uni.getStorageSync(STORAGE_KEYS.token)
  const fullUrl = options.url.startsWith('http') ? options.url : BASE_URL + options.url
  
  console.log('[request] 🌐 发起网络请求:')
  console.log('  - 原始URL:', options.url)
  console.log('  - 完整URL:', fullUrl)
  console.log('  - 方法:', options.method || 'GET')
  console.log('  - 有Token:', !!token)
  console.log('  - 请求数据:', JSON.stringify(options.data, null, 2))
  
  return new Promise((resolve, reject) => {
    const requestConfig = {
      url: fullUrl,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...(options.header || {}),
      },
      timeout: 30000, // 30秒超时
    }
    
    console.log('[request] 📤 请求配置:', JSON.stringify(requestConfig, null, 2))
    
    uni.request({
      ...requestConfig,
      success: res => {
        console.log('[request] 📥 请求成功:')
        console.log('  - 状态码:', res.statusCode)
        console.log('  - 响应头:', JSON.stringify(res.header, null, 2))
        console.log('  - 响应数据:', JSON.stringify(res.data, null, 2))
        
        if (res.statusCode !== 200) {
          console.error('[request] ❌ HTTP状态码错误:', res.statusCode)
          reject({
            message: `HTTP ${res.statusCode}`,
            statusCode: res.statusCode,
            data: res.data
          })
          return
        }
        
        // 兼容我们返回 {code, message, data} 与纯数组
        const data: any = res.data
        if (
          data &&
          typeof data === 'object' &&
          'code' in data &&
          'data' in data
        ) {
          console.log('[request] 📋 标准格式响应 - code:', data.code, 'message:', data.message)
          if (data.code === 200) {
            console.log('[request] ✅ 请求完全成功')
            resolve(data)
          } else {
            console.error('[request] ❌ 业务逻辑错误:', data)
            reject(data)
          }
        } else {
          console.log('[request] 📋 非标准格式响应，直接返回')
          resolve(data)
        }
      },
      fail: err => {
        console.error('[request] 💥 请求失败:')
        console.error('  - 错误对象:', JSON.stringify(err, null, 2))
        console.error('  - 错误消息:', err.errMsg || err.message)
        
        // 网络检查
        uni.getNetworkType({
          success: (netRes: any) => {
            console.log('[request] 📶 网络状态:', netRes.networkType)
            if (netRes.networkType === 'none') {
              console.error('[request] ❌ 设备无网络连接')
            }
          },
          fail: () => {
            console.warn('[request] ⚠️ 无法获取网络状态')
          }
        })
        
        // 提供更友好的错误信息
        let friendlyError = {
          message: '网络请求失败',
          originalError: err
        }
        
        if (err.errMsg) {
          if (err.errMsg.includes('timeout')) {
            friendlyError.message = '请求超时，请检查网络连接'
          } else if (err.errMsg.includes('fail')) {
            friendlyError.message = '网络连接失败，请检查服务器地址和网络'
          } else if (err.errMsg.includes('abort')) {
            friendlyError.message = '请求被中止'
          }
        }
        
        console.error('[request] 🎯 友好错误信息:', friendlyError.message)
        reject(friendlyError)
      },
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
