package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.pojo.Comment;

import java.util.List;
import java.util.Map;

/**
 * 评价服务接口
 */
public interface CommentService {
    
    /**
     * 根据商品ID查询评价列表
     * @param itemId 商品ID
     * @return 评价列表
     */
    List<Map<String, Object>> getCommentsByItemId(Integer itemId);
    
    /**
     * 根据商品ID查询评价列表（分页）
     * @param itemId 商品ID
     * @param page 页码
     * @param size 每页大小
     * @return 评价列表
     */
    List<Map<String, Object>> getCommentsByItemId(Integer itemId, Integer page, Integer size);
    
    /**
     * 添加商品评价
     * @param comment 评价对象
     * @return 是否添加成功
     */
    boolean addComment(Comment comment);
    
    /**
     * 检查用户是否可以评价商品
     * @param userId 用户ID
     * @param itemId 商品ID
     * @param orderId 订单ID
     * @return 是否可以评价
     */
    boolean canComment(Integer userId, Integer itemId, Integer orderId);
    
    /**
     * 获取商品的评分统计
     * @param itemId 商品ID
     * @return 评分统计信息
     */
    Map<String, Object> getItemRatingSummary(Integer itemId);
}
