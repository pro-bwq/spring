package com.bwq.framework.core.exception;

import com.bwq.framework.core.response.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLSyntaxErrorException;

/**
 * @author bwq
 * @date 2026-04-16 11:11:48
 * @description 兜底异常处理器（最低优先级）
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    // ========== 系统 BUG 异常 ==========
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NullPointerException.class)
    public ResponseVO<Void> handleNullPointer(NullPointerException e) {
        log.error("空指针异常", e);
        return ResponseVO.failure("系统繁忙，请稍后重试");
    }

    @ExceptionHandler(SQLSyntaxErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseVO<Void> handleSQLSyntaxError(SQLSyntaxErrorException e) {
        log.error("SQL 语法错误", e);
        return ResponseVO.failure("系统繁忙，请稍后重试");
    }


    // ========== 兜底异常 ==========
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseVO<Void> handleException(Exception e) {
        log.error("未捕获的系统异常", e);
        return ResponseVO.failure("系统繁忙，请稍后重试");
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseVO<Void> handleThrowable(Throwable e) {
        log.error("严重错误", e);
        return ResponseVO.failure("系统繁忙，请稍后重试");
    }
}
