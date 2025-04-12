package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.pojo.MallOrder;
import com.example.smart_mall_li_cr_springboot2.pojo.OrderDetail;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.example.smart_mall_li_cr_springboot2.pojo.AddMallOrderParam;
import com.example.smart_mall_li_cr_springboot2.service.MallOrderService;
import com.example.smart_mall_li_cr_springboot2.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class MallOrderController {
    @Autowired
    MallOrderService mallOrderService;
    
    @Autowired(required = false)
    OrderDetailService orderDetailService; // 可能不存在，所以标记为非必须
    
    @RequestMapping("/addMallOrder")
    public boolean AddMallOrder(AddMallOrderParam addMallOrderParam) {
        System.out.println("接收到订单提交请求: " + addMallOrderParam);
        try {
            return mallOrderService.AddMallOrder(addMallOrderParam);
        } catch (Exception e) {
            System.err.println("提交订单处理失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加订单详情
     * @param orderDetail 订单详情对象
     * @return 是否添加成功
     */
    @RequestMapping("/addOrderDetail")
    @ResponseBody
    public boolean addOrderDetail(OrderDetail orderDetail) {
        System.out.println("接收到订单详情提交请求: " + orderDetail);
        try {
            if (orderDetailService != null) {
                return orderDetailService.addOrderDetail(orderDetail);
            } else {
                System.out.println("OrderDetailService未注入，无法处理订单详情");
                return false;
            }
        } catch (Exception e) {
            System.err.println("添加订单详情失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建完整订单（包含订单详情）
     * 解决前端404错误问题
     */
    @RequestMapping("/createFullOrder")
    @ResponseBody
    public Result<Map<String, Object>> createFullOrder(@RequestBody Map<String, Object> requestData) {
        System.out.println("接收到完整订单创建请求: " + requestData);
        
        try {
            // 从请求中提取userId
            Integer userId = null;
            if (requestData.containsKey("userId")) {
                userId = Integer.parseInt(requestData.get("userId").toString());
            }
            
            // 从请求中提取地址信息
            String addressInfo = null;
            if (requestData.containsKey("addressInfo")) {
                addressInfo = requestData.get("addressInfo").toString();
            }
            
            // 检查必要参数
            if (userId == null || addressInfo == null) {
                return Result.error("缺少必要参数：userId或addressInfo");
            }
            
            // 检查是否是直接购买
            boolean isDirectBuy = requestData.containsKey("directBuy") && 
                                  Boolean.TRUE.equals(requestData.get("directBuy"));
            boolean isFromCart = requestData.containsKey("fromCart") && 
                                 Boolean.TRUE.equals(requestData.get("fromCart"));
            
            System.out.println("订单类型: " + (isDirectBuy ? "直接购买" : isFromCart ? "购物车结算" : "未知来源"));
            
            // 创建订单参数对象
            AddMallOrderParam param = new AddMallOrderParam();
            param.setUserId(userId);
            param.setAddressInfo(addressInfo);
            
            // 如果有商品列表，将其转为JSON字符串
            if (requestData.containsKey("items")) {
                try {
                    Object itemsObj = requestData.get("items");
                    // 使用Jackson正确处理JSON转换，而不是简单的toString
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    String itemsJson = mapper.writeValueAsString(itemsObj);
                    param.setItemsList(itemsJson);
                    System.out.println("商品列表JSON: " + itemsJson);
                } catch (Exception e) {
                    System.err.println("商品列表转换为JSON失败: " + e.getMessage());
                    // 如果JSON转换失败，尝试使用toString方法
                    Object itemsObj = requestData.get("items");
                    String itemsJson = itemsObj.toString();
                    param.setItemsList(itemsJson);
                    System.out.println("使用toString处理商品列表: " + itemsJson);
                }
            } else {
                System.out.println("请求中没有找到items参数");
            }
            
            // 增加一个标记，表明这是直接购买的订单
            if (isDirectBuy) {
                System.out.println("这是直接购买的订单");
                // 如果没有设置itemsList，但有items数组，将其设置到itemsList
                if (param.getItemsList() == null && requestData.containsKey("items")) {
                    try {
                        Object itemsObj = requestData.get("items");
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        String itemsJson = mapper.writeValueAsString(itemsObj);
                        param.setItemsList(itemsJson);
                        System.out.println("直接购买商品列表: " + itemsJson);
                    } catch (Exception e) {
                        System.err.println("处理直接购买商品列表失败: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            
            // 调用订单服务创建订单
            boolean success = mallOrderService.AddMallOrder(param);
            
            if (success) {
                // 构建返回数据
                Map<String, Object> resultData = new java.util.HashMap<>();
                resultData.put("userId", userId);
                resultData.put("orderCreated", true);
                resultData.put("timestamp", new java.util.Date().getTime());
                
                // 对直接购买添加特殊标识
                if (isDirectBuy) {
                    resultData.put("orderType", "direct_buy");
                    resultData.put("directBuySuccess", true);
                } else if (isFromCart) {
                    resultData.put("orderType", "cart_checkout");
                    resultData.put("cartCheckoutSuccess", true);
                }
                
                // 明确返回success=true
                return Result.success(resultData, "订单创建成功");
            } else {
                Map<String, Object> errorData = new java.util.HashMap<>();
                errorData.put("userId", userId);
                errorData.put("orderCreated", false);
                errorData.put("orderType", isDirectBuy ? "direct_buy" : "cart_checkout");
                errorData.put("reason", "订单服务处理失败");
                return Result.error("订单创建失败", errorData);
            }
        } catch (Exception e) {
            System.err.println("创建完整订单失败: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorData = new java.util.HashMap<>();
            errorData.put("error", e.getMessage());
            return Result.error("创建订单时发生错误: " + e.getMessage(), errorData);
        }
    }

    /**
     * 根据用户ID获取订单列表
     * @param userId 用户ID
     * @return 包含订单列表的Result对象
     */
    @GetMapping("/orders") // 映射 GET /orders 请求
    public Result<List<MallOrder>> getOrdersByUserId(@RequestParam("userId") Integer userId) {
        System.out.println("查询用户订单列表，用户ID: " + userId);
        try {
            // 调用 Service 层获取订单列表
            List<MallOrder> orderList = mallOrderService.getOrdersByUserId(userId);
            // 使用 Result.success 包装成功结果并返回
            return Result.success(orderList);
        } catch (Exception e) {
            System.err.println("查询用户订单列表失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("查询订单失败");
        }
    }
}
