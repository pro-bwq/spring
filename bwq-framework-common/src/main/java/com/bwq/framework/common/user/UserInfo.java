package com.bwq.framework.common.user;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

/**
 * @author bwq
 * @date 2026-05-30 01:12:15
 * @description 用户信息实体
 */

@Data
@Builder
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    // ========== 基础认证（必须） ==========
    private Long userId;
    private Long tenantId;
    private Set<String> roles;
    private Set<String> permissions;

    // ========== 公共属性（可选） ==========
    private String userName;
    private String avatarUrl;
    private String email;
    private String mobile;
}
