package com.silvercare.silvercarebackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // ⬅️ 新增：注入你的 JWT 过滤器

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS
                .cors(cors -> cors.configurationSource(req -> {
                    CorsConfiguration c = new CorsConfiguration();
                    c.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:8082","http://localhost:8083"));
                    c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
                    c.setAllowedHeaders(List.of("Authorization","Content-Type"));
                    c.setAllowCredentials(true);
                    return c;
                }))
                // 前后端分离一般关 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 无状态会话（不创建 JSESSIONID）
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 鉴权规则
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()   // 登录/注册开放
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()                  // 其他接口需要登录
                )
                // 禁用表单 & Basic
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // 把 JWT 过滤器加到用户名密码过滤器之前
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
