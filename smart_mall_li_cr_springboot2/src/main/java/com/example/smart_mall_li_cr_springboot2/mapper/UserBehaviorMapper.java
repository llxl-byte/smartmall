package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.UserBehavior;

/**
* @author ll.xl
* @description 针对表【user_behavior】的数据库操作Mapper
* @createDate 2025-03-20 14:45:51
* @Entity com.example.smart_mall_li_cr_springboot2.pojo.UserBehavior
*/
public interface UserBehaviorMapper {

    int deleteByPrimaryKey(Long id);

    int insert(UserBehavior record);

    int insertSelective(UserBehavior record);

    UserBehavior selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserBehavior record);

    int updateByPrimaryKey(UserBehavior record);

}
