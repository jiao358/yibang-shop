# 懿邦任务商城微信小程序

## 项目介绍
基于uni-app框架开发的微信小程序，支持任务发布、商城购物、收益提现等功能。

## 技术栈
- uni-app 3.x
- Vue 3
- Pinia (状态管理)
- Vant Weapp (UI组件库)

## 开发环境
- HBuilderX 3.8+
- Node.js 16+
- 微信开发者工具

## 项目结构
```
src/
├── pages/           # 页面文件
├── components/      # 组件文件
├── static/         # 静态资源
├── stores/         # Pinia状态管理
├── api/            # API接口
├── utils/          # 工具函数
├── App.vue         # 应用根组件
├── main.js         # 应用入口
├── manifest.json   # 应用配置
└── pages.json      # 页面配置
```

## 开发命令

### 安装依赖
```bash
npm install
```

### 开发模式
```bash
npm run dev
```

### 构建微信小程序
```bash
npm run build
```

## HBuilderX使用说明

1. 使用HBuilderX打开项目根目录
2. 在HBuilderX中运行到微信小程序
3. 会自动打开微信开发者工具并导入编译后的代码

## 微信开发者工具使用说明

1. 运行 `npm run build` 编译项目
2. 打开微信开发者工具
3. 导入 `dist/build/mp-weixin` 目录
4. 配置AppID后即可预览和调试

## 注意事项

- 项目使用uni-app 3.x版本，需要HBuilderX 3.8+支持
- 微信小程序需要配置正确的AppID
- 开发时建议使用HBuilderX的内置浏览器进行调试