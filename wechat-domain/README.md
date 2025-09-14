# 懿邦任务商城后端服务

## 项目简介

懿邦任务商城后端服务是基于Spring Boot 3.2.0开发的微服务应用，为微信小程序提供任务管理、收益管理、商城集成等核心功能。

## 技术栈

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security 6.x**
- **MyBatis Plus 3.5.9**
- **MySQL 8.0**
- **Redis 6.0**
- **RabbitMQ 3.x**
- **Maven 3.8+**

## 项目结构

```
wechat-domain/
├── pom.xml                          # Maven配置文件
├── src/
│   ├── main/
│   │   ├── java/com/yibang/taskmall/
│   │   │   ├── TaskMallApplication.java          # 启动类
│   │   │   ├── config/                          # 配置类
│   │   │   │   ├── SecurityConfig.java          # 安全配置
│   │   │   │   ├── RedisConfig.java             # Redis配置
│   │   │   │   └── MybatisPlusConfig.java       # MyBatis Plus配置
│   │   │   ├── controller/                      # 控制器层
│   │   │   │   ├── AuthController.java          # 认证控制器
│   │   │   │   ├── TaskController.java          # 任务控制器
│   │   │   │   ├── EarningsController.java      # 收益控制器
│   │   │   │   ├── WithdrawalController.java    # 提现控制器
│   │   │   │   └── MallController.java          # 商城控制器
│   │   │   ├── service/                         # 服务层
│   │   │   │   ├── AuthService.java             # 认证服务
│   │   │   │   ├── TaskService.java             # 任务服务
│   │   │   │   ├── EarningsService.java         # 收益服务
│   │   │   │   ├── WithdrawalService.java       # 提现服务
│   │   │   │   ├── MallService.java             # 商城服务
│   │   │   │   └── impl/                        # 服务实现
│   │   │   ├── entity/                          # 实体类
│   │   │   │   ├── Task.java                    # 任务实体
│   │   │   │   ├── UserTask.java                # 用户任务实体
│   │   │   │   ├── Earnings.java                # 收益实体
│   │   │   │   ├── Withdrawal.java              # 提现实体
│   │   │   │   └── UserLevelConfig.java         # 用户等级配置实体
│   │   │   ├── dto/                             # 数据传输对象
│   │   │   │   ├── request/                     # 请求DTO
│   │   │   │   └── response/                    # 响应DTO
│   │   │   ├── mapper/                          # 数据访问层
│   │   │   │   ├── TaskMapper.java              # 任务Mapper
│   │   │   │   ├── UserTaskMapper.java          # 用户任务Mapper
│   │   │   │   ├── EarningsMapper.java          # 收益Mapper
│   │   │   │   ├── WithdrawalMapper.java        # 提现Mapper
│   │   │   │   └── UserLevelConfigMapper.java   # 用户等级配置Mapper
│   │   │   ├── common/                          # 公共类
│   │   │   │   └── Result.java                  # 统一返回结果
│   │   │   └── security/                        # 安全相关
│   │   │       ├── JwtAuthenticationEntryPoint.java
│   │   │       └── JwtAuthenticationFilter.java
│   │   └── resources/
│   │       ├── application.yml                  # 应用配置
│   │       ├── application-dev.yml              # 开发环境配置
│   │       ├── application-prod.yml             # 生产环境配置
│   │       └── sql/
│   │           └── schema.sql                   # 数据库表结构
│   └── test/                                    # 测试代码
└── README.md                                    # 项目说明文档
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.x+

### 2. 数据库初始化

```sql
-- 创建数据库
CREATE DATABASE yibang_taskmall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 执行建表脚本
source src/main/resources/sql/schema.sql;
```

### 3. 配置文件

复制并修改配置文件：

```bash
# 开发环境
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml

# 生产环境
cp src/main/resources/application-prod.yml.example src/main/resources/application-prod.yml
```

### 4. 启动应用

```bash
# 开发环境启动
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 或者打包后启动
mvn clean package
java -jar target/yibang-taskmall-1.0.0.jar --spring.profiles.active=dev
```

### 5. 访问应用

- 应用地址: http://localhost:8080/api
- 健康检查: http://localhost:8080/api/actuator/health
- API文档: http://localhost:8080/api/swagger-ui.html

## API接口

### 认证接口

- `POST /api/auth/wechat-login` - 微信登录
- `POST /api/auth/refresh-token` - 刷新Token
- `GET /api/auth/user-info` - 获取用户信息
- `POST /api/auth/logout` - 退出登录

### 任务接口

- `GET /api/tasks` - 获取任务列表
- `GET /api/tasks/{taskId}` - 获取任务详情
- `POST /api/tasks/claim` - 领取任务
- `POST /api/tasks/{userTaskId}/complete` - 完成任务
- `GET /api/tasks/user-tasks` - 获取用户任务列表
- `POST /api/tasks/{userTaskId}/abandon` - 放弃任务

### 收益接口

- `GET /api/earnings/stats` - 获取收益统计
- `GET /api/earnings/records` - 获取收益记录
- `GET /api/earnings/sources` - 获取收益来源统计
- `GET /api/earnings/trend` - 获取收益趋势
- `GET /api/earnings/balance` - 获取用户余额

### 提现接口

- `POST /api/withdrawals/apply` - 申请提现
- `GET /api/withdrawals` - 获取提现记录
- `GET /api/withdrawals/{withdrawalId}` - 获取提现详情
- `POST /api/withdrawals/{withdrawalId}/cancel` - 取消提现
- `GET /api/withdrawals/config` - 获取提现配置
- `POST /api/withdrawals/calculate-fee` - 计算提现手续费

### 商城接口

- `GET /api/mall/products` - 获取商品列表
- `GET /api/mall/products/{productId}` - 获取商品详情
- `GET /api/mall/categories` - 获取商品分类
- `POST /api/mall/orders` - 创建订单
- `GET /api/mall/orders` - 获取订单列表
- `GET /api/mall/orders/{orderId}` - 获取订单详情
- `POST /api/mall/payment` - 处理支付
- `POST /api/mall/orders/{orderId}/cancel` - 取消订单
- `POST /api/mall/orders/{orderId}/confirm` - 确认收货

## 开发说明

### 1. 代码规范

- 使用Lombok减少样板代码
- 统一使用Result类封装返回结果
- 所有接口都需要添加Swagger注解
- 服务层方法需要添加日志记录

### 2. 数据库设计

- 使用MyBatis Plus进行数据访问
- 所有金额字段使用分为单位存储
- 时间字段统一使用LocalDateTime
- 软删除使用逻辑删除字段

### 3. 安全配置

- 使用JWT进行身份认证
- 公开接口不需要认证
- 所有业务接口需要JWT认证
- 支持CORS跨域请求

### 4. 异常处理

- 使用全局异常处理器
- 统一异常返回格式
- 记录异常日志
- 返回用户友好的错误信息

## 部署说明

### 1. 开发环境

```bash
# 启动MySQL
docker run -d --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 mysql:8.0

# 启动Redis
docker run -d --name redis -p 6379:6379 redis:6.0

# 启动RabbitMQ
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

### 2. 生产环境

```bash
# 使用Docker Compose部署
docker-compose up -d

# 或者使用Kubernetes部署
kubectl apply -f k8s/
```

## 监控和运维

### 1. 健康检查

- 应用健康状态: `/actuator/health`
- 数据库连接状态: `/actuator/health/db`
- Redis连接状态: `/actuator/health/redis`

### 2. 日志管理

- 应用日志: `/var/log/yibang-taskmall/application.log`
- 错误日志: `/var/log/yibang-taskmall/error.log`
- 访问日志: `/var/log/yibang-taskmall/access.log`

### 3. 性能监控

- JVM监控: `/actuator/metrics`
- 数据库监控: `/actuator/metrics/datasource`
- 缓存监控: `/actuator/metrics/redis`

## 常见问题

### 1. 启动失败

- 检查Java版本是否为17+
- 检查数据库连接配置
- 检查Redis连接配置
- 查看启动日志定位问题

### 2. 接口调用失败

- 检查JWT Token是否有效
- 检查请求参数是否正确
- 查看应用日志定位问题
- 检查数据库连接状态

### 3. 性能问题

- 检查数据库查询性能
- 检查Redis缓存命中率
- 检查JVM内存使用情况
- 优化慢查询和缓存策略

## 联系方式

- 项目负责人: yibang
- 邮箱: yibang@example.com
- 项目地址: https://github.com/yibang/taskmall

---

**版本**: v1.0.0  
**更新时间**: 2024-01-15  
**维护状态**: 活跃开发中
