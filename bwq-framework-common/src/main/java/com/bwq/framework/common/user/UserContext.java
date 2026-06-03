package com.bwq.framework.common.user;

import java.util.Set;

/**
 * @author bwq
 * @date 2026-05-30 01:11:40
 * @description ThreadLocal 存储用户信息
 */

public class UserContext<T extends UserInfo> {

    private static final ThreadLocal<UserInfo> USER_HOLDER = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> IGNORE_TENANT_HOLDER = new ThreadLocal<>();


    /**
     * 设置用户信息
     */
    public static void setUser(UserInfo user) {
        USER_HOLDER.set(user);
    }

    /**
     * 获取用户信息（需要类型转换）
     */
    @SuppressWarnings("unchecked")
    public static <T extends UserInfo> T  getUser() {
        return (T) USER_HOLDER.get();
    }

    /**
     * 获取用户ID
     */
    // ========== 快捷获取字段（可选，提高便利性） ==========
    public static Long getUserId() {
        UserInfo user = getUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * 获取租户ID
     */
    // ========== 快捷获取字段（可选，提高便利性） ==========
    public static Long getTenantId() {
        UserInfo user = getUser();
        return user != null ? user.getTenantId() : null;
    }

    /**
     * 获取权限集合
     */
    @SuppressWarnings("unchecked")
    public static Set<String> getPermissions() {
        UserInfo user = USER_HOLDER.get();
        return user != null ? user.getPermissions() : null;
    }

    /**
     * 判断是否已登录
     */
    public static boolean isLogin() {
        return USER_HOLDER.get() != null && USER_HOLDER.get().getUserId() != null;
    }



    // ========== 租户忽略标记 ==========
    /**
     * 设置当前线程是否忽略租户过滤
     */
    public static void setIgnoreTenant(boolean ignore) {
        IGNORE_TENANT_HOLDER.set(ignore);
    }

    /**
     * 获取当前线程是否忽略租户过滤
     */
    public static boolean isIgnoreTenant() {
        return Boolean.TRUE.equals(IGNORE_TENANT_HOLDER.get());
    }

    /**
     * 清除租户忽略标记
     */
    public static void clearIgnoreTenant() {
        IGNORE_TENANT_HOLDER.remove();
    }

    public static void clear() {
        USER_HOLDER.remove();
        IGNORE_TENANT_HOLDER.remove();
    }
}
