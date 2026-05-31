package com.bwq.framework.core.exception;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * 业务异常
 */
/**
 * @author bwq
 * @date 2026-05-28 10:40:10
 * @description 业务异常
 */

@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }
}
