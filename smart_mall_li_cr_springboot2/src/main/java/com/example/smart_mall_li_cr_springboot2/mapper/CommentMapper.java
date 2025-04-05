package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.Comment;

/**
* @author ll.xl
* @description 针对表【comment】的数据库操作Mapper
* @createDate 2025-03-20 14:45:51
* @Entity com.example.smart_mall_li_cr_springboot2.pojo.Comment
*/
public interface CommentMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Comment record);

    int insertSelective(Comment record);

    Comment selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Comment record);

    int updateByPrimaryKey(Comment record);

}
