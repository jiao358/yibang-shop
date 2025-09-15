#!/bin/bash

# 设置环境变量
export NODE_PATH="/opt/homebrew/bin/node"
export NPM_PATH="/opt/homebrew/bin/npm"
export PATH="/opt/homebrew/bin:$PATH"

echo "Node.js路径: $NODE_PATH"
echo "NPM路径: $NPM_PATH"

# 启动HBuilderX
open -a HBuilderX

echo "HBuilderX已启动，请在HBuilderX中打开项目: /Users/estela/work/yibang-wechat-system/wechat-vue"
