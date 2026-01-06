package com.ad.management.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    
    private Key key;
    private final long defaultExpiration = 3600000; // 1小时
    private final long rememberMeExpiration = 604800000; // 7天 (7 * 24 * 60 * 60 * 1000)
    
    public JwtUtil(@Value("${jwt.secret:mySecretKeyForDevelopmentOnlyDoNotUseInProduction}") String secret) {
        // 在生产环境中，应该从安全的地方获取密钥，比如环境变量或配置服务器
        byte[] decodedKey = Base64.getDecoder().decode(Base64.getEncoder().encodeToString(secret.getBytes()));
        this.key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }
    
    public String generateToken(String username, boolean rememberMe) {
        Date now = new Date();
        long expirationTime = rememberMe ? rememberMeExpiration : defaultExpiration;
        Date expiryDate = new Date(now.getTime() + expirationTime);
        
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }
    
    public String generateToken(String username) {
        return generateToken(username, false); // 默认不记住我
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public Long getDefaultExpiration() {
        return defaultExpiration;
    }
    
    public Long getRememberMeExpiration() {
        return rememberMeExpiration;
    }
}