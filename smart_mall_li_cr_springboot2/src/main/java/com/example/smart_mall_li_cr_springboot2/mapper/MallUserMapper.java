package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.MallUser;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUserLoginParam;
import com.example.smart_mall_li_cr_springboot2.pojo.MallUserRegisterParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List; // 新增导入
import org.apache.ibatis.annotations.Param;

/**
* @author ll.xl
* @description 针对表【mall_user】的数据库操作Mapper
* @createDate 2025-03-20 14:45:51
* @Entity com.example.smart_mall_li_cr_springboot2.pojo.MallUser
*/
@Mapper
public interface MallUserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(MallUser record);

    int insertSelective(MallUser record);

    MallUser selectByPrimaryKey(Integer id); // 修改参数类型为 Integer

    int updateByPrimaryKeySelective(MallUser record);

    int updateByPrimaryKey(MallUser record);



    MallUser selectByUsername(String username);
    MallUser selectByUsernameAndPassword(String username, String password);

    boolean mallUserRegister(MallUserRegisterParam mallUserRegisterParam);
    MallUser mallUserLogin(MallUserLoginParam mallUserLoginParam);
    boolean updateUserAvatar(@Param("userId") Integer userId, @Param("avatarUrl") String avatarUrl);
    boolean updateUserInfo(MallUser user);

    /**
     * 查询所有商城用户列表
     * @return 所有用户列表
     */
    List<MallUser> selectAll();

    /**
     * 获取总用户数
     * @return 总用户数
     */
    Integer getTotalUserCount();

    /**
     * 获取今日新增用户数
     * @return 今日新增用户数
     */
    Integer getNewUserCountToday();

    // 可以根据需要添加其他用户活跃度相关的统计方法，例如统计最近活跃用户等
}
