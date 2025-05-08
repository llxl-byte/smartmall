package com.example.smart_mall_li_cr_springboot2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 启用CORS并禁用CSRF保护
                .cors().and().csrf().disable()

                // 设置会话管理策略为无状态，因为JWT是无状态的身份验证方式
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // 定义授权规则
                .authorizeRequests()

                // 允许预检请求
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()

                // 允许匿名访问的路径，如登录和注册接口
                .antMatchers(
                    "/mallUserLogin",
                    "/mallUserRegister",
                    "/admin/login", // 管理员登录接口
                    "/admin/users/**", // 用户管理接口（临时允许匿名访问，用于测试）
                    "/admin/users/{userId}/status", // 用户状态更新接口（临时允许匿名访问，用于测试）
                    "/admin/items/**", // 商品管理接口（临时允许匿名访问，用于测试）
                    "/admin/orders/**", // 订单管理接口（临时允许匿名访问，用于测试）
                    "/admin/combos/**", // 套餐管理接口（临时允许匿名访问，用于测试）
                    "/selectAll",   // 分类查询接口
                    "/selectByCategoryId", // 商品查询接口
                    "/selectByItemId",     // 商品详情接口
                    "/searchItemByName",   // 商品搜索接口
                    "/hello",              // 测试接口
                    "/api/stats/**",       // 统计数据接口
                    "/doc.html",
                    "/webjars/**",
                    "/swagger-resources/**",
                    "/v2/api-docs"
                ).permitAll()

                // 其他所有请求都需要认证
                .anyRequest().authenticated();

        // 将JWT认证过滤器添加到UsernamePasswordAuthenticationFilter之前
        // 这样JWT过滤器会在Spring Security的默认认证过滤器之前执行
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
