package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUser;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUserLoginParam;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUserRegisterParam;
import com.example.smart_mall_li_cr_springboot2.service.MallUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class MallUserController {
    @Autowired
    MallUserService mallUserService;
    @RequestMapping("/mallUserRegister")
    public boolean mallUserRegister(MallUserRegisterParam mallUserRegisterParam) {
        return mallUserService.mallUserRegister(mallUserRegisterParam);
    }


    @RequestMapping("/mallUserLogin")
    public Result mallUserLogin(MallUserLoginParam mallUserLoginParam) {
        return mallUserService.mallUserLogin(mallUserLoginParam);
        }


}
