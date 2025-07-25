# 宠物主人PC端 - API接口文档

## 概述
本文档定义了宠物找回平台PC端应用所需的API接口，包括用户认证、宠物信息管理、订单管理、支付、数据分析等核心功能。PC端相比移动端提供更丰富的功能和更详细的数据展示。

## 基础信息
- **Base URL**: `https://api.petfinder.com/v1`
- **认证方式**: Bearer Token
- **数据格式**: JSON
- **字符编码**: UTF-8

## 通用响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1640995200000
}
```

## 1. 用户认证模块

### 1.1 发送验证码
**接口地址**: `POST /auth/send-code`

**请求参数**:
```json
{
  "phone": "13800138000",
  "type": "login" // login: 登录, register: 注册
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "验证码发送成功",
  "data": {
    "expire_time": 300
  }
}
```

### 1.2 手机号登录/注册
**接口地址**: `POST /auth/login`

**请求参数**:
```json
{
  "phone": "13800138000",
  "code": "123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user_info": {
      "user_id": "123456",
      "phone": "13800138000",
      "nickname": "宠物主人",
      "avatar": "https://example.com/avatar.jpg",
      "real_name": "张三",
      "email": "user@example.com",
      "is_new_user": false,
      "vip_level": "bronze", // bronze: 铜牌, silver: 银牌, gold: 金牌, diamond: 钻石
      "account_balance": 500.00
    }
  }
}
```

### 1.3 邮箱登录
**接口地址**: `POST /auth/email-login`

**请求参数**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

### 1.4 设置登录密码
**接口地址**: `POST /auth/set-password`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "password": "password123",
  "confirm_password": "password123"
}
```

### 1.5 修改密码
**接口地址**: `POST /auth/change-password`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "old_password": "old_password123",
  "new_password": "new_password123"
}
```

### 1.6 退出登录
**接口地址**: `POST /auth/logout`

**请求头**: `Authorization: Bearer {token}`

## 2. 用户信息模块

### 2.1 获取用户详细信息
**接口地址**: `GET /user/profile`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "user_id": "123456",
    "phone": "13800138000",
    "email": "user@example.com",
    "nickname": "宠物主人",
    "avatar": "https://example.com/avatar.jpg",
    "real_name": "张三",
    "gender": "male", // male: 男, female: 女, unknown: 未知
    "birthday": "1990-01-01",
    "address": {
      "province": "北京市",
      "city": "北京市",
      "district": "朝阳区",
      "detail": "三里屯街道"
    },
    "vip_level": "silver",
    "vip_expire_time": "2024-12-31 23:59:59",
    "account_balance": 500.00,
    "total_spent": 3000.00,
    "order_statistics": {
      "total_orders": 5,
      "completed_orders": 3,
      "in_progress_orders": 1,
      "cancelled_orders": 1,
      "success_rate": 75.0
    },
    "created_at": "2024-01-01 10:00:00",
    "last_login_time": "2024-01-01 10:00:00"
  }
}
```

### 2.2 更新用户信息
**接口地址**: `PUT /user/profile`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "nickname": "新昵称",
  "email": "newemail@example.com",
  "real_name": "张三",
  "gender": "male",
  "birthday": "1990-01-01",
  "address": {
    "province": "北京市",
    "city": "北京市",
    "district": "朝阳区",
    "detail": "三里屯街道"
  }
}
```

### 2.3 上传头像
**接口地址**: `POST /user/upload-avatar`

**请求头**: `Authorization: Bearer {token}`

**请求参数**: `multipart/form-data`
- `file`: 图片文件

### 2.4 获取账户余额
**接口地址**: `GET /user/balance`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "balance": 500.00,
    "frozen_amount": 100.00,
    "available_amount": 400.00
  }
}
```

### 2.5 账户充值
**接口地址**: `POST /user/recharge`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "amount": 100.00,
  "payment_method": "wechat" // wechat: 微信支付, alipay: 支付宝, bank: 银行卡
}
```

## 3. 宠物信息管理模块

### 3.1 获取我的宠物列表
**接口地址**: `GET /pets/my-pets`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "pets": [
      {
        "pet_id": "PET001",
        "pet_name": "小白",
        "pet_type": "dog",
        "breed": "金毛",
        "age": 2,
        "gender": "male",
        "color": "金黄色",
        "weight": 25.5,
        "avatar": "https://example.com/pet_avatar.jpg",
        "status": "normal", // normal: 正常, lost: 丢失, found: 已找到
        "created_at": "2023-12-01 10:00:00"
      }
    ],
    "total": 3
  }
}
```

### 3.2 添加宠物信息
**接口地址**: `POST /pets`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "pet_name": "小白",
  "pet_type": "dog",
  "breed": "金毛",
  "age": 2,
  "gender": "male",
  "color": "金黄色",
  "weight": 25.5,
  "description": "性格温顺，喜欢玩球",
  "health_status": "健康",
  "vaccination_records": [
    {
      "vaccine_name": "狂犬疫苗",
      "vaccination_date": "2023-06-01",
      "next_due_date": "2024-06-01"
    }
  ],
  "photos": [
    "https://example.com/pet1.jpg",
    "https://example.com/pet2.jpg"
  ],
  "microchip_id": "123456789012345",
  "registration_number": "REG123456"
}
```

### 3.3 更新宠物信息
**接口地址**: `PUT /pets/{pet_id}`

**请求头**: `Authorization: Bearer {token}`

### 3.4 删除宠物信息
**接口地址**: `DELETE /pets/{pet_id}`

**请求头**: `Authorization: Bearer {token}`

### 3.5 发布丢失宠物信息
**接口地址**: `POST /pets/{pet_id}/lost`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "lost_time": "2024-01-01 14:30:00",
  "lost_location": {
    "address": "北京市朝阳区三里屯",
    "latitude": 39.9042,
    "longitude": 116.4074
  },
  "lost_description": "在公园玩耍时走失",
  "reward_amount": 1000,
  "urgency_level": "high", // low: 低, medium: 中, high: 高
  "contact_phone": "13800138000",
  "contact_wechat": "wechat123",
  "contact_qq": "123456789",
  "additional_info": "宠物对陌生人比较警惕",
  "search_radius": 10 // 搜索半径（公里）
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "发布成功",
  "data": {
    "order_id": "ORD202401010001",
    "estimated_detectives": 15, // 预估可接单侦探数量
    "similar_cases": 3 // 相似案例数量
  }
}
```

## 4. 订单管理模块

### 4.1 获取订单列表（增强版）
**接口地址**: `GET /orders/my-orders`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `status`: 订单状态（可选）
- `pet_type`: 宠物类型（可选）
- `start_date`: 开始日期（可选）
- `end_date`: 结束日期（可选）
- `sort_by`: 排序字段 - `created_at`, `reward_amount`, `status`
- `sort_order`: 排序方向 - `asc`, `desc`
- `page`: 页码（默认1）
- `limit`: 每页数量（默认10）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "orders": [
      {
        "order_id": "ORD202401010001",
        "pet_info": {
          "pet_id": "PET001",
          "pet_name": "小白",
          "pet_type": "dog",
          "pet_photo": "https://example.com/pet1.jpg"
        },
        "status": "in_progress",
        "reward_amount": 1000,
        "urgency_level": "high",
        "detective_info": {
          "detective_id": "DET001",
          "name": "专业侦探",
          "avatar": "https://example.com/detective.jpg",
          "rating": 4.8,
          "success_rate": 85.5
        },
        "progress_summary": {
          "total_reports": 3,
          "last_report_time": "2024-01-01 15:00:00",
          "search_areas_covered": 5
        },
        "time_info": {
          "created_at": "2024-01-01 10:00:00",
          "accepted_at": "2024-01-01 11:00:00",
          "elapsed_time": "1天4小时",
          "estimated_completion": "2024-01-03 10:00:00"
        }
      }
    ],
    "summary": {
      "total": 5,
      "pending": 1,
      "in_progress": 2,
      "completed": 1,
      "cancelled": 1
    },
    "page": 1,
    "limit": 10
  }
}
```

### 4.2 获取订单详情（增强版）
**接口地址**: `GET /orders/{order_id}`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "order_id": "ORD202401010001",
    "status": "in_progress",
    "pet_info": {
      "pet_id": "PET001",
      "pet_name": "小白",
      "pet_type": "dog",
      "breed": "金毛",
      "age": 2,
      "gender": "male",
      "color": "金黄色",
      "weight": 25.5,
      "description": "性格温顺，喜欢玩球",
      "photos": [
        "https://example.com/pet1.jpg",
        "https://example.com/pet2.jpg"
      ],
      "microchip_id": "123456789012345"
    },
    "lost_info": {
      "lost_time": "2024-01-01 14:30:00",
      "lost_location": {
        "address": "北京市朝阳区三里屯",
        "latitude": 39.9042,
        "longitude": 116.4074
      },
      "lost_description": "在公园玩耍时走失",
      "weather_condition": "晴天",
      "temperature": "15°C"
    },
    "reward_amount": 1000,
    "urgency_level": "high",
    "detective_info": {
      "detective_id": "DET001",
      "name": "专业侦探",
      "avatar": "https://example.com/detective.jpg",
      "phone": "139****9000",
      "wechat": "detective_wechat",
      "rating": 4.8,
      "success_rate": 85.5,
      "total_cases": 120,
      "specialties": ["狗类寻找", "猫类寻找"],
      "certification_level": "silver"
    },
    "progress_reports": [
      {
        "report_id": "RPT001",
        "content": "已开始在三里屯附近寻找",
        "photos": ["https://example.com/search1.jpg"],
        "location": {
          "address": "三里屯SOHO",
          "latitude": 39.9042,
          "longitude": 116.4074
        },
        "next_plan": "明天将扩大搜索范围",
        "created_at": "2024-01-01 15:00:00"
      }
    ],
    "timeline": [
      {
        "event": "order_created",
        "description": "订单创建",
        "timestamp": "2024-01-01 10:00:00"
      },
      {
        "event": "order_accepted",
        "description": "侦探接单",
        "timestamp": "2024-01-01 11:00:00"
      }
    ],
    "search_statistics": {
      "areas_searched": 5,
      "people_contacted": 25,
      "flyers_distributed": 100,
      "social_media_shares": 50
    },
    "similar_cases": [
      {
        "case_id": "CASE001",
        "pet_name": "小黑",
        "pet_type": "dog",
        "success": true,
        "completion_time": "2天",
        "similarity_score": 85
      }
    ],
    "created_at": "2024-01-01 10:00:00"
  }
}
```

### 4.3 订单实时追踪
**接口地址**: `GET /orders/{order_id}/tracking`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "current_status": "searching",
    "detective_location": {
      "latitude": 39.9042,
      "longitude": 116.4074,
      "address": "北京市朝阳区三里屯",
      "updated_at": "2024-01-01 16:00:00"
    },
    "search_progress": {
      "completion_percentage": 35,
      "areas_covered": [
        {
          "area_name": "三里屯商圈",
          "status": "completed",
          "search_time": "2024-01-01 11:00:00 - 13:00:00"
        },
        {
          "area_name": "工体北路",
          "status": "in_progress",
          "search_time": "2024-01-01 14:00:00 - 进行中"
        }
      ]
    },
    "next_update_time": "2024-01-01 18:00:00"
  }
}
```

### 4.4 确认宠物找到
**接口地址**: `POST /orders/{order_id}/confirm-found`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "found_time": "2024-01-02 16:00:00",
  "found_location": {
    "address": "北京市朝阳区工体北路",
    "latitude": 39.9042,
    "longitude": 116.4074
  },
  "pet_condition": "健康状况良好",
  "handover_method": "direct", // direct: 直接交还, pickup: 主人自取
  "rating": 5,
  "comment": "非常专业，很快就找到了我的宠物",
  "bonus_amount": 200 // 额外奖励金额
}
```

### 4.5 取消订单
**接口地址**: `POST /orders/{order_id}/cancel`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "reason": "已自己找到宠物",
  "refund_method": "original" // original: 原路退回, balance: 退到余额
}
```

### 4.6 订单数据分析
**接口地址**: `GET /orders/analytics`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `period`: 分析周期 - `month`, `quarter`, `year`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "success_rate_trend": [
      {
        "month": "2024-01",
        "success_rate": 85.5,
        "total_orders": 20
      }
    ],
    "average_completion_time": "2.5天",
    "cost_analysis": {
      "average_cost": 850,
      "cost_by_pet_type": {
        "dog": 900,
        "cat": 750,
        "other": 1200
      }
    },
    "detective_performance": [
      {
        "detective_id": "DET001",
        "name": "专业侦探",
        "orders_completed": 5,
        "success_rate": 100,
        "average_time": "2天"
      }
    ]
  }
}
```

## 5. 支付模块

### 5.1 创建支付订单
**接口地址**: `POST /payment/create`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "order_id": "ORD202401010001",
  "amount": 1000,
  "payment_method": "balance", // balance: 余额支付, wechat: 微信支付, alipay: 支付宝, bank: 银行卡
  "use_coupon": true,
  "coupon_code": "DISCOUNT10"
}
```

### 5.2 获取支付记录
**接口地址**: `GET /payment/records`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `type`: 支付类型 - `order`订单支付, `recharge`充值, `refund`退款
- `start_date`: 开始日期
- `end_date`: 结束日期
- `page`: 页码（默认1）
- `limit`: 每页数量（默认20）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "records": [
      {
        "payment_id": "PAY202401010001",
        "type": "order",
        "order_id": "ORD202401010001",
        "amount": 1000,
        "actual_amount": 900, // 使用优惠券后的实际支付金额
        "payment_method": "wechat",
        "status": "paid",
        "coupon_discount": 100,
        "created_at": "2024-01-01 10:00:00",
        "paid_at": "2024-01-01 10:05:00"
      }
    ],
    "summary": {
      "total_spent": 5000,
      "total_saved": 500,
      "total_refunded": 200
    },
    "total": 15,
    "page": 1,
    "limit": 20
  }
}
```

### 5.3 申请退款
**接口地址**: `POST /payment/refund`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "payment_id": "PAY202401010001",
  "refund_amount": 500,
  "reason": "订单取消",
  "refund_method": "original" // original: 原路退回, balance: 退到余额
}
```

## 6. 优惠券模块

### 6.1 获取可用优惠券
**接口地址**: `GET /coupons/available`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `order_amount`: 订单金额（用于筛选可用优惠券）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "coupons": [
      {
        "coupon_id": "CPN001",
        "coupon_code": "DISCOUNT10",
        "name": "新用户专享券",
        "type": "discount", // discount: 折扣券, cash: 现金券
        "value": 100, // 折扣券为折扣金额，现金券为现金金额
        "min_amount": 500, // 最低使用金额
        "expire_time": "2024-12-31 23:59:59",
        "applicable_pet_types": ["dog", "cat"], // 适用宠物类型
        "description": "满500减100"
      }
    ],
    "total": 3
  }
}
```

### 6.2 领取优惠券
**接口地址**: `POST /coupons/claim`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "coupon_code": "NEWUSER2024"
}
```

### 6.3 获取优惠券使用记录
**接口地址**: `GET /coupons/usage-history`

**请求头**: `Authorization: Bearer {token}`

## 7. VIP会员模块

### 7.1 获取VIP信息
**接口地址**: `GET /vip/info`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "current_level": "silver",
    "expire_time": "2024-12-31 23:59:59",
    "points": 1250,
    "benefits": {
      "discount_rate": 0.9, // 9折优惠
      "priority_support": true,
      "exclusive_detectives": true,
      "free_urgent_service": 2, // 免费加急服务次数
      "extended_warranty": true
    },
    "upgrade_requirements": {
      "next_level": "gold",
      "points_needed": 750,
      "orders_needed": 3
    }
  }
}
```

### 7.2 VIP升级
**接口地址**: `POST /vip/upgrade`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "target_level": "gold",
  "payment_method": "balance"
}
```

### 7.3 积分兑换
**接口地址**: `POST /vip/redeem`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "reward_type": "coupon", // coupon: 优惠券, service: 服务, gift: 礼品
  "reward_id": "REWARD001",
  "points_cost": 500
}
```

## 8. 消息通知模块

### 8.1 获取消息列表（增强版）
**接口地址**: `GET /messages`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `type`: 消息类型（可选）- `system`系统通知, `order`订单消息, `promotion`推广消息, `vip`VIP消息
- `status`: 消息状态（可选）- `unread`未读, `read`已读
- `page`: 页码（默认1）
- `limit`: 每页数量（默认20）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "messages": [
      {
        "message_id": "MSG001",
        "type": "order",
        "title": "您的宠物找寻有新进展",
        "content": "侦探已开始寻找您的宠物小白",
        "is_read": false,
        "priority": "high", // low: 低, medium: 中, high: 高
        "action_required": true,
        "action_url": "/orders/ORD202401010001",
        "created_at": "2024-01-01 15:00:00",
        "related_order_id": "ORD202401010001",
        "attachments": [
          {
            "type": "image",
            "url": "https://example.com/progress.jpg",
            "description": "搜索进展照片"
          }
        ]
      }
    ],
    "unread_count": 5,
    "total": 25
  }
}
```

### 8.2 消息设置
**接口地址**: `PUT /messages/settings`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "email_notifications": true,
  "sms_notifications": true,
  "push_notifications": true,
  "notification_types": {
    "order_updates": true,
    "payment_notifications": true,
    "promotion_messages": false,
    "system_announcements": true
  },
  "quiet_hours": {
    "enabled": true,
    "start_time": "22:00",
    "end_time": "08:00"
  }
}
```

## 9. 数据导出模块

### 9.1 导出订单数据
**接口地址**: `GET /export/orders`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `format`: 导出格式 - `excel`, `csv`, `pdf`
- `start_date`: 开始日期
- `end_date`: 结束日期
- `status`: 订单状态（可选）

**响应**: 文件下载

### 9.2 导出支付记录
**接口地址**: `GET /export/payments`

**请求头**: `Authorization: Bearer {token}`

### 9.3 生成数据报告
**接口地址**: `POST /export/report`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "report_type": "annual", // monthly: 月度, quarterly: 季度, annual: 年度
  "year": 2024,
  "include_charts": true,
  "email_delivery": true
}
```

## 10. 地理位置模块

### 10.1 获取常用地址
**接口地址**: `GET /location/addresses`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "addresses": [
      {
        "address_id": "ADDR001",
        "name": "家",
        "address": "北京市朝阳区三里屯街道",
        "latitude": 39.9042,
        "longitude": 116.4074,
        "is_default": true
      }
    ]
  }
}
```

### 10.2 添加常用地址
**接口地址**: `POST /location/addresses`

**请求头**: `Authorization: Bearer {token}`

### 10.3 获取附近侦探
**接口地址**: `GET /location/nearby-detectives`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `latitude`: 纬度
- `longitude`: 经度
- `radius`: 搜索半径（公里，默认10）

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，需要登录 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 429 | 请求过于频繁 |
| 500 | 服务器内部错误 |
| 1001 | 验证码错误 |
| 1002 | 验证码已过期 |
| 1003 | 手机号格式错误 |
| 1004 | 邮箱格式错误 |
| 1005 | 密码格式错误 |
| 2001 | 订单不存在 |
| 2002 | 订单状态不允许此操作 |
| 2003 | 宠物信息不存在 |
| 3001 | 支付失败 |
| 3002 | 余额不足 |
| 3003 | 优惠券不可用 |
| 4001 | VIP等级不足 |
| 4002 | 积分不足 |

## 接口调用示例

### JavaScript示例
```javascript
// 获取订单详情（增强版）
const getOrderDetail = async (token, orderId) => {
  const response = await fetch(`https://api.petfinder.com/v1/orders/${orderId}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return await response.json();
};

// 订单实时追踪
const trackOrder = async (token, orderId) => {
  const response = await fetch(`https://api.petfinder.com/v1/orders/${orderId}/tracking`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return await response.json();
};

// 使用优惠券支付
const payWithCoupon = async (token, orderId, amount, couponCode) => {
  const response = await fetch('https://api.petfinder.com/v1/payment/create', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      order_id: orderId,
      amount: amount,
      payment_method: 'balance',
      use_coupon: true,
      coupon_code: couponCode
    })
  });
  return await response.json();
};

// 导出订单数据
const exportOrders = async (token, format, startDate, endDate) => {
  const params = new URLSearchParams({
    format,
    start_date: startDate,
    end_date: endDate
  });
  
  const response = await fetch(`https://api.petfinder.com/v1/export/orders?${params}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  if (response.ok) {
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `orders_${startDate}_${endDate}.${format}`;
    a.click();
  }
};
```

## PC端特色功能

### 1. 批量操作
- 批量管理宠物信息
- 批量导出数据
- 批量消息处理

### 2. 高级筛选
- 多维度订单筛选
- 自定义筛选条件保存
- 快速筛选模板

### 3. 数据可视化
- 订单趋势图表
- 成功率分析
- 成本效益分析

### 4. 桌面通知
- 系统级通知推送
- 自定义通知规则
- 通知历史记录

## 更新日志

### v1.0.0 (2024-12-01)
- 初始版本发布
- 包含用户认证、宠物管理、订单管理、支付、VIP会员等核心功能接口
- 支持数据导出和高级分析功能

---

**注意事项**:
1. PC端接口相比移动端提供更丰富的数据和功能
2. 所有接口都需要进行参数验证
3. 敏感信息需要加密传输
4. 大数据量操作需要做好性能优化
5. 建议使用HTTPS协议
6. 文件上传大小限制：单个文件不超过10MB
7. API调用频率限制：每分钟不超过200次
8. 数据导出功能需要VIP权限