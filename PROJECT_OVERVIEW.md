# 🐾 宠物找回平台 - 项目总览

## 📋 项目概述

宠物找回平台是一个完整的多端应用生态系统，旨在帮助宠物主人发布寻宠启事，协助宠物侦探接单找寻，并提供管理后台进行平台运营管理。

## 🏗️ 系统架构

### 后端服务 (Java)

- **技术栈**: Spring Boot + MySQL + JPA + Swagger
- **端口**: 8080
- **数据库**: pet_recovery (已包含数据)
- **启动方式**: `cd java && ./start.sh`

### 前端应用矩阵

#### 1. 移动端 (mobile/)

- **技术栈**: Vue 3 + Vant + Vite
- **目标用户**: 宠物主人
- **主要功能**: 发布寻宠启事、查看宠物信息、个人中心
- **启动**: `cd mobile && npm run dev`
- **端口**: 3182

#### 2. PC 端 (pc/)

- **技术栈**: Vue 3 + Element Plus + Vite
- **目标用户**: PC 端用户
- **主要功能**: 宠物信息展示、发布管理
- **启动**: `cd pc && npm run dev`

#### 3. 管理后台 (manage-web/)

- **技术栈**: Vue 3 + Element Plus + Vite + TypeScript
- **目标用户**: 平台管理员
- **主要功能**: 宠物侦探审核、启事审核、数据统计
- **启动**: `cd manage-web && npm run dev`
- **端口**: 3180

#### 4. uni-app 应用 (fpet/)

- **技术栈**: Vue 3 + uni-app + uView Plus
- **目标平台**: iOS、Android、微信小程序、抖音小程序、H5
- **主要功能**: 多平台发布寻宠启事、申请宠物侦探
- **启动**: `cd fpet && npm run dev:h5`

#### 5. 宠物侦探端 (detective-mobile/)

- **技术栈**: Vue 3 + 移动端优化
- **目标用户**: 专业宠物侦探
- **主要功能**: 接单、进度汇报、案例管理
- **启动**: `cd detective-mobile && npm run dev`

## 📊 数据库状态

当前数据库 `pet_recovery` 包含以下数据：

- 👥 用户数量: 3
- 🐕 宠物启事: 8
- 🕵️ 侦探申请: 3

### 主要数据表

- `users`: 用户表
- `lost_pets`: 宠物启事表
- `detective_applications`: 侦探申请表

## 🚀 快速启动

### 1. 启动后端服务

```bash
cd java
./start.sh
```

### 2. 启动前端应用

```bash
# 移动端
cd mobile && npm run dev

# 管理后台
cd manage-web && npm run dev

# uni-app H5版
cd fpet && npm run dev:h5
```

## 🌐 访问地址

### 后端 API

- 主接口: http://localhost:8080
- API 文档: http://localhost:8080/swagger-ui.html
- 测试接口: http://localhost:8080/api/users/test

### 前端应用

- 移动端: http://localhost:3182
- 管理后台: http://localhost:3180
- uni-app H5: http://localhost:3000 (或对应端口)

## 🔧 主要功能模块

### 宠物主人端

- ✅ 手机验证码登录
- ✅ 发布寻宠启事
- ✅ 查看我的发布
- ✅ 个人中心管理
- ✅ 下拉刷新、时间排序

### 管理后台

- ✅ 管理员登录
- ✅ 启事审核管理
- ✅ 宠物侦探审核
- ✅ 数据统计展示

### uni-app 多端

- ✅ TabBar 导航 (首页/发布/我的)
- ✅ 动态筛选 (宠物种类/城市)
- ✅ 申请宠物侦探
- ✅ 图片上传功能

## 📝 开发说明

### 环境要求

- Java 11+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 项目特色

- 🌟 完整的多端生态系统
- 🔒 JWT 认证体系
- 📱 响应式设计
- 🎨 现代化 UI 组件
- 📡 RESTful API 设计
- 🔄 下拉刷新机制
- 📊 实时数据统计

### 技术亮点

- Spring Boot 自动配置
- Vue 3 Composition API
- TypeScript 类型安全
- uni-app 跨平台能力
- Element Plus 组件库
- uView Plus 移动组件
- MySQL 数据持久化

## 📂 目录结构

```
pet/
├── java/                    # Spring Boot后端
├── mobile/                  # Vue3移动端
├── pc/                      # Vue3 PC端
├── manage-web/              # Vue3管理后台
├── fpet/                    # uni-app多端应用
├── detective-mobile/        # 宠物侦探端
├── docs/                    # 文档目录
└── scripts/                 # 工具脚本
```

## 🎯 下一步计划

- [ ] 完善宠物侦探接单流程
- [ ] 增加实时通讯功能
- [ ] 支付系统集成
- [ ] 地图定位服务
- [ ] 推送通知机制
- [ ] 数据分析报表

---

**最后更新**: 2024 年 12 月
**维护团队**: 宠物找回平台开发团队
