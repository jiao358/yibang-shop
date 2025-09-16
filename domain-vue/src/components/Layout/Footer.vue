<template>
  <div class="layout-footer">
    <div class="footer-content">
      <span class="copyright">
        © 2024 任务商城后台管理系统. All rights reserved.
      </span>
      <div class="footer-links">
        <a href="#" @click.prevent="showHelp">帮助文档</a>
        <span class="divider">|</span>
        <a href="#" @click.prevent="showAbout">关于系统</a>
        <span class="divider">|</span>
        <span class="version">v1.0.0</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'

// 显示帮助
const showHelp = () => {
  ElMessage.info('帮助文档功能开发中')
}

// 显示关于
const showAbout = () => {
  ElMessage.info('关于系统功能开发中')
}
</script>

<style lang="scss" scoped>
.layout-footer {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: $bg-color-container;
  border-top: 1px solid $border-color-split;
}

.footer-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  max-width: $content-max-width;
  padding: 0 $spacing-lg;

  @media (max-width: 768px) {
    flex-direction: column;
    gap: $spacing-sm;
  }
}

.copyright {
  font-size: $font-size-sm;
  color: $text-color-tertiary;
}

.footer-links {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  font-size: $font-size-sm;

  a {
    color: $text-color-tertiary;
    text-decoration: none;
    transition: color $motion-duration-fast $ease-out;

    &:hover {
      color: $primary-color;
    }
  }

  .divider {
    color: $border-color-base;
  }

  .version {
    color: $text-color-tertiary;
  }
}
</style>
