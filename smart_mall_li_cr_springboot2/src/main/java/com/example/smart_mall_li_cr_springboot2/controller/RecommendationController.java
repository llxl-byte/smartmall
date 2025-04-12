package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.pojo.Item;
import com.example.smart_mall_li_cr_springboot2.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推荐系统控制器
 */
@RestController
@RequestMapping("/recommend")
@CrossOrigin
public class RecommendationController {
    
    @Autowired
    private RecommendationService recommendationService;
    
    /**
     * 获取热门商品推荐
     * @param limit 限制返回数量，默认10
     * @return 热门商品列表
     */
    @GetMapping("/hot")
    public Result<List<Item>> getHotItems(
            @RequestParam(value = "limit", required = false) Integer limit) {
        
        List<Item> items = recommendationService.getHotItems(limit);
        return Result.success(items);
    }
    
    /**
     * 获取分类下的热门商品推荐
     * @param categoryId 分类ID
     * @param limit 限制返回数量，默认10
     * @return 分类下的热门商品列表
     */
    @GetMapping("/category/{categoryId}")
    public Result<List<Item>> getHotItemsByCategory(
            @PathVariable("categoryId") Integer categoryId,
            @RequestParam(value = "limit", required = false) Integer limit) {
        
        List<Item> items = recommendationService.getHotItemsByCategory(categoryId, limit);
        return Result.success(items);
    }
    
    /**
     * 获取用户的个性化推荐商品
     * @param userId 用户ID
     * @param limit 限制返回数量，默认10
     * @return 推荐商品列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<Item>> getPersonalizedRecommendations(
            @PathVariable("userId") Integer userId,
            @RequestParam(value = "limit", required = false) Integer limit) {
        
        List<Item> items = recommendationService.getPersonalizedRecommendations(userId, limit);
        return Result.success(items);
    }
}
