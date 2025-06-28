package com.example.smart_mall_li_cr_springboot2.mapper;

import com.example.smart_mall_li_cr_springboot2.pojo.MallOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单主表记录
     * @param order 订单对象
     * @return 影响行数
     */
    int insert(MallOrder order);

    // 未来可以添加根据订单号查询、更新状态等方法
} 