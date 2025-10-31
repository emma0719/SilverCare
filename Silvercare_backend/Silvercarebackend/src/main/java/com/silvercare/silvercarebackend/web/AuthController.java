package com.silvercare.silvercarebackend.web;

import com.silvercare.silvercarebackend.dto.AuthRequest;
import com.silvercare.silvercarebackend.dto.RegisterRequest;
import com.silvercare.silvercarebackend.dto.AuthResponse;
import com.silvercare.silvercarebackend.service.AuthService;
import com.silvercare.silvercarebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    // ------------------- 当前用户 -------------------
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        String username = authentication.getName();
        var userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        var user = userOpt.get();

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "fullName", user.getFullName(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "languagePreference", user.getLanguagePreference(),
                "country", user.getCountry(),
                "city", user.getCity(),
                "theme", user.getTheme(),
                "preferredUnits", user.getPreferredUnits()
        ));
    }

    // ------------------- 登录 -------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        AuthResponse res = authService.login(req);
        return ResponseEntity.ok(res);
    }

    // ------------------- 注册 -------------------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        AuthResponse res = authService.register(req);
        return ResponseEntity.ok(res);
    }

    // ------------------- 国际化测试 -------------------
    @GetMapping("/test-i18n")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> testI18n(@RequestParam String key, java.util.Locale locale) {
        String message = java.util.ResourceBundle
                .getBundle("messages", locale)
                .getString(key);
        return ResponseEntity.ok(Map.of("message", message));
    }

}
