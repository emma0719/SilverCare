package com.silvercare.silvercarebackend.config;

import com.silvercare.silvercarebackend.config.JwtUtil;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; // 改用 JwtUtil

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {

        // 已认证则跳过
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        String username;
        String role = null;
        try {
            username = jwtUtil.extractUsername(token);
            role = jwtUtil.extractRole(token); // 🔑 从token中提取role
        } catch (Exception e) {
            log.debug("JWT parse failed: {}", e.getMessage());
            chain.doFilter(request, response);
            return;
        }

        if (username != null && jwtUtil.validateToken(token, username)) {
            // 🔑 空角色兜底：没有role时给空权限（仅认证，无权限）
            List<GrantedAuthority> authorities =
                    (role != null && !role.isBlank())
                            ? List.of(new SimpleGrantedAuthority(role))  // "ADMIN"等，不加ROLE_前缀
                            : List.of();

            log.debug("[JWT] user={}, role={}, authorities={}", username, role, authorities); // 🔍 临时调试日志

            User principal = new User(username, "", authorities);
            var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}