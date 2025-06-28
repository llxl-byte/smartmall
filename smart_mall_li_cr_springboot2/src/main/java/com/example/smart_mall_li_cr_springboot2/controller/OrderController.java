package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.dto.CreateOrderRequestDTO;
import com.example.smart_mall_li_cr_springboot2.dto.CreateOrderResponseDTO;
import com.example.smart_mall_li_cr_springboot2.mapper.MallOrderMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.MallOrder;
import com.example.smart_mall_li_cr_springboot2.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MallOrderMapper mallOrderMapper;

    /**
     * 创建新订单
     * @param requestDTO 订单创建请求
     * @return 创建结果，包含订单号和支付金额
     */
    @PostMapping("/add")
    public Result<CreateOrderResponseDTO> createOrder(@RequestBody CreateOrderRequestDTO requestDTO) {
        System.out.println("收到创建订单请求: " + requestDTO);

        // 参数校验
        if (requestDTO.getUserId() == null || requestDTO.getAddressId() == null ||
            requestDTO.getItems() == null || requestDTO.getItems().isEmpty()) {
            return Result.error("缺少必要的参数 (userId, addressId, items)");
        }
        for (CreateOrderRequestDTO.OrderItemDTO item : requestDTO.getItems()) {
            if (item.getItemId() == null || item.getQuantity() == null || item.getQuantity() <= 0) {
                return Result.error("订单商品信息不完整或数量无效");
            }
        }

        // 调用 OrderService 处理订单创建逻辑
        try {
            CreateOrderResponseDTO responseDTO = orderService.createOrder(requestDTO);
            return Result.success(responseDTO);
        } catch (Exception e) { // 捕获 Service 抛出的业务异常
            System.err.println("创建订单时发生错误: " + e.getMessage());
            // 将业务异常信息返回给前端
            return Result.error("订单创建失败: " + e.getMessage());
        }

        // --- 移除模拟成功数据 ---
        /*
        CreateOrderResponseDTO mockResponse = new CreateOrderResponseDTO();
        mockResponse.setOrderId("MOCK_" + System.currentTimeMillis());
        BigDecimal mockPaymentAmount = new BigDecimal("99.99");
        mockResponse.setPaymentAmount(mockPaymentAmount);
        return Result.success(mockResponse);
        */
        // --- 模拟数据结束 ---
    }

    // 未来可以添加查询订单列表、订单详情、取消订单等接口

    /**
     * 确认收货
     * @param orderId 订单ID
     * @return 操作结果
     */
    @PostMapping("/confirm")
    public Result<Map<String, Object>> confirmOrder(@RequestParam("orderId") Integer orderId) {
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
            if (order.getStatus() != 2) { // 2: 待收货
                return Result.error("订单状态不正确，无法确认收货");
            }

            // 更新订单状态为已完成
            MallOrder updateOrder = new MallOrder();
            updateOrder.setId(order.getId());
            updateOrder.setStatus(3); // 3: 已完成
            updateOrder.setConfirmTime(new Date());

            int rows = mallOrderMapper.updateByPrimaryKeySelective(updateOrder);

            if (rows > 0) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("orderId", orderId);
                resultMap.put("status", 3);
                resultMap.put("confirmTime", updateOrder.getConfirmTime());
                return Result.success(resultMap, "确认收货成功");
            } else {
                return Result.error("确认收货失败，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("确认收货处理异常：" + e.getMessage());
        }
    }
}