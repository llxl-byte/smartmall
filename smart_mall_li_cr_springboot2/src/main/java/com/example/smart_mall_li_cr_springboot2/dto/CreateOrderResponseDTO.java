package com.example.smart_mall_li_cr_springboot2.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 创建订单接口的响应 DTO
 */
@Data
public class CreateOrderResponseDTO {

    /**
     * 新生成的订单号
     */
    private String orderId;

    /**
     * 实际需要支付的金额
     */
    private BigDecimal paymentAmount;

    // 可以根据需要添加其他返回信息，比如支付链接等
} 