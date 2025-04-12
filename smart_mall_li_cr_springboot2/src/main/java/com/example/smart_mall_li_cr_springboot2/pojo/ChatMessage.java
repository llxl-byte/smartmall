package com.example.smart_mall_li_cr_springboot2.pojo;

import lombok.Data;
import java.util.Date;

/**
 * 聊天消息实体类
 */
@Data
public class ChatMessage {
    private Integer id;
    private Integer userId;
    private String content;
    private Boolean isFromUser;
    private Date createTime;

    // Getter and Setter methods
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsFromUser() {
        return isFromUser;
    }

    public void setIsFromUser(Boolean isFromUser) {
        this.isFromUser = isFromUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
