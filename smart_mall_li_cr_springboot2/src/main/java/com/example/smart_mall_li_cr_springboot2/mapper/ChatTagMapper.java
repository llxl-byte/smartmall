package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.ChatTag;
import java.util.List;

/**
 * 聊天标签Mapper接口
 */
public interface ChatTagMapper {
    
    /**
     * 查询所有聊天标签，按排序字段排序
     * @return 聊天标签列表
     */
    List<ChatTag> selectAllOrderBySort();
    
    /**
     * 根据ID查询聊天标签
     * @param id 标签ID
     * @return 聊天标签
     */
    ChatTag selectById(Integer id);
    
    /**
     * 插入聊天标签
     * @param chatTag 聊天标签
     * @return 影响行数
     */
    int insert(ChatTag chatTag);
    
    /**
     * 更新聊天标签
     * @param chatTag 聊天标签
     * @return 影响行数
     */
    int update(ChatTag chatTag);
    
    /**
     * 删除聊天标签
     * @param id 标签ID
     * @return 影响行数
     */
    int deleteById(Integer id);
}
