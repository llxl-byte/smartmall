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
import java.util.UUID;

@CrossOrigin(origins = "*", allowCredentials = "false")
@RestController
public class MallUserController {
    @Autowired
    MallUserService mallUserService;
    @Autowired
    private JwtUtil jwtUtil;

    @RequestMapping("/mallUserRegister")
    public boolean mallUserRegister(MallUserRegisterParam mallUserRegisterParam) {
        return mallUserService.mallUserRegister(mallUserRegisterParam);
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
                // 将令牌添加到结果中，可以通过Result类的一个字段或附加数据来返回
                // 这里假设Result类有一个setData方法来设置额外数据
                loginResult.setData(token);
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
    public Result updateUserInfo(@RequestBody MallUser user) {
        try {
            boolean updated = mallUserService.updateUserInfo(user);
            if (updated) {
                return new Result(true, "用户信息更新成功");
            } else {
                return new Result(false, "用户信息更新失败");
            }
        } catch (Exception e) {
            return new Result(false, "更新失败：" + e.getMessage());
        }
    }
}
