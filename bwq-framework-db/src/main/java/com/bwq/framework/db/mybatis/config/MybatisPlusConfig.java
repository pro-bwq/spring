package com.bwq.framework.db.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.bwq.framework.db.mybatis.handler.BaseMetaObjectHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
/**
 * @author bwq
 * @date 2026-05-28 08:50:08
 * @description mybatis-plus 配置
 *
 *  职责：
 *  1. 配置 MyBatis-Plus 插件（分页、乐观锁、防全表删除）
 *  2. 配置自动填充处理器
 *  3. 提供默认属性配置（仅在业务项目未配置时生效）
 */
@Slf4j
@Configuration
@EnableTransactionManagement
@ConditionalOnClass(MybatisPlusAutoConfiguration.class)
@MapperScan(basePackages = {"com.bwq.**.mapper"})
public class MybatisPlusConfig {

    @Autowired(required = false)
    // 注入多租户拦截器（条件装配，未配置多租户时不会注入）
    private TenantLineInnerInterceptor tenantLineInnerInterceptor;

    /**
     * MyBatis-Plus 拦截器
     * 包含：分页插件、乐观锁插件、防止全表更新插件
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        log.debug("初始化 MyBatis-Plus 拦截器");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 单页最大限制
        paginationInterceptor.setMaxLimit(1000L);
        // 溢出后处理为第一页
        paginationInterceptor.setOverflow(true);
        interceptor.addInnerInterceptor(paginationInterceptor);

        // 2. 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 3. 防止全表更新/删除插件（攻击拦截）
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        // 4. 多租户插件（如果没有注入，跳过）
        if (tenantLineInnerInterceptor != null) {
            interceptor.addInnerInterceptor(tenantLineInnerInterceptor);
        }

        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusPropertiesCustomizer mybatisPlusCustomizer(BaseMetaObjectHandler metaObjectHandler) {
        return properties -> {
            // ========== 1. GlobalConfig 处理 ==========
            GlobalConfig globalConfig = properties.getGlobalConfig();
            if (globalConfig == null) {
                globalConfig = new GlobalConfig();
                properties.setGlobalConfig(globalConfig);
            }
            globalConfig.setMetaObjectHandler(metaObjectHandler);

            // ========== 2. DbConfig 处理 ==========
            GlobalConfig.DbConfig dbConfig = globalConfig.getDbConfig();
            if (dbConfig == null) {
                dbConfig = new GlobalConfig.DbConfig();
                globalConfig.setDbConfig(dbConfig);
            }

            // 主键策略
            if (dbConfig.getIdType() == null) {
                dbConfig.setIdType(IdType.ASSIGN_ID);
                log.debug("设置 MyBatis-Plus 默认主键策略: ASSIGN_ID");
            }

            // 逻辑删除
            if (dbConfig.getLogicDeleteField() == null) {
                dbConfig.setLogicDeleteField("deleted");
                log.debug("设置 MyBatis-Plus 默认逻辑删除字段: deleted");
            }
            if (dbConfig.getLogicDeleteValue() == null) {
                dbConfig.setLogicDeleteValue("1");
                log.debug("设置 MyBatis-Plus 默认逻辑删除删除值: 1");
            }
            if (dbConfig.getLogicNotDeleteValue() == null) {
                dbConfig.setLogicNotDeleteValue("0");
                log.debug("设置 MyBatis-Plus 默认逻辑删除未删除值: 0");
            }

            // 字段策略
            dbConfig.setInsertStrategy(FieldStrategy.NOT_NULL);
            dbConfig.setUpdateStrategy(FieldStrategy.NOT_NULL);
            log.debug("设置 MyBatis-Plus 字段策略：非空字段才能执行插入或更新操作");

            // ========== 3. Configuration 处理 ==========
            MybatisPlusProperties.CoreConfiguration configuration = properties.getConfiguration();
            if (configuration == null) {
                configuration = new MybatisPlusProperties.CoreConfiguration();
                properties.setConfiguration(configuration);
            }

            // 驼峰映射
            configuration.setMapUnderscoreToCamelCase(true);
            log.debug("设置 MyBatis-Plus 默认驼峰映射: true");

            // 日志实现
            if (configuration.getLogImpl() == null) {
                configuration.setLogImpl(Slf4jImpl.class);
                log.debug("设置 MyBatis-Plus 默认日志实现: Slf4jImpl");
            }
        };
    }
}
