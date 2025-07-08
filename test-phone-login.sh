#!/bin/bash

# 测试手机验证码登录接口
echo "=== 测试手机验证码登录接口 ==="

BASE_URL="http://localhost:8080"

# 测试1: 发送验证码
echo "\n1. 测试发送验证码:"
curl -X POST $BASE_URL/api/users/send-code \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "13800138000",
    "type": "auth"
  }' \
  -w "\nHTTP状态码: %{http_code}\n" \
  | python3 -m json.tool 2>/dev/null || echo "响应不是有效的JSON格式"

echo "\n" && sleep 2

# 测试2: 手机验证码登录（新用户自动注册）
echo "2. 测试手机验证码登录（新用户）:"
curl -X POST $BASE_URL/api/users/phone-login \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "13800138000",
    "verificationCode": "1111"
  }' \
  -w "\nHTTP状态码: %{http_code}\n" \
  | python3 -m json.tool 2>/dev/null || echo "响应不是有效的JSON格式"

echo "\n" && sleep 2

# 测试3: 手机验证码登录（已存在用户）
echo "3. 测试手机验证码登录（已存在用户）:"
curl -X POST $BASE_URL/api/users/phone-login \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "13800138000",
    "verificationCode": "1111"
  }' \
  -w "\nHTTP状态码: %{http_code}\n" \
  | python3 -m json.tool 2>/dev/null || echo "响应不是有效的JSON格式"

echo "\n" && sleep 2

# 测试4: 错误验证码
echo "4. 测试错误验证码:"
curl -X POST $BASE_URL/api/users/phone-login \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "13800138000",
    "verificationCode": "9999"
  }' \
  -w "\nHTTP状态码: %{http_code}\n" \
  | python3 -m json.tool 2>/dev/null || echo "响应不是有效的JSON格式"

echo "\n=== 测试完成 ==="
echo "提示：验证码固定为 1111（演示用）"
echo "前端访问地址: http://localhost:3180/"
echo "后端API文档: http://localhost:8080/swagger-ui.html"