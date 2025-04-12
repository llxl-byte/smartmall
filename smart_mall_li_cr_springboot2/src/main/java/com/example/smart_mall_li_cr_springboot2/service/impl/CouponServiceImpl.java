package com.example.smart_mall_li_cr_springboot2.service.impl;

import com.example.smart_mall_li_cr_springboot2.dto.AvailableCouponsRequestDTO;
import com.example.smart_mall_li_cr_springboot2.dto.CouponDTO;
import com.example.smart_mall_li_cr_springboot2.mapper.CouponMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.Coupon; // 假设 POJO 类名为 Coupon
import com.example.smart_mall_li_cr_springboot2.service.CouponService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service // 标记为 Spring 的 Service 组件
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper; // 注入 Mapper

    @Override
    public List<CouponDTO> findAvailableCoupons(AvailableCouponsRequestDTO requestDTO) {
        Long userId = requestDTO.getUserId();
        BigDecimal totalAmount = requestDTO.getTotalAmount();
        // List<Long> itemIds = requestDTO.getItemIds(); // 暂未使用

        // 1. 从数据库查询该用户所有未使用的、未过期的优惠券
        //    假设 CouponMapper 有一个方法 findUserValidCoupons(userId, currentDate)
        Date currentDate = new Date();
        List<Coupon> userCoupons = couponMapper.findUserValidCoupons(userId, currentDate);

        if (userCoupons == null || userCoupons.isEmpty()) {
            return new ArrayList<>(); // 没有查到，返回空列表
        }

        // 2. 根据订单金额过滤掉不满足使用门槛的优惠券
        List<Coupon> filteredCoupons = userCoupons.stream()
                .filter(coupon -> totalAmount.compareTo(coupon.getThreshold()) >= 0) // 过滤掉未达到门槛的
                // TODO: 还可以添加其他过滤条件，比如是否适用于特定商品 (需要 itemIds)
                .collect(Collectors.toList());

        // 3. 将过滤后的 Coupon POJO 列表转换为 CouponDTO 列表
        List<CouponDTO> resultDTOs = filteredCoupons.stream()
                .map(this::convertToDTO) // 调用转换方法
                .collect(Collectors.toList());

        return resultDTOs;
    }

    /**
     * 将 Coupon POJO 转换为 CouponDTO
     * @param coupon 数据库实体对象
     * @return DTO 对象
     */
    private CouponDTO convertToDTO(Coupon coupon) {
        CouponDTO dto = new CouponDTO();
        // 使用 Spring 的 BeanUtils 或手动映射字段
        BeanUtils.copyProperties(coupon, dto);
        // 注意：如果字段名或类型不完全匹配，需要手动设置
        // dto.setValidityEnd(coupon.getExpiryDate()); // 例子：如果字段名不同
        return dto;
    }
} 