package com.bwq.framework.web.config;

import com.bwq.framework.core.response.ResponseVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author bwq
 * @date 2026-05-31 12:16:12
 * @description 统一响应包装
 */

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        String className = returnType.getDeclaringClass().getName();
        if (className.contains("springdoc") || className.contains("knife4j")) {
            return false;
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        // 已经是 ResponseVO，直接返回
        if (body instanceof ResponseVO) {
            return body;
        }

        // 字符串类型特殊处理（否则会报类型转换错误）
        if (body instanceof String) {
            try {
                return objectMapper.writeValueAsString(ResponseVO.success(body));
            } catch (JsonProcessingException e) {
                log.error("响应包装失败", e);
                return ResponseVO.failure("系统错误");
            }
        }
        // 其他类型包装成 ResponseVO
        return ResponseVO.success(body);
    }
}
