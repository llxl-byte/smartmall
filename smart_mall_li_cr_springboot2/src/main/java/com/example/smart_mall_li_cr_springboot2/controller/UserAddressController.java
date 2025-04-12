package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.pojo.UserAddress;
import com.example.smart_mall_li_cr_springboot2.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户收货地址 Controller
 */
@RestController
@RequestMapping("/address")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    /**
     * 获取用户的收货地址列表
     * @param userId 用户ID
     * @return 收货地址列表
     */
    @GetMapping("/list")
    public Result<List<UserAddress>> getAddressList(@RequestParam Integer userId) {
        try {
            List<UserAddress> addressList = userAddressService.getAddressList(userId);
            return new Result<>(true, "获取成功", addressList);
        } catch (Exception e) {
            return new Result<>(false, "获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户的默认收货地址
     * @param userId 用户ID
     * @return 默认收货地址
     */
    @GetMapping("/default")
    public Result<UserAddress> getDefaultAddress(@RequestParam Integer userId) {
        try {
            UserAddress defaultAddress = userAddressService.getDefaultAddress(userId);
            if (defaultAddress != null) {
                return new Result<>(true, "获取成功", defaultAddress);
            } else {
                return new Result<>(false, "未设置默认地址");
            }
        } catch (Exception e) {
            return new Result<>(false, "获取失败：" + e.getMessage());
        }
    }

    /**
     * 添加收货地址
     * @param address 收货地址信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<UserAddress> addAddress(@RequestBody UserAddress address) {
        return userAddressService.addAddress(address);
    }

    /**
     * 更新收货地址
     * @param address 收货地址信息
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result<UserAddress> updateAddress(@RequestBody UserAddress address) {
        return userAddressService.updateAddress(address);
    }

    /**
     * 删除收货地址
     * @param id 收货地址ID
     * @param userId 用户ID
     * @return 操作结果
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteAddress(@RequestParam Long id, @RequestParam Integer userId) {
        return userAddressService.deleteAddress(id, userId);
    }

    /**
     * 设置默认收货地址
     * @param id 收货地址ID
     * @param userId 用户ID
     * @return 操作结果
     */
    @PostMapping("/setDefault")
    public Result<Boolean> setDefaultAddress(@RequestParam Long id, @RequestParam Integer userId) {
        return userAddressService.setDefaultAddress(id, userId);
    }
}
