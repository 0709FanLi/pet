# Mobile 文件夹目录结构

本文档列出了 `/Users/Zhuanz/Desktop/pet/mobile/` 文件夹的文件结构。此项目为宠物找回平台的移动端应用，主要面向宠物主人用户，提供宠物丢失信息发布、查看找寻进度、支付悬赏等功能。采用 markdown 树状格式设计，便于扩展。您可以通过编辑此文件来添加新条目。

```
/Users/Zhuanz/Desktop/pet/mobile/
├── env-version-record.js
├── index.html
├── package-lock.json
├── package.json
├── postcss.config.js
├── public/
├── src/
│   ├── app.vue
│   ├── components/
│   │   └── l-img.vue
│   ├── main.js
│   ├── router/
│   │   ├── constants.js
│   │   ├── index.js
│   │   ├── navigation.js
│   │   └── routers.js
│   ├── styles/
│   │   └── index.css
│   ├── utils/
│   │   ├── auth.js
│   │   ├── ui-feedback.js
│   │   └── util.js
│   └── views/
│       ├── home.vue
│       └── login.vue
└── vite.config.js
```

## 项目说明

### 功能定位
- **目标用户**：宠物主人
- **主要功能**：
  - 用户注册登录（手机号验证码）
  - 发布宠物丢失信息
  - 查看和管理发布的订单
  - 支付悬赏金额
  - 查看找寻进度
  - 确认宠物找到
  - 个人信息管理

### 技术架构
- **框架**：Vue 3 + Vite
- **UI库**：基于移动端优化的组件
- **路由**：Vue Router
- **样式**：CSS + PostCSS
- **构建工具**：Vite

### 目录结构说明
- `src/views/`：页面组件（登录页、首页等）
- `src/components/`：公共组件
- `src/router/`：路由配置
- `src/utils/`：工具函数（认证、UI反馈等）
- `src/styles/`：全局样式
- `public/`：静态资源

### 扩展指南
- 要扩展目录结构，只需按照树状格式添加新行
- 新增页面请在 `src/views/` 目录下创建
- 新增组件请在 `src/components/` 目录下创建
- 最后更新：2024年12月