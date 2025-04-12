package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.pojo.UserBehavior;
import com.example.smart_mall_li_cr_springboot2.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户行为控制器
 */
@RestController
@RequestMapping("/behavior")
@CrossOrigin
public class UserBehaviorController {
    
    @Autowired
    private UserBehaviorService userBehaviorService;
    
    /**
     * 记录用户行为
     * @param userId 用户ID
     * @param itemId 商品ID
     * @param behaviorType 行为类型：1-浏览,2-加入购物车,3-购买,4-收藏
     * @return 操作结果
     */
    @PostMapping("/record")
    public Result<Boolean> recordBehavior(
            @RequestParam("userId") Integer userId,
            @RequestParam("itemId") Integer itemId,
            @RequestParam("behaviorType") Integer behaviorType) {
        
        boolean success = userBehaviorService.recordBehavior(userId, itemId, behaviorType);
        if (success) {
            return Result.success(true, "记录成功");
        } else {
            return Result.error("记录失败");
        }
    }
    
    /**
     * 获取用户的行为记录
     * @param userId 用户ID
     * @param behaviorType 行为类型，可选
     * @return 用户行为记录列表
     */
    @GetMapping("/user")
    public Result<List<UserBehavior>> getUserBehaviors(
            @RequestParam("userId") Integer userId,
            @RequestParam(value = "behaviorType", required = false) Integer behaviorType) {
        
        List<UserBehavior> behaviors = userBehaviorService.getUserBehaviors(userId, behaviorType);
        return Result.success(behaviors);
    }
    
    /**
     * 获取商品的行为记录
     * @param itemId 商品ID
     * @param behaviorType 行为类型，可选
     * @return 商品行为记录列表
     */
    @GetMapping("/item")
    public Result<List<UserBehavior>> getItemBehaviors(
            @RequestParam("itemId") Integer itemId,
            @RequestParam(value = "behaviorType", required = false) Integer behaviorType) {
        
        List<UserBehavior> behaviors = userBehaviorService.getItemBehaviors(itemId, behaviorType);
        return Result.success(behaviors);
    }
}
