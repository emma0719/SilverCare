package com.silvercare.silvercarebackend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtService {

    private final Key key;

    public JwtService(@Value("${jwt.secret}") String secret) {
        // 尝试按 Base64 解；若失败则按普通字符串使用
        Key k;
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            k = Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException | DecodingException e) {
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            if (keyBytes.length < 32) {
                throw new IllegalStateException("jwt.secret must be at least 32 bytes");
            }
            k = Keys.hmacShaKeyFor(keyBytes);
        }
        this.key = k;
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return parse(token).getSubject(); // "sub"
    }

    public Collection<SimpleGrantedAuthority> extractAuthorities(String token) {
        Claims c = parse(token);
        Object claim = Optional.ofNullable(c.get("authorities")).orElse(c.get("roles"));
        if (claim == null) return List.of();

        if (claim instanceof Collection<?> col) {
            return col.stream().map(Object::toString).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }
        return Arrays.stream(claim.toString().split(","))
                .map(String::trim).filter(s -> !s.isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    public String buildToken(String username,
                             Collection<? extends org.springframework.security.core.GrantedAuthority> authorities,
                             long expireMinutes) {
        var claims = new java.util.HashMap<String, Object>();
        // 统一写到 "authorities"（JwtService.extractAuthorities 已兼容多字段）
        var roles = authorities == null ? java.util.List.of() :
                authorities.stream().map(a -> a.getAuthority()).collect(Collectors.toList());
        claims.put("authorities", roles);

        var now = new java.util.Date();
        var exp = java.util.Date.from(Instant.now().plus(expireMinutes, ChronoUnit.MINUTES));

        return io.jsonwebtoken.Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();
    }
}
