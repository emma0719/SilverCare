package com.silvercare.silvercarebackend.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expireMillis;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expire-minutes:120}") long expireMinutes
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("jwt.secret must not be blank");
        }

        byte[] keyBytes;
        try {
            // 尝试按 Base64 解析；解析失败（任意异常）则回退到纯文本
            keyBytes = Decoders.BASE64.decode(secret);
            if (keyBytes.length < 32) {
                throw new IllegalArgumentException("jwt.secret (Base64 decoded) must be at least 32 bytes");
            }
        } catch (Exception ignore) {  // 这里从 IllegalArgumentException 改为 Exception
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            if (keyBytes.length < 32) {
                throw new IllegalArgumentException("jwt.secret must be at least 32 bytes (UTF-8)");
            }
        }

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expireMillis = Math.max(1, expireMinutes) * 60_000L;
    }

    public String generateToken(String username) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username) // 0.11.x API
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expireMillis))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    public boolean validateToken(String token, String expectedUsername) {
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String subject = claims.getSubject();
            Date exp = claims.getExpiration();
            return subject != null
                    && subject.equals(expectedUsername)
                    && exp != null
                    && exp.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
