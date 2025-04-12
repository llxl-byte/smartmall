package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.pojo.ChatMessage;
import com.example.smart_mall_li_cr_springboot2.pojo.Faq;
import com.example.smart_mall_li_cr_springboot2.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天控制器
 */
@RestController
@RequestMapping("/chat")
@CrossOrigin
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    /**
     * 发送消息并获取回复
     * @param userId 用户ID
     * @param content 消息内容
     * @return 回复消息
     */
    @PostMapping("/send")
    public Map<String, Object> sendMessage(
            @RequestParam("userId") Integer userId,
            @RequestParam("content") String content) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            String reply = chatService.sendMessage(userId, content);
            result.put("success", true);
            result.put("reply", reply);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "发送消息失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取聊天历史记录
     * @param userId 用户ID
     * @return 聊天历史记录
     */
    @GetMapping("/history/{userId}")
    public Map<String, Object> getChatHistory(@PathVariable("userId") Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ChatMessage> chatHistory = chatService.getChatHistory(userId);
            result.put("success", true);
            result.put("data", chatHistory);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取聊天历史失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取所有常见问题
     * @return 常见问题列表
     */
    @GetMapping("/faqs")
    public Map<String, Object> getAllFaqs() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Faq> faqs = chatService.getAllFaqs();
            result.put("success", true);
            result.put("data", faqs);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取常见问题失败：" + e.getMessage());
        }
        return result;
    }
}
