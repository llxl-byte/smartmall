package com.example.smart_mall_li_cr_springboot2.service.impl;

import com.example.smart_mall_li_cr_springboot2.mapper.CommentMapper;
import com.example.smart_mall_li_cr_springboot2.mapper.MallOrderMapper;
import com.example.smart_mall_li_cr_springboot2.mapper.MallUserMapper;
import com.example.smart_mall_li_cr_springboot2.mapper.OrderDetailMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.Comment;
import com.example.smart_mall_li_cr_springboot2.pojo.MallOrder;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUser;
import com.example.smart_mall_li_cr_springboot2.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 评价服务实现类
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private MallUserMapper mallUserMapper;

    @Autowired
    private MallOrderMapper mallOrderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public List<Map<String, Object>> getCommentsByItemId(Integer itemId) {
        // 默认查询10条评价
        return getCommentsByItemId(itemId, 1, 10);
    }

    @Override
    public List<Map<String, Object>> getCommentsByItemId(Integer itemId, Integer page, Integer size) {
        // 参数校验
        if (itemId == null || itemId <= 0) {
            return new ArrayList<>();
        }

        if (page == null || page < 1) {
            page = 1;
        }

        if (size == null || size < 1) {
            size = 10;
        }

        // 计算偏移量
        int offset = (page - 1) * size;

        // 查询评价列表
        List<Comment> comments = commentMapper.selectByItemId(itemId, offset, size);

        // 转换为前端需要的格式
        List<Map<String, Object>> result = new ArrayList<>();
        for (Comment comment : comments) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", comment.getId());
            map.put("content", comment.getContent());
            map.put("rating", comment.getRating());
            map.put("createTime", comment.getCreateTime());

            // 处理图片
            if (comment.getImages() != null && !comment.getImages().isEmpty()) {
                // 假设images字段存储的是JSON格式的图片URL数组
                // 这里简单处理，实际可能需要JSON解析
                map.put("images", Arrays.asList(comment.getImages().split(",")));
            } else {
                map.put("images", new ArrayList<>());
            }

            // 获取用户信息
            MallUser user = mallUserMapper.selectByPrimaryKey(comment.getUserId().longValue());
            if (user != null) {
                // 如果是匿名评价，隐藏用户名
                if (comment.getIsAnonymous() != null && comment.getIsAnonymous() == 1) {
                    map.put("username", "匿名用户");
                    map.put("avatar", "/static/default_avatar.png");
                } else {
                    map.put("username", user.getNickname() != null ? user.getNickname() : user.getUsername());
                    map.put("avatar", user.getAvatar() != null ? user.getAvatar() : "/static/default_avatar.png");
                }
            } else {
                map.put("username", "未知用户");
                map.put("avatar", "/static/default_avatar.png");
            }

            result.add(map);
        }

        return result;
    }

    @Override
    public boolean addComment(Comment comment) {
        // 参数校验
        if (comment == null || comment.getUserId() == null || comment.getItemId() == null ||
            comment.getOrderId() == null || comment.getContent() == null) {
            return false;
        }

        // 检查是否可以评价
        if (!canComment(comment.getUserId(), comment.getItemId(), comment.getOrderId())) {
            return false;
        }

        // 设置默认值
        if (comment.getRating() == null) {
            comment.setRating(5); // 默认5星
        }

        if (comment.getIsAnonymous() == null) {
            comment.setIsAnonymous(0); // 默认不匿名
        }

        comment.setCreateTime(new Date());

        // 保存评价
        int rows = commentMapper.insertSelective(comment);

        return rows > 0;
    }

    @Override
    public boolean canComment(Integer userId, Integer itemId, Integer orderId) {
        // 参数校验
        if (userId == null || itemId == null || orderId == null) {
            return false;
        }

        // 检查订单是否存在且属于该用户
        MallOrder order = mallOrderMapper.selectByPrimaryKey(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }

        // 检查订单状态是否已完成
        if (order.getStatus() == null || order.getStatus() < 3) { // 假设状态3表示已完成
            return false;
        }

        // 检查订单中是否包含该商品
        boolean hasItem = orderDetailMapper.checkOrderContainsItem(orderId, itemId);
        if (!hasItem) {
            return false;
        }

        // 检查是否已经评价过
        boolean hasCommented = commentMapper.checkUserHasCommented(userId, itemId, orderId);

        return !hasCommented;
    }

    @Override
    public Map<String, Object> getItemRatingSummary(Integer itemId) {
        Map<String, Object> result = new HashMap<>();

        if (itemId == null || itemId <= 0) {
            return result;
        }

        // 获取评分统计
        Map<String, Object> stats = commentMapper.getItemRatingStats(itemId);

        if (stats != null) {
            result.putAll(stats);
        }

        return result;
    }
}
