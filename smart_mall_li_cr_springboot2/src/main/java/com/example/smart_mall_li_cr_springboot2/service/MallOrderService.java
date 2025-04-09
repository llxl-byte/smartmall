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
}
