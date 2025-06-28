package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.pojo.UserAddress;

import java.util.List;

/**
 * 用户收货地址 Service 接口
 */
public interface UserAddressService {

    /**
     * 获取用户的收货地址列表
     * @param userId 用户ID
     * @return 收货地址列表
     */
    List<UserAddress> getAddressList(Integer userId);

    /**
     * 获取用户的默认收货地址
     * @param userId 用户ID
     * @return 默认收货地址，如果没有则返回null
     */
    UserAddress getDefaultAddress(Integer userId);

    /**
     * 添加收货地址
     * @param address 收货地址信息
     * @return 操作结果
     */
    Result<UserAddress> addAddress(UserAddress address);

    /**
     * 更新收货地址
     * @param address 收货地址信息
     * @return 操作结果
     */
    Result<UserAddress> updateAddress(UserAddress address);

    /**
     * 删除收货地址
     * @param id 收货地址ID
     * @param userId 用户ID（用于验证权限）
     * @return 操作结果
     */
    Result<Boolean> deleteAddress(Long id, Integer userId);

    /**
     * 设置默认收货地址
     * @param id 收货地址ID
     * @param userId 用户ID（用于验证权限）
     * @return 操作结果
     */
    Result<Boolean> setDefaultAddress(Long id, Integer userId);
}
