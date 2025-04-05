package com.example.smart_mall_li_cr_springboot2.pojo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName order_detail
 */
public class OrderDetail {
    /**
     * 
     */
    private Integer id;

    /**
     * 
     */
    private Integer orderId;

    /**
     * 
     */
    private Integer itemId;

    /**
     * 
     */
    private Integer quantity;

    /**
     * 
     */
    private BigDecimal price;

    /**
     * 
     */
    private BigDecimal totalPrice;

    /**
     * 
     */
    private String itemName;

    /**
     * 
     */
    private String itemImage;

    /**
     * 
     */
    private Date createTime;

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
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * 
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     * 
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     * 
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * 
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * 
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 
     */
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    /**
     * 
     */
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * 
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * 
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * 
     */
    public String getItemImage() {
        return itemImage;
    }

    /**
     * 
     */
    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
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
        OrderDetail other = (OrderDetail) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderId() == null ? other.getOrderId() == null : this.getOrderId().equals(other.getOrderId()))
            && (this.getItemId() == null ? other.getItemId() == null : this.getItemId().equals(other.getItemId()))
            && (this.getQuantity() == null ? other.getQuantity() == null : this.getQuantity().equals(other.getQuantity()))
            && (this.getPrice() == null ? other.getPrice() == null : this.getPrice().equals(other.getPrice()))
            && (this.getTotalPrice() == null ? other.getTotalPrice() == null : this.getTotalPrice().equals(other.getTotalPrice()))
            && (this.getItemName() == null ? other.getItemName() == null : this.getItemName().equals(other.getItemName()))
            && (this.getItemImage() == null ? other.getItemImage() == null : this.getItemImage().equals(other.getItemImage()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderId() == null) ? 0 : getOrderId().hashCode());
        result = prime * result + ((getItemId() == null) ? 0 : getItemId().hashCode());
        result = prime * result + ((getQuantity() == null) ? 0 : getQuantity().hashCode());
        result = prime * result + ((getPrice() == null) ? 0 : getPrice().hashCode());
        result = prime * result + ((getTotalPrice() == null) ? 0 : getTotalPrice().hashCode());
        result = prime * result + ((getItemName() == null) ? 0 : getItemName().hashCode());
        result = prime * result + ((getItemImage() == null) ? 0 : getItemImage().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", orderId=").append(orderId);
        sb.append(", itemId=").append(itemId);
        sb.append(", quantity=").append(quantity);
        sb.append(", price=").append(price);
        sb.append(", totalPrice=").append(totalPrice);
        sb.append(", itemName=").append(itemName);
        sb.append(", itemImage=").append(itemImage);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}