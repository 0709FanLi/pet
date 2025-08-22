// @ts-ignore
declare const uni: any

interface MqttMessage {
  userId: number
  title: string
  content: string
  type: string
  extraData?: any
  timestamp: string
}

/**
 * MQTT包装器 - 只在H5环境中启用MQTT
 */
class MqttWrapper {
  private isSupported: boolean = false
  private isConnected: boolean = false
  private userId: string = ''
  private userToken: string = ''

  constructor() {
    // 检查运行环境
    // #ifdef H5
    this.isSupported = true
    // #endif

    // #ifndef H5
    this.isSupported = false
    console.info('当前环境不支持MQTT，将使用轮询方式获取消息')
    // #endif
  }

  /**
   * 连接MQTT
   */
  async connect(userId: string, token: string): Promise<boolean> {
    this.userId = userId
    this.userToken = token

    if (!this.isSupported) {
      console.info('MQTT不支持，连接跳过')
      return false
    }

    try {
      // #ifdef H5
      console.log('H5环境下初始化MQTT连接...')
      // 这里可以添加实际的MQTT连接逻辑
      // 暂时模拟连接成功
      this.isConnected = true
      return true
      // #endif
    } catch (error) {
      console.error('MQTT连接失败:', error)
      return false
    }

    return false
  }

  /**
   * 断开连接
   */
  disconnect(): void {
    if (!this.isSupported) {
      return
    }

    try {
      // #ifdef H5
      console.log('断开MQTT连接')
      this.isConnected = false
      // #endif
    } catch (error) {
      console.error('MQTT断开失败:', error)
    }
  }

  /**
   * 发送心跳
   */
  sendHeartbeat(): void {
    if (!this.isSupported || !this.isConnected) {
      return
    }

    // #ifdef H5
    console.log('发送MQTT心跳')
    // #endif
  }

  /**
   * 获取连接状态
   */
  getConnectionStatus(): boolean {
    return this.isSupported && this.isConnected
  }

  /**
   * 处理消息通知
   */
  handleNotification(message: MqttMessage): void {
    console.log('收到消息通知:', message)

    // 触发事件
    uni.$emit('mqtt:notification', message)

    // 更新未读数量
    uni.$emit('mqtt:unreadCountUpdate')
  }

  /**
   * 模拟接收推送消息（用于测试）
   */
  simulateMessage(message: MqttMessage): void {
    this.handleNotification(message)
  }
}

// 导出单例
export const mqttWrapper = new MqttWrapper()

// 便捷方法
export const connectMqtt = (userId: string, token: string) => {
  return mqttWrapper.connect(userId, token)
}

export const disconnectMqtt = () => {
  mqttWrapper.disconnect()
}

export const getMqttConnectionStatus = () => {
  return mqttWrapper.getConnectionStatus()
}

export const sendMqttHeartbeat = () => {
  mqttWrapper.sendHeartbeat()
}

// 导出类型
export type { MqttMessage }
