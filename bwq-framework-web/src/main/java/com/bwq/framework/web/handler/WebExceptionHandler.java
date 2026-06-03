package com.bwq.framework.web.handler;

import com.bwq.framework.core.exception.BusinessException;
import com.bwq.framework.core.response.ResponseVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author bwq
 * @date 2026-05-31 12:16:50
 * @description Web 层异常处理器（最高优先级，处理参数校验等）
 */

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebExceptionHandler {

    // ==================== 业务校验异常 ====================
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseVO<Void> handleBusiness(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return ResponseVO.failure(e.getCode(), e.getMessage());
    }

    // ==================== JWT 异常 ====================
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseVO<Void> handleExpiredJwt(ExpiredJwtException e) {
        log.warn("Token 已过期: {}", e.getMessage());
        return ResponseVO.failure(401, "Token已过期，请重新登录");
    }
    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseVO<Void> handleSignature(SignatureException e) {
        log.warn("Token 签名无效: {}", e.getMessage());
        return ResponseVO.failure(401, "Token签名无效");
    }
    @ExceptionHandler(MalformedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseVO<Void> handleMalformedJwt(MalformedJwtException e) {
        log.warn("Token 格式错误: {}", e.getMessage());
        return ResponseVO.failure(401, "Token格式错误");
    }



    // ==================== 参数/校验异常 ====================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseVO<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return ResponseVO.failure(400, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseVO<Void> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return ResponseVO.failure(400, message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseVO<Void> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("参数不合法: {}", e.getMessage());
        return ResponseVO.failure(400,e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseVO<Void> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        String message = String.format("参数 '%s' 类型错误，期望类型: %s",
                e.getName(), e.getRequiredType().getSimpleName());
        log.warn(message);
        return ResponseVO.failure(400, message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseVO<Void> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        String message = String.format("缺少必要参数: %s", e.getParameterName());
        log.warn(message);
        return ResponseVO.failure(400, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseVO<Void> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败: {}", e.getMessage());
        return ResponseVO.failure(400, "请求体格式错误");
    }

    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseVO<Void> handleJsonProcessing(JsonProcessingException e) {
        log.warn("JSON 解析失败: {}", e.getMessage());
        return ResponseVO.failure(400, "JSON解析失败");
    }

    // ==================== 请求方法/媒体类型异常 ====================

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseVO<Void> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        String message = String.format("请求方法 '%s' 不支持", e.getMethod());
        log.warn(message);
        return ResponseVO.failure(405, message);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseVO<Void> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        String message = String.format("Content-Type '%s' 不支持", e.getContentType());
        log.warn(message);
        return ResponseVO.failure(415, message);
    }

    // ==================== 资源异常 ====================

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseVO<Void> handleNoHandlerFound(Exception e) {
        log.warn("接口不存在: {}", e.getMessage());
        return ResponseVO.failure(404, "接口不存在");
    }

    // ==================== 文件上传异常 ====================

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseVO<Void> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        log.warn("文件大小超限: {}", e.getMessage());
        return ResponseVO.failure(400, "文件大小超过限制");
    }

    // ==================== 数据库异常 ====================

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseVO<Void> handleDuplicateKey(DuplicateKeyException e) {
        log.warn("数据重复: {}", e.getMessage());
        String message = extractDuplicateMessage(e.getMessage());
        return ResponseVO.failure(409, message);
    }


    // ==================== 第三方组件异常 ====================

//    @ExceptionHandler(com.alibaba.excel.exception.ExcelGenerateException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ResponseVO<Void> handleExcelGenerate(com.alibaba.excel.exception.ExcelGenerateException e) {
//        log.error("Excel 生成失败", e);
//        return ResponseVO.error(500, "Excel导出失败");
//    }

    // ==================== Servlet 异常 ====================
    @ExceptionHandler(ServletException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseVO<Void> handleServlet(ServletException e) {
        log.error("Servlet 异常", e);
        return ResponseVO.failure("请求处理失败");
    }

    // ==================== 异步异常 ====================

    @ExceptionHandler(CompletionException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseVO<Void> handleCompletion(CompletionException e) {
        Throwable cause = e.getCause();
        if (cause instanceof BusinessException) {
            BusinessException be = (BusinessException) cause;
            return ResponseVO.failure(be.getCode(), be.getMessage());
        }
        if (cause instanceof DuplicateKeyException) {
            return handleDuplicateKey((DuplicateKeyException) cause);
        }
        log.error("异步任务异常", e);
        return ResponseVO.failure("系统繁忙，请稍后重试");
    }

//    @ExceptionHandler(MybatisPlusException.class)
//    public ResponseVO handleException(MybatisPlusException e) {
//        log.warn("MyBatis-Plus 异常: {}", e.getMessage());
//        String errorMsg = e.getMessage();
//        return ResponseVO.failure("数据库操作失败");
//    }


    // =============================================

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseVO<Void> handleTypeMismatch(TypeMismatchException e) {
        log.warn("====TypeMismatchException====" + e.getMessage());
        return ResponseVO.failure(400, "参数类型错误");
    }
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public ResponseVO<Void> handleSQLSyntaxError(SQLSyntaxErrorException e) {
        String errorMsg = e.getMessage();
        log.warn("====SQLSyntaxErrorException====" + errorMsg);
        return ResponseVO.failure(400, "JSON解析失败");
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseVO<Void> handleBind(BindException e) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定失败: {}", message);
        return ResponseVO.failure(400, message);
    }


    // ========== 文件上传异常 ==========
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseVO<Void> handleUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.warn("==========MaxUploadSizeExceededException:{}", e.getMessage());
        String errorMsg = e.getMessage();
        return ResponseVO.failure(400, "文件大小超过限制");
    }


    // ========== 第三方组件异常 ==========
    //    @ExceptionHandler(ExcelGenerateException.class)
//    public ResponseVO<Void> handleException(ExcelGenerateException e) {
//        log.error("Excel 生成失败", e);
//        String errorMsg = e.getMessage();
//        return ResponseVO.failure("Excel导出失败");
//    }

    // ==================== 辅助方法 ====================
    /**
     * 从 DuplicateKeyException 中提取友好的提示信息
     */
    private String extractDuplicateMessage(String errorMessage) {
        if (errorMessage == null) {
            return "数据已存在";
        }

        // MySQL 格式
        Pattern mysqlPattern = Pattern.compile("Duplicate entry '([^']+)' for key '([^']+)'");
        Matcher mysqlMatcher = mysqlPattern.matcher(errorMessage);
        if (mysqlMatcher.find()) {
            String value = mysqlMatcher.group(1);
            String key = mysqlMatcher.group(2);
            String fieldName = convertKeyToFieldName(key);
            return fieldName + " '" + value + "' 已存在";
        }

        // PostgreSQL 格式
        Pattern pgPattern = Pattern.compile("duplicate key value violates unique constraint \"([^\"]+)\"");
        Matcher pgMatcher = pgPattern.matcher(errorMessage);
        if (pgMatcher.find()) {
            String key = pgMatcher.group(1);
            String fieldName = convertKeyToFieldName(key);
            return fieldName + " 已存在";
        }
        return "数据已存在，请勿重复添加";
    }


    /**
     * 将数据库约束名转换为用户友好的字段名
     */
    private String convertKeyToFieldName(String key) {
        if (key == null) {
            return "数据";
        }

        // 预定义映射
        Map<String, String> mapping = new HashMap<>();
        mapping.put("uk_user_email", "邮箱");
        mapping.put("uk_user_phone", "手机号");
        mapping.put("uk_user_name", "用户名");
        mapping.put("uk_order_no", "订单号");
        // 可以根据业务需要扩展

        if (mapping.containsKey(key)) {
            return mapping.get(key);
        }

        // 通用转换：uk_user_name -> 用户名
        String converted = key
                .replace("uk_", "")
                .replace("_", " ");
        return converted.substring(0, 1).toUpperCase() + converted.substring(1);
    }

}
