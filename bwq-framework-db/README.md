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