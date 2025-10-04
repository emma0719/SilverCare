package com.silvercare.silvercarebackend.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String root = Optional.ofNullable(ex.getMostSpecificCause())
                .map(Throwable::getMessage)
                .orElse(ex.getMessage());

        // 把数据库原始报错打出来，方便定位
        log.error("DB constraint error: {}", root, ex);

        // MySQL 常见文案判断
        if (root != null && root.contains("Duplicate entry")) {
            if (root.contains("ux_users_username") || root.contains("for key 'username'"))
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Username already exists"));
            if (root.toLowerCase().contains("email"))
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Email already exists"));
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Duplicate key"));
        }
        if (root != null && (root.contains("cannot be null") || root.contains("NULL not allowed"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", root)); // 例如 “Column 'email' cannot be null”
        }
        if (root != null && root.contains("Data too long")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", root)); // 例如 “Data too long for column 'phone_number'...”
        }

        // 其他约束错误，统一当作 400
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", root != null ? root : "Invalid data"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBad(IllegalArgumentException ex) {
        log.error("Bad request: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOther(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal error"));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleRse(ResponseStatusException ex) {
        // 按 RSE 自带的状态码 & 原因返回（原因就是我们在 service 里通过 messageSource 取的多语言文案）
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        return ResponseEntity.status(status).body(Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", ex.getReason()
        ));
    }
}
