package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.mapper.ItemMapper;
import com.example.smart_mall_li_cr_springboot2.mapper.UserBehaviorMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.Item;
import com.example.smart_mall_li_cr_springboot2.pojo.UserBehavior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐系统服务实现类
 */
@Service
public class RecommendationServiceImpl implements RecommendationService {
    
    @Autowired
    private ItemMapper itemMapper;
    
    @Autowired
    private UserBehaviorMapper userBehaviorMapper;
    
    @Override
    public List<Item> getHotItems(Integer limit) {
        // 默认返回10个热门商品
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return itemMapper.selectBySalesDesc(limit);
    }
    
    @Override
    public List<Item> getHotItemsByCategory(Integer categoryId, Integer limit) {
        if (categoryId == null) {
            return getHotItems(limit);
        }
        
        // 默认返回10个热门商品
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        
        return itemMapper.selectByCategoryIdAndSalesDesc(categoryId, limit);
    }
    
    @Override
    public List<Item> getPersonalizedRecommendations(Integer userId, Integer limit) {
        if (userId == null) {
            return getHotItems(limit);
        }
        
        // 默认返回10个推荐商品
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        
        // 获取用户的浏览、购买和收藏记录
        List<UserBehavior> viewBehaviors = userBehaviorMapper.selectByUserId(userId, 1); // 浏览行为
        List<UserBehavior> cartBehaviors = userBehaviorMapper.selectByUserId(userId, 2); // 加入购物车行为
        List<UserBehavior> purchaseBehaviors = userBehaviorMapper.selectByUserId(userId, 3); // 购买行为
        List<UserBehavior> favoriteBehaviors = userBehaviorMapper.selectByUserId(userId, 4); // 收藏行为
        
        // 如果用户没有任何行为记录，返回热门商品
        if ((viewBehaviors == null || viewBehaviors.isEmpty()) &&
            (cartBehaviors == null || cartBehaviors.isEmpty()) &&
            (purchaseBehaviors == null || purchaseBehaviors.isEmpty()) &&
            (favoriteBehaviors == null || favoriteBehaviors.isEmpty())) {
            return getHotItems(limit);
        }
        
        // 简单的协同过滤算法实现
        // 1. 收集用户交互过的商品ID
        Set<Integer> interactedItemIds = new HashSet<>();
        if (viewBehaviors != null) {
            interactedItemIds.addAll(viewBehaviors.stream().map(UserBehavior::getItemId).collect(Collectors.toSet()));
        }
        if (cartBehaviors != null) {
            interactedItemIds.addAll(cartBehaviors.stream().map(UserBehavior::getItemId).collect(Collectors.toSet()));
        }
        if (purchaseBehaviors != null) {
            interactedItemIds.addAll(purchaseBehaviors.stream().map(UserBehavior::getItemId).collect(Collectors.toSet()));
        }
        if (favoriteBehaviors != null) {
            interactedItemIds.addAll(favoriteBehaviors.stream().map(UserBehavior::getItemId).collect(Collectors.toSet()));
        }
        
        // 2. 获取热门商品，但排除用户已经交互过的商品
        List<Item> hotItems = getHotItems(limit * 2); // 获取更多热门商品，以便有足够的候选项
        List<Item> recommendations = hotItems.stream()
                .filter(item -> !interactedItemIds.contains(item.getId()))
                .limit(limit)
                .collect(Collectors.toList());
        
        // 如果推荐数量不足，补充一些热门商品
        if (recommendations.size() < limit) {
            int remaining = limit - recommendations.size();
            List<Item> additionalItems = hotItems.stream()
                    .filter(item -> !recommendations.contains(item))
                    .limit(remaining)
                    .collect(Collectors.toList());
            recommendations.addAll(additionalItems);
        }
        
        return recommendations;
    }
}
