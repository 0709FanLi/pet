// @ts-ignore
declare const uni: any

/**
 * 基于uni-app WebSocket的MQTT客户端
 * 避免第三方库的构建问题，使用原生WebSocket实现简化版MQTT
 */

interface MqttMessage {
  userId: number
  title: string
  content: string
  type: string
  extraData?: any
  timestamp: string
}

interface WebSocketMqttConfig {
  url: string
  clientId: string
  keepAlive: number
  reconnectInterval: number
  maxReconnectAttempts: number
}

class WebSocketMqttClient {
  private config: WebSocketMqttConfig
  private socketTask: any = null
  private isConnected: boolean = false
  private userId: string = ''
  private userToken: string = ''
  private reconnectAttempts: number = 0
  private reconnectTimer: any = null
  private heartbeatTimer: any = null
  private subscriptions: Set<string> = new Set()

  constructor() {
    this.config = {
      url: 'ws://192.168.1.18:8080/ws/notifications',
      clientId: this.generateClientId(),
      keepAlive: 60,
      reconnectInterval: 5000,
      maxReconnectAttempts: 5,
    }
  }

  /**
   * 生成客户端ID
   */
  private generateClientId(): string {
    const timestamp = Date.now()
    const random = Math.random().toString(36).substring(2, 8)
    return `fpet_${timestamp}_${random}`
  }

  /**
   * 连接WebSocket
   */
  async connect(userId: string, token: string): Promise<boolean> {
    this.userId = userId
    this.userToken = token

    if (this.isConnected) {
      console.log('WebSocket MQTT已连接')
      return true
    }

    return new Promise((resolve, reject) => {
      try {
        const wsUrl = `${this.config.url}/${userId}`
        console.log('连接WebSocket通知服务:', wsUrl)

        this.socketTask = uni.connectSocket({
          url: wsUrl,
          success: () => {
            console.log('WebSocket连接请求已发送')
          },
          fail: (error: any) => {
            console.error('WebSocket连接失败:', error)
            reject(false)
          },
        })

        // 连接成功
        this.socketTask.onOpen(() => {
          console.log('WebSocket通知服务连接成功')
          this.isConnected = true
          this.reconnectAttempts = 0

          // 启动心跳
          this.startHeartbeat()

          resolve(true)
        })

        // 连接关闭
        this.socketTask.onClose((res: any) => {
          console.log('WebSocket连接已关闭:', res)
          this.isConnected = false
          this.stopHeartbeat()

          // 自动重连
          if (this.reconnectAttempts < this.config.maxReconnectAttempts) {
            this.scheduleReconnect()
          }
        })

        // 连接错误
        this.socketTask.onError((error: any) => {
          console.error('WebSocket连接错误:', error)
          this.isConnected = false
          reject(false)
        })

        // 接收消息
        this.socketTask.onMessage((res: any) => {
          this.handleMessage(res.data)
        })
      } catch (error) {
        console.error('创建WebSocket连接失败:', error)
        reject(false)
      }
    })
  }

  /**
   * 处理接收到的消息
   */
  private handleMessage(data: any): void {
    try {
      // 尝试解析JSON消息
      let message: any
      if (typeof data === 'string') {
        message = JSON.parse(data)
      } else {
        message = data
      }

      console.log('收到WebSocket消息:', message)

      // 检查是否是通知消息
      if (message.type && message.title && message.content) {
        this.handleNotificationMessage(message)
      }
    } catch (error) {
      console.error('处理WebSocket消息失败:', error)
    }
  }

  /**
   * 处理通知消息
   */
  private handleNotificationMessage(message: MqttMessage): void {
    console.log('处理通知消息:', message)

    // 显示本地通知
    this.showLocalNotification(message)

    // 触发事件
    uni.$emit('mqtt:notification', message)
    uni.$emit('mqtt:unreadCountUpdate')

    // 特殊处理审核消息
    if (message.extraData && message.extraData.actionType) {
      const actionType = message.extraData.actionType

      if (actionType === 'audit_approved') {
        this.showAuditApprovedNotification(message)
      } else if (actionType === 'audit_rejected') {
        this.showAuditRejectedNotification(message)
      }
    }
  }

  /**
   * 显示本地通知
   */
  private showLocalNotification(message: MqttMessage): void {
    uni.showToast({
      title: message.title,
      icon: 'none',
      duration: 3000,
    })
  }

  /**
   * 显示审核通过通知
   */
  private showAuditApprovedNotification(message: MqttMessage): void {
    const petName = message.extraData?.petName || '宠物'

    uni.showModal({
      title: '审核通过 🎉',
      content: `恭喜！您的宠物"${petName}"寻找启示已通过审核，现在可以在平台上展示了！`,
      showCancel: false,
      confirmText: '知道了',
      success: () => {
        console.log('用户确认了审核通过通知')
      },
    })
  }

  /**
   * 显示审核拒绝通知
   */
  private showAuditRejectedNotification(message: MqttMessage): void {
    const petName = message.extraData?.petName || '宠物'
    const reason = message.extraData?.rejectReason || '不符合平台规范'

    uni.showModal({
      title: '审核未通过',
      content: `很抱歉，您的宠物"${petName}"寻找启示未通过审核。\n\n拒绝原因：${reason}\n\n请修改后重新提交。`,
      showCancel: true,
      cancelText: '稍后处理',
      confirmText: '去修改',
      success: res => {
        if (res.confirm) {
          uni.navigateTo({
            url: `/pages/publish/publish?id=${message.extraData?.petId}`,
          })
        }
      },
    })
  }

  /**
   * 启动心跳
   */
  private startHeartbeat(): void {
    this.heartbeatTimer = setInterval(() => {
      if (this.isConnected && this.socketTask) {
        // 发送心跳包
        this.sendHeartbeat()
      }
    }, this.config.keepAlive * 1000)
  }

  /**
   * 停止心跳
   */
  private stopHeartbeat(): void {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer)
      this.heartbeatTimer = null
    }
  }

  /**
   * 发送心跳包
   */
  private sendHeartbeat(): void {
    try {
      const heartbeat = {
        type: 'ping',
        clientId: this.config.clientId,
        timestamp: new Date().toISOString(),
      }

      this.socketTask.send({
        data: JSON.stringify(heartbeat),
        success: () => {
          console.log('心跳包已发送')
        },
        fail: (error: any) => {
          console.error('心跳包发送失败:', error)
        },
      })
    } catch (error) {
      console.error('发送心跳包失败:', error)
    }
  }

  /**
   * 安排重连
   */
  private scheduleReconnect(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
    }

    this.reconnectAttempts++
    const delay = this.config.reconnectInterval * this.reconnectAttempts

    console.log(`${delay}ms后尝试第${this.reconnectAttempts}次重连`)

    this.reconnectTimer = setTimeout(() => {
      if (!this.isConnected && this.userId) {
        console.log(`开始第${this.reconnectAttempts}次重连`)
        this.connect(this.userId, this.userToken)
      }
    }, delay)
  }

  /**
   * 断开连接
   */
  disconnect(): void {
    console.log('断开WebSocket MQTT连接')

    this.stopHeartbeat()

    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }

    if (this.socketTask) {
      this.socketTask.close({
        code: 1000,
        reason: '主动断开连接',
      })
      this.socketTask = null
    }

    this.isConnected = false
    this.reconnectAttempts = 0
    this.subscriptions.clear()
  }

  /**
   * 获取连接状态
   */
  getConnectionStatus(): boolean {
    return this.isConnected
  }

  /**
   * 发送消息
   */
  publish(topic: string, message: any): void {
    if (!this.isConnected || !this.socketTask) {
      console.warn('WebSocket未连接，无法发送消息')
      return
    }

    try {
      const payload = {
        topic: topic,
        message: message,
        timestamp: new Date().toISOString(),
      }

      this.socketTask.send({
        data: JSON.stringify(payload),
        success: () => {
          console.log('消息发送成功:', topic)
        },
        fail: (error: any) => {
          console.error('消息发送失败:', error)
        },
      })
    } catch (error) {
      console.error('发送消息失败:', error)
    }
  }
}

// 导出单例
export const webSocketMqttClient = new WebSocketMqttClient()

// 便捷方法
export const connectWebSocketMqtt = (userId: string, token: string) => {
  return webSocketMqttClient.connect(userId, token)
}

export const disconnectWebSocketMqtt = () => {
  webSocketMqttClient.disconnect()
}

export const getWebSocketMqttStatus = () => {
  return webSocketMqttClient.getConnectionStatus()
}
