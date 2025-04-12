package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.UserBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ll.xl
* @description 针对表【user_behavior】的数据库操作Mapper
* @createDate 2025-03-20 14:45:51
* @Entity com.example.smart_mall_li_cr_springboot2.pojo.UserBehavior
*/
@Mapper
public interface UserBehaviorMapper {

    int deleteByPrimaryKey(Long id);

    int insert(UserBehavior record);

    int insertSelective(UserBehavior record);

    UserBehavior selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserBehavior record);

    int updateByPrimaryKey(UserBehavior record);

    /**
     * 根据用户ID查询行为记录
     * @param userId 用户ID
     * @param behaviorType 行为类型，可为null
     * @return 行为记录列表
     */
    List<UserBehavior> selectByUserId(@Param("userId") Integer userId, @Param("behaviorType") Integer behaviorType);

    /**
     * 根据商品ID查询行为记录
     * @param itemId 商品ID
     * @param behaviorType 行为类型，可为null
     * @return 行为记录列表
     */
    List<UserBehavior> selectByItemId(@Param("itemId") Integer itemId, @Param("behaviorType") Integer behaviorType);

    /**
     * 统计商品的行为次数
     * @param itemId 商品ID
     * @param behaviorType 行为类型
     * @return 行为次数
     */
    int countByItemIdAndType(@Param("itemId") Integer itemId, @Param("behaviorType") Integer behaviorType);
}
