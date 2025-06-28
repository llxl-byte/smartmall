package com.example.smart_mall_li_cr_springboot2.service;


import com.example.smart_mall_li_cr_springboot2.pojo.AddMallOrderParam;
import com.example.smart_mall_li_cr_springboot2.pojo.MallOrder;

import java.util.List;

public interface MallOrderService {
    public boolean AddMallOrder(AddMallOrderParam addMallOrderParam);


    /**
     * 根据用户ID获取订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<MallOrder> getOrdersByUserId(Integer userId);

    /**
     * 后台管理端：获取所有订单列表
     * @param pageNum 当前页码 (可选, 用于分页)
     * @param pageSize 每页数量 (可选, 用于分页)
     * @return 所有订单列表
     */
    List<MallOrder> getAllOrders(Integer pageNum, Integer pageSize);

    /**
     * 后台管理端：根据订单ID获取订单详情
     * @param orderId 订单ID
     * @return 订单对象
     */
    MallOrder getOrderById(Long orderId);

    /**
     * 后台管理端：更新订单状态 (例如：处理退货、售后)
     * @param orderId 订单ID
     * @param status 新的订单状态
     * @param remarks 处理备注
     * @return 是否更新成功
     */
    boolean updateOrderStatus(Long orderId, String status, String remarks);

    /**
     * 统计用户的订单数量
     * @param userId 用户ID
     * @return 订单数量
     */
    int countOrdersByUserId(Integer userId);
}
