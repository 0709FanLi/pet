# 管理系统 文件夹目录结构

本文档列出了 `/Users/Zhuanz/Desktop/pet/manage-web/` 文件夹的文件结构。此项目为宠物找回平台的后台管理系统，主要面向平台管理员、运营人员和客服人员，提供宠物侦探审核、管理、订单管理、数据统计等功能。采用 markdown 树状格式设计，便于扩展。您可以通过编辑此文件来添加新条目。

```
/Users/Zhuanz/Desktop/pet/manage-web/
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
│   │   └── l-video.vue
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
- **目标用户**：平台管理员、运营人员、客服人员
- **平台特点**：后台管理系统，提供全面的平台管理功能
- **主要功能**：
  - 管理员登录注册（用户名密码+验证码）
  - 宠物侦探审核管理（待审核、审核详情、审核历史）
  - 宠物侦探列表管理（查看详情、上架下架、状态管理）
  - 订单管理（订单列表、详情查看、异常处理）
  - 数据统计分析（关键指标、趋势图表、排行榜）
  - 系统配置管理（基础配置、业务配置、审核配置）

### 技术架构
- **框架**：Vue 3 + Vite
- **UI库**：Element Plus（适配管理后台）
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

### 管理系统特色功能
- **权限管理**：基于角色的权限控制（超级管理员、运营管理员、客服管理员）
- **审核流程**：完整的宠物侦探认证审核流程
- **数据统计**：实时数据监控和统计分析
- **批量操作**：支持批量审核、批量管理等操作
- **操作日志**：完整的操作记录和审计功能
- **安全防护**：登录验证码、敏感信息脱敏、操作权限控制

### 核心业务模块
1. **用户认证模块**
   - 管理员登录/注册
   - 角色权限分配
   - 密码安全管理

2. **宠物侦探审核模块**
   - 待审核列表管理
   - 审核详情查看
   - 审核历史记录
   - 重新提交处理

3. **宠物侦探管理模块**
   - 侦探列表管理
   - 详情信息查看
   - 状态控制（上架/下架）
   - 业务数据统计

4. **订单管理模块**
   - 订单列表查看
   - 订单详情管理
   - 异常订单处理
   - 支付信息管理

5. **数据统计模块**
   - 关键指标展示
   - 趋势图表分析
   - 排行榜统计
   - 数据导出功能

6. **系统设置模块**
   - 基础配置管理
   - 业务参数设置
   - 审核规则配置

### 扩展指南
- 要扩展目录结构，只需按照树状格式添加新行
- 新增页面请在 `src/views/` 目录下创建
- 新增组件请在 `src/components/` 目录下创建
- 新增工具函数请在 `src/utils/` 目录下创建
- 新增类型定义请在 `src/types/` 目录下创建
- 最后更新：2024年12月