// @ts-ignore
declare const uni: any
import {
  startNotificationPolling,
  stopNotificationPolling,
} from './notification-polling'
import { connectWebSocketMqtt, disconnectWebSocketMqtt } from './mqtt-websocket'

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
    // 所有环境都支持MQTT WebSocket
    this.isSupported = true
    console.log('MQTT WebSocket支持已启用')
  }

  /**
   * 连接MQTT
   */
  async connect(userId: string, token: string): Promise<boolean> {
    this.userId = userId
    this.userToken = token

    try {
      console.log('初始化WebSocket MQTT连接...')

      // 连接WebSocket MQTT服务
      const connected = await connectWebSocketMqtt(userId, token)

      if (connected) {
        this.isConnected = true
        console.log('WebSocket MQTT连接成功')
        return true
      } else {
        console.error('WebSocket MQTT连接失败')
        return false
      }
    } catch (error) {
      console.error('WebSocket MQTT连接失败:', error)

      // 如果MQTT连接失败，启用轮询作为备选方案
      console.info('启用消息轮询作为备选方案')
      startNotificationPolling(userId)
      return true
    }
  }

  /**
   * 断开连接
   */
  disconnect(): void {
    try {
      console.log('断开WebSocket MQTT连接')

      // 断开WebSocket MQTT连接
      disconnectWebSocketMqtt()

      this.isConnected = false
      console.log('WebSocket MQTT连接已断开')
    } catch (error) {
      console.error('断开WebSocket MQTT连接失败:', error)
    }

    // 同时停止轮询（如果有的话）
    stopNotificationPolling()
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
