# 后台管理系统 CSS 设计规范

## 概述

本文档基于 [Figma B端企业浅色CRM系统设计稿](https://www.figma.com/design/Z3y3GToYTKRegaWw4Qst5k/B%E7%AB%AF%E4%BC%81%E4%B8%9A%E6%B5%85%E8%89%B2CRM%E7%B3%BB%E7%BB%9F-B-end-enterprise-light-colored-CRM-system--Community-?node-id=1-2605&t=Oj6kzWG1dZdq27Lo-4) 设计风格，为任务商城后台管理系统制定的 CSS 设计规范。

## 设计原则

### 1. 浅色企业风格
- 采用浅色调为主的配色方案
- 强调简洁、专业、高效的视觉体验
- 遵循现代化企业级应用设计趋势

### 2. 响应式设计
- 支持主流分辨率 (1920px, 1366px, 1280px)
- 最小支持宽度 1200px
- 组件可适配不同屏幕尺寸

### 3. 组件化思维
- 所有样式按功能模块组织
- 可复用的原子级样式类
- 统一的命名规范

## 色彩体系

### 主色调 (Primary Colors)
```scss
// 主品牌色
$primary-color: #1890FF;          // 主蓝色
$primary-light: #40A9FF;          // 浅蓝色
$primary-dark: #096DD9;           // 深蓝色

// 主色调变体
$primary-1: #E6F7FF;             // 最浅蓝 (背景色)
$primary-2: #BAE7FF;             // 浅蓝 (边框色)
$primary-3: #91D5FF;             // 中浅蓝
$primary-4: #69C0FF;             // 中蓝
$primary-5: #40A9FF;             // 标准蓝
$primary-6: #1890FF;             // 主蓝 (默认)
$primary-7: #096DD9;             // 深蓝 (hover)
$primary-8: #0050B3;             // 更深蓝 (active)
$primary-9: #003A8C;             // 最深蓝
$primary-10: #002766;            // 超深蓝
```

### 功能色彩 (Functional Colors)
```scss
// 成功色 (绿色系)
$success-color: #52C41A;
$success-light: #73D13D;
$success-dark: #389E0D;
$success-bg: #F6FFED;

// 警告色 (橙色系)
$warning-color: #FA8C16;
$warning-light: #FFA940;
$warning-dark: #D46B08;
$warning-bg: #FFF7E6;

// 错误色 (红色系)
$error-color: #FF4D4F;
$error-light: #FF7875;
$error-dark: #CF1322;
$error-bg: #FFF2F0;

// 信息色 (蓝色系)
$info-color: #1890FF;
$info-light: #40A9FF;
$info-dark: #096DD9;
$info-bg: #E6F7FF;
```

### 中性色彩 (Neutral Colors)
```scss
// 文字颜色
$text-color-primary: #262626;     // 主要文字
$text-color-secondary: #595959;   // 次要文字
$text-color-tertiary: #8C8C8C;    // 辅助文字
$text-color-disabled: #BFBFBF;    // 禁用文字

// 背景颜色
$bg-color-container: #FFFFFF;     // 容器背景
$bg-color-body: #F0F2F5;         // 页面背景
$bg-color-component: #FAFAFA;     // 组件背景
$bg-color-disabled: #F5F5F5;      // 禁用背景

// 边框颜色
$border-color-base: #D9D9D9;      // 基础边框
$border-color-split: #F0F0F0;     // 分割线
$border-color-light: #E8E8E8;     // 浅色边框
$border-color-dark: #BFBFBF;      // 深色边框

// 阴影颜色
$shadow-color-light: rgba(0, 0, 0, 0.04);
$shadow-color-base: rgba(0, 0, 0, 0.08);
$shadow-color-dark: rgba(0, 0, 0, 0.12);
```

## 字体系统

### 字体族
```scss
$font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans', sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol', 'Noto Color Emoji';

$font-family-code: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, Courier, monospace;
```

### 字体大小
```scss
// 标题字体
$font-size-h1: 32px;             // 页面主标题
$font-size-h2: 24px;             // 区块标题
$font-size-h3: 20px;             // 小节标题
$font-size-h4: 16px;             // 组件标题

// 正文字体
$font-size-lg: 16px;             // 大号正文
$font-size-base: 14px;           // 基础正文
$font-size-sm: 12px;             // 小号正文
$font-size-xs: 10px;             // 极小字体

// 行高
$line-height-h1: 40px;
$line-height-h2: 32px;
$line-height-h3: 28px;
$line-height-h4: 24px;
$line-height-base: 22px;
$line-height-sm: 20px;
```

### 字重
```scss
$font-weight-light: 300;
$font-weight-normal: 400;
$font-weight-medium: 500;
$font-weight-semibold: 600;
$font-weight-bold: 700;
```

## 间距系统

### 基础间距单位
```scss
// 8px 栅格系统
$spacing-xs: 4px;               // 极小间距
$spacing-sm: 8px;               // 小间距
$spacing-md: 16px;              // 中等间距
$spacing-lg: 24px;              // 大间距
$spacing-xl: 32px;              // 超大间距
$spacing-xxl: 48px;             // 极大间距

// 特殊间距
$spacing-page: 24px;            // 页面内边距
$spacing-section: 32px;         // 区块间距
$spacing-component: 16px;       // 组件间距
$spacing-element: 8px;          // 元素间距
```

### 布局间距
```scss
// 页面布局
$layout-header-height: 64px;    // 顶部导航高度
$layout-sidebar-width: 200px;   // 侧边栏宽度
$layout-sidebar-collapsed-width: 80px; // 折叠侧边栏宽度
$layout-footer-height: 48px;    // 底部高度

// 容器间距
$container-padding-horizontal: 24px;
$container-padding-vertical: 24px;
$content-max-width: 1200px;     // 内容最大宽度
```

## 圆角系统

```scss
$border-radius-sm: 2px;         // 小圆角 (标签、徽章)
$border-radius-base: 6px;       // 基础圆角 (按钮、输入框)
$border-radius-lg: 8px;         // 大圆角 (卡片)
$border-radius-xl: 12px;        // 超大圆角 (模态框)
$border-radius-circle: 50%;     // 圆形
```

## 阴影系统

```scss
// 基础阴影
$box-shadow-light: 0 1px 3px $shadow-color-light;
$box-shadow-base: 0 2px 8px $shadow-color-base;
$box-shadow-dark: 0 4px 12px $shadow-color-dark;

// 卡片阴影
$card-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03), 0 1px 6px -1px rgba(0, 0, 0, 0.02), 0 2px 4px 0 rgba(0, 0, 0, 0.02);
$card-shadow-hover: 0 4px 12px 0 rgba(0, 0, 0, 0.05), 0 2px 4px 0 rgba(0, 0, 0, 0.03);

// 模态框阴影
$modal-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.08), 0 8px 24px 0 rgba(0, 0, 0, 0.06);

// 下拉菜单阴影
$dropdown-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
```

## 动画系统

### 缓动函数
```scss
$ease-base-out: cubic-bezier(0.7, 0.3, 0.1, 0.7);
$ease-base-in: cubic-bezier(0.9, 0, 0.3, 0.7);
$ease-out: cubic-bezier(0.215, 0.61, 0.355, 1);
$ease-in: cubic-bezier(0.55, 0.055, 0.675, 0.19);
$ease-in-out: cubic-bezier(0.645, 0.045, 0.355, 1);
$ease-out-back: cubic-bezier(0.12, 0.4, 0.29, 1.46);
$ease-in-back: cubic-bezier(0.71, -0.46, 0.88, 0.6);
$ease-in-out-back: cubic-bezier(0.71, -0.46, 0.29, 1.46);
$ease-out-circ: cubic-bezier(0.08, 0.82, 0.17, 1);
$ease-in-circ: cubic-bezier(0.6, 0.04, 0.98, 0.34);
$ease-in-out-circ: cubic-bezier(0.78, 0.14, 0.15, 0.86);
$ease-out-quint: cubic-bezier(0.23, 1, 0.32, 1);
$ease-in-quint: cubic-bezier(0.755, 0.05, 0.855, 0.06);
$ease-in-out-quint: cubic-bezier(0.86, 0, 0.07, 1);
```

### 动画时长
```scss
$motion-duration-slow: 0.3s;     // 慢速动画
$motion-duration-mid: 0.2s;      // 中速动画
$motion-duration-fast: 0.1s;     // 快速动画
```

## 组件样式规范

### 按钮 (Button)
```scss
.btn {
  height: 32px;
  padding: 4px 15px;
  font-size: $font-size-base;
  border-radius: $border-radius-base;
  border: 1px solid transparent;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: all $motion-duration-fast $ease-out;
  user-select: none;
  
  &:focus {
    outline: none;
  }
  
  // 尺寸变体
  &--large {
    height: 40px;
    padding: 7px 15px;
    font-size: $font-size-lg;
  }
  
  &--small {
    height: 24px;
    padding: 0 7px;
    font-size: $font-size-sm;
  }
  
  // 类型变体
  &--primary {
    background-color: $primary-color;
    border-color: $primary-color;
    color: #ffffff;
    
    &:hover {
      background-color: $primary-light;
      border-color: $primary-light;
    }
    
    &:active {
      background-color: $primary-dark;
      border-color: $primary-dark;
    }
  }
  
  &--default {
    background-color: #ffffff;
    border-color: $border-color-base;
    color: $text-color-primary;
    
    &:hover {
      color: $primary-light;
      border-color: $primary-light;
    }
    
    &:active {
      color: $primary-dark;
      border-color: $primary-dark;
    }
  }
  
  &--text {
    background-color: transparent;
    border-color: transparent;
    color: $primary-color;
    box-shadow: none;
    
    &:hover {
      background-color: $primary-1;
    }
    
    &:active {
      background-color: $primary-2;
    }
  }
}
```

### 卡片 (Card)
```scss
.card {
  background: $bg-color-container;
  border-radius: $border-radius-lg;
  box-shadow: $card-shadow;
  border: 1px solid $border-color-split;
  overflow: hidden;
  transition: box-shadow $motion-duration-mid $ease-out;
  
  &:hover {
    box-shadow: $card-shadow-hover;
  }
  
  &__header {
    padding: $spacing-md $spacing-lg;
    border-bottom: 1px solid $border-color-split;
    
    .title {
      font-size: $font-size-h4;
      font-weight: $font-weight-medium;
      color: $text-color-primary;
      margin: 0;
    }
    
    .extra {
      color: $text-color-secondary;
      font-size: $font-size-sm;
    }
  }
  
  &__body {
    padding: $spacing-lg;
  }
  
  &__footer {
    padding: $spacing-sm $spacing-lg;
    border-top: 1px solid $border-color-split;
    background-color: $bg-color-component;
  }
}
```

### 表格 (Table)
```scss
.table {
  width: 100%;
  border-collapse: collapse;
  background-color: $bg-color-container;
  
  &__header {
    background-color: $bg-color-component;
    
    th {
      padding: $spacing-md $spacing-md;
      text-align: left;
      font-weight: $font-weight-medium;
      color: $text-color-primary;
      border-bottom: 1px solid $border-color-split;
      font-size: $font-size-base;
    }
  }
  
  &__body {
    tr {
      transition: background-color $motion-duration-fast $ease-out;
      
      &:hover {
        background-color: $bg-color-component;
      }
      
      &:nth-child(even) {
        background-color: rgba($bg-color-component, 0.5);
      }
    }
    
    td {
      padding: $spacing-md $spacing-md;
      border-bottom: 1px solid $border-color-split;
      color: $text-color-primary;
      font-size: $font-size-base;
    }
  }
  
  // 紧凑型表格
  &--compact {
    th, td {
      padding: $spacing-sm $spacing-md;
    }
  }
  
  // 无边框表格
  &--borderless {
    th, td {
      border: none;
    }
  }
}
```

### 表单 (Form)
```scss
.form {
  &__item {
    margin-bottom: $spacing-lg;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
  
  &__label {
    display: block;
    font-size: $font-size-base;
    color: $text-color-primary;
    font-weight: $font-weight-medium;
    margin-bottom: $spacing-sm;
    
    &--required::before {
      content: '*';
      color: $error-color;
      margin-right: 4px;
    }
  }
  
  &__control {
    position: relative;
  }
  
  &__input {
    width: 100%;
    height: 32px;
    padding: 4px 11px;
    border: 1px solid $border-color-base;
    border-radius: $border-radius-base;
    font-size: $font-size-base;
    color: $text-color-primary;
    background-color: $bg-color-container;
    transition: all $motion-duration-fast $ease-out;
    
    &:focus {
      border-color: $primary-color;
      box-shadow: 0 0 0 2px rgba($primary-color, 0.2);
      outline: none;
    }
    
    &:hover {
      border-color: $primary-light;
    }
    
    &::placeholder {
      color: $text-color-tertiary;
    }
    
    &[disabled] {
      background-color: $bg-color-disabled;
      color: $text-color-disabled;
      cursor: not-allowed;
    }
  }
  
  &__textarea {
    @extend .form__input;
    height: auto;
    min-height: 64px;
    padding: 6px 11px;
    resize: vertical;
  }
  
  &__error {
    margin-top: $spacing-xs;
    color: $error-color;
    font-size: $font-size-sm;
    line-height: $line-height-sm;
  }
  
  &__help {
    margin-top: $spacing-xs;
    color: $text-color-tertiary;
    font-size: $font-size-sm;
    line-height: $line-height-sm;
  }
}
```

## 布局系统

### 栅格系统
```scss
.container {
  max-width: $content-max-width;
  margin: 0 auto;
  padding: 0 $container-padding-horizontal;
}

.row {
  display: flex;
  flex-wrap: wrap;
  margin: 0 -#{$spacing-sm};
}

.col {
  padding: 0 $spacing-sm;
  flex: 1;
  
  // 响应式栅格
  @for $i from 1 through 24 {
    &-#{$i} {
      flex: 0 0 percentage($i / 24);
      max-width: percentage($i / 24);
    }
  }
}
```

### 页面布局
```scss
.layout {
  display: flex;
  min-height: 100vh;
  
  &__sidebar {
    width: $layout-sidebar-width;
    background-color: $bg-color-container;
    border-right: 1px solid $border-color-split;
    transition: width $motion-duration-mid $ease-out;
    
    &--collapsed {
      width: $layout-sidebar-collapsed-width;
    }
  }
  
  &__main {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-width: 0;
  }
  
  &__header {
    height: $layout-header-height;
    background-color: $bg-color-container;
    border-bottom: 1px solid $border-color-split;
    display: flex;
    align-items: center;
    padding: 0 $spacing-lg;
  }
  
  &__content {
    flex: 1;
    padding: $spacing-lg;
    background-color: $bg-color-body;
    overflow: auto;
  }
  
  &__footer {
    height: $layout-footer-height;
    background-color: $bg-color-container;
    border-top: 1px solid $border-color-split;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $text-color-tertiary;
    font-size: $font-size-sm;
  }
}
```

## 工具类 (Utility Classes)

### 间距工具类
```scss
// 外边距
@each $prop, $abbrev in (margin: m, padding: p) {
  @each $size, $length in (0: 0, 1: $spacing-xs, 2: $spacing-sm, 3: $spacing-md, 4: $spacing-lg, 5: $spacing-xl) {
    .#{$abbrev}-#{$size} { #{$prop}: $length !important; }
    .#{$abbrev}t-#{$size} { #{$prop}-top: $length !important; }
    .#{$abbrev}r-#{$size} { #{$prop}-right: $length !important; }
    .#{$abbrev}b-#{$size} { #{$prop}-bottom: $length !important; }
    .#{$abbrev}l-#{$size} { #{$prop}-left: $length !important; }
    .#{$abbrev}x-#{$size} { 
      #{$prop}-left: $length !important;
      #{$prop}-right: $length !important;
    }
    .#{$abbrev}y-#{$size} { 
      #{$prop}-top: $length !important;
      #{$prop}-bottom: $length !important;
    }
  }
}
```

### 文本工具类
```scss
.text-left { text-align: left !important; }
.text-center { text-align: center !important; }
.text-right { text-align: right !important; }

.text-primary { color: $text-color-primary !important; }
.text-secondary { color: $text-color-secondary !important; }
.text-tertiary { color: $text-color-tertiary !important; }
.text-disabled { color: $text-color-disabled !important; }

.text-success { color: $success-color !important; }
.text-warning { color: $warning-color !important; }
.text-error { color: $error-color !important; }
.text-info { color: $info-color !important; }

.font-light { font-weight: $font-weight-light !important; }
.font-normal { font-weight: $font-weight-normal !important; }
.font-medium { font-weight: $font-weight-medium !important; }
.font-semibold { font-weight: $font-weight-semibold !important; }
.font-bold { font-weight: $font-weight-bold !important; }
```

### 显示工具类
```scss
.d-none { display: none !important; }
.d-inline { display: inline !important; }
.d-inline-block { display: inline-block !important; }
.d-block { display: block !important; }
.d-flex { display: flex !important; }
.d-inline-flex { display: inline-flex !important; }

.flex-row { flex-direction: row !important; }
.flex-column { flex-direction: column !important; }
.flex-wrap { flex-wrap: wrap !important; }
.flex-nowrap { flex-wrap: nowrap !important; }

.justify-start { justify-content: flex-start !important; }
.justify-center { justify-content: center !important; }
.justify-end { justify-content: flex-end !important; }
.justify-between { justify-content: space-between !important; }
.justify-around { justify-content: space-around !important; }

.align-start { align-items: flex-start !important; }
.align-center { align-items: center !important; }
.align-end { align-items: flex-end !important; }
.align-stretch { align-items: stretch !important; }
```

## 响应式设计

### 断点系统
```scss
$breakpoints: (
  xs: 0,
  sm: 576px,
  md: 768px,
  lg: 992px,
  xl: 1200px,
  xxl: 1600px
);

// 媒体查询 mixin
@mixin media-breakpoint-up($name) {
  $min: map-get($breakpoints, $name);
  @if $min != 0 {
    @media (min-width: $min) {
      @content;
    }
  } @else {
    @content;
  }
}

@mixin media-breakpoint-down($name) {
  $max: map-get($breakpoints, $name) - 0.02;
  @media (max-width: $max) {
    @content;
  }
}
```

### 响应式工具类
```scss
@each $breakpoint in map-keys($breakpoints) {
  @include media-breakpoint-up($breakpoint) {
    .d-#{$breakpoint}-none { display: none !important; }
    .d-#{$breakpoint}-inline { display: inline !important; }
    .d-#{$breakpoint}-inline-block { display: inline-block !important; }
    .d-#{$breakpoint}-block { display: block !important; }
    .d-#{$breakpoint}-flex { display: flex !important; }
    .d-#{$breakpoint}-inline-flex { display: inline-flex !important; }
  }
}
```

## 主题定制

### CSS 自定义属性
```scss
:root {
  // 主色调
  --primary-color: #{$primary-color};
  --primary-light: #{$primary-light};
  --primary-dark: #{$primary-dark};
  
  // 功能色
  --success-color: #{$success-color};
  --warning-color: #{$warning-color};
  --error-color: #{$error-color};
  --info-color: #{$info-color};
  
  // 文字颜色
  --text-color-primary: #{$text-color-primary};
  --text-color-secondary: #{$text-color-secondary};
  --text-color-tertiary: #{$text-color-tertiary};
  --text-color-disabled: #{$text-color-disabled};
  
  // 背景颜色
  --bg-color-container: #{$bg-color-container};
  --bg-color-body: #{$bg-color-body};
  --bg-color-component: #{$bg-color-component};
  --bg-color-disabled: #{$bg-color-disabled};
  
  // 边框颜色
  --border-color-base: #{$border-color-base};
  --border-color-split: #{$border-color-split};
  --border-color-light: #{$border-color-light};
  --border-color-dark: #{$border-color-dark};
  
  // 间距
  --spacing-xs: #{$spacing-xs};
  --spacing-sm: #{$spacing-sm};
  --spacing-md: #{$spacing-md};
  --spacing-lg: #{$spacing-lg};
  --spacing-xl: #{$spacing-xl};
  --spacing-xxl: #{$spacing-xxl};
  
  // 圆角
  --border-radius-sm: #{$border-radius-sm};
  --border-radius-base: #{$border-radius-base};
  --border-radius-lg: #{$border-radius-lg};
  --border-radius-xl: #{$border-radius-xl};
}
```

### 暗色主题（预留）
```scss
[data-theme="dark"] {
  --primary-color: #177ddc;
  --text-color-primary: #ffffff;
  --text-color-secondary: #a6a6a6;
  --text-color-tertiary: #737373;
  --bg-color-container: #1f1f1f;
  --bg-color-body: #0f0f0f;
  --bg-color-component: #262626;
  --border-color-base: #434343;
  --border-color-split: #303030;
}
```

## 代码规范

### 命名规范
- 使用 BEM (Block Element Modifier) 命名方法
- 类名使用小写字母和连字符
- 组件类名以组件名开头
- 修饰符使用双连字符 `--`
- 元素使用双下划线 `__`

### 文件组织
```
src/styles/
├── variables.scss          # 变量定义
├── mixins.scss            # Mixin 函数
├── base.scss              # 基础样式
├── layout.scss            # 布局样式
├── components/            # 组件样式
│   ├── button.scss
│   ├── card.scss
│   ├── table.scss
│   └── form.scss
├── pages/                 # 页面样式
│   ├── dashboard.scss
│   ├── user.scss
│   └── task.scss
├── utilities.scss         # 工具类
└── index.scss            # 主入口文件
```

### 代码格式
```scss
// 良好的格式示例
.component {
  display: flex;
  align-items: center;
  padding: $spacing-md;
  background-color: $bg-color-container;
  border-radius: $border-radius-base;
  transition: all $motion-duration-fast $ease-out;
  
  &__element {
    flex: 1;
    font-size: $font-size-base;
    color: $text-color-primary;
  }
  
  &--modifier {
    background-color: $primary-color;
    color: #ffffff;
  }
  
  @include media-breakpoint-down(md) {
    flex-direction: column;
  }
}
```

---

**文档版本**: v1.0  
**创建时间**: 2024-01-15  
**维护人员**: 前端开发团队  
**参考设计**: [Figma B端企业浅色CRM系统](https://www.figma.com/design/Z3y3GToYTKRegaWw4Qst5k/B%E7%AB%AF%E4%BC%81%E4%B8%9A%E6%B5%85%E8%89%B2CRM%E7%B3%BB%E7%BB%9F-B-end-enterprise-light-colored-CRM-system--Community-?node-id=1-2605&t=Oj6kzWG1dZdq27Lo-4)
