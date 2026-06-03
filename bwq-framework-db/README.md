## 部分执行时机

Spring Boot 启动
│
▼
读取 application.yml 中的 mybatis-plus 配置
│
▼
封装成 MybatisPlusProperties 对象
│
▼
【执行所有 MybatisPlusPropertiesCustomizer】← 自定义的MybatisPlusPropertiesCustomizer
│
▼
MybatisPlusAutoConfiguration 使用最终配置创建 Bean

## 部分数据流量
application.yml
→ MybatisPlusProperties
→ MybatisPlusPropertiesCustomizer（修改）
→ MybatisPlusAutoConfiguration（使用）
→ SqlSessionFactory


## 租户
┌─────────────────────────────────────────────────────────────────────────┐
│                           执行流程                                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  调用 Mapper 方法（带 @IgnoreTenant）                                    │
│         │                                                               │
│         ▼                                                               │
│  AOP 切面（IgnoreTenantAspect）检测到注解                                │
│         │                                                               │
│         ▼                                                               │
│  UserContext.setIgnoreTenant(true)                                      │
│         │                                                               │
│         ▼                                                               │
│  执行 Mapper 方法                                                        │
│         │                                                               │
│         ▼                                                               │
│  MyBatis-Plus 调用 MultiTenantHandler                                   │
│         │                                                               │
│         ├── getTenantId() → 检查 isIgnoreTenant() → 返回 NullValue      │
│         └── ignoreTable() → 检查 isIgnoreTenant() → 返回 true           │
│         │                                                               │
│         ▼                                                               │
│  执行 SQL（不添加 tenant_id 过滤）                                       │
│         │                                                               │
│         ▼                                                               │
│  AOP After 执行 UserContext.clearIgnoreTenant()                         │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘