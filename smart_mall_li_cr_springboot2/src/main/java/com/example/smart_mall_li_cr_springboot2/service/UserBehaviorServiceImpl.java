package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.mapper.UserBehaviorMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.UserBehavior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 用户行为服务实现类
 */
@Service
public class UserBehaviorServiceImpl implements UserBehaviorService {

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    @Override
    public boolean recordBehavior(Integer userId, Integer itemId, Integer behaviorType) {
        if (userId == null || itemId == null || behaviorType == null) {
            return false;
        }

        // 创建用户行为对象
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setItemId(itemId);
        behavior.setBehaviorType(behaviorType);
        behavior.setCreateTime(new Date());

        // 插入记录
        int result = userBehaviorMapper.insertSelective(behavior);
        return result > 0;
    }

    @Override
    public List<UserBehavior> getUserBehaviors(Integer userId, Integer behaviorType) {
        if (userId == null) {
            return null;
        }
        return userBehaviorMapper.selectByUserId(userId, behaviorType);
    }

    @Override
    public List<UserBehavior> getItemBehaviors(Integer itemId, Integer behaviorType) {
        if (itemId == null) {
            return null;
        }
        return userBehaviorMapper.selectByItemId(itemId, behaviorType);
    }

    @Override
    public int countUserBehaviors(Integer userId, Integer behaviorType) {
        if (userId == null) {
            return 0;
        }

        try {
            // 调用Mapper层的方法进行统计
            // 如果没有直接的统计方法，可以先获取列表再计算大小
            List<UserBehavior> behaviors = userBehaviorMapper.selectByUserId(userId, behaviorType);
            return behaviors != null ? behaviors.size() : 0;

            // 如果Mapper中有直接的统计方法，可以使用下面的代码
            // return userBehaviorMapper.countByUserIdAndType(userId, behaviorType);
        } catch (Exception e) {
            System.err.println("统计用户行为数量失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}
