package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.pojo.Item;

import java.util.List;

/**
 * 推荐系统服务接口
 */
public interface RecommendationService {
    
    /**
     * 获取热门商品推荐（基于销量）
     * @param limit 限制返回数量
     * @return 商品列表
     */
    List<Item> getHotItems(Integer limit);
    
    /**
     * 获取分类下的热门商品推荐（基于销量）
     * @param categoryId 分类ID
     * @param limit 限制返回数量
     * @return 商品列表
     */
    List<Item> getHotItemsByCategory(Integer categoryId, Integer limit);
    
    /**
     * 获取用户的个性化推荐商品
     * @param userId 用户ID
     * @param limit 限制返回数量
     * @return 商品列表
     */
    List<Item> getPersonalizedRecommendations(Integer userId, Integer limit);
}
