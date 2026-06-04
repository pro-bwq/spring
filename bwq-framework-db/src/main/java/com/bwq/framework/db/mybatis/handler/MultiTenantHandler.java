package com.bwq.framework.db.mybatis.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.bwq.framework.common.user.UserContext;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author bwq
 * @date 2026-05-30 00:24:27
 * @description 租户处理器
 */

@Slf4j
@Component
public class MultiTenantHandler implements TenantLineHandler {

    /** 租户字段名 */
    private static final String TENANT_COLUMN = "tenant_id";

    /** 框架默认忽略表（系统级，不可覆盖） */
    private static final Set<String> DEFAULT_IGNORE_TABLES = new HashSet<>(Arrays.asList(
            "sys_tenant",        // 租户表本身
            "sys_dict",          // 字典表
            "sys_config"         // 配置表
    ));

    /** 业务自定义忽略表（从配置文件读取） */
    private Set<String> customIgnoreTables = new HashSet<>();

    public MultiTenantHandler(@Value("${bwq.db.tenant.ignore-tables:}") String ignoreTablesConfig) {
        parseCustomIgnoreTables(ignoreTablesConfig);
    }

    private void parseCustomIgnoreTables(String config) {
        if (config == null || config.trim().isEmpty()) {
            return;
        }
        this.customIgnoreTables = Arrays.stream(config.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    @PostConstruct
    public void init() {
        log.debug("多租户忽略表配置 - 框架默认: {}, 业务自定义: {}",
                DEFAULT_IGNORE_TABLES, customIgnoreTables);
    }

    @Override
    public Expression getTenantId() {
        // 最高优先级：检查是否忽略租户
        if (UserContext.isIgnoreTenant()) {
            log.debug("当前线程忽略租户过滤，返回 null");
            return new NullValue();
        }

        Long tenantId = UserContext.getTenantId();
        log.debug("当前租户 ID: {}", tenantId);

        if (tenantId == null || tenantId <= 0) {
            return new NullValue();
        }
        return new LongValue(tenantId);
    }

    @Override
    public String getTenantIdColumn() {
        return TENANT_COLUMN;
    }

    @Override
    public boolean ignoreTable(String tableName) {
        // 最高优先级：检查是否忽略租户
        if (UserContext.isIgnoreTenant()) {
            log.debug("当前线程忽略租户过滤，表名: {}", tableName);
            return true;
        }

        // 追加模式：默认表或自定义表，任一匹配就忽略
        boolean ignore = DEFAULT_IGNORE_TABLES.contains(tableName) ||
                customIgnoreTables.contains(tableName);
        if (ignore) {
            log.debug("忽略多租户过滤，表名: {}", tableName);
        }
        return ignore;
    }

}
