import { mqttWrapper } from './mqtt-wrapper'
import { STORAGE_KEYS } from './config'

declare const uni: any

/**
 * MQTT连接管理器
 */
class MqttManager {
  private heartbeatTimer: any = null
  private reconnectTimer: any = null
  private isInitialized: boolean = false

  /**
   * 初始化MQTT服务
   */
  async initialize(): Promise<void> {
    if (this.isInitialized) {
      return
    }

    console.log('初始化MQTT管理器')

    // 监听应用生命周期
    this.setupAppLifecycleListeners()

    // 监听网络状态变化
    this.setupNetworkListeners()

    // 尝试自动连接
    await this.autoConnect()

    this.isInitialized = true
  }

  /**
   * 自动连接MQTT
   */
  private async autoConnect(): Promise<void> {
    try {
      const token = uni.getStorageSync(STORAGE_KEYS.token)
      const userInfo = uni.getStorageSync(STORAGE_KEYS.userInfo)

      if (token && userInfo && userInfo.id) {
        console.log('尝试自动连接MQTT...')
        const connected = await mqttWrapper.connect(
          userInfo.id.toString(),
          token
        )

        if (connected) {
          console.log('MQTT自动连接成功')
          this.startHeartbeat()
        } else {
          console.log('MQTT自动连接失败，将在网络恢复时重试')
        }
      } else {
        console.log('用户未登录，跳过MQTT连接')
      }
    } catch (error) {
      console.error('MQTT自动连接异常:', error)
    }
  }

  /**
   * 手动连接MQTT
   */
  async connect(userId: string, token: string): Promise<boolean> {
    try {
      console.log('手动连接MQTT...')
      const connected = await mqttWrapper.connect(userId, token)

      if (connected) {
        this.startHeartbeat()
        console.log('MQTT手动连接成功')
        return true
      } else {
        console.log('MQTT手动连接失败')
        return false
      }
    } catch (error) {
      console.error('MQTT手动连接异常:', error)
      return false
    }
  }

  /**
   * 断开MQTT连接
   */
  disconnect(): void {
    console.log('断开MQTT连接')
    this.stopHeartbeat()
    this.stopReconnectTimer()
    mqttWrapper.disconnect()
  }

  /**
   * 设置应用生命周期监听
   */
  private setupAppLifecycleListeners(): void {
    // 应用显示时
    uni.onAppShow(() => {
      console.log('应用进入前台，检查MQTT连接')
      this.handleAppShow()
    })

    // 应用隐藏时
    uni.onAppHide(() => {
      console.log('应用进入后台，保持MQTT连接')
      this.handleAppHide()
    })

    // 页面显示时
    uni.$on('pageShow', () => {
      this.checkConnectionStatus()
    })
  }

  /**
   * 设置网络状态监听
   */
  private setupNetworkListeners(): void {
    // 监听网络状态变化
    uni.onNetworkStatusChange((res: any) => {
      console.log('网络状态变化:', res)

      if (res.isConnected) {
        console.log('网络已连接，尝试重连MQTT')
        this.handleNetworkReconnect()
      } else {
        console.log('网络已断开')
        this.handleNetworkDisconnect()
      }
    })
  }

  /**
   * 处理应用显示
   */
  private handleAppShow(): void {
    // 检查连接状态
    if (!mqttWrapper.getConnectionStatus()) {
      console.log('MQTT未连接，尝试重连')
      this.scheduleReconnect()
    } else {
      // 如果已连接，发送心跳确认连接有效
      mqttWrapper.sendHeartbeat()
    }

    // 重启心跳
    this.startHeartbeat()
  }

  /**
   * 处理应用隐藏
   */
  private handleAppHide(): void {
    // 停止心跳定时器以节省资源
    this.stopHeartbeat()

    // 但保持MQTT连接，以便接收推送消息
    // mqttWrapper 将继续在后台运行
  }

  /**
   * 处理网络重连
   */
  private handleNetworkReconnect(): void {
    // 延迟一点时间等待网络稳定
    setTimeout(() => {
      this.checkConnectionStatus()
    }, 2000)
  }

  /**
   * 处理网络断开
   */
  private handleNetworkDisconnect(): void {
    console.log('网络断开，MQTT连接可能受影响')
    this.stopReconnectTimer()
  }

  /**
   * 检查连接状态
   */
  private checkConnectionStatus(): void {
    const isConnected = mqttWrapper.getConnectionStatus()
    console.log('MQTT连接状态:', isConnected)

    if (!isConnected) {
      this.scheduleReconnect()
    }
  }

  /**
   * 计划重连
   */
  private scheduleReconnect(): void {
    // 防止重复计划
    this.stopReconnectTimer()

    this.reconnectTimer = setTimeout(async () => {
      try {
        const token = uni.getStorageSync(STORAGE_KEYS.token)
        const userInfo = uni.getStorageSync(STORAGE_KEYS.userInfo)

        if (token && userInfo && userInfo.id) {
          console.log('执行计划重连...')
          const connected = await mqttWrapper.reconnect()

          if (connected) {
            console.log('计划重连成功')
            this.startHeartbeat()
          } else {
            console.log('计划重连失败，稍后再试')
            // 递归重试，但增加延迟
            setTimeout(() => {
              this.scheduleReconnect()
            }, 10000)
          }
        }
      } catch (error) {
        console.error('计划重连异常:', error)
        // 出错时也安排下次重试
        setTimeout(() => {
          this.scheduleReconnect()
        }, 15000)
      }
    }, 5000)
  }

  /**
   * 开始心跳
   */
  private startHeartbeat(): void {
    // 先停止已有的心跳
    this.stopHeartbeat()

    // 每60秒发送一次心跳
    this.heartbeatTimer = setInterval(() => {
      if (mqttWrapper.getConnectionStatus()) {
        mqttWrapper.sendHeartbeat()
      } else {
        console.log('MQTT未连接，停止心跳并尝试重连')
        this.stopHeartbeat()
        this.scheduleReconnect()
      }
    }, 60000)

    console.log('MQTT心跳已启动')
  }

  /**
   * 停止心跳
   */
  private stopHeartbeat(): void {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer)
      this.heartbeatTimer = null
      console.log('MQTT心跳已停止')
    }
  }

  /**
   * 停止重连定时器
   */
  private stopReconnectTimer(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
  }

  /**
   * 获取连接状态
   */
  getConnectionStatus(): boolean {
    return mqttWrapper.getConnectionStatus()
  }

  /**
   * 销毁管理器
   */
  destroy(): void {
    console.log('销毁MQTT管理器')
    this.stopHeartbeat()
    this.stopReconnectTimer()
    this.disconnect()
    this.isInitialized = false

    // 移除事件监听
    uni.$off('pageShow')
  }
}

// 导出单例
export const mqttManager = new MqttManager()

// 便捷方法
export const connectMqtt = (userId: string, token: string) => {
  return mqttManager.connect(userId, token)
}

export const disconnectMqtt = () => {
  mqttManager.disconnect()
}

export const getMqttConnectionStatus = () => {
  return mqttManager.getConnectionStatus()
}
