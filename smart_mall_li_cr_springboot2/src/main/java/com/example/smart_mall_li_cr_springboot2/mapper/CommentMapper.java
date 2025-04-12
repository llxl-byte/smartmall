package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author ll.xl
* @description 针对表【comment】的数据库操作Mapper
* @createDate 2025-03-20 14:45:51
* @Entity com.example.smart_mall_li_cr_springboot2.pojo.Comment
*/
@Mapper
public interface CommentMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Comment record);

    int insertSelective(Comment record);

    Comment selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Comment record);

    int updateByPrimaryKey(Comment record);

    /**
     * 根据商品ID查询评价列表
     * @param itemId 商品ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 评价列表
     */
    List<Comment> selectByItemId(@Param("itemId") Integer itemId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 检查用户是否已经评价过该订单中的商品
     * @param userId 用户ID
     * @param itemId 商品ID
     * @param orderId 订单ID
     * @return 是否已评价
     */
    boolean checkUserHasCommented(@Param("userId") Integer userId, @Param("itemId") Integer itemId, @Param("orderId") Integer orderId);

    /**
     * 获取商品的评分统计信息
     * @param itemId 商品ID
     * @return 评分统计信息
     */
    Map<String, Object> getItemRatingStats(@Param("itemId") Integer itemId);
}
