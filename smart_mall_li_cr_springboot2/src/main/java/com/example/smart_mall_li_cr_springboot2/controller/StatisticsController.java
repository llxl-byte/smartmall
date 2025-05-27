package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计控制器
 */
@RestController
@RequestMapping("/api/stats") // 修改为前端请求的路径
@CrossOrigin(originPatterns = {"https://*.app.github.dev", "http://localhost:*", "http://127.0.0.1:*"}, allowCredentials = "true")
public class StatisticsController {

    // 注入统计服务，目前未使用，但保留以便后续扩展
    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取统计摘要数据
     * @return 统计摘要数据
     */
    @GetMapping("/summary")
    public Result<Map<String, Object>> getSummaryData() {
        try {
            // 创建模拟数据（实际项目中应从数据库获取）
            Map<String, Object> summaryData = new HashMap<>();
            summaryData.put("totalSales", 1256);
            summaryData.put("totalOrders", 328);
            summaryData.put("totalUsers", 520);
            summaryData.put("userActivity", 68);
            summaryData.put("salesTrend", 12.5);
            summaryData.put("ordersTrend", 8.3);
            summaryData.put("usersTrend", 15.2);
            summaryData.put("activityTrend", 5.7);

            return Result.success(summaryData, "获取统计摘要成功");
        } catch (Exception e) {
            return Result.error("获取统计摘要失败: " + e.getMessage());
        }
    }

    /**
     * 获取销量趋势数据
     * @return 销量趋势数据
     */
    @GetMapping("/sales-trend")
    public Result<List<Map<String, Object>>> getSalesTrend() {
        try {
            // 创建模拟数据（实际项目中应从数据库获取）
            List<Map<String, Object>> trendData = new ArrayList<>();

            // 模拟最近7天的销量数据
            String[] dates = {"2025-05-01", "2025-05-02", "2025-05-03", "2025-05-04", "2025-05-05", "2025-05-06", "2025-05-07"};
            int[] values = {120, 132, 101, 134, 90, 230, 210};

            for (int i = 0; i < dates.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("date", dates[i]);
                item.put("value", values[i]);
                trendData.add(item);
            }

            return Result.success(trendData, "获取销量趋势成功");
        } catch (Exception e) {
            return Result.error("获取销量趋势失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户活跃度趋势数据
     * @return 用户活跃度趋势数据
     */
    @GetMapping("/activity-trend")
    public Result<List<Map<String, Object>>> getActivityTrend() {
        try {
            // 创建模拟数据（实际项目中应从数据库获取）
            List<Map<String, Object>> trendData = new ArrayList<>();

            // 模拟最近7天的用户活跃度数据
            String[] dates = {"2025-05-01", "2025-05-02", "2025-05-03", "2025-05-04", "2025-05-05", "2025-05-06", "2025-05-07"};
            int[] values = {65, 59, 80, 81, 56, 55, 70};

            for (int i = 0; i < dates.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("date", dates[i]);
                item.put("value", values[i]);
                trendData.add(item);
            }

            return Result.success(trendData, "获取用户活跃度趋势成功");
        } catch (Exception e) {
            return Result.error("获取用户活跃度趋势失败: " + e.getMessage());
        }
    }

    /**
     * 获取商品销量排行数据
     * @return 商品销量排行数据
     */
    @GetMapping("/item-sales")
    public Result<List<Map<String, Object>>> getItemSales() {
        try {
            // 创建模拟数据（实际项目中应从数据库获取）
            List<Map<String, Object>> salesData = new ArrayList<>();

            // 模拟热门商品销量数据
            String[] names = {"Xiaomi15Ultra", "HuaWei-Mate60", "iPhone 15", "OPPO Find X", "vivo X100"};
            int[] values = {320, 302, 301, 290, 250};

            for (int i = 0; i < names.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", names[i]);
                item.put("value", values[i]);
                salesData.add(item);
            }

            return Result.success(salesData, "获取商品销量排行成功");
        } catch (Exception e) {
            return Result.error("获取商品销量排行失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户地域分布数据
     * @return 用户地域分布数据
     */
    @GetMapping("/user-region")
    public Result<List<Map<String, Object>>> getUserRegion() {
        try {
            // 创建模拟数据（实际项目中应从数据库获取）
            List<Map<String, Object>> regionData = new ArrayList<>();

            // 模拟用户地域分布数据
            String[] regions = {"北京", "上海", "广州", "深圳", "杭州", "其他"};
            int[] values = {120, 110, 90, 82, 70, 128};

            for (int i = 0; i < regions.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", regions[i]);
                item.put("value", values[i]);
                regionData.add(item);
            }

            return Result.success(regionData, "获取用户地域分布成功");
        } catch (Exception e) {
            return Result.error("获取用户地域分布失败: " + e.getMessage());
        }
    }
}