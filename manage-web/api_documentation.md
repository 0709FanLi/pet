# 宠物找回平台管理系统 - API接口文档

## 概述
本文档定义了宠物找回平台后台管理系统所需的API接口，包括管理员认证、用户管理、侦探审核、订单管理、数据统计等核心功能。

## 基础信息
- **Base URL**: `https://api.petfinder.com/v1/admin`
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

## 1. 管理员认证模块

### 1.1 管理员登录
**接口地址**: `POST /auth/login`

**请求参数**:
```json
{
  "username": "admin",
  "password": "password123",
  "captcha": "ABCD",
  "captcha_key": "captcha_key_123"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "admin_info": {
      "admin_id": "ADMIN001",
      "username": "admin",
      "name": "系统管理员",
      "role": "super_admin", // super_admin: 超级管理员, admin: 普通管理员
      "permissions": ["user_manage", "detective_manage", "order_manage", "data_view"],
      "last_login_time": "2024-01-01 09:00:00"
    }
  }
}
```

### 1.2 获取验证码
**接口地址**: `GET /auth/captcha`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "captcha_key": "captcha_key_123",
    "captcha_image": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
  }
}
```

### 1.3 退出登录
**接口地址**: `POST /auth/logout`

**请求头**: `Authorization: Bearer {token}`

### 1.4 修改密码
**接口地址**: `POST /auth/change-password`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "old_password": "old_password123",
  "new_password": "new_password123"
}
```

## 2. 用户管理模块

### 2.1 获取用户列表
**接口地址**: `GET /users`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `keyword`: 搜索关键词（手机号、昵称）
- `status`: 用户状态 - `active`正常, `banned`封禁
- `start_date`: 注册开始日期
- `end_date`: 注册结束日期
- `page`: 页码（默认1）
- `limit`: 每页数量（默认20）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "users": [
      {
        "user_id": "USER123456",
        "phone": "13800138000",
        "nickname": "宠物主人",
        "avatar": "https://example.com/avatar.jpg",
        "real_name": "张三",
        "status": "active",
        "total_orders": 5,
        "completed_orders": 3,
        "total_spent": 3000,
        "last_login_time": "2024-01-01 10:00:00",
        "created_at": "2023-12-01 10:00:00"
      }
    ],
    "total": 1250,
    "page": 1,
    "limit": 20
  }
}
```

### 2.2 获取用户详情
**接口地址**: `GET /users/{user_id}`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "user_id": "USER123456",
    "phone": "13800138000",
    "nickname": "宠物主人",
    "avatar": "https://example.com/avatar.jpg",
    "real_name": "张三",
    "status": "active",
    "registration_ip": "192.168.1.100",
    "last_login_ip": "192.168.1.101",
    "last_login_time": "2024-01-01 10:00:00",
    "created_at": "2023-12-01 10:00:00",
    "order_statistics": {
      "total_orders": 5,
      "completed_orders": 3,
      "cancelled_orders": 1,
      "in_progress_orders": 1,
      "total_spent": 3000,
      "average_order_amount": 600
    },
    "recent_orders": [
      {
        "order_id": "ORD202401010001",
        "pet_name": "小白",
        "status": "completed",
        "reward_amount": 1000,
        "created_at": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### 2.3 封禁/解封用户
**接口地址**: `POST /users/{user_id}/ban`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "action": "ban", // ban: 封禁, unban: 解封
  "reason": "违规发布虚假信息",
  "duration": 7 // 封禁天数，0表示永久封禁
}
```

### 2.4 用户统计数据
**接口地址**: `GET /users/statistics`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `period`: 统计周期 - `today`今日, `week`本周, `month`本月, `year`本年

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total_users": 12500,
    "new_users_today": 25,
    "active_users_today": 1200,
    "banned_users": 50,
    "user_growth": [
      {
        "date": "2024-01-01",
        "new_users": 25,
        "active_users": 1200
      }
    ]
  }
}
```

## 3. 侦探管理模块

### 3.1 获取侦探列表
**接口地址**: `GET /detectives`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `keyword`: 搜索关键词（手机号、姓名）
- `status`: 审核状态 - `pending`待审核, `approved`已通过, `rejected`已拒绝
- `certification_level`: 认证等级 - `bronze`铜牌, `silver`银牌, `gold`金牌
- `start_date`: 注册开始日期
- `end_date`: 注册结束日期
- `page`: 页码（默认1）
- `limit`: 每页数量（默认20）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "detectives": [
      {
        "detective_id": "DET123456",
        "phone": "13900139000",
        "name": "专业侦探",
        "real_name": "李四",
        "avatar": "https://example.com/detective.jpg",
        "status": "approved",
        "certification_level": "silver",
        "success_rate": 85.5,
        "total_cases": 120,
        "completed_cases": 102,
        "rating": 4.8,
        "total_income": 45000,
        "last_active_time": "2024-01-01 15:00:00",
        "created_at": "2023-11-01 10:00:00"
      }
    ],
    "total": 850,
    "page": 1,
    "limit": 20
  }
}
```

### 3.2 获取侦探详情
**接口地址**: `GET /detectives/{detective_id}`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "detective_id": "DET123456",
    "phone": "13900139000",
    "name": "专业侦探",
    "real_name": "李四",
    "id_card": "110101199001011234",
    "avatar": "https://example.com/detective.jpg",
    "status": "approved",
    "certification_level": "silver",
    "service_areas": ["北京市朝阳区", "北京市海淀区"],
    "specialties": ["狗类寻找", "猫类寻找"],
    "introduction": "专业宠物侦探，经验丰富",
    "experience_description": "从事宠物找寻工作3年",
    "certification_materials": {
      "id_card_front": "https://example.com/id_front.jpg",
      "id_card_back": "https://example.com/id_back.jpg",
      "skill_certificates": [
        "https://example.com/cert1.jpg"
      ]
    },
    "statistics": {
      "success_rate": 85.5,
      "total_cases": 120,
      "completed_cases": 102,
      "cancelled_cases": 8,
      "in_progress_cases": 10,
      "rating": 4.8,
      "total_reviews": 95,
      "total_income": 45000,
      "pending_income": 2000
    },
    "recent_cases": [
      {
        "order_id": "ORD202401010001",
        "pet_name": "小白",
        "status": "completed",
        "reward_amount": 1000,
        "completed_at": "2024-01-02 18:00:00"
      }
    ],
    "created_at": "2023-11-01 10:00:00",
    "approved_at": "2023-11-02 14:00:00",
    "approved_by": "ADMIN001"
  }
}
```

### 3.3 审核侦探申请
**接口地址**: `POST /detectives/{detective_id}/review`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "action": "approve", // approve: 通过, reject: 拒绝
  "reason": "资料齐全，符合要求",
  "certification_level": "bronze" // 通过时设置认证等级
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "审核完成",
  "data": {
    "detective_id": "DET123456",
    "status": "approved",
    "certification_level": "bronze",
    "reviewed_at": "2024-01-01 14:00:00",
    "reviewed_by": "ADMIN001"
  }
}
```

### 3.4 调整侦探等级
**接口地址**: `POST /detectives/{detective_id}/level`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "certification_level": "silver",
  "reason": "表现优秀，成功率高"
}
```

### 3.5 侦探统计数据
**接口地址**: `GET /detectives/statistics`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total_detectives": 850,
    "pending_review": 25,
    "approved_detectives": 780,
    "rejected_detectives": 45,
    "active_detectives_today": 120,
    "level_distribution": {
      "bronze": 600,
      "silver": 150,
      "gold": 30
    },
    "performance_metrics": {
      "average_success_rate": 82.3,
      "average_rating": 4.6,
      "total_completed_cases": 5420
    }
  }
}
```

## 4. 订单管理模块

### 4.1 获取订单列表
**接口地址**: `GET /orders`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `keyword`: 搜索关键词（订单号、宠物名称）
- `status`: 订单状态 - `pending`待接单, `accepted`已接单, `in_progress`进行中, `completed`已完成, `cancelled`已取消
- `pet_type`: 宠物类型
- `reward_min`: 最小悬赏金额
- `reward_max`: 最大悬赏金额
- `start_date`: 创建开始日期
- `end_date`: 创建结束日期
- `page`: 页码（默认1）
- `limit`: 每页数量（默认20）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "orders": [
      {
        "order_id": "ORD202401010001",
        "pet_name": "小白",
        "pet_type": "dog",
        "pet_photo": "https://example.com/pet1.jpg",
        "status": "completed",
        "reward_amount": 1000,
        "owner_info": {
          "user_id": "USER123456",
          "nickname": "宠物主人",
          "phone": "138****8000"
        },
        "detective_info": {
          "detective_id": "DET123456",
          "name": "专业侦探",
          "phone": "139****9000"
        },
        "lost_location": "北京市朝阳区三里屯",
        "created_at": "2024-01-01 10:00:00",
        "completed_at": "2024-01-02 18:00:00",
        "duration": "1天8小时"
      }
    ],
    "total": 2580,
    "page": 1,
    "limit": 20
  }
}
```

### 4.2 获取订单详情
**接口地址**: `GET /orders/{order_id}`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "order_id": "ORD202401010001",
    "status": "completed",
    "pet_info": {
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
      ]
    },
    "lost_info": {
      "lost_time": "2024-01-01 14:30:00",
      "lost_location": {
        "address": "北京市朝阳区三里屯",
        "latitude": 39.9042,
        "longitude": 116.4074
      },
      "lost_description": "在公园玩耍时走失"
    },
    "reward_amount": 1000,
    "owner_info": {
      "user_id": "USER123456",
      "nickname": "宠物主人",
      "phone": "13800138000",
      "real_name": "张三"
    },
    "detective_info": {
      "detective_id": "DET123456",
      "name": "专业侦探",
      "phone": "13900139000",
      "real_name": "李四"
    },
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
      },
      {
        "event": "progress_report",
        "description": "进度汇报",
        "timestamp": "2024-01-01 15:00:00"
      },
      {
        "event": "pet_found",
        "description": "宠物找到",
        "timestamp": "2024-01-02 16:00:00"
      },
      {
        "event": "order_completed",
        "description": "订单完成",
        "timestamp": "2024-01-02 18:00:00"
      }
    ],
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
        "created_at": "2024-01-01 15:00:00"
      }
    ],
    "payment_info": {
      "payment_id": "PAY202401010001",
      "amount": 1000,
      "status": "paid",
      "paid_at": "2024-01-02 18:05:00",
      "commission_rate": 0.1,
      "commission_amount": 100,
      "detective_income": 900
    },
    "rating_info": {
      "rating": 5,
      "comment": "非常专业，很快就找到了我的宠物",
      "rated_at": "2024-01-02 18:30:00"
    },
    "created_at": "2024-01-01 10:00:00",
    "completed_at": "2024-01-02 18:00:00"
  }
}
```

### 4.3 订单统计数据
**接口地址**: `GET /orders/statistics`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `period`: 统计周期 - `today`今日, `week`本周, `month`本月, `year`本年

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total_orders": 2580,
    "pending_orders": 45,
    "in_progress_orders": 120,
    "completed_orders": 2200,
    "cancelled_orders": 215,
    "success_rate": 91.1,
    "average_completion_time": "2.5天",
    "total_reward_amount": 1580000,
    "total_commission": 158000,
    "order_trends": [
      {
        "date": "2024-01-01",
        "new_orders": 15,
        "completed_orders": 12,
        "total_amount": 8500
      }
    ],
    "pet_type_distribution": {
      "dog": 1548,
      "cat": 860,
      "other": 172
    },
    "reward_range_distribution": {
      "0-500": 580,
      "500-1000": 1200,
      "1000-2000": 650,
      "2000+": 150
    }
  }
}
```

## 5. 财务管理模块

### 5.1 获取财务概览
**接口地址**: `GET /finance/overview`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `period`: 统计周期 - `today`今日, `week`本周, `month`本月, `year`本年

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total_revenue": 158000,
    "total_payout": 1422000,
    "pending_payout": 25000,
    "commission_rate": 0.1,
    "revenue_trends": [
      {
        "date": "2024-01-01",
        "revenue": 850,
        "payout": 7650
      }
    ],
    "top_earning_detectives": [
      {
        "detective_id": "DET123456",
        "name": "专业侦探",
        "total_income": 45000,
        "completed_cases": 102
      }
    ]
  }
}
```

### 5.2 获取提现申请列表
**接口地址**: `GET /finance/withdrawals`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `status`: 提现状态 - `pending`待处理, `approved`已批准, `rejected`已拒绝, `completed`已完成
- `start_date`: 申请开始日期
- `end_date`: 申请结束日期
- `page`: 页码（默认1）
- `limit`: 每页数量（默认20）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "withdrawals": [
      {
        "withdrawal_id": "WD202401010001",
        "detective_id": "DET123456",
        "detective_name": "专业侦探",
        "amount": 5000,
        "withdraw_method": "bank",
        "account_info": {
          "account_number": "6222021234567890",
          "account_name": "李四",
          "bank_name": "中国工商银行"
        },
        "status": "pending",
        "applied_at": "2024-01-01 10:00:00",
        "processed_at": null,
        "processed_by": null
      }
    ],
    "total": 125,
    "page": 1,
    "limit": 20
  }
}
```

### 5.3 处理提现申请
**接口地址**: `POST /finance/withdrawals/{withdrawal_id}/process`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "action": "approve", // approve: 批准, reject: 拒绝
  "reason": "审核通过",
  "transaction_id": "TXN202401010001" // 批准时的交易流水号
}
```

## 6. 系统设置模块

### 6.1 获取系统配置
**接口地址**: `GET /settings/config`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "commission_rate": 0.1,
    "min_reward_amount": 100,
    "max_reward_amount": 10000,
    "auto_cancel_hours": 72,
    "detective_review_required": true,
    "max_upload_file_size": 5242880,
    "supported_pet_types": ["dog", "cat", "bird", "other"],
    "notification_settings": {
      "email_enabled": true,
      "sms_enabled": true,
      "push_enabled": true
    }
  }
}
```

### 6.2 更新系统配置
**接口地址**: `PUT /settings/config`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "commission_rate": 0.12,
  "min_reward_amount": 200,
  "max_reward_amount": 15000,
  "auto_cancel_hours": 48
}
```

### 6.3 获取管理员列表
**接口地址**: `GET /settings/admins`

**请求头**: `Authorization: Bearer {token}`

### 6.4 添加管理员
**接口地址**: `POST /settings/admins`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "username": "new_admin",
  "password": "password123",
  "name": "新管理员",
  "role": "admin",
  "permissions": ["user_manage", "detective_manage"]
}
```

## 7. 数据统计模块

### 7.1 获取平台概览数据
**接口地址**: `GET /statistics/overview`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total_users": 12500,
    "total_detectives": 850,
    "total_orders": 2580,
    "success_rate": 91.1,
    "total_revenue": 158000,
    "active_users_today": 1200,
    "active_detectives_today": 120,
    "new_orders_today": 15,
    "completed_orders_today": 12,
    "growth_metrics": {
      "user_growth_rate": 15.2,
      "detective_growth_rate": 8.5,
      "order_growth_rate": 22.3,
      "revenue_growth_rate": 18.7
    }
  }
}
```

### 7.2 获取详细统计报表
**接口地址**: `GET /statistics/reports`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `type`: 报表类型 - `user`用户报表, `detective`侦探报表, `order`订单报表, `finance`财务报表
- `period`: 统计周期 - `week`本周, `month`本月, `quarter`本季度, `year`本年
- `start_date`: 开始日期
- `end_date`: 结束日期

### 7.3 导出统计报表
**接口地址**: `GET /statistics/export`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `type`: 报表类型
- `format`: 导出格式 - `excel`, `csv`
- `start_date`: 开始日期
- `end_date`: 结束日期

**响应**: 文件下载

## 8. 消息通知模块

### 8.1 发送系统通知
**接口地址**: `POST /notifications/send`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "target_type": "all", // all: 所有用户, users: 指定用户, detectives: 指定侦探
  "target_ids": [], // 当target_type不为all时，指定目标ID列表
  "title": "系统维护通知",
  "content": "系统将于今晚22:00-24:00进行维护",
  "notification_type": "system", // system: 系统通知, promotion: 推广通知
  "channels": ["app", "sms", "email"] // 通知渠道
}
```

### 8.2 获取通知发送记录
**接口地址**: `GET /notifications/records`

**请求头**: `Authorization: Bearer {token}`

## 9. 日志管理模块

### 9.1 获取操作日志
**接口地址**: `GET /logs/operations`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `admin_id`: 管理员ID（可选）
- `action`: 操作类型（可选）
- `start_date`: 开始日期
- `end_date`: 结束日期
- `page`: 页码（默认1）
- `limit`: 每页数量（默认50）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "logs": [
      {
        "log_id": "LOG001",
        "admin_id": "ADMIN001",
        "admin_name": "系统管理员",
        "action": "detective_review",
        "action_desc": "审核侦探申请",
        "target_type": "detective",
        "target_id": "DET123456",
        "details": {
          "result": "approved",
          "reason": "资料齐全，符合要求"
        },
        "ip_address": "192.168.1.100",
        "user_agent": "Mozilla/5.0...",
        "created_at": "2024-01-01 14:00:00"
      }
    ],
    "total": 1250,
    "page": 1,
    "limit": 50
  }
}
```

### 9.2 获取系统日志
**接口地址**: `GET /logs/system`

**请求头**: `Authorization: Bearer {token}`

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，需要登录 |
| 403 | 禁止访问，权限不足 |
| 404 | 资源不存在 |
| 429 | 请求过于频繁 |
| 500 | 服务器内部错误 |
| 1001 | 用户名或密码错误 |
| 1002 | 验证码错误 |
| 1003 | 账户已被锁定 |
| 2001 | 用户不存在 |
| 2002 | 侦探不存在 |
| 2003 | 订单不存在 |
| 3001 | 权限不足 |
| 3002 | 操作不被允许 |
| 4001 | 数据验证失败 |
| 4002 | 重复操作 |

## 接口调用示例

### JavaScript示例
```javascript
// 管理员登录
const adminLogin = async (username, password, captcha, captchaKey) => {
  const response = await fetch('https://api.petfinder.com/v1/admin/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      username,
      password,
      captcha,
      captcha_key: captchaKey
    })
  });
  return await response.json();
};

// 获取用户列表
const getUsers = async (token, params = {}) => {
  const queryString = new URLSearchParams(params).toString();
  const response = await fetch(`https://api.petfinder.com/v1/admin/users?${queryString}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return await response.json();
};

// 审核侦探申请
const reviewDetective = async (token, detectiveId, action, reason) => {
  const response = await fetch(`https://api.petfinder.com/v1/admin/detectives/${detectiveId}/review`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      action,
      reason,
      certification_level: action === 'approve' ? 'bronze' : undefined
    })
  });
  return await response.json();
};
```

## 权限说明

### 角色权限
- **super_admin（超级管理员）**: 拥有所有权限
- **admin（普通管理员）**: 拥有部分管理权限，不能管理其他管理员

### 权限列表
- `user_manage`: 用户管理权限
- `detective_manage`: 侦探管理权限
- `order_manage`: 订单管理权限
- `finance_manage`: 财务管理权限
- `system_manage`: 系统管理权限
- `data_view`: 数据查看权限
- `notification_send`: 通知发送权限

## 更新日志

### v1.0.0 (2024-12-01)
- 初始版本发布
- 包含用户管理、侦探审核、订单管理、财务管理、系统设置等核心功能接口

---

**注意事项**:
1. 所有管理接口都需要管理员权限验证
2. 敏感操作需要记录操作日志
3. 批量操作需要做好事务处理
4. 数据导出功能需要做好性能优化
5. 建议使用HTTPS协议
6. API调用频率限制：每分钟不超过200次
7. 大数据量查询建议使用分页