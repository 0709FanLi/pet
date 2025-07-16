/**
 * 项目通用 枚举 定义
 */

/**
 * 生成类型枚举
 */
export enum GenerationType {
  /** 文本生成图像 */
  TEXT_TO_IMAGE = 'text_to_image',
  /** 文本生成视频 */
  TEXT_TO_VIDEO = 'text_to_video',
  /** 图像生成图像 */
  IMAGE_TO_IMAGE = 'image_to_image',
  /** 图像生成视频 */
  IMAGE_TO_VIDEO = 'image_to_video'
}

/**
 * 生成状态枚举
 */
export enum GenerationStatus {
  /** 等待中 */
  PENDING = 'pending',
  /** 处理中 */
  PROCESSING = 'processing',
  /** 已完成 */
  COMPLETED = 'completed',
  /** 失败 */
  FAILED = 'failed',
  /** 已取消 */
  CANCELLED = 'cancelled'
}