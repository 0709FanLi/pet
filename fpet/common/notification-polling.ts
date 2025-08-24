// @ts-ignore
declare const uni: any
import { request } from './request'
import { API } from './config'

interface NotificationPollingConfig {
  interval: number // 轮询间隔（毫秒）
  maxRetries: number // 最大重试次数
  enabled: boolean // 是否启用轮询
}

/**
 * 消息通知轮询服务
 * 用于非H5环境（小程序、App）获取消息通知
 */
class NotificationPollingService {
  private config: NotificationPollingConfig
  private pollingTimer: any = null
  private isPolling: boolean = false
  private userId: string = ''
  private lastCheckTime: string = ''
  private retryCount: number = 0

  constructor() {
    this.config = {
      interval: 30000, // 30秒轮询一次
      maxRetries: 3,
      enabled: false,
    }
  }

  /**
   * 启动消息轮询
   */
  start(userId: string): void {
    if (this.isPolling) {
      console.log('消息轮询已在运行中')
      return
    }

    this.userId = userId
    this.isPolling = true
    this.config.enabled = true
    this.lastCheckTime = new Date().toISOString()

    console.log('启动消息轮询服务，用户ID:', userId)

    // 立即执行一次
    this.checkNewNotifications()

    // 设置定时轮询
    this.pollingTimer = setInterval(() => {
      this.checkNewNotifications()
    }, this.config.interval)
  }

  /**
   * 停止消息轮询
   */
  stop(): void {
    if (this.pollingTimer) {
      clearInterval(this.pollingTimer)
      this.pollingTimer = null
    }

    this.isPolling = false
    this.config.enabled = false
    console.log('消息轮询服务已停止')
  }

  /**
   * 检查新消息通知
   */
  private async checkNewNotifications(): Promise<void> {
    if (!this.config.enabled || !this.userId) {
      return
    }

    try {
      console.log('检查新消息通知...')

      // 使用已导入的模块

      // 获取未读消息数量
      const unreadCountResponse = await request({
        url: API.notifications.unreadCount,
        method: 'GET',
      })

      if (unreadCountResponse && unreadCountResponse.code === 200) {
        const unreadCount = unreadCountResponse.data?.all || 0

        // 如果有未读消息，获取最新消息列表
        if (unreadCount > 0) {
          const messagesResponse = await request({
            url: API.notifications.list,
            method: 'GET',
            data: {
              page: 1,
              pageSize: 5, // 只获取最新的5条
            },
          })

          if (messagesResponse && messagesResponse.code === 200) {
            const messages = messagesResponse.data?.list || []

            // 处理新消息
            this.processNewMessages(messages)
          }
        }

        // 更新未读数量
        this.updateUnreadCount(unreadCount)

        // 重置重试计数
        this.retryCount = 0
      } else {
        console.warn('获取未读消息数量失败:', unreadCountResponse)
        this.handlePollingError()
      }
    } catch (error) {
      console.error('消息轮询检查失败:', error)
      this.handlePollingError()
    }
  }

  /**
   * 处理新消息
   */
  private processNewMessages(messages: any[]): void {
    const currentTime = new Date().toISOString()

    // 筛选出新消息（创建时间晚于上次检查时间）
    const newMessages = messages.filter(msg => {
      return msg.createdAt > this.lastCheckTime && msg.status === 'unread'
    })

    console.log(`发现 ${newMessages.length} 条新消息`)

    // 处理每条新消息
    newMessages.forEach(message => {
      this.handleNewMessage(message)
    })

    // 更新最后检查时间
    this.lastCheckTime = currentTime
  }

  /**
   * 处理单条新消息
   */
  private handleNewMessage(message: any): void {
    console.log('处理新消息:', message)

    // 显示本地通知
    this.showLocalNotification(message)

    // 触发事件，通知其他组件
    uni.$emit('notification:new', message)
    uni.$emit('mqtt:notification', message) // 兼容MQTT事件名
    uni.$emit('mqtt:unreadCountUpdate')

    // 如果是审核相关的消息，特殊处理
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
  private showLocalNotification(message: any): void {
    // 显示Toast通知
    uni.showToast({
      title: message.title,
      icon: 'none',
      duration: 3000,
    })

    // 在Android环境中，可以使用系统通知
    // #ifdef APP-PLUS
    try {
      // 创建本地通知
      plus.push.createMessage(message.content, '', {
        title: message.title,
        when: new Date(),
      })
    } catch (error) {
      console.warn('创建系统通知失败:', error)
    }
    // #endif
  }

  /**
   * 显示审核通过通知
   */
  private showAuditApprovedNotification(message: any): void {
    const petName = message.extraData?.petName || '宠物'

    uni.showModal({
      title: '审核通过 🎉',
      content: `恭喜！您的宠物"${petName}"寻找启示已通过审核，现在可以在平台上展示了！`,
      showCancel: false,
      confirmText: '知道了',
      success: () => {
        // 可以跳转到相关页面
        console.log('用户确认了审核通过通知')
      },
    })
  }

  /**
   * 显示审核拒绝通知
   */
  private showAuditRejectedNotification(message: any): void {
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
          // 跳转到编辑页面
          uni.navigateTo({
            url: `/pages/publish/publish?id=${message.extraData?.petId}`,
          })
        }
      },
    })
  }

  /**
   * 更新未读数量
   */
  private updateUnreadCount(count: number): void {
    // 触发未读数量更新事件
    uni.$emit('unreadCount:update', count)
  }

  /**
   * 处理轮询错误
   */
  private handlePollingError(): void {
    this.retryCount++

    if (this.retryCount >= this.config.maxRetries) {
      console.warn('消息轮询连续失败，暂停轮询')
      this.stop()

      // 5分钟后重试
      setTimeout(() => {
        if (this.userId) {
          console.log('重新启动消息轮询')
          this.retryCount = 0
          this.start(this.userId)
        }
      }, 5 * 60 * 1000)
    }
  }

  /**
   * 获取轮询状态
   */
  getStatus(): { isPolling: boolean; userId: string; interval: number } {
    return {
      isPolling: this.isPolling,
      userId: this.userId,
      interval: this.config.interval,
    }
  }

  /**
   * 设置轮询间隔
   */
  setInterval(interval: number): void {
    this.config.interval = interval

    // 如果正在轮询，重启以应用新间隔
    if (this.isPolling) {
      const currentUserId = this.userId
      this.stop()
      this.start(currentUserId)
    }
  }
}

// 导出单例
export const notificationPolling = new NotificationPollingService()

// 便捷方法
export const startNotificationPolling = (userId: string) => {
  notificationPolling.start(userId)
}

export const stopNotificationPolling = () => {
  notificationPolling.stop()
}

export const getNotificationPollingStatus = () => {
  return notificationPolling.getStatus()
}
