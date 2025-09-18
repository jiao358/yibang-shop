#!/bin/bash

# 懿邦任务商城启动脚本（带Nacos服务发现）
# 作者: yibang
# 创建时间: 2024-01-15

echo "================================="
echo "懿邦任务商城后端服务启动脚本"
echo "================================="

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请先安装Java 17"
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven环境，请先安装Maven"
    exit 1
fi

# 设置环境变量
export SPRING_PROFILES_ACTIVE=dev
export NACOS_SERVER_ADDR=127.0.0.1:8848
export NACOS_NAMESPACE=public
export NACOS_GROUP=DEFAULT_GROUP

echo "环境变量设置:"
echo "  SPRING_PROFILES_ACTIVE: $SPRING_PROFILES_ACTIVE"
echo "  NACOS_SERVER_ADDR: $NACOS_SERVER_ADDR"
echo "  NACOS_NAMESPACE: $NACOS_NAMESPACE"
echo "  NACOS_GROUP: $NACOS_GROUP"
echo ""

# 检查Nacos是否运行
echo "检查Nacos服务状态..."
if curl -s http://127.0.0.1:8848/nacos/v1/ns/operator/servers > /dev/null 2>&1; then
    echo "✓ Nacos服务运行正常"
else
    echo "⚠ 警告: 无法连接到Nacos服务 (127.0.0.1:8848)"
    echo "  请确保Nacos已启动，或修改配置中的server-addr"
    echo ""
fi

# 编译项目
echo "编译项目..."
mvn clean compile -q
if [ $? -ne 0 ]; then
    echo "错误: 项目编译失败"
    exit 1
fi
echo "✓ 项目编译成功"

# 启动应用
echo ""
echo "启动应用..."
echo "服务将在以下地址启动:"
echo "  - 应用地址: http://localhost:8080/api"
echo "  - 时间服务: http://localhost:8080/api/hsf/time"
echo "  - 测试接口: http://localhost:8080/api/hsf/test"
echo "  - Swagger文档: http://localhost:8080/api/swagger-ui.html"
echo ""
echo "Nacos控制台: http://127.0.0.1:8848/nacos"
echo "  默认账号: nacos / nacos"
echo ""

# 启动Spring Boot应用
java -jar target/yibang-taskmall-1.0.0.jar \
    --spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848 \
    --spring.cloud.nacos.discovery.namespace=public \
    --spring.cloud.nacos.discovery.group=DEFAULT_GROUP \
    --spring.application.name=yibang-taskmall
