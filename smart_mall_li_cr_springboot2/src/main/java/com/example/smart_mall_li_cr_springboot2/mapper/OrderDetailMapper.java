package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.OrderDetail;

/**
* @author ll.xl
* @description 针对表【order_detail】的数据库操作Mapper
* @createDate 2025-03-20 14:45:51
* @Entity com.example.smart_mall_li_cr_springboot2.pojo.OrderDetail
*/
public interface OrderDetailMapper {

    int deleteByPrimaryKey(Long id);

    int insert(OrderDetail record);

    int insertSelective(OrderDetail record);

    OrderDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderDetail record);

    int updateByPrimaryKey(OrderDetail record);

}
