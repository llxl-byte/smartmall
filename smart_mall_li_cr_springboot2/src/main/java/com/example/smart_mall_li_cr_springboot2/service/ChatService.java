package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.pojo.ChatMessage;
import com.example.smart_mall_li_cr_springboot2.pojo.Faq;

import java.util.List;

/**
 * 聊天服务接口
 */
public interface ChatService {
    
    /**
     * 保存用户消息并获取回复
     * @param userId 用户ID
     * @param content 用户消息内容
     * @return 系统回复消息
     */
    String sendMessage(Integer userId, String content);
    
    /**
     * 获取用户的聊天历史记录
     * @param userId 用户ID
     * @return 聊天记录列表
     */
    List<ChatMessage> getChatHistory(Integer userId);
    
    /**
     * 获取所有常见问题
     * @return 常见问题列表
     */
    List<Faq> getAllFaqs();
}
