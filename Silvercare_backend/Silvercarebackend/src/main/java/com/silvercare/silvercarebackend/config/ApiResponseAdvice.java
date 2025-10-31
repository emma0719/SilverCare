package com.silvercare.silvercarebackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvercare.silvercarebackend.common.ApiResponse;
import com.silvercare.silvercarebackend.util.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Component
@RestControllerAdvice
@RequiredArgsConstructor
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;
    private final MessageUtil messageUtil;
    private final HttpServletRequest httpServletRequest;

    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return !returnType.getParameterType().equals(ApiResponse.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {
        if (body instanceof ApiResponse<?>) {
            return body;
        }
        if (body instanceof String) {
            try {
                String localizedMessage = messageUtil.getMessage((String) body, httpServletRequest);
                return objectMapper.writeValueAsString(ApiResponse.builder()
                        .success(true)
                        .message(localizedMessage)
                        .data(null)
                        .build());
            } catch (Exception e) {
                return body;
            }
        }
        return ApiResponse.builder()
                .success(true)
                .message(null)
                .data(body)
                .build();
    }
}
