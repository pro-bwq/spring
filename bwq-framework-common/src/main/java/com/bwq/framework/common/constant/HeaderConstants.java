package com.bwq.framework.common.constant;

/**
 * @author bwq
 * @date 2026-05-30 01:14:12
 * @description 请求头常量
 */

public final class HeaderConstants {

    private HeaderConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }


    // ========== 用户信息 ==========
    public static final String X_USER_ID = "X-User-Id";
    public static final String X_USER_NAME = "X-User-Name";
    public static final String X_TENANT_ID = "X-Tenant-Id";
    public static final String X_AVATAR_URL = "X-Avatar-Url";


    // ========== 权限信息 ==========
    public static final String X_ROLES = "X-Roles";
    public static final String X_PERMISSIONS = "X-Permissions";


    // ========== 请求追踪 ==========
    public static final String X_REQUEST_ID = "X-Request-Id";
    public static final String X_TRACE_ID = "X-Trace-Id";
}

