# 统一登录/注册接口测试指南

## 接口信息
- **URL**: `POST /api/users/login-or-register`
- **功能**: 检查用户是否已注册，如果已注册则直接登录，如果未注册则先注册再登录
- **Content-Type**: `application/json`

## 请求参数
```json
{
  "username": "用户名",
  "password": "密码",
  "email": "邮箱地址"
}
```

## 测试用例

### 1. 新用户注册并登录
```bash
curl -X POST http://localhost:8080/api/users/login-or-register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "email": "newuser@example.com"
  }'
```

### 2. 已存在用户直接登录
```bash
curl -X POST http://localhost:8080/api/users/login-or-register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "email": "newuser@example.com"
  }'
```

### 3. 错误密码测试
```bash
curl -X POST http://localhost:8080/api/users/login-or-register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "wrongpassword",
    "email": "newuser@example.com"
  }'
```

## 响应格式

### 成功响应 (HTTP 200)
```json
{
  "code": 200,
  "message": "登录成功" 或 "注册并登录成功",
  "data": {
    "user": {
      "id": 1,
      "username": "用户名",
      "email": "邮箱",
      "phoneNumber": "手机号",
      "createdAt": "创建时间"
    },
    "token": "JWT令牌"
  }
}
```

### 失败响应 (HTTP 400)
```json
{
  "code": 400,
  "message": "错误信息"
}
```

## Swagger文档
访问 http://localhost:8080/swagger-ui.html 查看完整的API文档

## 其他可用接口
- `POST /api/users/register` - 用户注册
- `POST /api/users/login` - 用户登录
- `GET /api/users/test` - 测试接口