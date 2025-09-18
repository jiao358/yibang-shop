# 懿邦HSF时间工具服务

## 概述

这是一个基于Spring Cloud Alibaba + Nacos的简单HSF（High Speed Framework）服务，提供时间相关的工具方法。

## 功能特性

- ✅ 获取当前时间（多种格式）
- ✅ 获取时间戳（毫秒/秒）
- ✅ 获取日期、时间、年份、月份等信息
- ✅ 支持自定义时间格式
- ✅ 注册到Nacos服务发现
- ✅ 提供HTTP REST API接口
- ✅ 支持Feign客户端调用
- ✅ 完整的Swagger API文档

## 服务接口

### 基础时间接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/hsf/time/current` | GET | 获取当前时间 (yyyy-MM-dd HH:mm:ss) |
| `/api/hsf/time/timestamp` | GET | 获取当前时间戳（毫秒） |
| `/api/hsf/time/timestamp/seconds` | GET | 获取当前时间戳（秒） |
| `/api/hsf/time/date` | GET | 获取当前日期 (yyyy-MM-dd) |
| `/api/hsf/time/time-only` | GET | 获取当前时间 (HH:mm:ss) |

### 详细信息接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/hsf/time/info` | GET | 获取完整时间信息 |
| `/api/hsf/time/year` | GET | 获取当前年份 |
| `/api/hsf/time/month` | GET | 获取当前月份 |
| `/api/hsf/time/day` | GET | 获取当前日期（几号） |
| `/api/hsf/time/day-of-week` | GET | 获取当前周几 |

### 测试接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/hsf/test/local` | GET | 测试本地时间服务 |
| `/api/hsf/test/feign` | GET | 测试Feign客户端调用 |
| `/api/hsf/test/status` | GET | 测试服务注册状态 |

## 快速开始

### 1. 启动Nacos

```bash
# 下载并启动Nacos
# 访问 http://127.0.0.1:8848/nacos
# 默认账号: nacos / nacos
```

### 2. 启动服务

```bash
# 进入项目目录
cd wechat-domain

# 编译项目
mvn clean compile

# 使用启动脚本（推荐）
./start-with-nacos.sh

# 或手动启动
java -jar target/yibang-taskmall-1.0.0.jar
```

### 3. 验证服务

```bash
# 检查服务状态
curl http://localhost:8080/api/hsf/test/status

# 获取当前时间
curl http://localhost:8080/api/hsf/time/current

# 获取时间信息详情
curl http://localhost:8080/api/hsf/time/info
```

### 4. 查看API文档

访问 Swagger UI: http://localhost:8080/api/swagger-ui.html

## Nacos配置

### 服务发现配置

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
        namespace: public
        group: DEFAULT_GROUP
        service: yibang-taskmall
        enabled: true
```

### 环境变量

```bash
export SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR=127.0.0.1:8848
export SPRING_CLOUD_NACOS_DISCOVERY_NAMESPACE=public
export SPRING_CLOUD_NACOS_DISCOVERY_GROUP=DEFAULT_GROUP
```

## 服务调用示例

### 1. HTTP调用

```bash
# 获取当前时间
curl -X GET "http://localhost:8080/api/hsf/time/current"

# 获取格式化的时间
curl -X GET "http://localhost:8080/api/hsf/time/current/formatted?pattern=yyyy年MM月dd日 HH:mm:ss"

# 获取时间信息详情
curl -X GET "http://localhost:8080/api/hsf/time/info"
```

### 2. Feign客户端调用

```java
@Autowired
private TimeServiceClient timeServiceClient;

// 获取当前时间
Result<String> result = timeServiceClient.getCurrentTime();

// 获取时间信息详情
Result<Map<String, Object>> timeInfo = timeServiceClient.getTimeInfo();
```

### 3. 本地服务调用

```java
@Autowired
private TimeService timeService;

// 获取当前时间
String currentTime = timeService.getCurrentTime();

// 获取时间信息详情
Map<String, Object> timeInfo = timeService.getTimeInfo();
```

## 监控和调试

### 1. 服务注册状态

- 访问Nacos控制台: http://127.0.0.1:8848/nacos
- 查看服务列表中的 `yibang-taskmall` 服务
- 检查实例健康状态

### 2. 日志查看

```bash
# 查看服务启动日志
tail -f logs/application.log

# 查看Nacos相关日志
grep "nacos" logs/application.log
```

### 3. 健康检查

```bash
# 检查应用健康状态
curl http://localhost:8080/api/actuator/health

# 检查服务注册状态
curl http://localhost:8080/api/hsf/test/status
```

## 故障排除

### 1. 服务注册失败

- 检查Nacos服务是否运行
- 检查网络连接
- 检查配置文件中的server-addr

### 2. Feign调用失败

- 检查服务是否已注册到Nacos
- 检查服务名称是否正确
- 检查网络连接

### 3. 端口冲突

- 修改application.yml中的server.port
- 确保端口未被占用

## 扩展功能

### 1. 添加新的时间工具方法

在 `TimeService` 接口中添加新方法，在 `TimeServiceImpl` 中实现。

### 2. 添加新的API接口

在 `TimeController` 中添加新的REST接口。

### 3. 添加缓存

可以使用Redis缓存时间信息，减少重复计算。

## 技术栈

- Spring Boot 3.2.0
- Spring Cloud 2022.0.4
- Spring Cloud Alibaba 2022.0.0.0
- Nacos 2.x
- OpenFeign
- Swagger/OpenAPI 3

## 作者

yibang - 2024-01-15
