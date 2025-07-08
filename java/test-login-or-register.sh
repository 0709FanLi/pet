#!/bin/bash

# 测试统一登录/注册接口
echo "=== 测试统一登录/注册接口 ==="

# 测试1: 新用户注册并登录
echo "\n1. 测试新用户注册并登录:"
curl -X POST http://localhost:8080/api/users/login-or-register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser2",
    "password": "password123",
    "email": "newuser2@example.com"
  }' \
  -v > test-output1.txt 2>&1
cat test-output1.txt | grep -v "^*" | grep -v "^<" | grep -v "^>" | grep -v "^{" | grep -v "^}"

# 测试2: 已存在用户直接登录
echo "\n2. 测试已存在用户直接登录:"
curl -X POST http://localhost:8080/api/users/login-or-register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser2",
    "password": "password123",
    "email": "newuser2@example.com"
  }' \
  -v > test-output2.txt 2>&1
cat test-output2.txt | grep -v "^*" | grep -v "^<" | grep -v "^>" | grep -v "^{" | grep -v "^}"

# 测试3: 错误密码登录
echo "\n3. 测试错误密码登录:"
curl -X POST http://localhost:8080/api/users/login-or-register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser2",
    "password": "wrongpassword",
    "email": "newuser2@example.com"
  }' \
  -v > test-output3.txt 2>&1
cat test-output3.txt | grep -v "^*" | grep -v "^<" | grep -v "^>" | grep -v "^{" | grep -v "^}"

echo "\n=== 测试完成 ==="