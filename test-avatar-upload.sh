#!/bin/bash

echo "=== 测试头像上传功能 ==="

# 等待服务器启动
echo "等待服务器启动..."
sleep 5

# 1. 首先发送验证码
echo "1. 发送验证码..."
curl -X POST http://192.168.1.18:8080/api/users/send-code \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber": "13800138000"}'
echo ""

# 2. 登录获取token
echo "2. 登录获取token..."
LOGIN_RESPONSE=$(curl -s -X POST http://192.168.1.18:8080/api/users/phone-login \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber": "13800138000", "code": "123456"}')

echo "登录响应: $LOGIN_RESPONSE"

# 提取token（假设响应格式包含token字段）
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "❌ 无法获取token，登录失败"
  exit 1
fi

echo "✅ 获取到token: ${TOKEN:0:20}..."

# 3. 创建一个测试图片文件
echo "3. 创建测试图片..."
TEST_IMAGE="test-avatar.jpg"
# 创建一个小的测试图片（1x1像素的JPEG）
printf '\xff\xd8\xff\xe0\x00\x10JFIF\x00\x01\x01\x01\x00H\x00H\x00\x00\xff\xdb\x00C\x00\x08\x06\x06\x07\x06\x05\x08\x07\x07\x07\t\t\x08\n\x0c\x14\r\x0c\x0b\x0b\x0c\x19\x12\x13\x0f\x14\x1d\x1a\x1f\x1e\x1d\x1a\x1c\x1c $.\x27 ",#\x1c\x1c(7),01444\x1f\x27=DI=69C\xff\xc0\x00\x11\x08\x00\x01\x00\x01\x01\x01\x11\x00\x02\x11\x01\x03\x11\x01\xff\xc4\x00\x14\x00\x01\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x08\xff\xc4\x00\x14\x10\x01\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\xff\xda\x00\x0c\x03\x01\x00\x02\x11\x03\x11\x00\x3f\x00\xaa\xff\xd9' > $TEST_IMAGE

echo "测试图片已创建: $TEST_IMAGE"

# 4. 测试头像上传
echo "4. 测试头像上传..."
UPLOAD_RESPONSE=$(curl -s -X POST http://192.168.1.18:8080/api/avatar/upload \
  -H "Authorization: Bearer $TOKEN" \
  -F "avatar=@$TEST_IMAGE")

echo "上传响应: $UPLOAD_RESPONSE"

# 5. 检查上传结果
if echo "$UPLOAD_RESPONSE" | grep -q '"success":true'; then
  echo "✅ 头像上传成功！"
else
  echo "❌ 头像上传失败"
fi

# 清理测试文件
rm -f $TEST_IMAGE

echo "=== 测试完成 ==="
