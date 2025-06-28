package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.ChatMessage;
import java.util.List;

/**
 * 聊天消息Mapper接口
 */
public interface ChatMessageMapper {
    /**
     * 插入聊天消息
     * @param chatMessage 聊天消息
     * @return 影响行数
     */
    int insert(ChatMessage chatMessage);
    
    /**
     * 根据用户ID查询聊天记录
     * @param userId 用户ID
     * @return 聊天记录列表
     */
    List<ChatMessage> selectByUserId(Integer userId);
}
