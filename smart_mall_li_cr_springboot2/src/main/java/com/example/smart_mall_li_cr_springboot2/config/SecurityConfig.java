package com.example.smart_mall_li_cr_springboot2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF保护，因为我们使用的是JWT令牌，不需要CSRF保护
                .csrf().disable()
                // 设置会话管理策略为无状态，因为JWT是无状态的身份验证方式
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 定义授权规则
                .authorizeRequests()
                // 允许匿名访问的路径，如登录和注册接口
                .antMatchers("/mallUserLogin", "/mallUserRegister", "/doc.html", "/webjars/**", "/swagger-resources/**", "/v2/api-docs").permitAll()
                // 其他所有请求都需要认证
                .anyRequest().authenticated();

        // 将JWT认证过滤器添加到UsernamePasswordAuthenticationFilter之前
        // 这样JWT过滤器会在Spring Security的默认认证过滤器之前执行
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
