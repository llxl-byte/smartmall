package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.UserAddress;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户收货地址 Mapper 接口
 * @author ll.xl
 * @description 针对表【user_address】的数据库操作Mapper
 * @createDate 2025-03-28
 * @Entity com.example.smart_mall_li_cr_springboot2.pojo.UserAddress
 */
public interface UserAddressMapper {

    /**
     * 根据主键删除
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入记录
     * @param record 记录
     * @return 影响行数
     */
    int insert(UserAddress record);

    /**
     * 选择性插入记录
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(UserAddress record);

    /**
     * 根据主键查询
     * @param id 主键ID
     * @return 记录
     */
    UserAddress selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(UserAddress record);

    /**
     * 根据主键更新
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(UserAddress record);

    /**
     * 根据用户ID查询收货地址列表
     * @param userId 用户ID
     * @return 收货地址列表
     */
    List<UserAddress> selectByUserId(Integer userId);

    /**
     * 根据用户ID查询默认收货地址
     * @param userId 用户ID
     * @return 默认收货地址
     */
    UserAddress selectDefaultByUserId(Integer userId);

    /**
     * 将用户的所有地址设置为非默认
     * @param userId 用户ID
     * @return 影响行数
     */
    int resetDefaultByUserId(Integer userId);

    /**
     * 根据地址ID和用户ID查询地址信息
     * @param id 地址ID
     * @param userId 用户ID
     * @return 地址对象
     */
    UserAddress findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
