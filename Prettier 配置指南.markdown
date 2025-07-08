# 在 Cursor 和 Vue3 项目中配置 Prettier（不考虑 ESLint）

Prettier 是一个强大的代码格式化工具，能够帮助开发者保持代码风格一致，提升代码可读性和团队协作效率。本文将详细介绍如何在 Cursor 编辑器和 Vue3 项目中配置 Prettier（不依赖 ESLint），包括安装步骤、配置项详解以及推荐配置示例。

---

## 一、在 Cursor 中配置 Prettier

Cursor 是一款 AI 驱动的代码编辑器，支持 Prettier 的集成。以下是配置步骤：

1. **安装 Prettier 扩展**  
   - 打开 Cursor 的扩展市场（Extensions Marketplace）。  
   - 搜索 "Prettier - Code Formatter" 并安装。

2. **创建 Prettier 配置文件**  
   - 在项目根目录创建 `.prettierrc` 文件。  
   - 添加配置项（详见下文配置项说明）。

3. **设置编辑器使用 Prettier**  
   - 打开 Cursor 的设置（Settings）。  
   - 搜索 "Default Formatter"，选择 "Prettier - Code Formatter" 作为默认格式化工具。  
   - 勾选 "Format On Save"，保存文件时自动格式化。

---

## 二、在 Vue3 项目中配置 Prettier

即使不使用 ESLint，Prettier 在 Vue3 项目中也能独立运行。以下是具体步骤：

1. **安装 Prettier**  
   - 在项目根目录运行以下命令：  
     ```bash
     npm install --save-dev prettier
     ```

2. **创建 Prettier 配置文件**  
   - 在项目根目录创建 `.prettierrc` 文件，添加配置项（见下文）。

3. **配置编辑器**  
   - 确保 Cursor 中已安装 Prettier 扩展并设置为默认格式化工具。  
   - 启用 "Format On Save"，保存时自动格式化。

---

## 三、配置 Prettier 的好处

Prettier 的使用有以下优势：

- **一致性**：自动格式化代码，确保团队风格统一。  
- **效率**：无需手动调整格式，节省时间。  
- **可读性**：代码整齐划一，便于阅读和维护。  
- **多语言支持**：支持 JavaScript、TypeScript、Vue、CSS、HTML 等，完美适配 Vue3 项目。

---

## 四、Prettier 的配置项详解

Prettier 提供丰富的配置项，可在 `.prettierrc` 文件中自定义。以下是所有主要配置项及其说明：

- **`printWidth`**：每行最大字符数，默认 80。  
  - 示例：80（适合大多数屏幕宽度）。  
- **`tabWidth`**：缩进空格数，默认 2。  
  - 示例：2 或 4（2 更紧凑）。  
- **`useTabs`**：缩进使用制表符（true）或空格（false），默认 false。  
  - 推荐：false（空格显示更一致）。  
- **`semi`**：语句末尾加分号（true）或不加（false），默认 true。  
  - 示例：false（现代 JavaScript 风格）。  
- **`singleQuote`**：使用单引号（true）或双引号（false），默认 false。  
  - 示例：true（更简洁）。  
- **`quoteProps`**：对象属性名是否加引号（"as-needed" 仅必要时、"consistent" 保持一致、"preserve" 保留原文），默认 "as-needed"。  
  - 示例："as-needed"（减少冗余引号）。  
- **`trailingComma`**：尾随逗号（"none" 无、"es5" ES5 有效处、"all" 全部），默认 "es5"。  
  - 示例："es5"（便于 Git diff）。  
- **`bracketSpacing`**：对象字面量括号内是否加空格，默认 true。  
  - 示例：true（`{ foo: bar }` 更易读）。  
- **`bracketSameLine`**：多行 HTML/JSX 元素闭合标签是否与开始标签同行，默认 false。  
  - 示例：false（换行更清晰）。  
- **`arrowParens`**：箭头函数参数是否加括号（"always" 总是、"avoid" 尽可能不加），默认 "always"。  
  - 示例："avoid"（`x => x` 更简洁）。  
- **`rangeStart`** 和 **`rangeEnd`**：格式化代码的起始和结束位置，默认 0 和 Infinity。  
  - 示例：仅格式化部分代码时使用。  
- **`parser`**：指定解析器，默认根据文件类型自动选择。  
  - 示例："vue"（Vue 文件专用）。  
- **`filepath`**：指定文件路径，用于推断解析器，默认无。  
  - 通常无需手动设置。  
- **`requirePragma`**：仅格式化含特定注释（如 `/** @prettier */`）的文件，默认 false。  
  - 示例：false（格式化所有文件）。  
- **`insertPragma`**：格式化后插入特定注释，默认 false。  
  - 示例：false（不插入）。  
- **`proseWrap`**：Markdown 文本换行方式（"always" 总是、"never" 从不、"preserve" 保留原文），默认 "preserve"。  
  - 示例："preserve"（尊重原文）。  
- **`htmlWhitespaceSensitivity`**：HTML 中空白处理（"css" 按 CSS、"strict" 严格、"ignore" 忽略），默认 "css"。  
  - 示例："css"（与 CSS 行为一致）。  
- **`vueIndentScriptAndStyle`**：Vue 文件中 `<script>` 和 `<style>` 是否缩进，默认 false。  
  - 示例：true（结构更清晰）。  
- **`endOfLine`**：行尾换行符（"lf"、"crlf"、"auto"），默认 "lf"。  
  - 示例："lf"（Unix 风格，跨平台兼容）。  
- **`embeddedLanguageFormatting`**：内嵌语言（如 CSS in JS）是否格式化（"auto" 自动、"off" 关闭），默认 "auto"。  
  - 示例："auto"（保持一致）。  
- **`singleAttributePerLine`**：HTML/JSX 属性是否每行一个，默认 false。  
  - 示例：false（紧凑显示）。  

---

## 五、推荐的 Prettier 配置示例

以下是一个适用于 Vue3 项目的 `.prettierrc` 配置示例，包含更多实用配置项：

```json
{
  "printWidth": 80,
  "tabWidth": 2,
  "useTabs": false,
  "semi": false,
  "singleQuote": true,
  "quoteProps": "as-needed",
  "trailingComma": "es5",
  "bracketSpacing": true,
  "bracketSameLine": false,
  "arrowParens": "avoid",
  "vueIndentScriptAndStyle": true,
  "endOfLine": "lf",
  "embeddedLanguageFormatting": "auto",
  "htmlWhitespaceSensitivity": "css",
  "proseWrap": "preserve",
  "singleAttributePerLine": false
}
```

### 配置说明：
- **`printWidth: 80`**：限制行宽，适配大多数屏幕。  
- **`tabWidth: 2`**：缩进 2 空格，紧凑清晰。  
- **`semi: false`**：无分号，现代风格。  
- **`singleQuote: true`**：单引号更简洁。  
- **`trailingComma: "es5"`**：ES5 允许处加逗号，便于版本管理。  
- **`vueIndentScriptAndStyle: true`**：Vue 文件中缩进 `<script>` 和 `<style>`，结构清晰。  
- **`htmlWhitespaceSensitivity: "css"`**：HTML 空白处理与 CSS 一致。  

---

## 六、总结

在 Cursor 中配置 Prettier 只需安装扩展并设置默认格式化工具，而在 Vue3 项目中安装 Prettier 并创建 `.prettierrc` 文件即可。通过丰富的配置项，你可以根据团队需求自定义代码风格。上述步骤和推荐配置能帮助你快速上手，确保代码一致性和可读性。希望这份指南对你的团队有所帮助！