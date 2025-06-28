package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.mapper.ChatMessageMapper;
import com.example.smart_mall_li_cr_springboot2.mapper.ChatTagMapper;
import com.example.smart_mall_li_cr_springboot2.mapper.FaqMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.ChatMessage;
import com.example.smart_mall_li_cr_springboot2.pojo.ChatTag;
import com.example.smart_mall_li_cr_springboot2.pojo.Faq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    private ChatTagMapper chatTagMapper;

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

    @Override
    public List<ChatTag> getAllTags() {
        try {
            // 尝试从数据库获取聊天标签
            return chatTagMapper.selectAllOrderBySort();
        } catch (Exception e) {
            // 如果发生异常（例如表不存在），返回模拟数据
            System.err.println("获取聊天标签失败，返回模拟数据: " + e.getMessage());
            List<ChatTag> mockTags = new ArrayList<>();

            // 创建一些模拟标签
            ChatTag tag1 = new ChatTag();
            tag1.setId(1);
            tag1.setName("商品咨询");
            tag1.setSort(1);
            mockTags.add(tag1);

            ChatTag tag2 = new ChatTag();
            tag2.setId(2);
            tag2.setName("订单问题");
            tag2.setSort(2);
            mockTags.add(tag2);

            ChatTag tag3 = new ChatTag();
            tag3.setId(3);
            tag3.setName("售后服务");
            tag3.setSort(3);
            mockTags.add(tag3);

            ChatTag tag4 = new ChatTag();
            tag4.setId(4);
            tag4.setName("物流配送");
            tag4.setSort(4);
            mockTags.add(tag4);

            ChatTag tag5 = new ChatTag();
            tag5.setId(5);
            tag5.setName("账户问题");
            tag5.setSort(5);
            mockTags.add(tag5);

            return mockTags;
        }
    }
}
