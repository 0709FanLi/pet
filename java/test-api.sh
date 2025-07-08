#!/bin/bash

# API测试脚本

BASE_URL="http://localhost:8080"

echo "=== 宠物找回平台API测试脚本 ==="
echo "基础URL: $BASE_URL"
echo

# 测试服务是否运行
echo "1. 测试服务状态..."
response=$(curl -s -w "%{http_code}" -o /tmp/test_response "$BASE_URL/api/users/test")
if [ "$response" = "200" ]; then
    echo "✅ 服务运行正常"
    cat /tmp/test_response | python3 -m json.tool 2>/dev/null || cat /tmp/test_response
else
    echo "❌ 服务未运行或无法访问 (HTTP: $response)"
    echo "请确保服务已启动: ./start.sh"
    exit 1
fi

echo
echo "2. 测试用户注册..."

# 生成随机用户名避免重复
RANDOM_NUM=$(date +%s)
USERNAME="testuser$RANDOM_NUM"
EMAIL="test$RANDOM_NUM@example.com"

# 注册请求
register_data='{
  "username": "'$USERNAME'",
  "password": "password123",
  "email": "'$EMAIL'",
  "phoneNumber": "13800138000"
}'

echo "注册用户: $USERNAME"
register_response=$(curl -s -w "%{http_code}" -o /tmp/register_response \
  -H "Content-Type: application/json" \
  -d "$register_data" \
  "$BASE_URL/api/users/register")

if [ "$register_response" = "200" ]; then
    echo "✅ 用户注册成功"
    cat /tmp/register_response | python3 -m json.tool 2>/dev/null || cat /tmp/register_response
    
    # 提取token
    TOKEN=$(cat /tmp/register_response | python3 -c "import sys, json; print(json.load(sys.stdin)['data']['token'])" 2>/dev/null)
    echo "Token: $TOKEN"
else
    echo "❌ 用户注册失败 (HTTP: $register_response)"
    cat /tmp/register_response
fi

echo
echo "3. 测试用户登录..."

# 登录请求
login_data='{
  "username": "'$USERNAME'",
  "password": "password123"
}'

login_response=$(curl -s -w "%{http_code}" -o /tmp/login_response \
  -H "Content-Type: application/json" \
  -d "$login_data" \
  "$BASE_URL/api/users/login")

if [ "$login_response" = "200" ]; then
    echo "✅ 用户登录成功"
    cat /tmp/login_response | python3 -m json.tool 2>/dev/null || cat /tmp/login_response
else
    echo "❌ 用户登录失败 (HTTP: $login_response)"
    cat /tmp/login_response
fi

echo
echo "4. 测试错误情况..."

# 测试重复注册
echo "测试重复用户名注册...
duplicate_response=$(curl -s -w "%{http_code}" -o /tmp/duplicate_response \
  -H "Content-Type: application/json" \
  -d "$register_data" \
  "$BASE_URL/api/users/register")

if [ "$duplicate_response" = "400" ]; then
    echo "✅ 正确处理重复用户名"
    cat /tmp/duplicate_response | python3 -m json.tool 2>/dev/null || cat /tmp/duplicate_response
else
    echo "⚠️  重复用户名处理异常 (HTTP: $duplicate_response)"
fi

echo
# 测试错误登录
echo "测试错误密码登录..."
wrong_login_data='{
  "username": "'$USERNAME'",
  "password": "wrongpassword"
}'

wrong_login_response=$(curl -s -w "%{http_code}" -o /tmp/wrong_login_response \
  -H "Content-Type: application/json" \
  -d "$wrong_login_data" \
  "$BASE_URL/api/users/login")

if [ "$wrong_login_response" = "400" ]; then
    echo "✅ 正确处理错误密码"
    cat /tmp/wrong_login_response | python3 -m json.tool 2>/dev/null || cat /tmp/wrong_login_response
else
    echo "⚠️  错误密码处理异常 (HTTP: $wrong_login_response)"
fi

echo
echo "=== 测试完成 ==="
echo "如需查看详细API文档，请访问: $BASE_URL/swagger-ui.html"

# 清理临时文件
rm -f /tmp/test_response /tmp/register_response /tmp/login_response /tmp/duplicate_response /tmp/wrong_login_response