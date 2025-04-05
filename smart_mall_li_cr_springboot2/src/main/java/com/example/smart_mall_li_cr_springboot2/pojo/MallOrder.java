package com.example.smart_mall_li_cr_springboot2.pojo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName mall_order
 */
public class MallOrder {
    /**
     * 
     */
    private Integer id;

    /**
     * 
     */
    private String orderNo;

    /**
     * 
     */
    private Integer userId;

    /**
     * 
     */
    private BigDecimal totalAmount;

    /**
     * 
     */
    private BigDecimal actualAmount;

    /**
     * 0-待付款,1-待发货,2-待收货,3-已完成,4-已取消
     */
    private Integer status;

    /**
     * JSON格式地址信息
     */
    private String addressInfo;

    /**
     * 
     */
    private Date paymentTime;

    /**
     * 
     */
    private Date deliveryTime;

    /**
     * 
     */
    private Date confirmTime;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * 
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * 
     */
    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    /**
     * 
     */
    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    /**
     * 0-待付款,1-待发货,2-待收货,3-已完成,4-已取消
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 0-待付款,1-待发货,2-待收货,3-已完成,4-已取消
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * JSON格式地址信息
     */
    public String getAddressInfo() {
        return addressInfo;
    }

    /**
     * JSON格式地址信息
     */
    public void setAddressInfo(String addressInfo) {
        this.addressInfo = addressInfo;
    }

    /**
     * 
     */
    public Date getPaymentTime() {
        return paymentTime;
    }

    /**
     * 
     */
    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    /**
     * 
     */
    public Date getDeliveryTime() {
        return deliveryTime;
    }

    /**
     * 
     */
    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    /**
     * 
     */
    public Date getConfirmTime() {
        return confirmTime;
    }

    /**
     * 
     */
    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    /**
     * 
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        MallOrder other = (MallOrder) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderNo() == null ? other.getOrderNo() == null : this.getOrderNo().equals(other.getOrderNo()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getTotalAmount() == null ? other.getTotalAmount() == null : this.getTotalAmount().equals(other.getTotalAmount()))
            && (this.getActualAmount() == null ? other.getActualAmount() == null : this.getActualAmount().equals(other.getActualAmount()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getAddressInfo() == null ? other.getAddressInfo() == null : this.getAddressInfo().equals(other.getAddressInfo()))
            && (this.getPaymentTime() == null ? other.getPaymentTime() == null : this.getPaymentTime().equals(other.getPaymentTime()))
            && (this.getDeliveryTime() == null ? other.getDeliveryTime() == null : this.getDeliveryTime().equals(other.getDeliveryTime()))
            && (this.getConfirmTime() == null ? other.getConfirmTime() == null : this.getConfirmTime().equals(other.getConfirmTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderNo() == null) ? 0 : getOrderNo().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getTotalAmount() == null) ? 0 : getTotalAmount().hashCode());
        result = prime * result + ((getActualAmount() == null) ? 0 : getActualAmount().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getAddressInfo() == null) ? 0 : getAddressInfo().hashCode());
        result = prime * result + ((getPaymentTime() == null) ? 0 : getPaymentTime().hashCode());
        result = prime * result + ((getDeliveryTime() == null) ? 0 : getDeliveryTime().hashCode());
        result = prime * result + ((getConfirmTime() == null) ? 0 : getConfirmTime().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", userId=").append(userId);
        sb.append(", totalAmount=").append(totalAmount);
        sb.append(", actualAmount=").append(actualAmount);
        sb.append(", status=").append(status);
        sb.append(", addressInfo=").append(addressInfo);
        sb.append(", paymentTime=").append(paymentTime);
        sb.append(", deliveryTime=").append(deliveryTime);
        sb.append(", confirmTime=").append(confirmTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}