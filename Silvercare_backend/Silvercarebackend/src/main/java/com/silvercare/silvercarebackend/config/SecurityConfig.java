package com.silvercare.silvercarebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 关闭 CSRF（前后端分离时一般关闭）
                .csrf(AbstractHttpConfigurer::disable)

                // 允许跨域（默认配置，后续可细化）
                .cors(cors -> { })

                // 放行特定接口
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  // 登录注册接口
                        .requestMatchers("/api/sms/**").permitAll()   // 短信接口
                        .anyRequest().permitAll()                     // 其他请求也全部放行（开发环境）
                )

                // 禁用 session，避免自动生成 JSESSIONID
                .sessionManagement(session -> session.disable())

                // 禁用默认表单登录 & Basic Auth
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
