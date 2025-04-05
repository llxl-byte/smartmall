package com.example.smart_mall_li_cr_springboot2.pojo;

import java.util.Date;

/**
 * 
 * @TableName user_behavior
 */
public class UserBehavior {
    /**
     * 
     */
    private Integer id;

    /**
     * 
     */
    private Integer userId;

    /**
     * 
     */
    private Integer itemId;

    /**
     * 1-浏览,2-加入购物车,3-购买,4-收藏
     */
    private Integer behaviorType;

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
     * 1-浏览,2-加入购物车,3-购买,4-收藏
     */
    public Integer getBehaviorType() {
        return behaviorType;
    }

    /**
     * 1-浏览,2-加入购物车,3-购买,4-收藏
     */
    public void setBehaviorType(Integer behaviorType) {
        this.behaviorType = behaviorType;
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
        UserBehavior other = (UserBehavior) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getItemId() == null ? other.getItemId() == null : this.getItemId().equals(other.getItemId()))
            && (this.getBehaviorType() == null ? other.getBehaviorType() == null : this.getBehaviorType().equals(other.getBehaviorType()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getItemId() == null) ? 0 : getItemId().hashCode());
        result = prime * result + ((getBehaviorType() == null) ? 0 : getBehaviorType().hashCode());
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
        sb.append(", userId=").append(userId);
        sb.append(", itemId=").append(itemId);
        sb.append(", behaviorType=").append(behaviorType);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}