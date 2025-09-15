#!/bin/bash

# HBuilderX Node.js环境设置脚本

echo "设置HBuilderX Node.js环境..."

# 获取当前Node.js路径
NODE_PATH=$(which node)
NPM_PATH=$(which npm)

echo "Node.js路径: $NODE_PATH"
echo "NPM路径: $NPM_PATH"

# 创建HBuilderX环境变量文件
cat > ~/.hbuilderx_env << EOF
export NODE_PATH="$NODE_PATH"
export NPM_PATH="$NPM_PATH"
export PATH="/opt/homebrew/bin:$PATH"
EOF

echo "HBuilderX环境变量已设置到 ~/.hbuilderx_env"

# 创建HBuilderX启动脚本
cat > ~/start-hbuilderx.sh << 'EOF'
#!/bin/bash

# 加载环境变量
source ~/.hbuilderx_env

# 启动HBuilderX
open -a HBuilderX
EOF

chmod +x ~/start-hbuilderx.sh

echo "HBuilderX启动脚本已创建: ~/start-hbuilderx.sh"
echo "请使用以下命令启动HBuilderX:"
echo "source ~/start-hbuilderx.sh"
