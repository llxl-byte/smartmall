package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.pojo.MallOrder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import com.example.smart_mall_li_cr_springboot2.pojo.AddMallOrderParam;
import com.example.smart_mall_li_cr_springboot2.service.MallOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class MallOrderController {
    @Autowired
    MallOrderService mallOrderService;
    @RequestMapping("/addMallOrder")
    public boolean AddMallOrder(AddMallOrderParam addMallOrderParam) {
        return mallOrderService.AddMallOrder(addMallOrderParam);
    }


    /**
     * 根据用户ID获取订单列表
     * @param userId 用户ID
     * @return 包含订单列表的Result对象
     */
    @GetMapping("/orders") // 映射 GET /orders 请求
    public Result<List<MallOrder>> getOrdersByUserId(@RequestParam("userId") Integer userId) {
        // 调用 Service 层获取订单列表
        List<MallOrder> orderList = mallOrderService.getOrdersByUserId(userId);
        // 使用 Result.success 包装成功结果并返回
        return Result.success(orderList);
    }
}
