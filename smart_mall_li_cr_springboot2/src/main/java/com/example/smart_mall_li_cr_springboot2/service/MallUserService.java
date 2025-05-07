package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUser;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUserLoginParam;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUserRegisterParam;

import java.util.List;

public interface MallUserService {
    public boolean mallUserRegister(MallUserRegisterParam mallUserRegisterParam);
    public Result mallUserLogin(MallUserLoginParam mallUserLoginParam);
    public boolean updateUserAvatar(Integer userId, String avatarUrl);
    public boolean updateUserInfo(MallUser user);


    /**
     * 获取所有商城用户列表 (后台管理使用)
     * @param pageNum 当前页码 (可选)
     * @param pageSize 每页数量 (可选)
     * @return 用户列表
     */
    List<MallUser> getAllUsers(Integer pageNum, Integer pageSize);

    /**
     * 根据用户ID获取单个商城用户信息 (后台管理使用)
     * @param userId 用户ID
     * @return 用户信息
     */
    MallUser getMallUserById(Integer userId);

    /**
     * 禁用或启用商城用户账户 (后台管理使用)
     * @param userId 用户ID
     * @param isActive 是否启用
     * @return 操作结果
     */
    boolean updateUserStatus(Integer userId, Boolean isActive);

}
