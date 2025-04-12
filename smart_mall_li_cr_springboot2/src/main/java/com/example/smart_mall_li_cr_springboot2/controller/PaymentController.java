package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.mapper.MallOrderMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.MallOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付相关控制器
 */
@RestController
@RequestMapping("/payment")
@CrossOrigin
public class PaymentController {

    @Autowired
    private MallOrderMapper mallOrderMapper;

    /**
     * 模拟支付成功，更新订单状态
     * @param orderId 订单ID
     * @return 支付结果
     */
    @PostMapping("/pay")
    public Result<Map<String, Object>> payOrder(@RequestParam(value = "orderId", required = false) Integer orderId,
                                              @RequestBody(required = false) Map<String, Object> requestBody) {
        // 如果从 URL 参数中没有获取到 orderId，尝试从请求体中获取
        if (orderId == null && requestBody != null && requestBody.containsKey("orderId")) {
            Object orderIdObj = requestBody.get("orderId");
            if (orderIdObj instanceof Integer) {
                orderId = (Integer) orderIdObj;
            } else if (orderIdObj instanceof String) {
                try {
                    orderId = Integer.parseInt((String) orderIdObj);
                } catch (NumberFormatException e) {
                    return Result.error("订单ID格式不正确");
                }
            }
        }
        try {
            if (orderId == null) {
                return Result.error("订单ID不能为空");
            }

            // 查询订单
            MallOrder order = mallOrderMapper.selectByPrimaryKey(orderId.longValue());
            if (order == null) {
                return Result.error("订单不存在");
            }

            // 检查订单状态
            if (order.getStatus() != 0) {
                return Result.error("订单状态不正确，无法支付");
            }

            // 更新订单状态为已支付
            MallOrder updateOrder = new MallOrder();
            updateOrder.setId(order.getId());
            updateOrder.setStatus(1); // 1: 已支付/待发货
            updateOrder.setPaymentTime(new Date());

            int rows = mallOrderMapper.updateByPrimaryKeySelective(updateOrder);

            if (rows > 0) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("orderId", orderId);
                resultMap.put("status", 1);
                resultMap.put("paymentTime", updateOrder.getPaymentTime());
                return Result.success(resultMap, "支付成功");
            } else {
                return Result.error("支付失败，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("支付处理异常：" + e.getMessage());
        }
    }
}
