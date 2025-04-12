package com.example.smart_mall_li_cr_springboot2.dto;

import lombok.Data;
import java.util.List;

/**
 * 创建订单接口的请求 DTO
 */
@Data
public class CreateOrderRequestDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收货地址ID
     */
    private Long addressId;

    /**
     * 购买的商品列表
     */
    private List<OrderItemDTO> items;

    /**
     * 使用的优惠券ID (可选, 如果未使用则为 null)
     */
    private Long couponId;

    /**
     * 用户备注 (可选)
     */
    private String remark;

    /**
     * 内部类，表示订单中的单个商品项
     */
    @Data
    public static class OrderItemDTO {
        /**
         * 商品ID
         */
        private Long itemId;
        /**
         * 购买数量
         */
        private Integer quantity;
    }
} 