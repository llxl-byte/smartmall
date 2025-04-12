package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.dto.CreateOrderRequestDTO;
import com.example.smart_mall_li_cr_springboot2.dto.CreateOrderResponseDTO;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建新订单
     *
     * @param requestDTO 包含 userId, addressId, items, couponId, remark 的请求对象
     * @return 包含 orderId 和 paymentAmount 的响应对象
     * @throws Exception 可能抛出业务异常，如库存不足、优惠券无效等
     */
    CreateOrderResponseDTO createOrder(CreateOrderRequestDTO requestDTO) throws Exception;

    // 未来可以添加查询订单、取消订单等方法

} 