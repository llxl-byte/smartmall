package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.pojo.UserBehavior;

import java.util.List;

/**
 * 用户行为服务接口
 */
public interface UserBehaviorService {
    
    /**
     * 记录用户行为
     * @param userId 用户ID
     * @param itemId 商品ID
     * @param behaviorType 行为类型：1-浏览,2-加入购物车,3-购买,4-收藏
     * @return 是否记录成功
     */
    boolean recordBehavior(Integer userId, Integer itemId, Integer behaviorType);
    
    /**
     * 获取用户的行为记录
     * @param userId 用户ID
     * @param behaviorType 行为类型，可为null表示查询所有类型
     * @return 用户行为记录列表
     */
    List<UserBehavior> getUserBehaviors(Integer userId, Integer behaviorType);
    
    /**
     * 获取商品的行为记录
     * @param itemId 商品ID
     * @param behaviorType 行为类型，可为null表示查询所有类型
     * @return 商品行为记录列表
     */
    List<UserBehavior> getItemBehaviors(Integer itemId, Integer behaviorType);
}
