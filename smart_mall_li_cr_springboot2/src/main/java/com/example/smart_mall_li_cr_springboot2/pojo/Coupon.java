package com.example.smart_mall_li_cr_springboot2.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券实体类 (对应数据库表)
 */
@Data // Lombok 注解，自动生成 getter, setter, toString 等
public class Coupon {

    /**
     * 优惠券实例ID (主键)
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 优惠券模板ID (如果使用模板)
     */
    // private Long couponTemplateId;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券描述
     */
    private String description;

    /**
     * 优惠券类型 (例如: "满减", "折扣")
     */
    private String type;

    /**
     * 使用门槛金额 (0表示无门槛)
     */
    private BigDecimal threshold;

    /**
     * 抵扣金额 (用于满减券)
     */
    private BigDecimal discountAmount;

    /**
     * 折扣百分比 (1-100, 用于折扣券)
     */
    // private Integer discountPercent;

    /**
     * 优惠券状态 (例如: 0-未使用, 1-已使用, 2-已过期)
     * 建议使用枚举或常量定义
     */
    private Integer status;

    /**
     * 领取/发放时间
     */
    private Date createTime;

    /**
     * 过期日期 (包含当天)
     */
    private Date expiryDate;

    /**
     * 使用时间
     */
    private Date useTime;

    /**
     * 关联的订单ID (使用后关联)
     */
    private String orderId;

    // 可以根据数据库设计添加其他字段

} 