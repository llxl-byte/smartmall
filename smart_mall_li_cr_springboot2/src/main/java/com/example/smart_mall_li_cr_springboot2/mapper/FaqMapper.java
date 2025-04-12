package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.Faq;
import java.util.List;

/**
* @author ll.xl
* @description 针对表【faq】的数据库操作Mapper
* @createDate 2025-03-20 14:45:51
* @Entity com.example.smart_mall_li_cr_springboot2.pojo.Faq
*/
public interface FaqMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Faq record);

    int insertSelective(Faq record);

    Faq selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Faq record);

    int updateByPrimaryKey(Faq record);

    /**
     * 查询所有FAQ，按排序字段排序
     * @return FAQ列表
     */
    List<Faq> selectAllOrderBySort();

    /**
     * 根据关键词搜索FAQ
     * @param keyword 关键词
     * @return 匹配的FAQ列表
     */
    List<Faq> searchByKeyword(String keyword);
}
