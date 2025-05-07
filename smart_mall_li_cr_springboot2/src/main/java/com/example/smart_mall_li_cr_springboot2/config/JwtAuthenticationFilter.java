package com.example.smart_mall_li_cr_springboot2.config;

import com.example.smart_mall_li_cr_springboot2.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器，用于拦截请求并验证JWT令牌
 * 这个过滤器会在每个请求到达控制器之前执行，检查请求头中是否包含有效的JWT令牌。
 * 如果令牌有效，则将用户身份信息设置到Spring Security上下文中，以便后续的控制器和方法可以访问当前用户信息。
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 过滤器核心方法，处理每个HTTP请求的JWT验证逻辑
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param filterChain 过滤器链，用于继续执行后续过滤器或目标资源
     * @throws ServletException 如果处理过程中出现异常
     * @throws IOException 如果处理过程中出现IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 从请求头中获取Authorization字段，通常格式为"Bearer <token>"
        String header = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        // 检查Authorization头是否存在且以"Bearer "开头
        if (header != null && header.startsWith("Bearer ")) {
            // 提取JWT令牌，去掉"Bearer "前缀
            jwtToken = header.substring(7);
            // 从令牌中提取用户名
            username = jwtUtil.extractUsername(jwtToken);
        }

        // 如果成功提取到用户名，且当前Security上下文中没有已认证的用户
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 验证令牌是否有效
            if (jwtUtil.validateToken(jwtToken, username)) {
                // 创建一个认证对象，包含用户名和权限信息（这里简化为无权限）
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, null);
                // 设置认证对象的详细信息，如远程地址等
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 将认证对象设置到Security上下文中，表示当前用户已认证
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // 继续执行过滤器链中的下一个过滤器或目标资源
        filterChain.doFilter(request, response);
    }
}
