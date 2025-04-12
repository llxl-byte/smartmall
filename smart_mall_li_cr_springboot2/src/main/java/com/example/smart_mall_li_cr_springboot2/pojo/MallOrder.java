package com.example.smart_mall_li_cr_springboot2.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单主表实体类
 */
@Data
public class MallOrder {

    /**
     * 主键ID (数据库自增整数)
     */
    private Long id;

    /**
     * 订单号 (业务主键, VARCHAR)
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 商品总金额
     */
    private BigDecimal totalAmount;

    /**
     * 实付金额
     */
    private BigDecimal actualAmount;

    /**
     * 优惠金额（不在数据库表中，只在逻辑上使用）
     */
    private BigDecimal discountAmount;

    /**
     * 订单状态 (例如: 0-待支付, 1-已支付/待发货, 2-已发货, 3-已完成, 4-已取消, 5-售后中)
     */
    private Integer status;

    /**
     * 地址信息 JSON字符串
     */
    private String addressInfo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 支付时间
     */
    private Date paymentTime;

    /**
     * 发货时间
     */
    private Date deliveryTime;

    /**
     * 确认时间（完成时间）
     */
    private Date confirmTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户备注
     */
    private String remark;

    /**
     * 关联的收货地址ID
     */
    private Long addressId;

    // --- 地址信息冗余 ---
    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 收货地址详情
     */
    private String receiverAddress;

}