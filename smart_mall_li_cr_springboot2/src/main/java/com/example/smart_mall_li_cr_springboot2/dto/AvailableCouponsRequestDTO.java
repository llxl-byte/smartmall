package com.example.smart_mall_li_cr_springboot2.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 获取可用优惠券接口的请求 DTO
 */
@Data // 使用 Lombok 简化 getter, setter, toString 等
public class AvailableCouponsRequestDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单中的商品ID列表
     */
    private List<Long> itemIds;

    /**
     * 当前订单商品总金额
     */
    private BigDecimal totalAmount;

    // 注意：确保前端传递的数据类型与这里定义的匹配
    // 例如，如果前端传的是字符串形式的金额，后端可能需要转换或添加注解处理
}