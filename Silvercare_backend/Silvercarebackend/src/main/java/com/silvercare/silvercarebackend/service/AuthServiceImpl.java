package com.silvercare.silvercarebackend.service;

import com.silvercare.silvercarebackend.domain.User;
import com.silvercare.silvercarebackend.dto.AuthRequest;
import com.silvercare.silvercarebackend.dto.AuthResponse;
import com.silvercare.silvercarebackend.dto.RegisterRequest;
import com.silvercare.silvercarebackend.repository.UserRepository;
import com.silvercare.silvercarebackend.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String username = trim(request.getUsername());
        String email = trim(request.getEmail());
        String rawPassword = request.getPassword();
        final String fullName = (request.getFullName() != null && !request.getFullName().isBlank())
                ? request.getFullName().trim()
                : request.getUsername().trim();

        log.info("Register attempt: username={}, email={}", username, email);

        if (username == null || username.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username required");
        if (rawPassword == null || rawPassword.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password required");
        if (email == null || email.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email required");

        if (request.getRole() == null) request.setRole(User.Role.FAMILY);
        if (request.getLanguagePreference() == null || request.getLanguagePreference().isBlank())
            request.setLanguagePreference("en");

        User user = User.builder()
                .username(request.getUsername().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(fullName)
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .languagePreference(request.getLanguagePreference())
                .active(true)
                .build();

        try {
            User saved = userRepository.saveAndFlush(user);
            log.info("User created: id={}, username={}", saved.getId(), saved.getUsername());
            String token = jwtUtil.generateToken(saved.getUsername());
            return new AuthResponse(token, saved.getUsername(), saved.getRole().name());
        } catch (DataIntegrityViolationException e) {
            // 命中唯一索引(username/email)才会到这里
            log.warn("Duplicate username/email: username={}, email={}", username, email, e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate username or email");
        }
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        String username = trim(request.getUsername());
        log.info("Login attempt: username={}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }

    private static String trim(String s) { return s == null ? null : s.trim(); }
}
