package com.example.smart_mall_li_cr_springboot2.pojo;

import lombok.Data;

/**
 * 管理员登录请求参数
 */
@Data
public class AdminLoginParam {
    private String username;
    private String password;
}