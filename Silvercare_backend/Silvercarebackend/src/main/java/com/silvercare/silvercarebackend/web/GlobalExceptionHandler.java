package com.silvercare.silvercarebackend.web;

import com.silvercare.silvercarebackend.common.ApiResponse;
import com.silvercare.silvercarebackend.util.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageUtil messageUtil;
    private final HttpServletRequest request;

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String root = Optional.ofNullable(ex.getMostSpecificCause()).map(Throwable::getMessage).orElse(ex.getMessage());
        log.error("DB constraint error: {}", root, ex);

        String key = "error.unexpected";
        if (root != null && root.contains("Duplicate entry")) {
            if (root.contains("username")) key = "auth.duplicate.username";
            else if (root.contains("email")) key = "auth.duplicate.email";
            else key = "auth.duplicate";
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message(messageUtil.getMessage(key, request))
                            .build());
        }
        if (root != null && (root.contains("cannot be null") || root.contains("NULL not allowed"))) {
            key = "validation.field.required";
        } else if (root != null && root.contains("Data too long")) {
            key = "validation.field.toolong";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(messageUtil.getMessage(key, request))
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBad(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(messageUtil.getMessage(ex.getMessage(), request))
                        .build());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleRse(ResponseStatusException ex) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        log.warn("ResponseStatusException: {} - {}", status, ex.getReason());
        return ResponseEntity.status(status)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(messageUtil.getMessage(ex.getReason(), request))
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleOther(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(messageUtil.getMessage("error.unexpected", request))
                        .build());
    }
}
