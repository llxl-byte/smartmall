package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.MallUser;

/**
* @author ll.xl
* @description 针对表【mall_user】的数据库操作Mapper
* @createDate 2025-03-20 14:45:51
* @Entity com.example.smart_mall_li_cr_springboot2.pojo.MallUser
*/
public interface MallUserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(MallUser record);

    int insertSelective(MallUser record);

    MallUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MallUser record);

    int updateByPrimaryKey(MallUser record);



    MallUser selectByUsername(String username);
    MallUser selectByUsernameAndPassword(String username, String password);

}
