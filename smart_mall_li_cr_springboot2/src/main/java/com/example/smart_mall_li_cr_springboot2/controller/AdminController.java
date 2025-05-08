package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.common.Result; // 引入统一结果类
import com.example.smart_mall_li_cr_springboot2.pojo.Admin;
import com.example.smart_mall_li_cr_springboot2.pojo.AdminLoginParam; // 引入登录参数类
import com.example.smart_mall_li_cr_springboot2.pojo.MallUser;
import com.example.smart_mall_li_cr_springboot2.service.AdminService;
import com.example.smart_mall_li_cr_springboot2.service.MallUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin; // 需要导入这个注解

import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 */
@RestController // 标记为 RESTful 控制器
@RequestMapping("/admin") // 基础请求路径
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"}, allowCredentials = "true") // 允许本地前端访问
public class AdminController {

    @Autowired // 自动注入 AdminService 实例
    private AdminService adminService;

    @Autowired
    private MallUserService mallUserService;

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

    /**
     * 后台管理：获取所有商城用户列表
     * @param pageNum 当前页码 (可选)
     * @param pageSize 每页数量 (可选)
     * @return 包含用户列表的Result对象
     */
    @GetMapping("/users")
    public Result getAllMallUsers(@RequestParam(required = false) Integer pageNum,
                                  @RequestParam(required = false) Integer pageSize) {
        try {
            // 调用 MallUserService 获取用户列表
            // 注意：您需要在 MallUserService 中实现 getAllUsers 方法
            List<MallUser> userList = mallUserService.getAllUsers(pageNum, pageSize);
            return Result.success(userList);
        } catch (Exception e) {
            // 记录日志 e.printStackTrace();
            return Result.error("查询用户列表失败: " + e.getMessage());
        }
    }


    /**
     * 后台管理：根据用户ID获取单个商城用户信息
     * @param userId 用户ID
     * @return 包含用户信息的Result对象
     */
    @GetMapping("/users/{userId}")
    public Result getMallUserById(@PathVariable Integer userId) { // 修改参数类型为 Integer
        try {
            // 调用 MallUserService 获取用户信息
            MallUser user = mallUserService.getMallUserById(userId);
            if (user != null) {
                return Result.success(user);
            }else {
                return Result.error("用户不存在");
            }
        }catch (Exception e) {
            // 记录日志 e.printStackTrace();
            return Result.error("查询用户信息失败: " + e.getMessage());
        }
    }


    /**
     * 后台管理：禁用或启用商城用户账户
     * @param userId 用户ID
     * @param payload 请求体，应包含如 {"isActive": true/false}
     * @return 操作结果
     */
    @PostMapping("/users/{userId}/status")
    public Result updateMallUserStatus(@PathVariable Integer userId, @RequestBody Map<String, Boolean> payload) {
        try {
            Boolean isActive = payload.get("isActive");
            if (isActive == null) {
                return Result.error("请求参数错误，缺少 'isActive' 字段");
            }
            // 调用 MallUserService 更新用户状态
            // 注意：您需要在 MallUserService 中实现 updateUserStatus 方法
            boolean success = mallUserService.updateUserStatus(userId, isActive);
            if (success) {
                return Result.success(null, "用户状态更新成功");
            } else {
                return Result.error("用户状态更新失败或用户不存在");
            }
        } catch (Exception e) {
            // 记录日志 e.printStackTrace();
            return Result.error("更新用户状态时发生错误: " + e.getMessage());
        }
    }


}