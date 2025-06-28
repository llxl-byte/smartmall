package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.mapper.UserAddressMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.UserAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 用户收货地址 Service 实现类
 */
@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public List<UserAddress> getAddressList(Integer userId) {
        return userAddressMapper.selectByUserId(userId);
    }

    @Override
    public UserAddress getDefaultAddress(Integer userId) {
        return userAddressMapper.selectDefaultByUserId(userId);
    }

    @Override
    @Transactional
    public Result<UserAddress> addAddress(UserAddress address) {
        try {
            // 设置创建时间和更新时间
            address.setCreateTime(new Date());
            address.setUpdateTime(new Date());
            
            // 如果是默认地址，需要将该用户的其他地址设为非默认
            if (address.getIsDefault() != null && address.getIsDefault() == 1) {
                userAddressMapper.resetDefaultByUserId(address.getUserId());
            }
            
            // 插入地址
            userAddressMapper.insertSelective(address);
            
            return new Result<>(true, "添加成功", address);
        } catch (Exception e) {
            return new Result<>(false, "添加失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<UserAddress> updateAddress(UserAddress address) {
        try {
            // 查询原地址信息，验证权限
            UserAddress existingAddress = userAddressMapper.selectByPrimaryKey(address.getId().longValue());
            if (existingAddress == null) {
                return new Result<>(false, "地址不存在");
            }
            
            if (!existingAddress.getUserId().equals(address.getUserId())) {
                return new Result<>(false, "无权限修改此地址");
            }
            
            // 设置更新时间
            address.setUpdateTime(new Date());
            
            // 如果是默认地址，需要将该用户的其他地址设为非默认
            if (address.getIsDefault() != null && address.getIsDefault() == 1) {
                userAddressMapper.resetDefaultByUserId(address.getUserId());
            }
            
            // 更新地址
            userAddressMapper.updateByPrimaryKeySelective(address);
            
            // 查询更新后的地址信息
            UserAddress updatedAddress = userAddressMapper.selectByPrimaryKey(address.getId().longValue());
            
            return new Result<>(true, "更新成功", updatedAddress);
        } catch (Exception e) {
            return new Result<>(false, "更新失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Boolean> deleteAddress(Long id, Integer userId) {
        try {
            // 查询地址信息，验证权限
            UserAddress existingAddress = userAddressMapper.selectByPrimaryKey(id);
            if (existingAddress == null) {
                return new Result<>(false, "地址不存在");
            }
            
            if (!existingAddress.getUserId().equals(userId)) {
                return new Result<>(false, "无权限删除此地址");
            }
            
            // 删除地址
            userAddressMapper.deleteByPrimaryKey(id);
            
            return new Result<>(true, "删除成功", true);
        } catch (Exception e) {
            return new Result<>(false, "删除失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Boolean> setDefaultAddress(Long id, Integer userId) {
        try {
            // 查询地址信息，验证权限
            UserAddress existingAddress = userAddressMapper.selectByPrimaryKey(id);
            if (existingAddress == null) {
                return new Result<>(false, "地址不存在");
            }
            
            if (!existingAddress.getUserId().equals(userId)) {
                return new Result<>(false, "无权限设置此地址为默认地址");
            }
            
            // 将该用户的所有地址设为非默认
            userAddressMapper.resetDefaultByUserId(userId);
            
            // 将当前地址设为默认
            UserAddress updateAddress = new UserAddress();
            updateAddress.setId(existingAddress.getId());
            updateAddress.setIsDefault(1);
            updateAddress.setUpdateTime(new Date());
            userAddressMapper.updateByPrimaryKeySelective(updateAddress);
            
            return new Result<>(true, "设置成功", true);
        } catch (Exception e) {
            return new Result<>(false, "设置失败：" + e.getMessage());
        }
    }
}
