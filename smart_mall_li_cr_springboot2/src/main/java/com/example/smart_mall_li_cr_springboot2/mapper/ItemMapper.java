package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ll.xl
* @description 针对表【item】的数据库操作Mapper
* @createDate 2025-03-20 14:45:51
* @Entity com.example.smart_mall_li_cr_springboot2.pojo.Item
*/
@Mapper
public interface ItemMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Item record);

    int insertSelective(Item record);

    Item selectByPrimaryKey(Long itemId);

    int updateByPrimaryKeySelective(Item record);

    int updateByPrimaryKey(Item record);



    List<Item>selectByCategoryId(Integer categoryId);

    List<Item>selectByItemName(String itemName);

    /**
     * 查询所有商品
     * @return 所有商品列表
     */
    List<Item> selectAll();

    /**
     * 根据ID查询商品信息，并锁定行 (用于防止并发修改库存)
     * @param id 商品ID
     * @return 商品对象
     */
    Item findByIdForUpdate(@Param("id") Long id);

    /**
     * 根据ID查询商品信息 (普通查询)
     * @param id 商品ID
     * @return 商品对象
     */
    Item findById(@Param("id") Long id);

    /**
     * 扣减商品库存
     * @param id 商品ID
     * @param quantity 扣减数量
     * @return 影响行数 (成功为1，失败为0)
     */
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 按销量降序查询商品
     * @param limit 限制返回数量
     * @return 商品列表
     */
    List<Item> selectBySalesDesc(@Param("limit") Integer limit);

    /**
     * 按分类和销量降序查询商品
     * @param categoryId 分类ID
     * @param limit 限制返回数量
     * @return 商品列表
     */
    List<Item> selectByCategoryIdAndSalesDesc(@Param("categoryId") Integer categoryId, @Param("limit") Integer limit);
}
