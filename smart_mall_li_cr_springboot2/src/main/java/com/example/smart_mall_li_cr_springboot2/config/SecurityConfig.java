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
                    "/updateUserInfo",
                    "/uploadAvatar",
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
                    "/v2/api-docs",
                    "/behavior/count",      // 用户行为数量接口
                    "/behavior/items",      // 用户行为商品接口
                    "/behavior/record",     // 记录用户行为接口
                    "/behavior/user",       // 用户行为列表接口
                    "/behavior/item",       // 商品行为列表接口
                    "/order/count",         // 订单数量接口
                    "/coupon/count",        // 优惠券数量接口
                    "/address/list",        // 收货地址列表接口
                    "/address/add",         // 添加收货地址接口
                    "/address/update",      // 更新收货地址接口
                    "/address/delete",      // 删除收货地址接口
                    "/orders",              // 订单列表接口
                    "/addMallOrder",        // 添加订单接口
                    "/createFullOrder",     // 创建完整订单接口
                    "/chat/tags",           // 聊天标签接口
                    "/chat/history/**",     // 聊天历史接口
                    "/chat/faqs",           // 常见问题接口
                    "/chat/send",           // 发送聊天消息接口
                    "/selectByUserId",      // 购物车查询接口
                    "/addCart",             // 添加购物车接口
                    "/updateCart",          // 更新购物车接口
                    "/deleteCart",          // 删除购物车接口
                    "/batchUpdateCartSelected", // 批量更新购物车选中状态接口
                    "/api/coupons/**",      // 优惠券相关接口
                    "/recommend/**",        // 推荐接口
                    "/comment/**"           // 评论接口
                ).permitAll()

                // 其他所有请求都需要认证
                .anyRequest().authenticated();

        // 将JWT认证过滤器添加到UsernamePasswordAuthenticationFilter之前
        // 这样JWT过滤器会在Spring Security的默认认证过滤器之前执行
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
