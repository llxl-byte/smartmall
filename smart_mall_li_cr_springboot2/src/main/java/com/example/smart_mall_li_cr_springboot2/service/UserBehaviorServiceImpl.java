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
}
