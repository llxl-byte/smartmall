package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.dto.AvailableCouponsRequestDTO;
import com.example.smart_mall_li_cr_springboot2.dto.CouponDTO;
import com.example.smart_mall_li_cr_springboot2.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin
public class CouponController {

    // 注入 Service
    @Autowired
    private CouponService couponService;

    /**
     * 获取当前订单可用的优惠券列表
     * @param requestDTO 包含 userId, itemIds, totalAmount
     * @return 可用优惠券列表
     */
    @PostMapping("/available")
    public Result<List<CouponDTO>> getAvailableCoupons(@RequestBody AvailableCouponsRequestDTO requestDTO) {
        // 校验参数 ( userId 是否存在等)
        if (requestDTO.getUserId() == null || requestDTO.getTotalAmount() == null) {
            return Result.error("缺少必要的参数 (userId, totalAmount)");
        }

        System.out.println("收到获取可用优惠券请求: " + requestDTO);

        // 调用 Service 层方法获取真实的可用优惠券列表
        List<CouponDTO> availableCoupons = couponService.findAvailableCoupons(requestDTO);

        // --- 移除模拟数据 ---
        /*
        List<CouponDTO> mockCoupons = new ArrayList<>();
        // 模拟一些优惠券数据，注意金额使用 BigDecimal
        CouponDTO coupon1 = new CouponDTO();
        coupon1.setId(1L);
        coupon1.setName("满100减10元");
        coupon1.setDiscountAmount(new BigDecimal("10.00"));
        coupon1.setThreshold(new BigDecimal("100.00"));
        coupon1.setDescription("全场通用，满100元可用");

        CouponDTO coupon2 = new CouponDTO();
        coupon2.setId(3L);
        coupon2.setName("无门槛减2元");
        coupon2.setDiscountAmount(new BigDecimal("2.00"));
        coupon2.setThreshold(new BigDecimal("0.00"));
        coupon2.setDescription("全场通用，无门槛");

        // 模拟根据总金额筛选
        if (requestDTO.getTotalAmount().compareTo(coupon1.getThreshold()) >= 0) {
            mockCoupons.add(coupon1);
        }
        if (requestDTO.getTotalAmount().compareTo(coupon2.getThreshold()) >= 0) {
            mockCoupons.add(coupon2);
        }
        */
        // --- 模拟数据结束 ---

        // return Result.success(mockCoupons); // 返回模拟数据
        return Result.success(availableCoupons); // 实际应返回 Service 查询结果
    }

    /**
     * 获取用户的所有优惠券
     * @param userId 用户ID
     * @return 用户的优惠券列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<CouponDTO>> getUserCoupons(@PathVariable("userId") Long userId) {
        System.out.println("获取用户优惠券，用户ID: " + userId);
        try {
            // 调用Service层获取用户的优惠券
            List<CouponDTO> coupons = couponService.getUserCoupons(userId);
            return Result.success(coupons, "获取用户优惠券成功");
        } catch (Exception e) {
            System.err.println("获取用户优惠券失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取用户优惠券失败");
        }
    }

    // 未来可以添加其他优惠券相关的接口，比如领取优惠券等

    /**
     * 获取用户优惠券数量
     * @param userId 用户ID
     * @return 优惠券数量
     */
    @GetMapping("/count")
    public Result<Integer> getCouponCount(@RequestParam("userId") Integer userId) {
        System.out.println("获取用户优惠券数量，用户ID: " + userId);
        try {
            // 调用Service层获取优惠券数量
            int count = couponService.countUserCoupons(userId);
            return Result.success(count);
        } catch (Exception e) {
            System.err.println("获取用户优惠券数量失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取优惠券数量失败");
        }
    }
}