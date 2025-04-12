package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.pojo.OrderDetail;
import java.util.List;

/**
 * 订单详情服务接口
 */
public interface OrderDetailService {
    
    /**
     * 添加订单详情
     * @param orderDetail 订单详情对象
     * @return 是否添加成功
     */
    boolean addOrderDetail(OrderDetail orderDetail);
    
    /**
     * 批量添加订单详情
     * @param orderDetails 订单详情列表
     * @return 是否添加成功
     */
    boolean batchAddOrderDetails(List<OrderDetail> orderDetails);
    
    /**
     * 根据订单ID获取订单详情列表
     * @param orderId 订单ID
     * @return 订单详情列表
     */
    List<OrderDetail> getOrderDetailsByOrderId(Long orderId);
} 