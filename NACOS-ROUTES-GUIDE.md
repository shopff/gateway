# Gateway 路由配置指南 (Nacos 动态配置)

## 概述

Gateway 的路由配置已从 Java 代码迁移至 Nacos 配置中心，实现动态路由管理。

### 优势

✅ **动态更新**：修改路由配置后自动生效，无需重启服务
✅ **集中管理**：所有配置统一在 Nacos 管理，便于维护
✅ **环境隔离**：不同环境（dev/test/prod）使用不同命名空间
✅ **版本控制**：Nacos 支持配置历史版本和回滚

---

## 配置步骤

### 1. 登录 Nacos 控制台

```bash
URL: http://localhost:8848/nacos
默认账号: nacos
默认密码: nacos
```

### 2. 创建路由配置

#### 2.1 进入配置管理

1. 点击左侧菜单 **配置管理** → **配置列表**
2. 选择正确的命名空间（如 `shopoo-dev`）
3. 点击右上角 **+** 按钮创建配置

#### 2.2 填写配置信息

| 字段 | 值 |
|-----|---|
| **Data ID** | `gateway.yaml` |
| **Group** | `DEFAULT_GROUP` |
| **配置格式** | `YAML` |
| **配置内容** | 参考下面的配置示例 |

#### 2.3 配置内容示例

```yaml
spring:
  cloud:
    gateway:
      routes:
        # Auth Service 路由
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

完整配置请参考项目根目录下的 `nacos-config-example.yaml` 文件。

### 3. 发布配置

1. 点击 **发布** 按钮
2. Gateway 会自动监听配置变化并刷新路由（约 1-2 秒）

### 4. 验证配置

```bash
# 方式 1: 查看 Gateway 日志
tail -f gateway.log | grep "RouteDefinition"

# 方式 2: 访问 Actuator 端点查看路由
curl http://localhost:8701/actuator/gateway/routes | jq

# 方式 3: 测试实际请求
curl -X POST http://localhost:8701/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

---

## 路由配置详解

### 基本路由配置

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: 路由唯一标识
          uri: 目标服务地址
          predicates: 路由匹配规则
          filters: 请求/响应过滤器
          order: 路由优先级（可选）
          metadata: 路由元数据（可选）
```

### URI 配置方式

| 方式 | 示例 | 说明 |
|-----|------|------|
| **服务发现** | `lb://auth-service` | 通过 Nacos 服务发现，自动负载均衡 |
| **HTTP** | `http://192.168.1.100:8702` | 直接指定 HTTP 地址 |
| **HTTPS** | `https://api.example.com` | HTTPS 地址 |
| **WebSocket** | `ws://localhost:9000` | WebSocket 协议 |

### 常用 Predicates（断言）

| 断言类型 | 示例 | 说明 |
|---------|------|------|
| **Path** | `Path=/auth/**` | 路径匹配 |
| **Method** | `Method=GET,POST` | HTTP 方法匹配 |
| **Header** | `Header=X-Request-Id, \d+` | 请求头匹配 |
| **Query** | `Query=token` | 查询参数匹配 |
| **Host** | `Host=**.example.com` | 主机名匹配 |
| **Cookie** | `Cookie=session, abc` | Cookie 匹配 |
| **RemoteAddr** | `RemoteAddr=192.168.1.0/24` | IP 地址匹配 |
| **Weight** | `Weight=group1, 8` | 权重路由（灰度发布） |

### 常用 Filters（过滤器）

#### 1. StripPrefix - 去除路径前缀

```yaml
filters:
  - StripPrefix=2  # 去除路径前 2 段
# 示例: /api/users/123 → /123
```

#### 2. AddRequestHeader - 添加请求头

```yaml
filters:
  - AddRequestHeader=X-Request-Source, gateway
```

#### 3. Retry - 重试

```yaml
filters:
  - name: Retry
    args:
      retries: 3
      statuses: BAD_GATEWAY,SERVICE_UNAVAILABLE
      methods: GET,POST
      backoff:
        firstBackoff: 50ms
        maxBackoff: 500ms
        factor: 2
```

#### 4. RequestRateLimiter - 限流

```yaml
filters:
  - name: RequestRateLimiter
    args:
      redis-rate-limiter.replenishRate: 10   # 每秒生成 10 个令牌
      redis-rate-limiter.burstCapacity: 20   # 令牌桶容量 20
      key-resolver: "#{@ipKeyResolver}"      # IP 限流
```

#### 5. CircuitBreaker - 熔断器

```yaml
filters:
  - name: CircuitBreaker
    args:
      name: myCircuitBreaker
      fallbackUri: forward:/fallback
```

#### 6. RewritePath - 路径重写

```yaml
filters:
  - RewritePath=/api/(?<segment>.*), /$\{segment}
# 示例: /api/users → /users
```

---

## 高级配置

### 1. 全局过滤器

```yaml
spring:
  cloud:
    gateway:
      default-filters:
        - AddResponseHeader=X-Response-Time, ${response.time}
        - AddResponseHeader=X-Gateway-Version, 2025.11
```

### 2. CORS 配置

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins:
              - "http://localhost:3000"
              - "https://app.example.com"
            allowedMethods:
              - GET
              - POST
              - PUT
              - DELETE
            allowedHeaders: "*"
            allowCredentials: true
            maxAge: 3600
```

### 3. 超时配置

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        connect-timeout: 3000      # 连接超时 3 秒
        response-timeout: 10s      # 响应超时 10 秒
        pool:
          max-connections: 500     # 最大连接数
          max-idle-time: 30s       # 最大空闲时间
```

### 4. 服务发现路由

```yaml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true                   # 启用服务发现路由
          lower-case-service-id: true     # 服务名转小写
          url-expression: "'lb://'+serviceId"  # URL 表达式
```

启用后，所有注册到 Nacos 的服务都会自动生成路由：
- `http://gateway:8701/auth-service/**` → `lb://auth-service/**`

---

## 灰度发布示例

### 场景：新版本灰度发布

```yaml
spring:
  cloud:
    gateway:
      routes:
        # 90% 流量到稳定版本
        - id: auth-service-stable
          uri: lb://auth-service
          predicates:
            - Path=/auth/**
            - Weight=auth-group, 90
          metadata:
            version: stable

        # 10% 流量到灰度版本
        - id: auth-service-canary
          uri: lb://auth-service-canary
          predicates:
            - Path=/auth/**
            - Weight=auth-group, 10
          metadata:
            version: canary
```

---

## 多环境配置

### 方式 1: 使用不同的 Nacos 命名空间

```
shopoo-dev     → gateway.yaml (开发环境路由)
shopoo-test    → gateway.yaml (测试环境路由)
shopoo-prod    → gateway.yaml (生产环境路由)
```

### 方式 2: 使用不同的配置文件

```
gateway-dev.yaml   → 开发环境路由
gateway-test.yaml  → 测试环境路由
gateway-prod.yaml  → 生产环境路由
```

在 `application.yaml` 中引用：

```yaml
spring:
  config:
    import:
      - nacos:gateway-${spring.profiles.active}.yaml
```

---

## 监控和调试

### 1. 查看当前路由

```bash
curl http://localhost:8701/actuator/gateway/routes | jq
```

### 2. 刷新路由

```bash
curl -X POST http://localhost:8701/actuator/gateway/refresh
```

### 3. 查看路由详情

```bash
curl http://localhost:8701/actuator/gateway/routes/auth-service | jq
```

### 4. 开启 Debug 日志

在 Nacos 中配置：

```yaml
logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    reactor.netty.http.client: DEBUG
```

---

## 常见问题

### Q1: 修改配置后没有生效？

**解决方案**：
1. 检查 Gateway 日志是否有 Nacos 配置刷新日志
2. 确认 Data ID 和 Group 是否正确
3. 检查命名空间是否匹配
4. 手动刷新路由：`curl -X POST http://localhost:8701/actuator/gateway/refresh`

### Q2: 路由匹配优先级问题？

**解决方案**：
使用 `order` 属性控制优先级（数字越小优先级越高）：

```yaml
routes:
  - id: specific-route
    order: 1  # 优先级高
    # ...

  - id: general-route
    order: 100  # 优先级低
    # ...
```

### Q3: 如何实现 IP 黑白名单？

**解决方案**：
使用 `RemoteAddr` 断言：

```yaml
routes:
  - id: admin-api
    uri: lb://admin-service
    predicates:
      - Path=/admin/**
      - RemoteAddr=192.168.1.0/24,10.0.0.0/8  # 白名单
```

---

## 最佳实践

1. **路由命名规范**：使用 `服务名-功能` 格式，如 `auth-service-login`
2. **版本管理**：重要变更前在 Nacos 创建配置备份
3. **监控告警**：配置路由失败的监控告警
4. **渐进式发布**：使用灰度发布验证新路由
5. **文档同步**：路由变更同步更新文档

---

## 参考资料

- [Spring Cloud Gateway 官方文档](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/)
- [Nacos 配置中心文档](https://nacos.io/zh-cn/docs/quick-start.html)

---

**最后更新**: 2025-11-16
**维护者**: Joe <android_li@sina.cn>