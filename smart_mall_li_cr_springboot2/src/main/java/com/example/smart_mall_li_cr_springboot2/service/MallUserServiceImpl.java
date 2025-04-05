package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.mapper.MallUserMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUser;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUserLoginParam;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUserRegisterParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service//为类创建对象，把对象放在容器中
public class MallUserServiceImpl implements MallUserService {

    @Autowired//从容器中找个对象赋值给mallUserMapper
    MallUserMapper mallUserMapper;
    @Override
    public boolean mallUserRegister(MallUserRegisterParam mallUserRegisterParam) {
        MallUser mallUser = mallUserMapper.selectByUsername(mallUserRegisterParam.getUsername());//个人认为直接填入变量也可以，但是不安全，不好更改
        if(mallUser==null){
            //用户不存在，保存用户信息
            MallUser insertMallUser = new MallUser();
            //设置实体类insertMallUser的用户名和密码
            insertMallUser.setUsername(mallUserRegisterParam.getUsername());
            insertMallUser.setPassword(mallUserRegisterParam.getPassword());
            //保存用户信息到数据库
            int insertRows = mallUserMapper.insertSelective(insertMallUser);
            if(insertRows>0){
                return true;
            }else{
                return false;
            }
        }
            //用户存在，返回false
            return false;
    }

//    @Override
//    public MallUser mallUserLogin(MallUserLoginParam mallUserLoginParam) {
//        MallUser mallUser = mallUserMapper.selectByUsername(mallUserLoginParam.getUsername());
//        if(mallUser!=null){
//            //用户存在，判断密码是否正确
//            String password = mallUser.getPassword();
//            if(password.equals(mallUserLoginParam.getPassword())){
//                return mallUser;
//            }else{
//                return"用户名或密码错误";
//            }
//        }
//        //用户不存在，返回提示信息
//            return "用户不存在";
//    }

    @Override
    public Result mallUserLogin(MallUserLoginParam mallUserLoginParam) {
        MallUser mallUser = mallUserMapper.selectByUsername(mallUserLoginParam.getUsername());

        if (mallUser == null) {
            return new Result(false, "用户不存在");
        }

        if (mallUser.getPassword().equals(mallUserLoginParam.getPassword())) {
            return new Result(true, "登录成功", mallUser);
        } else {
            return new Result(false, "用户名或密码错误");
        }
    }

}
