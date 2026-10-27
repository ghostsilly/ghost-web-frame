package com.ghost.frame.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 * @author 34592
 */
@Component
public class JwtUtil {
    /** 访问令牌 */
    public static final String ACCESS = "access";
    
    /** 刷新令牌 */
    public static final String REFRESH = "refresh";
    
    /** 令牌类型 */
    public static final String TOKEN_TYPE = "tokenType";

    /** 密钥 */
    private final SecretKey key;
    
    /** 过期时间 */
    private final long expirationMs;
    
    /** 刷新令牌过期时间 */
    private final long refreshExpirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs,
            @Value("${jwt.refresh-expiration-ms}") long refreshExpirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
        
    }

    /**
     * 签发：生成 access token
     * @param userId 用户id
     * @param username 用户名
     * @return access token
     */
    public String generateAccessToken(Long userId, String username) {
        return buildToken(userId, username, ACCESS, expirationMs);
    }

    /**
     * 签发：生成 refresh token
     * @param userId 用户id
     * @param username 用户名
     * @return refresh token
     */
    public String generateRefreshToken(Long userId, String username) {
        return buildToken(userId, username, REFRESH, refreshExpirationMs);
    }

    /**
     * 解析token获取userId
     * @param token 令牌
     * @return userId
     */
    public Long getUserId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    /**
     * 解析token获取username
     * @param token 令牌
     * @return username
     */
    public String getUsername(String token) {
        return parseClaims(token).get("username", String.class);
    }

    /**
     * 判断是否是 access token
     * @param token access token 令牌
     * @return 是否是 access token
     */
    public boolean isAccessToken(String token){
        return matchesType(token, ACCESS);
    }
    
    /**
     * 判断是否是 refresh token
     * @param token refresh token 令牌
     * @return 是否是 refresh token
     */
    public boolean isRefreshToken(String token){
        return matchesType(token, REFRESH);
    }

    /**
     * 解析token
     * @param token 令牌
     * @return Claims
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * 构建token
     * @param userId 用户id
     * @param username 用户名
     * @param tokenType 令牌类型
     * @param time 过期时间
     * @return token
     */
    private String buildToken(Long userId, String username, String tokenType, long time){
        return Jwts.builder()
            .subject(String.valueOf(userId))
            .claim("username", username)
            .claim(TOKEN_TYPE, tokenType)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + time))
            .signWith(key)
            .compact();
    }
    
    /**
     * 判断令牌类型
     * @param token 令牌
     * @param tokenType 令牌类型
     * @return 是否匹配
     */
    private boolean matchesType(String token, String tokenType){
        try {
            String actual = parseClaims(token).get(TOKEN_TYPE, String.class);
            return tokenType.equals(actual);
        }catch (Exception exception){
            return false;
        }
    }
}
