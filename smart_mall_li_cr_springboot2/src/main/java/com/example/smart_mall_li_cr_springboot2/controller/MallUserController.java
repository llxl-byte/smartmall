package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUser;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUserLoginParam;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUserRegisterParam;
import com.example.smart_mall_li_cr_springboot2.service.MallUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowCredentials = "false")
@RestController
public class MallUserController {
    @Autowired
    MallUserService mallUserService;
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
            return mallUserService.mallUserLogin(loginParam);
        } catch (Exception e) {
            return new Result(false, "登录失败：" + e.getMessage());
        }
    }
}
