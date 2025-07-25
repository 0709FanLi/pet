# 宠物主人移动端 - API接口文档

## 概述
本文档定义了宠物找回平台宠物主人移动端应用所需的API接口，包括用户认证、宠物信息管理、订单管理、支付等核心功能。

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
      "is_new_user": false
    }
  }
}
```

### 1.3 退出登录
**接口地址**: `POST /auth/logout`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "退出成功"
}
```

## 2. 用户信息模块

### 2.1 获取用户信息
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
    "nickname": "宠物主人",
    "avatar": "https://example.com/avatar.jpg",
    "real_name": "张三",
    "created_at": "2024-01-01 10:00:00"
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
  "avatar": "https://example.com/new_avatar.jpg"
}
```

### 2.3 上传头像
**接口地址**: `POST /user/upload-avatar`

**请求头**: `Authorization: Bearer {token}`

**请求参数**: `multipart/form-data`
- `file`: 图片文件

**响应示例**:
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "avatar_url": "https://example.com/avatar/123456.jpg"
  }
}
```

## 3. 宠物信息模块

### 3.1 发布丢失宠物信息
**接口地址**: `POST /pets/lost`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "pet_name": "小白",
  "pet_type": "dog", // dog: 狗, cat: 猫, other: 其他
  "breed": "金毛",
  "age": 2,
  "gender": "male", // male: 公, female: 母
  "color": "金黄色",
  "weight": 25.5,
  "description": "性格温顺，喜欢玩球",
  "lost_time": "2024-01-01 14:30:00",
  "lost_location": {
    "address": "北京市朝阳区三里屯",
    "latitude": 39.9042,
    "longitude": 116.4074
  },
  "lost_description": "在公园玩耍时走失",
  "reward_amount": 1000,
  "contact_phone": "13800138000",
  "contact_wechat": "wechat123",
  "photos": [
    "https://example.com/pet1.jpg",
    "https://example.com/pet2.jpg"
  ]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "发布成功",
  "data": {
    "order_id": "ORD202401010001"
  }
}
```

### 3.2 上传宠物照片
**接口地址**: `POST /pets/upload-photos`

**请求头**: `Authorization: Bearer {token}`

**请求参数**: `multipart/form-data`
- `files`: 图片文件数组（最多9张）

**响应示例**:
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "photo_urls": [
      "https://example.com/pet/123456_1.jpg",
      "https://example.com/pet/123456_2.jpg"
    ]
  }
}
```

## 4. 订单管理模块

### 4.1 获取我的订单列表
**接口地址**: `GET /orders/my-orders`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `status`: 订单状态（可选）- `pending`待接单, `accepted`已接单, `in_progress`进行中, `completed`已完成, `cancelled`已取消
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
        "created_at": "2024-01-01 10:00:00",
        "detective_info": {
          "detective_id": "DET001",
          "name": "专业侦探",
          "avatar": "https://example.com/detective.jpg",
          "phone": "13900139000"
        }
      }
    ],
    "total": 1,
    "page": 1,
    "limit": 10
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
    "status": "in_progress",
    "pet_info": {
      "pet_name": "小白",
      "pet_type": "dog",
      "breed": "金毛",
      "photos": ["https://example.com/pet1.jpg"]
    },
    "lost_info": {
      "lost_time": "2024-01-01 14:30:00",
      "lost_location": {
        "address": "北京市朝阳区三里屯",
        "latitude": 39.9042,
        "longitude": 116.4074
      }
    },
    "reward_amount": 1000,
    "detective_info": {
      "detective_id": "DET001",
      "name": "专业侦探",
      "phone": "13900139000",
      "wechat": "detective_wechat"
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
        "created_at": "2024-01-01 15:00:00"
      }
    ],
    "created_at": "2024-01-01 10:00:00"
  }
}
```

### 4.3 确认宠物找到
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
  "rating": 5,
  "comment": "非常专业，很快就找到了我的宠物"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "确认成功，奖励已自动发放",
  "data": {
    "payment_amount": 1000,
    "payment_time": "2024-01-02 16:05:00"
  }
}
```

### 4.4 取消订单
**接口地址**: `POST /orders/{order_id}/cancel`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "reason": "已自己找到宠物"
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
  "payment_method": "wechat" // wechat: 微信支付, alipay: 支付宝
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "payment_id": "PAY202401010001",
    "payment_params": {
      "appId": "wx1234567890",
      "timeStamp": "1640995200",
      "nonceStr": "abc123",
      "package": "prepay_id=wx123456789",
      "signType": "RSA",
      "paySign": "signature"
    }
  }
}
```

### 5.2 查询支付状态
**接口地址**: `GET /payment/{payment_id}/status`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "payment_id": "PAY202401010001",
    "status": "paid", // pending: 待支付, paid: 已支付, failed: 支付失败
    "amount": 1000,
    "paid_at": "2024-01-01 10:05:00"
  }
}
```

## 6. 消息通知模块

### 6.1 获取消息列表
**接口地址**: `GET /messages`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:
- `type`: 消息类型（可选）- `system`系统通知, `order`订单消息
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
        "created_at": "2024-01-01 15:00:00",
        "related_order_id": "ORD202401010001"
      }
    ],
    "unread_count": 3,
    "total": 10
  }
}
```

### 6.2 标记消息已读
**接口地址**: `POST /messages/{message_id}/read`

**请求头**: `Authorization: Bearer {token}`

### 6.3 获取未读消息数量
**接口地址**: `GET /messages/unread-count`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "unread_count": 3
  }
}
```

## 7. 地理位置模块

### 7.1 地址解析（地址转坐标）
**接口地址**: `GET /location/geocode`

**请求参数**:
- `address`: 地址字符串

**响应示例**:
```json
{
  "code": 200,
  "message": "解析成功",
  "data": {
    "latitude": 39.9042,
    "longitude": 116.4074,
    "formatted_address": "北京市朝阳区三里屯街道"
  }
}
```

### 7.2 逆地址解析（坐标转地址）
**接口地址**: `GET /location/reverse-geocode`

**请求参数**:
- `latitude`: 纬度
- `longitude`: 经度

**响应示例**:
```json
{
  "code": 200,
  "message": "解析成功",
  "data": {
    "address": "北京市朝阳区三里屯街道",
    "province": "北京市",
    "city": "北京市",
    "district": "朝阳区"
  }
}
```

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
| 2001 | 订单不存在 |
| 2002 | 订单状态不允许此操作 |
| 3001 | 支付失败 |
| 3002 | 余额不足 |

## 接口调用示例

### JavaScript示例
```javascript
// 发送验证码
const sendCode = async (phone) => {
  const response = await fetch('https://api.petfinder.com/v1/auth/send-code', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      phone: phone,
      type: 'login'
    })
  });
  return await response.json();
};

// 登录
const login = async (phone, code) => {
  const response = await fetch('https://api.petfinder.com/v1/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      phone: phone,
      code: code
    })
  });
  return await response.json();
};

// 获取订单列表
const getOrders = async (token) => {
  const response = await fetch('https://api.petfinder.com/v1/orders/my-orders', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return await response.json();
};
```

## 更新日志

### v1.0.0 (2024-12-01)
- 初始版本发布
- 包含用户认证、宠物信息、订单管理、支付、消息通知等核心功能接口

---

**注意事项**:
1. 所有接口都需要进行参数验证
2. 敏感信息需要加密传输
3. 接口调用需要做好错误处理
4. 建议使用HTTPS协议
5. 上传文件大小限制：单个文件不超过5MB
6. API调用频率限制：每分钟不超过100次