# PC 文件夹目录结构

本文档列出了 `/Users/Zhuanz/Desktop/pet/pc/` 文件夹的文件结构。此项目为宠物找回平台的PC端应用，主要面向宠物主人用户，提供更丰富的桌面端体验，包括宠物丢失信息发布、订单管理、支付等功能。采用 markdown 树状格式设计，便于扩展。您可以通过编辑此文件来添加新条目。

```
/Users/Zhuanz/Desktop/pet/pc/
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
│   │   ├── common-dialog.vue
│   │   ├── l-button.vue
│   │   ├── l-dropdown.vue
│   │   ├── l-img.vue
│   │   ├── l-loading.vue
│   │   ├── l-pagination.vue
│   │   ├── l-send-button.vue
│   │   ├── l-tooltip.vue
│   │   ├── l-video.vue
│   │   ├── UserAgreement.vue
│   │   └── PrivacyPolicy.vue
│   ├── composables/
│   ├── language/
│   │   ├── en.json
│   │   └── index.js
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
│   ├── styles/
│   │   ├── color.scss
│   │   ├── element-plus-override.scss
│   │   ├── global.scss
│   │   └── scroll.scss
│   ├── types/
│   │   ├── common.js
│   │   ├── date.ts
│   │   ├── enums.ts
│   │   └── global.d.ts
│   ├── utils/
│   │   ├── auth.ts
│   │   ├── device-detector.ts
│   │   ├── global-properties.js
│   │   ├── global.js
│   │   ├── mobile-ui-feedback.js
│   │   ├── px-to-rem.js
│   │   ├── storage.js
│   │   └── util.js
│   └── views/
│       ├── Home.vue
│       ├── Login.vue
│       └── NotFound.vue
└── vite.config.js
```

## 项目说明

### 功能定位
- **目标用户**：宠物主人
- **平台特点**：PC端桌面应用，提供更大屏幕的操作体验
- **主要功能**：
  - 用户注册登录（手机号验证码）
  - 发布宠物丢失信息（支持多图上传）
  - 查看和管理发布的订单
  - 支付悬赏金额
  - 查看找寻进度和详情
  - 确认宠物找到
  - 个人信息管理
  - 订单历史查看

### 技术架构
- **框架**：Vue 3 + Vite
- **UI库**：Element Plus（适配PC端）
- **路由**：Vue Router
- **状态管理**：Vuex/Pinia
- **样式**：SCSS + PostCSS
- **构建工具**：Vite
- **类型支持**：TypeScript

### 目录结构说明
- `src/views/`：页面组件（登录页、首页、404页等）
- `src/components/`：公共组件（按钮、对话框、分页等）
- `src/router/`：路由配置和中间件
- `src/store/`：状态管理模块
- `src/utils/`：工具函数（认证、设备检测、存储等）
- `src/styles/`：全局样式和主题
- `src/types/`：TypeScript类型定义
- `src/composables/`：Vue 3组合式API
- `src/language/`：国际化语言包
- `src/assets/`：静态资源
- `public/`：公共静态资源

### PC端特色功能
- **响应式设计**：适配不同屏幕尺寸
- **丰富组件**：提供完整的PC端UI组件库
- **多语言支持**：国际化语言切换
- **类型安全**：TypeScript类型检查
- **状态管理**：集中式状态管理
- **路由守卫**：完善的路由权限控制

### 扩展指南
- 要扩展目录结构，只需按照树状格式添加新行
- 新增页面请在 `src/views/` 目录下创建
- 新增组件请在 `src/components/` 目录下创建
- 新增工具函数请在 `src/utils/` 目录下创建
- 新增类型定义请在 `src/types/` 目录下创建
- 最后更新：2024年12月