// @ts-ignore
declare const uni: any
// @ts-ignore
declare const plus: any

// 注意：此文件已弃用，请使用 mqtt-websocket.ts
// 保留此文件是为了兼容性，但不再使用第三方MQTT库

console.warn('mqtt.ts已弃用，请使用mqtt-websocket.ts')

interface MqttMessage {
  userId: number
  title: string
  content: string
  type: string
  extraData?: any
  timestamp: string
}

interface MqttConfig {
  brokerUrl: string
  topicPrefix: string
  clientId?: string
  username?: string
  password?: string
}

class MqttService {
  private client: any = null
  private config: MqttConfig
  private isConnected: boolean = false
  private reconnectAttempts: number = 0
  private maxReconnectAttempts: number = 5
  private reconnectInterval: number = 5000
  private userToken: string = ''
  private userId: string = ''

  constructor() {
    this.config = {
      brokerUrl: this.getBrokerUrl(),
      topicPrefix: 'pet_recovery',
      clientId: this.generateClientId(),
    }
  }

  /**
   * 获取MQTT Broker地址 - 统一使用WebSocket
   */
  private getBrokerUrl(): string {
    // 所有环境都使用WebSocket协议，确保跨平台兼容
    return 'ws://192.168.1.18:9001'
  }

  /**
   * 生成客户端ID
   */
  private generateClientId(): string {
    const timestamp = Date.now()
    const random = Math.random().toString(36).substring(7)
    return `fpet_${timestamp}_${random}`
  }

  /**
   * 初始化并连接MQTT
   */
  async connect(userId: string, token: string): Promise<boolean> {
    try {
      // 动态加载MQTT库
      mqtt = await loadMqtt()
      if (!mqtt) {
        console.warn('MQTT不可用，跳过连接')
        return false
      }

      if (this.isConnected && this.userId === userId) {
        console.log('MQTT已连接，无需重复连接')
        return true
      }

      this.userId = userId
      this.userToken = token

      // 如果已有连接，先断开
      if (this.client) {
        this.disconnect()
      }

      const options = {
        clientId: this.config.clientId,
        clean: true,
        connectTimeout: 30000,
        keepalive: 60,
        reconnectPeriod: this.reconnectInterval,
        will: {
          topic: `${this.config.topicPrefix}/user/${userId}/status`,
          payload: JSON.stringify({
            userId: userId,
            status: 'offline',
            timestamp: new Date().toISOString(),
          }),
          qos: 1,
          retain: false,
        },
      }

      // 添加认证信息（如果需要）
      if (this.config.username) {
        options.username = this.config.username
      }
      if (this.config.password) {
        options.password = this.config.password
      }

      console.log('正在连接MQTT Broker:', this.config.brokerUrl)

      this.client = mqtt.connect(this.config.brokerUrl, options)

      return new Promise((resolve, reject) => {
        this.client.on('connect', () => {
          console.log('MQTT连接成功')
          this.isConnected = true
          this.reconnectAttempts = 0
          this.subscribeToUserTopics()
          this.sendOnlineStatus()
          resolve(true)
        })

        this.client.on('error', (error: any) => {
          console.error('MQTT连接错误:', error)
          this.isConnected = false
          reject(error)
        })

        this.client.on('close', () => {
          console.log('MQTT连接已关闭')
          this.isConnected = false
        })

        this.client.on('reconnect', () => {
          console.log('MQTT正在重连...')
          this.reconnectAttempts++

          if (this.reconnectAttempts >= this.maxReconnectAttempts) {
            console.log('达到最大重连次数，停止重连')
            this.client.end()
          }
        })

        this.client.on('message', (topic: string, message: Buffer) => {
          this.handleMessage(topic, message.toString())
        })

        // 设置连接超时
        setTimeout(() => {
          if (!this.isConnected) {
            console.error('MQTT连接超时')
            reject(new Error('连接超时'))
          }
        }, 30000)
      })
    } catch (error) {
      console.error('MQTT连接失败:', error)
      return false
    }
  }

  /**
   * 订阅用户相关主题
   */
  private subscribeToUserTopics(): void {
    if (!this.client || !this.isConnected) {
      return
    }

    const topics = [
      `${this.config.topicPrefix}/user/${this.userId}/notifications/+`,
      `${this.config.topicPrefix}/user/${this.userId}/orders/+`,
      `${this.config.topicPrefix}/broadcast/+`,
    ]

    topics.forEach(topic => {
      this.client.subscribe(topic, { qos: 1 }, (error: any) => {
        if (error) {
          console.error(`订阅主题失败: ${topic}`, error)
        } else {
          console.log(`成功订阅主题: ${topic}`)
        }
      })
    })
  }

  /**
   * 发送在线状态
   */
  private sendOnlineStatus(): void {
    if (!this.client || !this.isConnected) {
      return
    }

    const statusMessage = {
      userId: this.userId,
      status: 'online',
      timestamp: new Date().toISOString(),
      platform: this.getPlatform(),
    }

    const topic = `${this.config.topicPrefix}/user/${this.userId}/status`
    this.client.publish(
      topic,
      JSON.stringify(statusMessage),
      { qos: 1 },
      (error: any) => {
        if (error) {
          console.error('发送在线状态失败:', error)
        } else {
          console.log('在线状态已发送')
        }
      }
    )
  }

  /**
   * 处理接收到的消息
   */
  private handleMessage(topic: string, payload: string): void {
    try {
      console.log(`收到MQTT消息 - Topic: ${topic}, Payload: ${payload}`)

      const message: MqttMessage = JSON.parse(payload)

      // 根据消息类型处理
      if (topic.includes('/notifications/')) {
        this.handleNotification(message)
      } else if (topic.includes('/orders/')) {
        this.handleOrderUpdate(message)
      } else if (topic.includes('/broadcast/')) {
        this.handleBroadcast(message)
      }
    } catch (error) {
      console.error('处理MQTT消息失败:', error)
    }
  }

  /**
   * 处理通知消息
   */
  private handleNotification(message: MqttMessage): void {
    // 显示系统通知
    this.showNotification(message)

    // 更新应用内未读数量
    this.updateUnreadCount()

    // 触发自定义事件
    uni.$emit('mqtt:notification', message)
  }

  /**
   * 处理订单更新
   */
  private handleOrderUpdate(message: MqttMessage): void {
    console.log('收到订单更新:', message)

    // 显示订单状态通知
    this.showNotification(message)

    // 触发订单更新事件
    uni.$emit('mqtt:orderUpdate', message)
  }

  /**
   * 处理广播消息
   */
  private handleBroadcast(message: MqttMessage): void {
    console.log('收到广播消息:', message)

    // 显示广播通知
    this.showNotification(message)

    // 触发广播事件
    uni.$emit('mqtt:broadcast', message)
  }

  /**
   * 显示通知
   */
  private showNotification(message: MqttMessage): void {
    // 检查应用是否在前台
    if (this.isAppInForeground()) {
      // 前台时显示toast或弹窗
      uni.showToast({
        title: message.title,
        icon: 'none',
        duration: 3000,
      })
    } else {
      // 后台时显示系统通知
      // #ifdef APP-PLUS
      plus.push.createMessage(message.content, '', {
        title: message.title,
      })
      // #endif
    }
  }

  /**
   * 更新未读数量
   */
  private updateUnreadCount(): void {
    // 这里可以调用API获取最新的未读数量
    // 或者从本地存储中更新
    uni.$emit('mqtt:unreadCountUpdate')
  }

  /**
   * 检查应用是否在前台
   */
  private isAppInForeground(): boolean {
    // #ifdef APP-PLUS
    return plus.runtime.isApplicationExist()
    // #endif

    // #ifdef H5
    return document.visibilityState === 'visible'
    // #endif

    // #ifdef MP
    return true // 小程序默认认为在前台
    // #endif

    return true
  }

  /**
   * 获取平台信息
   */
  private getPlatform(): string {
    // #ifdef H5
    return 'h5'
    // #endif

    // #ifdef APP-PLUS
    return 'app'
    // #endif

    // #ifdef MP-WEIXIN
    return 'mp-weixin'
    // #endif

    // #ifdef MP-ALIPAY
    return 'mp-alipay'
    // #endif

    return 'unknown'
  }

  /**
   * 发送心跳
   */
  sendHeartbeat(): void {
    if (!this.client || !this.isConnected) {
      return
    }

    const heartbeatMessage = {
      userId: this.userId,
      timestamp: new Date().toISOString(),
      platform: this.getPlatform(),
    }

    const topic = `${this.config.topicPrefix}/user/${this.userId}/heartbeat`
    this.client.publish(topic, JSON.stringify(heartbeatMessage), { qos: 1 })
  }

  /**
   * 断开连接
   */
  disconnect(): void {
    if (this.client) {
      // 发送离线状态
      if (this.isConnected) {
        const statusMessage = {
          userId: this.userId,
          status: 'offline',
          timestamp: new Date().toISOString(),
        }

        const topic = `${this.config.topicPrefix}/user/${this.userId}/status`
        this.client.publish(topic, JSON.stringify(statusMessage), { qos: 1 })
      }

      this.client.end()
      this.client = null
    }

    this.isConnected = false
    console.log('MQTT连接已断开')
  }

  /**
   * 获取连接状态
   */
  getConnectionStatus(): boolean {
    return this.isConnected
  }

  /**
   * 重新连接
   */
  async reconnect(): Promise<boolean> {
    if (this.userId && this.userToken) {
      return await this.connect(this.userId, this.userToken)
    }
    return false
  }
}

// 导出单例
export const mqttService = new MqttService()

// 导出类型
export type { MqttMessage, MqttConfig }
