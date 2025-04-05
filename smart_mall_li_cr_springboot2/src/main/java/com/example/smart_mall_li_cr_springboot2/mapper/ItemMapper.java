package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.Item;

import java.util.List;

/**
* @author ll.xl
* @description 针对表【item】的数据库操作Mapper
* @createDate 2025-03-20 14:45:51
* @Entity com.example.smart_mall_li_cr_springboot2.pojo.Item
*/
public interface ItemMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Item record);

    int insertSelective(Item record);

    Item selectByPrimaryKey(Long itemId);

    int updateByPrimaryKeySelective(Item record);

    int updateByPrimaryKey(Item record);



    List<Item>selectByCategoryId(Integer categoryId);

    List<Item>selectByItemName(String itemName);

}
