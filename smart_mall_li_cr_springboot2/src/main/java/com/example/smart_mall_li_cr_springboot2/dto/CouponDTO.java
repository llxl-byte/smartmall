package com.example.smart_mall_li_cr_springboot2.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券信息的 DTO
 */
@Data
public class CouponDTO {

    /**
     * 优惠券ID (对应数据库中的主键)
     * 或者优惠券模板ID + 用户领取记录ID 组合?
     * 这里简化处理，假设是唯一的优惠券实例ID
     */
    private Long id;

    /**
     * 优惠券名称 (供前端展示)
     */
    private String name;

    /**
     * 抵扣金额
     */
    private BigDecimal discountAmount;

    /**
     * 使用门槛金额 (满多少可用，0表示无门槛)
     */
    private BigDecimal threshold;

    /**
     * 优惠券描述 (更详细的说明)
     */
    private String description;

    /**
     * 有效期结束日期 (可选)
     */
    private Date validityEnd;

    /**
     * 优惠券类型 (可选, 例如：满减、折扣、免邮等)
     */
    private String type;

    // 可以根据需要添加更多字段，如适用商品范围等
} 