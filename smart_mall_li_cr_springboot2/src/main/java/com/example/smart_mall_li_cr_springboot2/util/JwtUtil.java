package com.example.smart_mall_li_cr_springboot2.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类，用于生成和验证JWT令牌
 * JWT（JSON Web Token）是一种基于JSON的开放标准，用于在网络应用间传递信息。
 * 它由三部分组成：Header（头）、Payload（负载）和Signature（签名），用点号（.）分隔。
 * JWT的主要用途是进行身份验证和信息交换。
 */
@Component
public class JwtUtil {

    // 密钥，用于签名和验证JWT令牌，实际项目中应从配置中读取
    private static final String SECRET_KEY = "your-secret-key-here";
    // 令牌有效期，单位为毫秒，这里设置为24小时
    private static final long EXPIRATION_TIME = 86400000;

    /**
     * 生成JWT令牌
     * @param username 用户名，作为令牌的主体
     * @return 生成的JWT令牌字符串
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username); // 主体，即用户名
        claims.put("iat", new Date()); // 发行时间
        claims.put("exp", new Date(System.currentTimeMillis() + EXPIRATION_TIME)); // 过期时间

        return Jwts.builder()
                .setClaims(claims) // 设置负载内容
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY) // 使用HS512算法和密钥进行签名
                .compact(); // 生成令牌字符串
    }

    /**
     * 从JWT令牌中提取用户名
     * @param token JWT令牌字符串
     * @return 用户名，如果令牌无效则返回null
     */
    public String extractUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY) // 设置密钥用于验证签名
                    .parseClaimsJws(token) // 解析令牌
                    .getBody();
            return claims.getSubject(); // 获取主体，即用户名
        } catch (Exception e) {
            return null; // 如果解析失败，返回null
        }
    }

    /**
     * 验证JWT令牌是否有效
     * @param token JWT令牌字符串
     * @param username 用户名，用于验证令牌中的主体是否匹配
     * @return 令牌是否有效
     */
    public boolean validateToken(String token, String username) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            boolean isTokenExpired = claims.getExpiration().before(new Date()); // 检查令牌是否过期
            boolean isUsernameMatch = claims.getSubject().equals(username); // 检查用户名是否匹配
            return !isTokenExpired && isUsernameMatch; // 令牌有效条件：未过期且用户名匹配
        } catch (Exception e) {
            return false; // 如果解析失败，令牌无效
        }
    }
}
