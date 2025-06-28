package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.ComboItem;
import java.util.List;

/**
* @author ll.xl
* @description 针对表【combo_item】的数据库操作Mapper
* @createDate 2025-03-20 14:45:51
* @Entity com.example.smart_mall_li_cr_springboot2.pojo.ComboItem
*/
public interface ComboItemMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ComboItem record);

    int insertSelective(ComboItem record);

    ComboItem selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ComboItem record);

    int updateByPrimaryKey(ComboItem record);

    /**
     * 根据套餐ID删除商品项
     * @param comboId 套餐ID
     * @return 删除的记录数
     */
    int deleteByComboId(Long comboId);

    /**
     * 批量插入商品项
     * @param comboItems 商品项列表
     * @return 插入的记录数
     */
    int batchInsert(List<ComboItem> comboItems);

}
