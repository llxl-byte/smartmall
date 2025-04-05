package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.Combo;

/**
* @author ll.xl
* @description 针对表【combo】的数据库操作Mapper
* @createDate 2025-03-20 14:45:51
* @Entity com.example.smart_mall_li_cr_springboot2.pojo.Combo
*/
public interface ComboMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Combo record);

    int insertSelective(Combo record);

    Combo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Combo record);

    int updateByPrimaryKey(Combo record);

}
