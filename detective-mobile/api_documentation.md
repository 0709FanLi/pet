# 宠物侦探移动端 - API接口文档

## 概述
本文档定义了宠物找回平台宠物侦探移动端应用所需的API接口，包括侦探认证、接单管理、进度汇报、收入管理等核心功能。

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

## 1. 侦探认证模块

### 1.1 发送验证码
**接口地址**: `POST /detective/auth/send-code`

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
**接口地址**: `POST /detective/auth/login`

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
    "detective_info": {
      "detective_id": "DET123456",
      "phone": "13800138000",
      "name": "专业侦探",
      "avatar": "https://example.com/avatar.jpg",
      "status": "pending", // pending: 待审核, approved: 已通过, rejected: 已拒绝
      "certification_level": "bronze", // bronze: 铜牌, silver: 银牌, gold: 金牌
      "success_rate": 85.5,
      "total_cases": 120,
      "is_new_user": false
    }
  }
}
```

### 1.3 实名认证
**接口地址**: `POST /detective/auth/certification`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "real_name": "张三",
  "id_card": "110101199001011234",
  "id_card_front": "https://example.com/id_front.jpg",
  "id_card_back": "https://example.com/id_back.jpg",
  "experience_description": "从事宠物找寻工作3年",
  "skill_certificates": [
    "https://example.com/cert1.jpg"
  ]
}
```

### 1.4 退出登录
**接口地址**: `POST /detective/auth/logout`

**请求头**: `Authorization: Bearer {token}`

## 2. 侦探信息模块

### 2.1 获取侦探信息
**接口地址**: `GET /detective/profile`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "detective_id": "DET123456",
    "phone": "13800138000",
    "name": "专业侦探",
    "avatar": "https://example.com/avatar.jpg",
    "real_name": "张三",
    "status": "approved",
    "certification_level": "silver",
    "success_rate": 85.5,
    "total_cases": 120,
    "completed_cases": 102,
    "rating": 4.8,
    "service_areas": ["北京市朝阳区", "北京市海淀区"],
    "specialties": ["狗类寻找", "猫类寻找"],
    "created_at": "2024-01-01 10:00:00"
  }
}
```

### 2.2 更新侦探信息
**接口地址**: `PUT /detective/profile`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "name": "新名称",
  "avatar": "https://example.com/new_avatar.jpg",
  "service_areas": ["北京市朝阳区", "北京市海淀区"],
  "specialties": ["狗类寻找", "猫类寻找"],
  "introduction": "专业宠物侦探，经验丰富"
}
```

### 2.3 上传头像
**接口地址**: `POST /detective/upload-avatar`

**请求头**: `Authorization: Bearer {token}`

**请求参数**: `multipart/form-data`
- `file`: 图片文件

## 3. 接单管理模块

### 3.1 获取可接订单列表
**接口地址**: `GET /detective/orders/available`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `location`: 位置筛选（可选）
- `pet_type`: 宠物类型筛选（可选）
- `reward_min`: 最小悬赏金额（可选）
- `reward_max`: 最大悬赏金额（可选）
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
        "pet_name": "小白",
        "pet_type": "dog",
        "breed": "金毛",
        "pet_photo": "https://example.com/pet1.jpg",
        "lost_time": "2024-01-01 14:30:00",
        "lost_location": {
          "address": "北京市朝阳区三里屯",
          "latitude": 39.9042,
          "longitude": 116.4074
        },
        "reward_amount": 1000,
        "urgency_level": "high", // low: 低, medium: 中, high: 高
        "distance": 2.5, // 距离侦探当前位置的公里数
        "owner_info": {
          "nickname": "宠物主人",
          "phone": "138****8000",
          "rating": 4.5
        },
        "created_at": "2024-01-01 10:00:00"
      }
    ],
    "total": 15,
    "page": 1,
    "limit": 10
  }
}
```

### 3.2 获取订单详情
**接口地址**: `GET /detective/orders/{order_id}/detail`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "order_id": "ORD202401010001",
    "status": "pending",
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
    "urgency_level": "high",
    "owner_info": {
      "user_id": "USER123",
      "nickname": "宠物主人",
      "phone": "138****8000",
      "contact_wechat": "wechat123",
      "rating": 4.5
    },
    "requirements": "希望尽快找到，宠物对陌生人比较警惕",
    "created_at": "2024-01-01 10:00:00"
  }
}
```

### 3.3 接受订单
**接口地址**: `POST /detective/orders/{order_id}/accept`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "estimated_time": "预计3天内找到",
  "search_plan": "将从丢失地点开始，扩大搜索范围"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "接单成功",
  "data": {
    "order_id": "ORD202401010001",
    "accepted_at": "2024-01-01 11:00:00",
    "owner_contact": {
      "phone": "13800138000",
      "wechat": "wechat123"
    }
  }
}
```

## 4. 我的接单模块

### 4.1 获取我的接单列表
**接口地址**: `GET /detective/orders/my-orders`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `status`: 订单状态（可选）- `in_progress`进行中, `completed`已完成, `cancelled`已取消
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
        "pet_name": "小白",
        "pet_photo": "https://example.com/pet1.jpg",
        "status": "in_progress",
        "reward_amount": 1000,
        "accepted_at": "2024-01-01 11:00:00",
        "last_report_time": "2024-01-01 15:00:00",
        "owner_info": {
          "nickname": "宠物主人",
          "phone": "138****8000"
        }
      }
    ],
    "total": 5,
    "page": 1,
    "limit": 10
  }
}
```

### 4.2 提交进度汇报
**接口地址**: `POST /detective/orders/{order_id}/report`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "content": "已在三里屯附近进行搜索，询问了多位路人",
  "location": {
    "address": "北京市朝阳区三里屯SOHO",
    "latitude": 39.9042,
    "longitude": 116.4074
  },
  "photos": [
    "https://example.com/search1.jpg",
    "https://example.com/search2.jpg"
  ],
  "next_plan": "明天将扩大搜索范围到工体附近"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "汇报提交成功",
  "data": {
    "report_id": "RPT001",
    "created_at": "2024-01-01 15:00:00"
  }
}
```

### 4.3 上传搜索照片
**接口地址**: `POST /detective/orders/{order_id}/upload-photos`

**请求头**: `Authorization: Bearer {token}`

**请求参数**: `multipart/form-data`
- `files`: 图片文件数组（最多9张）
- `description`: 照片描述（可选）

### 4.4 提交找寻结果
**接口地址**: `POST /detective/orders/{order_id}/submit-result`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "result_type": "found", // found: 找到, not_found: 未找到
  "found_time": "2024-01-02 16:00:00",
  "found_location": {
    "address": "北京市朝阳区工体北路",
    "latitude": 39.9042,
    "longitude": 116.4074
  },
  "pet_condition": "健康状况良好，无外伤",
  "found_description": "在工体北路一家宠物店附近发现",
  "evidence_photos": [
    "https://example.com/found1.jpg",
    "https://example.com/found2.jpg"
  ],
  "handover_method": "direct", // direct: 直接交还, pickup: 主人自取
  "additional_notes": "宠物情绪稳定，已喂食"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "结果提交成功，等待主人确认",
  "data": {
    "result_id": "RES001",
    "status": "pending_confirmation",
    "submitted_at": "2024-01-02 16:05:00"
  }
}
```

## 5. 收入管理模块

### 5.1 获取收入明细
**接口地址**: `GET /detective/income/details`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `start_date`: 开始日期（可选）
- `end_date`: 结束日期（可选）
- `page`: 页码（默认1）
- `limit`: 每页数量（默认20）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "income_records": [
      {
        "record_id": "INC001",
        "order_id": "ORD202401010001",
        "pet_name": "小白",
        "amount": 1000,
        "commission_rate": 0.1,
        "commission_amount": 100,
        "actual_income": 900,
        "status": "paid", // pending: 待结算, paid: 已支付
        "paid_at": "2024-01-02 18:00:00",
        "created_at": "2024-01-02 16:05:00"
      }
    ],
    "summary": {
      "total_income": 5400,
      "pending_income": 800,
      "paid_income": 4600,
      "total_commission": 600
    },
    "total": 6,
    "page": 1,
    "limit": 20
  }
}
```

### 5.2 获取收入统计
**接口地址**: `GET /detective/income/statistics`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `period`: 统计周期 - `week`本周, `month`本月, `year`本年

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "period": "month",
    "total_income": 5400,
    "completed_orders": 6,
    "average_income": 900,
    "success_rate": 85.7,
    "daily_income": [
      {
        "date": "2024-01-01",
        "income": 900
      },
      {
        "date": "2024-01-02",
        "income": 1200
      }
    ]
  }
}
```

### 5.3 申请提现
**接口地址**: `POST /detective/income/withdraw`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "amount": 1000,
  "withdraw_method": "bank", // bank: 银行卡, alipay: 支付宝, wechat: 微信
  "account_info": {
    "account_number": "6222021234567890",
    "account_name": "张三",
    "bank_name": "中国工商银行"
  }
}
```

## 6. 消息通知模块

### 6.1 获取消息列表
**接口地址**: `GET /detective/messages`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `type`: 消息类型（可选）- `system`系统通知, `order`订单消息, `income`收入消息
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
        "title": "新订单通知",
        "content": "有新的宠物找寻订单，悬赏金额1000元",
        "is_read": false,
        "created_at": "2024-01-01 10:00:00",
        "related_order_id": "ORD202401010001"
      }
    ],
    "unread_count": 3,
    "total": 15
  }
}
```

### 6.2 标记消息已读
**接口地址**: `POST /detective/messages/{message_id}/read`

**请求头**: `Authorization: Bearer {token}`

### 6.3 获取未读消息数量
**接口地址**: `GET /detective/messages/unread-count`

**请求头**: `Authorization: Bearer {token}`

## 7. 成功案例模块

### 7.1 获取我的成功案例
**接口地址**: `GET /detective/cases/success`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `page`: 页码（默认1）
- `limit`: 每页数量（默认10）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "cases": [
      {
        "case_id": "CASE001",
        "order_id": "ORD202401010001",
        "pet_name": "小白",
        "pet_type": "dog",
        "pet_photo": "https://example.com/pet1.jpg",
        "lost_time": "2024-01-01 14:30:00",
        "found_time": "2024-01-02 16:00:00",
        "search_duration": "1天1小时30分钟",
        "reward_amount": 1000,
        "owner_rating": 5,
        "owner_comment": "非常专业，很快就找到了我的宠物",
        "case_summary": "通过走访附近商户，最终在宠物店找到",
        "completed_at": "2024-01-02 18:00:00"
      }
    ],
    "total": 12,
    "page": 1,
    "limit": 10
  }
}
```

### 7.2 分享成功案例
**接口地址**: `POST /detective/cases/{case_id}/share`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "share_platform": "wechat", // wechat: 微信, weibo: 微博, qq: QQ
  "share_content": "又成功找到一只走失的宠物！"
}
```

## 8. 地理位置模块

### 8.1 更新当前位置
**接口地址**: `POST /detective/location/update`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "latitude": 39.9042,
  "longitude": 116.4074,
  "address": "北京市朝阳区三里屯"
}
```

### 8.2 获取附近订单
**接口地址**: `GET /detective/location/nearby-orders`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `radius`: 搜索半径（公里，默认10）
- `limit`: 返回数量（默认20）

## 9. 评价模块

### 9.1 获取我的评价
**接口地址**: `GET /detective/reviews`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `page`: 页码（默认1）
- `limit`: 每页数量（默认10）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "reviews": [
      {
        "review_id": "REV001",
        "order_id": "ORD202401010001",
        "pet_name": "小白",
        "rating": 5,
        "comment": "非常专业，很快就找到了我的宠物",
        "owner_info": {
          "nickname": "宠物主人",
          "avatar": "https://example.com/owner.jpg"
        },
        "created_at": "2024-01-02 18:30:00"
      }
    ],
    "rating_summary": {
      "average_rating": 4.8,
      "total_reviews": 25,
      "rating_distribution": {
        "5": 20,
        "4": 3,
        "3": 2,
        "2": 0,
        "1": 0
      }
    },
    "total": 25,
    "page": 1,
    "limit": 10
  }
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，需要登录 |
| 403 | 禁止访问，可能是认证未通过 |
| 404 | 资源不存在 |
| 429 | 请求过于频繁 |
| 500 | 服务器内部错误 |
| 1001 | 验证码错误 |
| 1002 | 验证码已过期 |
| 1003 | 手机号格式错误 |
| 2001 | 订单不存在 |
| 2002 | 订单状态不允许此操作 |
| 2003 | 订单已被其他侦探接受 |
| 3001 | 侦探认证未通过 |
| 3002 | 侦探状态异常，无法接单 |
| 4001 | 提现金额不足 |
| 4002 | 提现账户信息错误 |

## 接口调用示例

### JavaScript示例
```javascript
// 获取可接订单列表
const getAvailableOrders = async (token) => {
  const response = await fetch('https://api.petfinder.com/v1/detective/orders/available', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return await response.json();
};

// 接受订单
const acceptOrder = async (token, orderId, plan) => {
  const response = await fetch(`https://api.petfinder.com/v1/detective/orders/${orderId}/accept`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      estimated_time: plan.estimatedTime,
      search_plan: plan.searchPlan
    })
  });
  return await response.json();
};

// 提交进度汇报
const submitReport = async (token, orderId, report) => {
  const response = await fetch(`https://api.petfinder.com/v1/detective/orders/${orderId}/report`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(report)
  });
  return await response.json();
};
```

## 更新日志

### v1.0.0 (2024-12-01)
- 初始版本发布
- 包含侦探认证、接单管理、进度汇报、收入管理等核心功能接口

---

**注意事项**:
1. 侦探必须通过实名认证才能接单
2. 所有接口都需要进行参数验证
3. 敏感信息需要加密传输
4. 接口调用需要做好错误处理
5. 建议使用HTTPS协议
6. 上传文件大小限制：单个文件不超过5MB
7. API调用频率限制：每分钟不超过100次
8. 进度汇报建议每天至少提交一次