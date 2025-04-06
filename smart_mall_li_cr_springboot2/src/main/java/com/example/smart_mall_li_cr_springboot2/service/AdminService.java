package com.example.smart_mall_li_cr_springboot2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.smart_mall_li_cr_springboot2.pojo.Admin;

/**
 * 管理员 Service 接口
 */
public interface AdminService extends IService<Admin> {

    // 继承 IService 后，自动拥有了常用的业务方法，如 save, removeById, updateById, getById, list 等

    // 可以在这里定义更具体的业务方法，例如：
    /**
     * 管理员登录校验
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回 Admin 对象，失败返回 null 或抛出异常
     */
    Admin login(String username, String password);

    // 其他需要的业务方法...
}