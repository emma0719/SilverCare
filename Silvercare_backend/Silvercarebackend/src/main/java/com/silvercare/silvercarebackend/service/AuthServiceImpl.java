package com.silvercare.silvercarebackend.service;

import com.silvercare.silvercarebackend.config.JwtUtil;
import com.silvercare.silvercarebackend.domain.User;
import com.silvercare.silvercarebackend.dto.AuthRequest;
import com.silvercare.silvercarebackend.dto.AuthResponse;
import com.silvercare.silvercarebackend.dto.RegisterRequest;
import com.silvercare.silvercarebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String username = trim(request.getUsername());
        String email = trim(request.getEmail());
        String rawPassword = request.getPassword();
        String lang = (request.getLanguagePreference() != null && !request.getLanguagePreference().isBlank())
                ? request.getLanguagePreference().trim().toLowerCase()
                : "en";
        Locale locale = Locale.forLanguageTag(lang);

        final String fullName = (request.getFullName() != null && !request.getFullName().isBlank())
                ? request.getFullName().trim()
                : (username != null ? username : "");

        log.info("[REG] attempt username={}, email={}, lang={}", username, email, lang);

        if (username == null || username.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg("auth.username.required", locale));
        if (rawPassword == null || rawPassword.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg("auth.password.required", locale));
        if (email == null || email.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg("auth.email.required", locale));

        if (request.getRole() == null) request.setRole(User.Role.FAMILY);

        log.info("[REG] building user entity…");
        Boolean notify = request.getNotifyEmail() != null ? request.getNotifyEmail() : Boolean.TRUE;

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .email(email)
                .fullName(fullName)
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .languagePreference(lang)
                // 这些是兜底，确保永远不写 NULL
                .active(Boolean.TRUE)
                .notifyEmail(Boolean.TRUE)
                .notifySms(Boolean.FALSE)
                .notifyPush(Boolean.TRUE)
                .twoFactorEnabled(Boolean.FALSE)
                .preferredUnits("METRIC")
                .theme("LIGHT")
                .build();

        try {
            log.info("[REG] saving user…");
            User saved = userRepository.saveAndFlush(user);
            log.info("[REG] saved id={}, generating jwt…", saved.getId());

            String token = jwtUtil.generateToken(saved.getUsername()); // 若这里 NPE/IAE，会在日志里看到卡在这行
            log.info("[REG] jwt ok, returning");

            return new AuthResponse(
                    token,
                    saved.getUsername(),
                    saved.getRole().name(),
                    msg("auth.success", locale),
                    saved.getLanguagePreference()
            );
        } catch (DataIntegrityViolationException e) {
            // 把最具体的数据库错误拿出来（MySQL 会包含 Duplicate entry / cannot be null / Data too long...）
            String root = (e.getMostSpecificCause() != null) ? e.getMostSpecificCause().getMessage() : e.getMessage();
            log.warn("[REG] data integrity violation: {}", root, e);

            // 只有出现“Duplicate entry”才判定为重复（409）
            if (root != null && root.contains("Duplicate entry")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, msg("auth.duplicate", locale));
            }

            // 其它约束错误（NOT NULL、长度、外键等）统一返回 400，并带上简短提示，方便你定位列名
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    msg("error.db.constraint", locale) + (root != null ? (": " + root) : "")
            );

        }
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        String username = trim(request.getUsername());
        String rawPassword = request.getPassword();
        log.info("Login attempt: username={}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, msg("auth.invalid", Locale.ENGLISH)));

        String lang = (user.getLanguagePreference() == null || user.getLanguagePreference().isBlank())
                ? "en" : user.getLanguagePreference();
        Locale locale = Locale.forLanguageTag(lang);

        if (rawPassword == null || rawPassword.isBlank() || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, msg("auth.invalid", locale));
        }

        user.setLastLoginAt(OffsetDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRole().name(),
                msg("auth.login.success", locale),
                lang
        );
    }

    private static String trim(String s) { return s == null ? null : s.trim(); }

    // ⬇️ 关键：带兜底，缺 key 不再 500
    private String msg(String code, Locale locale) {
        try {
            return messageSource.getMessage(code, null, locale);
        } catch (NoSuchMessageException e) {
            log.warn("Missing i18n key: {} (locale={})", code, locale);
            return code;
        }
    }
}
