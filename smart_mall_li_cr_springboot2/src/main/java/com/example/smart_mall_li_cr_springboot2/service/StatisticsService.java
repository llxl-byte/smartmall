package com.example.smart_mall_li_cr_springboot2.service;

import java.util.List;
import java.util.Map;

/**
 * 数据统计服务接口
 */
public interface StatisticsService {

    // 在这里添加数据统计相关的方法声明，例如：

    /**
     * 获取商品销量统计数据
     * @return 销量统计列表
     */
    List<Map<String, Object>> getSalesStatistics();

    /**
     * 获取用户活跃度统计数据
     * @return 用户活跃度统计数据
     */
    Map<String, Object> getUserActivityStatistics();

    // 可以根据需要添加其他统计方法，例如按时间范围统计等
}