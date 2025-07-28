# Detective Mobile 文件夹目录结构

本文档列出了 `/Users/Zhuanz/Desktop/pet/detective-mobile/` 文件夹的文件结构。此项目为宠物找回平台的宠物侦探移动端应用，主要面向专业宠物侦探、宠物找寻团队和个人宠物找寻志愿者，提供接单、找寻管理、进度汇报、收入管理等功能。采用 markdown 树状格式设计，便于扩展。您可以通过编辑此文件来添加新条目。

```
/Users/Zhuanz/Desktop/pet/detective-mobile/
├── env-version-record.js
├── index.html
├── package-lock.json
├── package.json
├── postcss.config.js
├── public/
├── src/
│   ├── App.vue
│   ├── assets/
│   ├── components/
│   │   ├── order-card.vue
│   │   ├── progress-timeline.vue
│   │   ├── pet-info-card.vue
│   │   ├── upload-evidence.vue
│   │   ├── location-picker.vue
│   │   ├── success-case-card.vue
│   │   ├── common/
│   │       ├── l-button.vue
│   │       ├── l-input.vue
│   │       ├── l-loading.vue
│   │       └── l-pagination.vue
│   │   ├── UserAgreement.vue
│   │   └── PrivacyPolicy.vue
│   ├── composables/
│   ├── main.js
│   ├── router/
│   │   ├── constants.js
│   │   ├── index.js
│   │   ├── middlewares/
│   │   ├── navigation.js
│   │   └── routers.js
│   ├── store/
│   │   ├── index.js
│   │   └── modules/
│   │       ├── auth.js
│   │       ├── orders.js
│   │       ├── user.js
│   │       └── income.js
│   ├── styles/
│   │   ├── index.css
│   │   ├── variables.css
│   │   └── components.css
│   ├── utils/
│   │   ├── auth.js
│   │   ├── location.js
│   │   ├── upload.js
│   │   ├── ui-feedback.js
│   │   └── util.js
│   └── views/
│       ├── auth/
│       │   ├── login.vue
│       │   ├── register.vue
│       │   ├── real-name-auth.vue
│       │   ├── auth-result.vue
│       │   └── user-guide.vue
│       ├── orders/
│       │   ├── order-list.vue
│       │   ├── order-detail.vue
│       │   ├── order-confirm.vue
│       │   ├── my-orders.vue
│       │   ├── progress-report.vue
│       │   └── submit-result.vue
│       ├── cases/
│       │   ├── success-cases.vue
│       │   └── case-detail.vue
│       ├── profile/
│       │   ├── personal-center.vue
│       │   ├── edit-profile.vue
│       │   ├── income-detail.vue
│       │   └── settings.vue
│       ├── messages/
│       │   ├── message-center.vue
│       │   └── notification-list.vue
│       ├── home.vue
│       └── not-found.vue
└── vite.config.js
```

## 项目说明

### 功能定位
- **目标用户**：专业宠物侦探、宠物找寻团队、个人宠物找寻志愿者
- **平台特点**：专业接单平台，提供完整的宠物找寻服务流程
- **主要功能**：
  - 用户注册登录（手机号验证码）
  - 实名认证（身份证+人脸识别）
  - 可接单列表浏览和筛选
  - 订单详情查看和接单
  - 我的接单管理
  - 找寻进度汇报
  - 找寻结果提交
  - 成功案例展示
  - 个人信息管理
  - 收入明细查看
  - 消息通知中心

### 技术架构
- **框架**：Vue 3 + Vite
- **UI库**：基于移动端优化的组件
- **路由**：Vue Router
- **样式**：CSS + PostCSS
- **构建工具**：Vite
- **地图服务**：支持GPS定位和地图展示
- **文件上传**：支持图片、视频上传

### 核心业务模块
1. **用户认证模块**
   - 手机号验证码登录注册
   - 实名认证（身份证+人脸识别）
   - 新用户引导
   - 管理系统审核对接

2. **接单管理模块**
   - 可接单列表（首页）
   - 订单详情查看
   - 接单确认
   - 地理位置筛选
   - 金额和时间排序

3. **我的接单模块**
   - 接单历史列表
   - 正在进行的订单
   - 进度汇报功能
   - 找寻结果提交
   - 统计数据展示

4. **成功案例模块**
   - 成功案例展示
   - 案例详情查看
   - 案例分享功能

5. **个人信息模块**
   - 个人中心
   - 信息编辑
   - 认证管理
   - 设置功能

6. **收入模块**
   - 收入明细查看
   - 收入统计图表
   - 自动打款记录

7. **消息通知模块**
   - 系统通知
   - 订单消息
   - 评价消息
   - 审核结果通知

### 特色功能
- **智能推荐**：基于地理位置推荐附近订单
- **实时通讯**：支持微信、电话直接联系宠物主人
- **进度追踪**：完整的找寻过程记录和汇报
- **证据收集**：支持拍照、录像、位置标记
- **自动结算**：宠物主人确认后自动打款
- **专业认证**：多层级认证体系
- **数据统计**：详细的业务数据分析

### 目录结构说明
- `src/views/`：页面组件（登录页、首页、接单页面等）
- `src/components/`：公共组件（订单卡片、进度组件等）
- `src/router/`：路由配置
- `src/utils/`：工具函数（认证、地图、上传等）
- `src/styles/`：全局样式
- `public/`：静态资源

### 页面结构规划
- **认证相关**：登录注册、实名认证、认证结果
- **接单相关**：首页列表、订单详情、接单确认
- **管理相关**：我的接单、进度汇报、结果提交
- **展示相关**：成功案例、案例详情
- **个人相关**：个人中心、信息编辑、收入明细
- **消息相关**：消息中心、通知列表

### 设计规范
- **主色调**：温暖橙色（#FF8C42）
- **辅助色**：深蓝色（#2E86AB）、浅灰色（#F8F9FA）
- **字体**：苹方/思源黑体
- **图标风格**：线性图标，宠物主题
- **交互**：按钮反馈、页面动画、加载提示

### 扩展指南
- 要扩展目录结构，只需按照树状格式添加新行
- 新增页面请在 `src/views/` 目录下创建
- 新增组件请在 `src/components/` 目录下创建
- 新增工具函数请在 `src/utils/` 目录下创建
- 最后更新：2024年12月