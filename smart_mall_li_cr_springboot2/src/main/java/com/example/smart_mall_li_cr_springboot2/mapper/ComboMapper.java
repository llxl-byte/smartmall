package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.Combo;

import java.util.List; // 新增导入

/**
* @author ll.xl
* @description 针对表【combo】的数据库操作Mapper
* @createDate 2025-03-20 14:45:51
* @Entity com.example.smart_mall_li_cr_springboot2.pojo.Combo
*/
public interface ComboMapper {

    int deleteByPrimaryKey(Integer id); // 修改参数类型为 Integer

    int insert(Combo record);

    int insertSelective(Combo record);

    Combo selectByPrimaryKey(Integer id); // 修改参数类型为 Integer

    int updateByPrimaryKeySelective(Combo record);

    int updateByPrimaryKey(Combo record);

    /**
     * 查询所有套餐列表
     * @return 所有套餐列表
     */
    List<Combo> selectAll();

}
