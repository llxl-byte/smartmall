package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.pojo.Comment;
import com.example.smart_mall_li_cr_springboot2.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 评价控制器
 */
@RestController
@RequestMapping("/comment")
@CrossOrigin
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    /**
     * 获取商品评价列表
     * @param itemId 商品ID
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @return 评价列表
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getCommentList(
            @RequestParam("itemId") Integer itemId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        
        try {
            List<Map<String, Object>> comments;
            if (page != null && size != null) {
                comments = commentService.getCommentsByItemId(itemId, page, size);
            } else {
                comments = commentService.getCommentsByItemId(itemId);
            }
            return Result.success(comments);
        } catch (Exception e) {
            return Result.error("获取评价失败：" + e.getMessage());
        }
    }
    
    /**
     * 添加商品评价
     * @param comment 评价对象
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Boolean> addComment(@RequestBody Comment comment) {
        try {
            // 设置创建时间
            comment.setCreateTime(new Date());
            
            // 检查是否可以评价
            if (!commentService.canComment(comment.getUserId(), comment.getItemId(), comment.getOrderId())) {
                return Result.error("您已经评价过该商品或无权评价");
            }
            
            boolean success = commentService.addComment(comment);
            if (success) {
                return Result.success(true, "评价成功");
            } else {
                return Result.error("评价失败，请稍后重试");
            }
        } catch (Exception e) {
            return Result.error("评价失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取商品评分统计
     * @param itemId 商品ID
     * @return 评分统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getItemRatingStats(@RequestParam("itemId") Integer itemId) {
        try {
            Map<String, Object> stats = commentService.getItemRatingSummary(itemId);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取评分统计失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查用户是否可以评价商品
     * @param userId 用户ID
     * @param itemId 商品ID
     * @param orderId 订单ID
     * @return 是否可以评价
     */
    @GetMapping("/check")
    public Result<Boolean> checkCanComment(
            @RequestParam("userId") Integer userId,
            @RequestParam("itemId") Integer itemId,
            @RequestParam("orderId") Integer orderId) {
        
        try {
            boolean canComment = commentService.canComment(userId, itemId, orderId);
            return Result.success(canComment);
        } catch (Exception e) {
            return Result.error("检查评价权限失败：" + e.getMessage());
        }
    }
}
