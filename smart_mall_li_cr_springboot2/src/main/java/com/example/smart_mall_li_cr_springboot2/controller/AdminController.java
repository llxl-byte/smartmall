package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.common.Result; // 引入统一结果类
import com.example.smart_mall_li_cr_springboot2.pojo.Admin;
import com.example.smart_mall_li_cr_springboot2.pojo.AdminLoginParam; // 引入登录参数类
import com.example.smart_mall_li_cr_springboot2.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin; // 需要导入这个注解

/**
 * 管理员控制器
 */
@RestController // 标记为 RESTful 控制器
@RequestMapping("/admin") // 基础请求路径
@CrossOrigin(origins = "https://animated-space-cod-r4ppq64r6r6jfx6vq-5173.app.github.dev", allowCredentials = "true") // 添加 CrossOrigin 注解
public class AdminController {

    @Autowired // 自动注入 AdminService 实例
    private AdminService adminService;

    /**
     * 管理员登录接口
     * @param loginParam 包含用户名和密码的请求体
     * @return 统一的 Result 对象
     */
    @PostMapping("/login") // 处理 POST /admin/login 请求
    public Result login(@RequestBody AdminLoginParam loginParam) {
        // 从参数对象中获取用户名和密码
        String username = loginParam.getUsername();
        String password = loginParam.getPassword();

        // 调用 Service 层的登录方法
        Admin admin = adminService.login(username, password);

        // 判断登录结果
        if (admin != null) {
            // 登录成功
            // 注意：为了安全，不应该将整个 admin 对象（尤其是密码）返回给前端
            // 实际项目中，通常会生成一个 token 返回给前端
            // 这里暂时返回一个成功的消息和部分安全信息（例如用户名和角色）
            admin.setPassword(null); // 清除密码信息
            return new Result(true, "登录成功", admin); // 使用构造函数创建成功响应
        } else {
            // 登录失败
            return new Result(false, "用户名或密码错误"); // 使用构造函数创建失败响应
        }
    }

    // 后续可以添加其他管理员相关的接口，例如：
    // @GetMapping("/{id}") 获取管理员信息
    // @PostMapping("/register") 管理员注册 (如果需要)
    // @PutMapping 更新管理员信息
    // @DeleteMapping("/{id}") 删除管理员
    // @GetMapping("/list") 获取管理员列表 (需要分页和权限控制)
}