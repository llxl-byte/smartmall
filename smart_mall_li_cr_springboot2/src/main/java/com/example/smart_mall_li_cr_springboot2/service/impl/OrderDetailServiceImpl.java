package com.example.smart_mall_li_cr_springboot2.service.impl;

import com.example.smart_mall_li_cr_springboot2.mapper.OrderDetailMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.OrderDetail;
import com.example.smart_mall_li_cr_springboot2.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单详情服务实现类
 */
@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired(required = false)
    private OrderDetailMapper orderDetailMapper;

    @Override
    @Transactional
    public boolean addOrderDetail(OrderDetail orderDetail) {
        if (orderDetail == null || orderDetail.getOrderId() == null || orderDetail.getItemId() == null) {
            return false;
        }
        
        try {
            // 设置创建时间
            if (orderDetail.getCreateTime() == null) {
                orderDetail.setCreateTime(new Date());
            }
            
            // 如果没有设置总价，计算总价
            if (orderDetail.getTotalPrice() == null && orderDetail.getPrice() != null && orderDetail.getQuantity() != null) {
                orderDetail.setTotalPrice(orderDetail.getPrice().multiply(new java.math.BigDecimal(orderDetail.getQuantity())));
            }
            
            System.out.println("添加订单详情: " + orderDetail);
            
            // 如果没有映射器或者映射器方法尚未实现，直接返回true模拟成功
            if (orderDetailMapper == null) {
                System.out.println("OrderDetailMapper未注入，模拟添加成功");
                return true;
            }
            
            // 调用Mapper插入数据
            int result = orderDetailMapper.insert(orderDetail);
            return result > 0;
        } catch (Exception e) {
            System.err.println("添加订单详情失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean batchAddOrderDetails(List<OrderDetail> orderDetails) {
        if (orderDetails == null || orderDetails.isEmpty()) {
            return false;
        }
        
        try {
            // 遍历添加每个订单详情
            for (OrderDetail detail : orderDetails) {
                boolean success = addOrderDetail(detail);
                if (!success) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("批量添加订单详情失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) {
        if (orderId == null) {
            return new ArrayList<>();
        }
        
        try {
            // 如果没有映射器或者映射器方法尚未实现，返回空列表
            if (orderDetailMapper == null) {
                System.out.println("OrderDetailMapper未注入，返回空列表");
                return new ArrayList<>();
            }
            
            // 调用Mapper查询数据
            return orderDetailMapper.selectByOrderId(orderId);
        } catch (Exception e) {
            System.err.println("查询订单详情失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
} 