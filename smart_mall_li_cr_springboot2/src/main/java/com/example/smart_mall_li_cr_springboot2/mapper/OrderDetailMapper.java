package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.OrderDetail;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author ll.xl
* @description 针对表【order_detail】的数据库操作Mapper
* @createDate 2025-03-20 14:45:51
* @Entity com.example.smart_mall_li_cr_springboot2.pojo.OrderDetail
*/
@Mapper
public interface OrderDetailMapper {

    int deleteByPrimaryKey(Long id);

    /**
     * 插入订单详情记录
     * @param detail 订单详情对象
     * @return 影响行数
     */
    int insert(OrderDetail detail);

    int insertSelective(OrderDetail record);

    OrderDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderDetail record);

    int updateByPrimaryKey(OrderDetail record);

    /**
     * 批量插入订单详情
     *
     * @param orderDetails 订单详情列表
     * @return 影响行数
     */
    int batchInsert(List<OrderDetail> orderDetails);

    /**
     * 根据订单ID查询订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情列表
     */
    List<OrderDetail> selectByOrderId(Long orderId);

    /**
     * 检查订单是否包含指定商品
     * @param orderId 订单ID
     * @param itemId 商品ID
     * @return 是否包含该商品
     */
    boolean checkOrderContainsItem(@Param("orderId") Integer orderId, @Param("itemId") Integer itemId);

}
