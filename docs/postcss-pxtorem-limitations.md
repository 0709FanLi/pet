# PostCSS PxToRem 转换限制详解

## 概述
PostCSS PxToRem 是一个在**构建时**处理 CSS 的插件，它有特定的工作范围和限制。本文档详细列出了所有无法被转换的情况。

---

## 1. 样式定义位置限制

### ✅ 可以转换的位置
- CSS 文件中的样式
- SCSS/SASS 文件中的样式
- Vue/React 组件的 `<style>` 块
- 通过 `@import` 导入的样式文件

### ❌ 无法转换的位置

#### 1.1 行内样式（Inline Styles）
```html
<!-- 不会被转换 -->
<div style="width: 100px; height: 200px; margin: 10px;"></div>
<img style="border-radius: 8px; padding: 16px;" />
```

#### 1.2 JavaScript 中的样式对象
```javascript
// 不会被转换
const styles = {
  width: '100px',
  height: '200px',
  fontSize: '14px'
};

// Vue 中的动态样式
const dynamicStyle = {
  marginTop: '20px',
  paddingLeft: '15px'
};
```

#### 1.3 Vue/React 中的动态样式绑定
```vue
<!-- Vue 示例 - 不会被转换 -->
<div :style="{ width: '100px', height: dynamicHeight + 'px' }"></div>
<div :style="styleObject"></div>

<!-- React 示例 - 不会被转换 -->
<div style={{ width: '100px', height: '200px' }}></div>
```

---

## 2. 配置排除规则限制

### ❌ 被配置排除的选择器
根据我们的配置，以下选择器会被跳过：

```javascript
selectorBlackList: [
  '.el-',      // Element Plus 组件
  '.van-',     // Vant 组件
  '.mint-',    // Mint UI 组件
  '[data-v-',  // Vue scoped 样式特殊情况
]
```

**示例：**
```css
/* 不会被转换 */
.el-button {
  padding: 12px 20px;
  font-size: 14px;
}

.van-cell {
  height: 44px;
  line-height: 24px;
}

/* 会被转换 */
.my-button {
  padding: 12px 20px; /* → padding: 0.75rem 1.25rem; */
  font-size: 14px;    /* → font-size: 0.875rem; */
}
```

### ❌ 被文件路径排除
```javascript
exclude: /node_modules/i
```

`node_modules` 目录下的所有文件都不会被处理。

---

## 3. 数值限制

### ❌ 小于最小转换值的 px
```javascript
minPixelValue: 1  // 小于1px的值不转换
```

**示例：**
```css
.example {
  border: 0.5px solid #ccc;  /* 不会被转换 */
  margin: 1px;               /* 会被转换 → 0.0625rem */
  padding: 2px;              /* 会被转换 → 0.125rem */
}
```

---

## 4. 媒体查询限制

### ❌ 媒体查询中的 px（根据配置）
```javascript
mediaQuery: false  // 媒体查询中不转换
```

**示例：**
```css
/* 不会被转换 */
@media (max-width: 768px) {
  .container {
    width: 100px;    /* 这里的px不会被转换 */
    height: 200px;   /* 这里的px不会被转换 */
  }
}

/* 但是媒体查询外的会被转换 */
.container {
  width: 100px;      /* 会被转换 → 6.25rem */
  height: 200px;     /* 会被转换 → 12.5rem */
}
```

---

## 5. 特殊 CSS 属性和值

### ❌ 某些特殊属性值
```css
/* 以下情况可能不会被转换或转换后有问题 */
.example {
  /* calc() 函数中的 px */
  width: calc(100% - 20px);
  
  /* 复杂的属性值 */
  background: url('image.png') 10px 20px no-repeat;
  
  /* 渐变中的位置值 */
  background: linear-gradient(to right, red 10px, blue 50px);
  
  /* 阴影中的多个值 */
  box-shadow: 0 2px 4px rgba(0,0,0,0.1), inset 0 1px 2px rgba(0,0,0,0.2);
}
```

### ❌ 伪元素和伪类中的特殊情况
```css
/* 某些复杂选择器可能有问题 */
.example::before {
  content: "width: 100px";  /* 字符串内容不会被转换 */
}

.example[data-width="100px"] {  /* 属性选择器的值不会被转换 */
  height: 200px;  /* 但这里会被转换 */
}
```

---

## 6. 构建时机限制

### ❌ 运行时动态生成的样式
```javascript
// 运行时动态添加的样式不会被转换
document.head.insertAdjacentHTML('beforeend', 
  '<style>.dynamic { width: 100px; }</style>'
);

// 通过 JavaScript 设置的样式
element.style.width = '100px';
element.style.setProperty('height', '200px');
```

### ❌ 通过 AJAX 加载的 CSS
```javascript
// 动态加载的 CSS 文件不会被处理
fetch('/api/dynamic-styles.css')
  .then(response => response.text())
  .then(css => {
    const style = document.createElement('style');
    style.textContent = css;  // 这里的 px 不会被转换
    document.head.appendChild(style);
  });
```

---

## 7. 框架特定限制

### ❌ CSS-in-JS 库
```javascript
// styled-components (React)
const Button = styled.button`
  width: 100px;     /* 不会被转换 */
  height: 40px;     /* 不会被转换 */
`;

// Emotion
const styles = css`
  padding: 16px;    /* 不会被转换 */
  margin: 8px;      /* 不会被转换 */
`;
```

### ❌ 内联样式库
```javascript
// Radium, Aphrodite 等内联样式库
const styles = {
  button: {
    width: '100px',    /* 不会被转换 */
    height: '40px'     /* 不会被转换 */
  }
};
```

---

## 8. 解决方案和最佳实践

### 8.1 对于无法转换的情况

#### 方案1：创建工具函数
```javascript
// utils/px-to-rem.js
export const px2rem = (px, base = 16) => {
  if (typeof px === 'number') {
    return `${px / base}rem`;
  }
  if (typeof px === 'string') {
    return px.replace(/(\d+(?:\.\d+)?)px/g, (match, value) => {
      return `${parseFloat(value) / base}rem`;
    });
  }
  return px;
};

// 使用示例
const dynamicStyle = {
  width: px2rem(100),      // '6.25rem'
  height: px2rem('200px')  // '12.5rem'
};
```

#### 方案2：使用 CSS 自定义属性
```css
:root {
  --button-width: 6.25rem;   /* 100px / 16 */
  --button-height: 2.5rem;   /* 40px / 16 */
}

.button {
  width: var(--button-width);
  height: var(--button-height);
}
```

#### 方案3：避免行内样式，使用 CSS 类
```vue
<!-- 不推荐 -->
<div style="width: 100px; height: 200px;"></div>

<!-- 推荐 -->
<div class="container"></div>

<style scoped>
.container {
  width: 100px;    /* 会被自动转换 */
  height: 200px;   /* 会被自动转换 */
}
</style>
```

### 8.2 配置优化建议

```javascript
// postcss.config.js
pxtorem({
  rootValue: 16,
  propList: ['*'],
  selectorBlackList: [
    '.el-',
    '.van-',
    '.mint-',
    '[data-v-',
    // 添加更多需要排除的选择器
    '.no-rem',  // 自定义排除类
  ],
  replace: true,
  mediaQuery: true,    // 如果需要转换媒体查询中的px
  minPixelValue: 1,
  exclude: /node_modules/i,
  unitPrecision: 5,
})
```

---

## 9. 检查和调试

### 9.1 如何验证转换效果
```bash
# 构建项目
npm run build

# 检查生成的 CSS 文件
find dist -name "*.css" -exec grep -l "px" {} \;
```

### 9.2 添加调试类
```css
/* 临时调试类 - 不会被转换 */
.debug-px {
  border: 1px solid red !important;
  /* 这里的 px 会被转换，用于对比 */
}

/* 排除转换的调试类 */
.no-rem.debug-px {
  border: 1px solid blue !important;
  /* 这里的 px 不会被转换 */
}
```

---

## 总结

PostCSS PxToRem 是一个强大的工具，但它只能处理**构建时的静态 CSS**。对于动态样式、行内样式、JavaScript 中的样式等，需要使用其他方案来实现 px 到 rem 的转换。

理解这些限制有助于：
1. 正确使用 PostCSS PxToRem
2. 为无法转换的情况选择合适的解决方案
3. 优化项目的响应式设计实现

---

*最后更新：2024年* 