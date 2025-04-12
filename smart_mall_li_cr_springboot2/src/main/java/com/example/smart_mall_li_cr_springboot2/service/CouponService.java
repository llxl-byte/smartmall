package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.dto.AvailableCouponsRequestDTO;
import com.example.smart_mall_li_cr_springboot2.dto.CouponDTO;

import java.util.List;

/**
 * 优惠券服务接口
 */
public interface CouponService {

    /**
     * 根据请求条件查找用户在当前订单可用的优惠券
     *
     * @param requestDTO 包含 userId, itemIds, totalAmount 的请求对象
     * @return 可用的优惠券 DTO 列表
     */
    List<CouponDTO> findAvailableCoupons(AvailableCouponsRequestDTO requestDTO);

    // 未来可以添加其他方法，如：领取优惠券、核销优惠券等
} 