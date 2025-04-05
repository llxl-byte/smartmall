package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUserLoginParam;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUserRegisterParam;

public interface MallUserService {
    public boolean mallUserRegister(MallUserRegisterParam mallUserRegisterParam);
    public Result mallUserLogin(MallUserLoginParam mallUserLoginParam);
}
