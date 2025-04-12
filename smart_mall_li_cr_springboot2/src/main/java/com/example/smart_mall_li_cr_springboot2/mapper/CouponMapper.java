package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
// import org.apache.ibatis.annotations.Select; // 如果使用注解方式

import java.util.Date;
import java.util.List;

/**
 * 优惠券 Mapper 接口
 */
@Mapper // 标记为 MyBatis Mapper 接口
public interface CouponMapper {

    /**
     * 查询用户所有未使用的、且在有效期内的优惠券
     *
     * @param userId 用户ID
     * @param currentDate 当前日期 (用于比较有效期)
     * @return 用户有效的优惠券列表
     */
    List<Coupon> findUserValidCoupons(@Param("userId") Long userId, @Param("currentDate") Date currentDate);

    // 如果使用 Mybatis-Plus，可以继承 BaseMapper<Coupon>
    // extends BaseMapper<Coupon>

    // 示例：如果使用注解方式定义 SQL
    /*
    @Select("SELECT * FROM coupon " +
            "WHERE user_id = #{userId} " +
            "AND status = 0 " + // 假设 0 代表未使用
            "AND expiry_date >= #{currentDate}")
    List<Coupon> findUserValidCoupons(@Param("userId") Long userId, @Param("currentDate") Date currentDate);
    */

    /**
     * 根据ID和用户ID查询优惠券，并锁定行 (防止并发使用)
     * @param id 优惠券ID
     * @param userId 用户ID
     * @return 优惠券对象
     */
    Coupon findByIdAndUserIdForUpdate(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 更新优惠券状态为已使用
     * @param id 优惠券ID
     * @param status 新状态 (例如 1)
     * @param useTime 使用时间
     * @param orderId 关联的订单号
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("useTime") Date useTime, @Param("orderId") String orderId);

    // 未来可以添加根据 ID 查询、更新状态等方法

} 