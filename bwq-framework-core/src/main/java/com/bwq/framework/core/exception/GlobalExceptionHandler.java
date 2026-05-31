package com.bwq.framework.core.exception;

import com.bwq.framework.core.response.ResultCode;
import com.bwq.framework.core.response.ResponseVO;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.sql.SQLSyntaxErrorException;
import java.util.concurrent.CompletionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bwq
 * @date 2026-04-16 11:11:48
 * @description 兜底异常处理器（最低优先级）
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseVO<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return ResponseVO.failure(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseVO<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return ResponseVO.failure("系统繁忙，请稍后重试");
    }

    //    @ExceptionHandler(DuplicateKeyException.class)
//    public ResponseVO<Void> handleException(DuplicateKeyException e) {
//        e.printStackTrace();
//        log.warn("==========DuplicateKeyException:{}", e.getMessage());
//        String errorMsg = e.getMessage();
//        return ResponseVO.failure(ResultCode.SQL_ERROR, errorMsg);
//    }

    //    @ExceptionHandler(MybatisPlusException.class)
//    public ResponseVO handleException(MybatisPlusException e) {
//        log.warn("==========MybatisPlusException:{}", e.getMessage(), e);
//        String errorMsg = e.getMessage();
//        return ResponseVO.failure(HttpCodeEnum.BUSINESS_ERROR , errorMsg);
//    }


//    @ExceptionHandler(ExcelGenerateException.class)
//    public ResponseVO<Void> handleException(ExcelGenerateException e) {
//        log.warn("==========ExcelGenerateException:{}", e.getMessage(), e);
//        String errorMsg = e.getMessage();
//        return ResponseVO.failure(ResultCode.BUSINESS_ERROR , errorMsg);
//    }

    /**
     * ServletException
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServletException.class)
    public ResponseVO<Void> processException(ServletException e) {
        log.warn("===ServletException==={}", e.getMessage());
        return ResponseVO.failure(ResultCode.SERVLET_ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseVO<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("===========非法参数异常，异常原因：{}", e.getMessage());
        return ResponseVO.failure(ResultCode.BUSINESS_ERROR,e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseVO<Void> handleJsonProcessingException(JsonProcessingException e) {
        log.warn("============Json转换异常，异常原因：{}", e.getMessage());
        return ResponseVO.failure(ResultCode.JSON_ERROR.getValue(), e.getMessage());
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseVO<Void> processException(TypeMismatchException e) {
        log.warn("====TypeMismatchException====" + e.getMessage());
        return ResponseVO.failure(ResultCode.TYPE_MATCH_ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public ResponseVO<Void> processSQLSyntaxErrorException(SQLSyntaxErrorException e) {
        String errorMsg = e.getMessage();
        log.warn("====SQLSyntaxErrorException====" + errorMsg);
        return ResponseVO.failure(ResultCode.SQL_ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CompletionException.class)
    public ResponseVO<Void> processException(CompletionException e) {
        log.warn("=======CompletionException异常:{}", e.getMessage());
        return ResponseVO.failure(ResultCode.ASYNC_TASK_ERROR, e.getMessage());
    }


    /**
     * 自定义Feign异常 ，目前无效，未在FeignConfig中申明 ErrorDecoder
     * 因为采用了FeignException.BadRequest
     */
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(CustomFeignException.class)
//    public ResponseVO<Void> handleBusinessException(CustomFeignException e) {
//        log.warn("==========CustomFeignException:{}", e.getMessage());
//        return ResponseVO.failure(HttpCodeEnum.BUSINESS_ERROR, e.getMessage());
//    }
    /**
     * 处理Feign异常，如果想自定义异常，可在FeignConfig中申明 ErrorDecoder
     */
//    @ExceptionHandler(FeignException.BadRequest.class)
//    public ResponseVO<Void> processException(FeignException.BadRequest e) {
//        try{
//            ByteBuffer byteBuffer = e.responseBody().get();
//            Charset charset = StandardCharsets.UTF_8;
//            String s = charset.decode(byteBuffer).toString();
//            log.warn("======FeignException.BadRequest==s:{}", s);
//            return JsonUtil.jsonCovertToObject(s, ResponseVO.class);
//
//        }catch (Exception ex){
//            log.warn("===FeignException.BadRequest==ex:{}",ex.getMessage());
//        }
//        log.warn("======FeignException.BadRequest==微服务feign调用异常:{}", e.getMessage());
//        return ResponseVO.failure(HttpCodeEnum.FEIGN_ERROR, e.getMessage());
//    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseVO<Void> handleUploadSizeExceededException(MaxUploadSizeExceededException e) {
        e.printStackTrace();
        log.warn("==========MaxUploadSizeExceededException:{}", e.getMessage());
        String errorMsg = e.getMessage();
        return ResponseVO.failure(ResultCode.FILE_SIZE_EXCEED, errorMsg);
    }

    /**
     * 传参错误时，用于消息转换
     *
     * @param throwable 异常
     * @return 错误信息
     */
    private String convertMessage(Throwable throwable) {
        String error = throwable.toString();
        String regulation = "\\[\"(.*?)\"]+";
        Pattern pattern = Pattern.compile(regulation);
        Matcher matcher = pattern.matcher(error);
        String group = "";
        if (matcher.find()) {
            String matchString = matcher.group();
            matchString = matchString.replace("[", "").replace("]", "");
            matchString = matchString.replaceAll("\\\"", "") + "字段有误";
            group += matchString;
        }
        return group;
    }
}
