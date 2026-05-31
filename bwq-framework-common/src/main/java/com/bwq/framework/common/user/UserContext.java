package com.bwq.framework.common.user;

import java.util.Set;

/**
 * @author bwq
 * @date 2026-05-30 01:11:40
 * @description ThreadLocal 存储用户信息
 */

public class UserContext {

    private static final ThreadLocal<UserInfo> USER_HOLDER = new ThreadLocal<>();

    public static void setUser(UserInfo user) {
        USER_HOLDER.set(user);
    }

    public static UserInfo getUser() {
        return USER_HOLDER.get();
    }

    public static Long getUserId() {
        UserInfo user = getUser();
        return user != null ? user.getUserId() : null;
    }

    public static Long getTenantId() {
        UserInfo user = getUser();
        return user != null ? user.getTenantId() : null;
    }

    public static Set<String> getPermissions() {
        UserInfo user = getUser();
        return user != null ? user.getPermissions() : null;
    }

    public static void clear() {
        USER_HOLDER.remove();
    }
}
