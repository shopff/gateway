# Gateway - Spring Cloud Gateway 微服务网关

> 基于 Spring Cloud Gateway + WebFlux 的响应式 API 网关

## 概述

这是一个现代化的微服务网关，负责请求路由、JWT Token 验证、限流熔断等功能。

**核心特性**：
- ✅ 响应式架构（WebFlux）
- ✅ JWT Token 认证
- ✅ 动态路由配置（Nacos）
- ✅ 服务发现（Nacos）
- ✅ 限流熔断
- ✅ 请求重试

**架构角色**：
- **路由转发**：将请求路由到对应的微服务
- **JWT 验证**：验证请求中的 JWT Token 有效性
- **流量控制**：限流、熔断、重试等
- **统一入口**：为所有微服务提供统一访问入口

## 技术栈

| 组件 | 版本 |
|-----|------|
| Java | JDK 17 |
| Spring Boot | 3.5.7 |
| Spring Cloud Gateway | 4.3.0 |
| Spring Security | 6.2.12 |
| JWT (JJWT) | 0.11.2 |
| Nacos | - |
| Redis | 响应式 Lettuce 客户端 |

## 项目结构

```
gateway/
├── pom.xml                              # Maven 配置
├── Dockerfile                           # Docker 镜像构建
├── README.md                            # 本文件
├── NACOS-ROUTES-GUIDE.md               # Nacos 路由配置指南
├── nacos-config-example.yaml           # Nacos 配置示例
├── claude.md                           # 完整架构文档
├── src/
│   ├── main/
│   │   ├── java/com/szmengran/gateway/
│   │   │   ├── Application.java        # 启动类
│   │   │   ├── config/                 # 配置类
│   │   │   │   └── RequestRateLimiterConfig.java
│   │   │   ├── security/               # 安全模块
│   │   │   │   ├── config/
│   │   │   │   │   └── ReactiveSecurityConfig.java
│   │   │   │   ├── filter/
│   │   │   │   │   └── JwtAuthorizationFilter.java
│   │   │   │   └── service/
│   │   │   │       └── JwtService.java
│   │   │   └── fallback/
│   │   │       └── FallbackController.java
│   │   └── resources/
│   │       └── application.yaml        # 主配置文件
│   └── test/
└── target/
```

## 快速开始

### 前置条件

- JDK 17+
- Maven 3.6+
- Nacos Server
- Redis
- MySQL（用于 Auth Service）

### 1. 配置环境变量

```bash
export ENVIRONMENT=dev
export NACOS_USERNAME=nacos
export NACOS_PASSWORD=nacos
export NACOS_SERVER_ADDRESS=localhost:8848
```

### 2. 在 Nacos 中配置路由

**重要**：本项目使用 **Nacos 动态配置路由**，而非硬编码在代码中。

#### 步骤：

1. 访问 Nacos 控制台：http://localhost:8848/nacos
2. 登录（默认 nacos/nacos）
3. 进入 **配置管理** → **配置列表**
4. 创建配置：
   - **Data ID**: `gateway.yaml`
   - **Group**: `DEFAULT_GROUP`
   - **配置格式**: `YAML`
   - **配置内容**: 复制 `nacos-config-example.yaml` 中的内容

详细配置指南请查看：[NACOS-ROUTES-GUIDE.md](./NACOS-ROUTES-GUIDE.md)

### 3. 本地运行

```bash
# 构建
mvn clean package

# 运行
java -jar target/gateway-2025.11.jar
```

### 4. Docker 运行

```bash
# 使用 JIB 构建镜像
mvn jib:build

# 运行容器
docker run -d \
  -p 8701:8701 \
  -e NACOS_SERVER_ADDRESS=nacos:8848 \
  -e NACOS_USERNAME=nacos \
  -e NACOS_PASSWORD=nacos \
  --name gateway \
  registry.cn-guangzhou.aliyuncs.com/szmengran/gateway:2025.11
```

## 路由配置

### 🎯 Nacos 动态路由（推荐）

本项目使用 Nacos 配置中心管理路由，支持动态修改无需重启。

**示例路由配置**：

```yaml
spring:
  cloud:
    gateway:
      routes:
        # Auth Service 认证服务
        - id: auth-service
          uri: lb://auth-service
          predicates:
            - Path=/auth/**
          filters:
            - StripPrefix=0
            - name: Retry
              args:
                retries: 3
                statuses: SERVICE_UNAVAILABLE
```

**完整配置指南**：请查看 [NACOS-ROUTES-GUIDE.md](./NACOS-ROUTES-GUIDE.md)

## API 端点

### Gateway 端点 (8701)

| 端点 | 方法 | 说明 |
|-----|------|------|
| `/auth/**` | ALL | 认证服务（转发到 auth-service:8702） |
| `/actuator/gateway/routes` | GET | 查看当前路由 |
| `/actuator/gateway/refresh` | POST | 刷新路由 |
| `/actuator/health` | GET | 健康检查 |
| `/fallback` | GET | 熔断降级端点 |

### 认证流程

```bash
# 1. 登录获取 Token (通过 Gateway 转发到 Auth Service)
curl -X POST http://localhost:8701/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'

# 返回: {"token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."}

# 2. 使用 Token 访问受保护的资源
curl http://localhost:8701/api/users/me \
  -H "Authorization: Bearer {YOUR_TOKEN}"
```

## 配置说明

### application.yaml

```yaml
server:
  port: 8701

spring:
  application:
    name: gateway
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_SERVER_ADDRESS}
      config:
        server-addr: ${NACOS_SERVER_ADDRESS}
  config:
    import:
      - nacos:gateway.yaml          # 路由配置
      - nacos:shopoo-common.yaml    # 公共配置
```

### JWT 配置

```yaml
secure:
  key: 5Vtq4Qf3XeThWmZq4t7w9zxCW3A1CNcR...  # 256-bit 密钥
  issuer: szmengran
  expireTime: 604800000  # 7 天
```

## 监控和运维

### 查看当前路由

```bash
curl http://localhost:8701/actuator/gateway/routes | jq
```

### 刷新路由（Nacos 配置变更后自动刷新）

```bash
curl -X POST http://localhost:8701/actuator/gateway/refresh
```

### 健康检查

```bash
curl http://localhost:8701/actuator/health
```

### 查看 Prometheus 指标

```bash
curl http://localhost:8701/actuator/prometheus
```

## 限流配置

Gateway 支持两种限流策略：

1. **IP 限流**：基于客户端 IP 地址
2. **用户限流**：基于认证用户身份

限流使用 Redis 存储计数器，基于令牌桶算法。

**Nacos 配置示例**：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: api-service
          uri: lb://api-service
          predicates:
            - Path=/api/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
                key-resolver: "#{@ipKeyResolver}"
```

## 熔断降级

当后端服务不可用时，Gateway 会返回降级响应。

**配置示例**：

```yaml
filters:
  - name: CircuitBreaker
    args:
      name: myCircuitBreaker
      fallbackUri: forward:/fallback
```

## 与 Auth Service 集成

Gateway 与独立的 Auth Service 配合工作：

```
Client → Gateway (8701) → Auth Service (8702)
              ↓
         JWT Validation
```

- **Auth Service**：负责用户认证、Token 生成
- **Gateway**：负责 Token 验证、请求路由

详见：[Auth Service README](../auth-service/README.md)

## 开发指南

### 添加新的路由

1. 登录 Nacos 控制台
2. 编辑 `gateway.yaml` 配置
3. 添加新路由配置
4. 发布配置（Gateway 自动刷新）

### 调试路由

开启 Debug 日志：

```yaml
logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    reactor.netty.http.client: DEBUG
```

### 自定义过滤器

创建自定义 Gateway Filter：

```java
@Component
public class CustomGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomGatewayFilterFactory.Config> {

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 自定义逻辑
            return chain.filter(exchange);
        };
    }

    public static class Config {
        // 配置属性
    }
}
```

## 常见问题

### Q: 路由配置修改后没有生效？

**A**: 检查以下几点：
1. Nacos 配置是否正确发布
2. Gateway 日志是否有配置刷新记录
3. Data ID 和 Group 是否正确
4. 手动刷新：`curl -X POST http://localhost:8701/actuator/gateway/refresh`

### Q: JWT Token 验证失败？

**A**: 确认：
1. Token 格式正确（`Bearer {token}`）
2. Token 未过期
3. 签名密钥配置一致（Gateway 和 Auth Service）

### Q: 如何实现灰度发布？

**A**: 使用 Weight 断言实现流量分配，详见 [NACOS-ROUTES-GUIDE.md](./NACOS-ROUTES-GUIDE.md)

## 部署架构

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ▼
┌──────────────────┐
│  Gateway (8701)  │
│  - JWT 验证      │
│  - 路由转发      │
│  - 限流熔断      │
└──────┬───────────┘
       │
   ┌───┴────┐
   │        │
   ▼        ▼
Auth     Other
Service  Services
(8702)
```

## 性能优化

- **连接池**：使用 Reactor Netty 连接池
- **缓存**：Redis 缓存用户信息
- **异步非阻塞**：WebFlux 响应式架构
- **水平扩展**：多实例部署 + Nginx 负载均衡

## 安全特性

- ✅ JWT Token 认证
- ✅ HTTPS 支持（建议在生产环境启用）
- ✅ CORS 配置
- ✅ 限流防止 DDoS
- ✅ 请求/响应日志审计

## 文档

- **完整架构文档**：[claude.md](./claude.md)
- **Nacos 路由配置指南**：[NACOS-ROUTES-GUIDE.md](./NACOS-ROUTES-GUIDE.md)
- **Auth Service 文档**：[../auth-service/README.md](../auth-service/README.md)

## 许可证

Copyright © 2025 Szmengran

## 联系方式

**维护者**：Joe <android_li@sina.cn>

---

**最后更新**：2025-11-16