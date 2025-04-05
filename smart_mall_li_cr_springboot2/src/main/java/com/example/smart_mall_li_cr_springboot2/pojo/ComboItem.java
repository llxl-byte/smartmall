package com.example.smart_mall_li_cr_springboot2.pojo;

import java.util.Date;

/**
 * 
 * @TableName combo_item
 */
public class ComboItem {
    /**
     * 
     */
    private Integer id;

    /**
     * 
     */
    private Integer comboId;

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
    public Integer getComboId() {
        return comboId;
    }

    /**
     * 
     */
    public void setComboId(Integer comboId) {
        this.comboId = comboId;
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
        ComboItem other = (ComboItem) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getComboId() == null ? other.getComboId() == null : this.getComboId().equals(other.getComboId()))
            && (this.getItemId() == null ? other.getItemId() == null : this.getItemId().equals(other.getItemId()))
            && (this.getQuantity() == null ? other.getQuantity() == null : this.getQuantity().equals(other.getQuantity()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getComboId() == null) ? 0 : getComboId().hashCode());
        result = prime * result + ((getItemId() == null) ? 0 : getItemId().hashCode());
        result = prime * result + ((getQuantity() == null) ? 0 : getQuantity().hashCode());
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
        sb.append(", comboId=").append(comboId);
        sb.append(", itemId=").append(itemId);
        sb.append(", quantity=").append(quantity);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}