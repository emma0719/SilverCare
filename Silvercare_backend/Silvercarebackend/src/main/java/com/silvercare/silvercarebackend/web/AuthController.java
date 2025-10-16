package com.silvercare.silvercarebackend.web;

import com.silvercare.silvercarebackend.config.JwtUtil;
import com.silvercare.silvercarebackend.domain.User;
import com.silvercare.silvercarebackend.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Value("${jwt.expire-minutes:120}")
    private long expireMinutes;
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
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        var authorities = auth.getAuthorities();
        String role = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();

        // 生成 JWT
        String token = jwtUtil.generateToken(auth.getName(), role);

        // 更新最后登录时间
        userRepository.findByUsername(auth.getName()).ifPresent(user -> {
            user.setLastLoginAt(OffsetDateTime.now());
            userRepository.save(user);
        });

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", auth.getName(),
                "role", role,
                "message", "auth.success"
        ));
    }

    // ------------------- 注册 -------------------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterReq req) {
        // 检查用户名是否重复
        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username already exists"));
        }

        // 检查邮箱是否重复
        if (req.getEmail() != null && userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already registered"));
        }

        // 加密密码
        String encodedPassword = passwordEncoder.encode(req.getPassword());

        // 构建用户
        User newUser = User.builder()
                .username(req.getUsername())
                .password(encodedPassword)
                .fullName(req.getFullName() != null ? req.getFullName() : req.getUsername())
                .email(req.getEmail())
                .phoneNumber(req.getPhoneNumber())
                .role(User.Role.fromString(req.getRole()))
                .languagePreference(req.getLanguagePreference() != null ? req.getLanguagePreference() : "en")
                .country(req.getCountry())
                .city(req.getCity())
                .createdAt(OffsetDateTime.now())
                .build();

        userRepository.save(newUser);

        // 生成 token
        String token = jwtUtil.generateToken(req.getUsername(), req.getRole());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", req.getUsername(),
                "role", req.getRole(),
                "message", "auth.success",
                "preferredLanguage", newUser.getLanguagePreference()
        ));
    }

    // ------------------- 请求体 -------------------
    @Data
    public static class LoginReq {
        private String username;
        private String password;
    }

    @Data
    public static class RegisterReq {
        private String username;
        private String password;
        private String fullName;
        private String email;
        private String phoneNumber;
        private String role;
        private String languagePreference;
        private String country;
        private String city;
    }
}
