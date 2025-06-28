package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.MallOrder;

import java.util.List;
import java.util.Map; // 新增导入

/**
* @author ll.xl
* @description 针对表【mall_order】的数据库操作Mapper
* @createDate 2025-03-20 14:45:51
* @Entity com.example.smart_mall_li_cr_springboot2.pojo.MallOrder
*/
public interface MallOrderMapper {

    int deleteByPrimaryKey(Long id);

    int insert(MallOrder record);

    int insertSelective(MallOrder mallOrder);

    MallOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MallOrder record);

    int updateByPrimaryKey(MallOrder record);


    /**
     * 根据用户ID查询订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<MallOrder> selectByUserId(Integer userId);

    /**
     * 查询所有订单列表
     * @return 所有订单列表
     */
    List<MallOrder> selectAll();

    /**
     * 获取商品销量统计数据
     * @return 销量统计列表，包含商品名称和销量
     */
    List<Map<String, Object>> getSalesStatistics();
}
