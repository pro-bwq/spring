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


    // ========== 基础认证（必须） ==========
    public static final String X_USER_ID = "X-User-Id";
    public static final String X_TENANT_ID = "X-Tenant-Id";
    public static final String X_ROLES = "X-Roles";
    public static final String X_PERMISSIONS = "X-Permissions";

    // ========== 公共属性（可选） ==========
    public static final String X_USER_NAME = "X-User-Name";
    public static final String X_AVATAR_URL = "X-Avatar-Url";
    public static final String X_EMAIL = "X-Email";
    public static final String X_MOBILE = "X-Mobile";
}

