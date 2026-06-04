# spring
spring轻量级框架

## 使用方法 
### 方法一：业务项目中根据需要引入所需模块
```markdown

<!-- 只需要核心功能 -->
<dependency>
    <groupId>io.github.pro-bwq</groupId>
    <artifactId>bwq-framework-core</artifactId>
    <version>1.0.2-SNAPSHOT</version>
</dependency>

<!-- 需要数据库功能时再加 -->
<dependency>
    <groupId>io.github.pro-bwq</groupId>
    <artifactId>bwq-framework-db</artifactId>
    <version>1.0.2-SNAPSHOT</version>
</dependency>
等等


```

### 方法二： 通过BOM统一管理版本
```markdown
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.pro-bwq</groupId>
            <artifactId>bwq-framework</artifactId>
            <version>1.0.2-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

