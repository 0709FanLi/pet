#!/bin/bash

# 测试用例1: 新用户注册并登录
echo "测试用例1: 新用户注册并登录"
curl -v -X POST http://localhost:8080/api/users/login-or-register \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser2","password":"password123","email":"newuser2@example.com"}' \
  2>&1 | grep -v "^*" | grep -v "^}" | grep -v "^{" > response1.txt

echo "响应已保存到 response1.txt"
cat response1.txt

# 测试用例2: 已存在用户直接登录
echo "\n测试用例2: 已存在用户直接登录"
curl -v -X POST http://localhost:8080/api/users/login-or-register \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser2","password":"password123","email":"newuser2@example.com"}' \
  2>&1 | grep -v "^*" | grep -v "^}" | grep -v "^{" > response2.txt

echo "响应已保存到 response2.txt"
cat response2.txt

# 测试用例3: 错误密码登录
echo "\n测试用例3: 错误密码登录"
curl -v -X POST http://localhost:8080/api/users/login-or-register \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser2","password":"wrongpassword","email":"newuser2@example.com"}' \
  2>&1 | grep -v "^*" | grep -v "^}" | grep -v "^{" > response3.txt

echo "响应已保存到 response3.txt"
cat response3.txt

echo "\n测试完成"