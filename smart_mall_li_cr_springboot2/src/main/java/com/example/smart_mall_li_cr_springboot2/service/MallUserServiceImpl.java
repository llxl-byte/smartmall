package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.mapper.MallUserMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUser;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUserLoginParam;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUserRegisterParam;

import java.util.List; // 新增导入
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MallUserServiceImpl implements MallUserService {
    @Autowired
    private MallUserMapper mallUserMapper;

    @Override
    public boolean mallUserRegister(MallUserRegisterParam mallUserRegisterParam) {
        try {
            // 检查用户名是否已存在
            MallUser existingUser = mallUserMapper.selectByUsername(mallUserRegisterParam.getUsername());
            if (existingUser != null) {
                return false; // 用户名已存在
            }

            // 检查手机号是否已存在（如果提供了手机号）
            if (mallUserRegisterParam.getPhone() != null && !mallUserRegisterParam.getPhone().isEmpty()) {
                // 这里需要添加一个根据手机号查询用户的方法
                // 暂时可以跳过这个检查，或者在数据库层面处理
            }

            return mallUserMapper.mallUserRegister(mallUserRegisterParam);
        } catch (Exception e) {
            System.err.println("注册失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Result mallUserLogin(MallUserLoginParam mallUserLoginParam) {
        MallUser user = mallUserMapper.mallUserLogin(mallUserLoginParam);
        if (user != null) {
            return new Result(true, "登录成功", user);
        }
        return new Result(false, "用户名或密码错误");
    }

    @Override
    public boolean updateUserAvatar(Integer userId, String avatarUrl) {
        return mallUserMapper.updateUserAvatar(userId, avatarUrl);
    }

    @Override
    public boolean updateUserInfo(MallUser user) {
        return mallUserMapper.updateUserInfo(user);
    }

    @Override
    public List<MallUser> getAllUsers(Integer pageNum, Integer pageSize) {
        // TODO: 实现分页逻辑
        // 实际项目中，这里应该调用Mapper进行分页查询
        // 例如，使用PageHelper:
        // if (pageNum != null && pageSize != null) {
        //     PageHelper.startPage(pageNum, pageSize);
        // }
        // return mallUserMapper.selectAllUsers(); // 假设有一个selectAllUsers方法
        // 当前mallUserMapper已经添加了selectAll方法
        return mallUserMapper.selectAll();
    }

    @Override
    public MallUser getMallUserById(Integer userId) {
        // 调用Mapper根据主键查询用户
        return mallUserMapper.selectByPrimaryKey(userId);
    }

    @Override
    public boolean updateUserStatus(Integer userId, Boolean isActive) {
        if (userId == null || isActive == null) {
            // 参数无效
            return false;
        }
        MallUser user = mallUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            // 用户不存在
            return false;
        }
        // 更新用户状态字段
        // 假设 MallUser 实体中有一个 status 字段，0 表示禁用，1 表示启用
        user.setStatus(isActive ? 1 : 0);
        // 或者如果 MallUser 实体中有一个 boolean 类型的 isActive 字段
        // user.setIsActive(isActive);

        // 调用 Mapper 更新用户
        int updatedRows = mallUserMapper.updateByPrimaryKeySelective(user);
        return updatedRows > 0;
    }
}