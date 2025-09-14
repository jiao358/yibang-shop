#!/bin/bash

# 懿邦任务商城后端服务启动脚本

echo "=========================================="
echo "懿邦任务商城后端服务启动脚本"
echo "=========================================="

# 检查Java版本
echo "检查Java版本..."
java -version
if [ $? -ne 0 ]; then
    echo "错误: 未找到Java，请先安装Java 17+"
    exit 1
fi

# 检查Maven版本
echo "检查Maven版本..."
mvn -version
if [ $? -ne 0 ]; then
    echo "错误: 未找到Maven，请先安装Maven 3.8+"
    exit 1
fi

# 设置环境变量
export JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-17-openjdk}
export MAVEN_HOME=${MAVEN_HOME:-/usr/share/maven}

# 设置启动参数
SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-dev}
SERVER_PORT=${SERVER_PORT:-8080}

echo "启动参数:"
echo "  - 环境: $SPRING_PROFILES_ACTIVE"
echo "  - 端口: $SERVER_PORT"
echo "  - Java Home: $JAVA_HOME"
echo "  - Maven Home: $MAVEN_HOME"
echo ""

# 检查数据库连接
echo "检查数据库连接..."
mysql -h localhost -u root -p123456 -e "SELECT 1" 2>/dev/null
if [ $? -ne 0 ]; then
    echo "警告: 无法连接到MySQL数据库，请确保数据库已启动"
fi

# 检查Redis连接
echo "检查Redis连接..."
redis-cli ping 2>/dev/null | grep -q PONG
if [ $? -ne 0 ]; then
    echo "警告: 无法连接到Redis，请确保Redis已启动"
fi

# 编译项目
echo "编译项目..."
mvn clean compile -DskipTests
if [ $? -ne 0 ]; then
    echo "错误: 项目编译失败"
    exit 1
fi

# 启动应用
echo "启动应用..."
echo "=========================================="
mvn spring-boot:run \
    -Dspring-boot.run.profiles=$SPRING_PROFILES_ACTIVE \
    -Dspring-boot.run.arguments="--server.port=$SERVER_PORT" \
    -Dspring-boot.run.jvmArguments="-Xms512m -Xmx1024m -XX:+UseG1GC"
