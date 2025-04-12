package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.mapper.ChatMessageMapper;
import com.example.smart_mall_li_cr_springboot2.mapper.FaqMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.ChatMessage;
import com.example.smart_mall_li_cr_springboot2.pojo.Faq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 聊天服务实现类
 */
@Service
public class ChatServiceImpl implements ChatService {
    
    @Autowired
    private ChatMessageMapper chatMessageMapper;
    
    @Autowired
    private FaqMapper faqMapper;
    
    @Override
    public String sendMessage(Integer userId, String content) {
        // 保存用户消息
        ChatMessage userMessage = new ChatMessage();
        userMessage.setUserId(userId);
        userMessage.setContent(content);
        userMessage.setIsFromUser(true);
        chatMessageMapper.insert(userMessage);
        
        // 根据用户消息内容查找匹配的FAQ
        List<Faq> matchedFaqs = faqMapper.searchByKeyword(content);
        
        // 生成回复消息
        String reply;
        if (!matchedFaqs.isEmpty()) {
            // 使用最匹配的FAQ作为回复
            reply = matchedFaqs.get(0).getAnswer();
        } else {
            // 默认回复
            reply = "抱歉，我无法理解您的问题。您可以尝试其他问题，或者联系人工客服。";
        }
        
        // 保存系统回复消息
        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setUserId(userId);
        systemMessage.setContent(reply);
        systemMessage.setIsFromUser(false);
        chatMessageMapper.insert(systemMessage);
        
        return reply;
    }
    
    @Override
    public List<ChatMessage> getChatHistory(Integer userId) {
        return chatMessageMapper.selectByUserId(userId);
    }
    
    @Override
    public List<Faq> getAllFaqs() {
        return faqMapper.selectAllOrderBySort();
    }
}
