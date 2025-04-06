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
        // 1. 根据用户名查询管理员
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username); // 构建查询条件：username = ?
        Admin admin = this.baseMapper.selectOne(queryWrapper); // 使用 baseMapper 执行查询

        // 2. 判断管理员是否存在
        if (admin == null) {
            // 用户名不存在
            return null;
        }

        // 3. 比较密码 (注意：这里是明文比较，非常不安全，仅作演示)
        // 实际应用中应比较加密后的密码
        if (!password.equals(admin.getPassword())) {
            // 密码错误
            return null;
        }

        // 4. 登录成功
        return admin;
    }

    // 其他 AdminService 接口中定义的方法的实现...
}