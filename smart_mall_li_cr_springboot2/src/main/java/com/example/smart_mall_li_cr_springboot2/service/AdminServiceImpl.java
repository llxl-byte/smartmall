package com.example.smart_mall_li_cr_springboot2.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.smart_mall_li_cr_springboot2.mapper.AdminMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.Admin;
import org.springframework.stereotype.Service;

/**
 * 管理员 Service 实现类
 */
@Service // 标记为 Spring Service 组件
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    // ServiceImpl 基类会自动注入 AdminMapper，可以直接使用 this.baseMapper 访问

    /**
     * 管理员登录校验
     * @param username 用户名
     * @param password 密码 (明文，后续需要改为加密比较)
     * @return 登录成功返回 Admin 对象，失败返回 null
     */
    @Override
    public Admin login(String username, String password) {
        System.out.println("Attempting login for username: " + username);

        // 1. 根据用户名查询管理员 (不区分大小写)
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        // 使用数据库的 LOWER 函数进行不区分大小写的比较
        queryWrapper.apply("LOWER(username) = LOWER({0})", username);
        Admin admin = this.baseMapper.selectOne(queryWrapper);

        // 2. 判断管理员是否存在
        if (admin == null) {
            System.out.println("Login failed: User '" + username + "' not found.");
            return null;
        }
        System.out.println("User found: " + admin.getUsername()); // 打印从数据库查到的实际用户名

        // 3. 比较密码 (去除前后空格)
        String storedPassword = admin.getPassword();
        System.out.println("Stored password: '" + storedPassword + "'"); // 打印数据库密码
        System.out.println("Provided password: '" + password + "'"); // 打印传入密码

        if (password == null || storedPassword == null) {
             System.out.println("Login failed: Provided or stored password is null.");
             return null;
        }

        if (!password.trim().equals(storedPassword.trim())) {
            System.out.println("Login failed: Password mismatch for user '" + username + "'.");
            return null;
        }

        // 4. 登录成功
        System.out.println("Login successful for user: " + username);
        return admin;
    }

    // 其他 AdminService 接口中定义的方法的实现...
}