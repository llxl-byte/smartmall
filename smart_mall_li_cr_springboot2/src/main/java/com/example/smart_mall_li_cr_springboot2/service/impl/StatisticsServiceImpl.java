package com.example.smart_mall_li_cr_springboot2.service.impl;

import com.example.smart_mall_li_cr_springboot2.mapper.MallOrderMapper; // 可能需要
import com.example.smart_mall_li_cr_springboot2.mapper.MallUserMapper; // 可能需要
import com.example.smart_mall_li_cr_springboot2.mapper.UserBehaviorMapper; // 可能需要
import com.example.smart_mall_li_cr_springboot2.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired(required = false) // 可能需要注入相关Mapper
    private MallOrderMapper mallOrderMapper;

    @Autowired(required = false) // 可能需要注入相关Mapper
    private MallUserMapper mallUserMapper;

    @Autowired(required = false) // 可能需要注入相关Mapper
    private UserBehaviorMapper userBehaviorMapper;

    @Override
    public List<Map<String, Object>> getSalesStatistics() {
        System.out.println("执行商品销量统计...");
        // 调用 MallOrderMapper 中的方法获取销量统计数据
        // 假设 MallOrderMapper 中有 getSalesStatistics 方法
        return mallOrderMapper.getSalesStatistics();
    }

    @Override
    public Map<String, Object> getUserActivityStatistics() {
        System.out.println("执行用户活跃度统计...");
        // 调用 MallUserMapper 和 UserBehaviorMapper 中的方法获取用户活跃度统计数据
        // 假设 MallUserMapper 中有 getTotalUserCount 和 getNewUserCountToday 方法
        // 假设 UserBehaviorMapper 中有 getActiveUserCountLast7Days 方法

        Map<String, Object> activityData = new HashMap<>();

        // 获取总用户数
        Integer totalUsers = mallUserMapper.getTotalUserCount();
        activityData.put("totalUsers", totalUsers != null ? totalUsers : 0);

        // 获取今日新增用户数
        Integer newUsersToday = mallUserMapper.getNewUserCountToday();
        activityData.put("newUsersToday", newUsersToday != null ? newUsersToday : 0);

        // 获取最近7天的活跃用户数
        String endTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        String startTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L));
        Integer activeUsersLast7Days = userBehaviorMapper.countActiveUsersByTimeRange(startTime, endTime);
        activityData.put("activeUsersLast7Days", activeUsersLast7Days != null ? activeUsersLast7Days : 0);

        return activityData;
    }

    // 可以根据需要添加其他统计方法的实现
}