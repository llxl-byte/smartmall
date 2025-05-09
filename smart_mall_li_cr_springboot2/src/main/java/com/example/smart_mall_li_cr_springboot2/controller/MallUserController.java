package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUser;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUserLoginParam;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUserRegisterParam;
import com.example.smart_mall_li_cr_springboot2.service.MallUserService;
import com.example.smart_mall_li_cr_springboot2.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@CrossOrigin(originPatterns = {"https://*.app.github.dev", "http://localhost:*", "http://127.0.0.1:*"}, allowCredentials = "false")
@RestController
public class MallUserController {
    @Autowired
    MallUserService mallUserService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/mallUserRegister")
    public Result<Boolean> mallUserRegister(@RequestBody MallUserRegisterParam mallUserRegisterParam) {
        try {
            System.out.println("接收到注册请求: " + mallUserRegisterParam.getUsername());
            boolean success = mallUserService.mallUserRegister(mallUserRegisterParam);
            if (success) {
                return new Result<>(true, "注册成功", true);
            } else {
                return new Result<>(false, "注册失败，用户名可能已存在", false);
            }
        } catch (Exception e) {
            System.err.println("注册失败: " + e.getMessage());
            e.printStackTrace();
            return new Result<>(false, "注册失败: " + e.getMessage(), false);
        }
    }

    @GetMapping("/mallUserLogin")
    public Result mallUserLogin(@RequestParam String username, @RequestParam String password) {
        // 处理登录逻辑
        try {
            // 创建登录参数对象
            MallUserLoginParam loginParam = new MallUserLoginParam();
            loginParam.setUsername(username);
            loginParam.setPassword(password);

            // 调用service层处理登录
            Result loginResult = mallUserService.mallUserLogin(loginParam);

            // 如果登录成功，生成JWT令牌
            if (loginResult.isSuccess()) {
                String token = jwtUtil.generateToken(username);

                // 获取用户信息
                MallUser user = (MallUser) loginResult.getData();

                // 创建一个Map来存储用户信息和token
                java.util.Map<String, Object> resultMap = new java.util.HashMap<>();
                resultMap.put("userInfo", user);
                resultMap.put("token", token);

                // 设置返回数据为包含token的用户信息
                loginResult.setData(resultMap);
            }
            return loginResult;
        } catch (Exception e) {
            return new Result(false, "登录失败：" + e.getMessage());
        }
    }

    @PostMapping("/uploadAvatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam("userId") Integer userId) {
        try {
            // 确保上传目录存在
            String uploadDir = "uploads/avatars/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一的文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;

            // 保存文件
            File destFile = new File(dir, newFilename);
            file.transferTo(destFile);

            // 生成访问URL
            String avatarUrl = "/uploads/avatars/" + newFilename;

            // 更新用户头像URL
            boolean updated = mallUserService.updateUserAvatar(userId, avatarUrl);

            if (updated) {
                return new Result(true, "头像上传成功", avatarUrl);
            } else {
                return new Result(false, "更新用户头像失败");
            }
        } catch (IOException e) {
            return new Result(false, "头像上传失败：" + e.getMessage());
        }
    }

    @PostMapping("/updateUserInfo")
    public Result<Boolean> updateUserInfo(@RequestBody MallUser user) {
        try {
            System.out.println("接收到更新用户信息请求: " + user);

            // 验证用户ID
            if (user.getId() == null) {
                return new Result<>(false, "缺少用户ID", false);
            }

            // 查询用户是否存在
            MallUser existingUser = mallUserService.getMallUserById(user.getId());
            if (existingUser == null) {
                return new Result<>(false, "用户不存在", false);
            }

            // 保留原有信息，只更新提供的字段
            if (user.getNickname() == null) {
                user.setNickname(existingUser.getNickname());
            }
            if (user.getPhone() == null) {
                user.setPhone(existingUser.getPhone());
            }
            if (user.getAvatar() == null) {
                user.setAvatar(existingUser.getAvatar());
            }

            boolean updated = mallUserService.updateUserInfo(user);
            if (updated) {
                // 获取更新后的用户信息
                MallUser updatedUser = mallUserService.getMallUserById(user.getId());
                return new Result<Boolean>(true, "用户信息更新成功", true);
            } else {
                return new Result<>(false, "用户信息更新失败", false);
            }
        } catch (Exception e) {
            System.err.println("更新用户信息失败: " + e.getMessage());
            e.printStackTrace();
            return new Result<>(false, "更新失败: " + e.getMessage(), false);
        }
    }

    // 用户管理相关接口已移至 AdminController 类中
}
