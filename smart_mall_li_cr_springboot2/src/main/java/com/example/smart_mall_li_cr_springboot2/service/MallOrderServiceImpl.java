package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.mapper.CartMapper;
import com.example.smart_mall_li_cr_springboot2.mapper.ItemMapper;
import com.example.smart_mall_li_cr_springboot2.mapper.MallOrderMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.AddMallOrderParam;
import com.example.smart_mall_li_cr_springboot2.pojo.Cart;
import com.example.smart_mall_li_cr_springboot2.pojo.Item;
import com.example.smart_mall_li_cr_springboot2.pojo.MallOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.HashMap;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class MallOrderServiceImpl implements MallOrderService {
    @Autowired
    private MallOrderMapper mallOrderMapper;
    @Autowired
    private CartService cartService;
    @Autowired
    private ItemMapper itemMapper;
    //生成订单号，日期+最后四位随机数
    private String generateOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        Random random = new Random();
        int randomNum = random.nextInt(10000);
        return timestamp + String.format("%04d", randomNum);
    }

    @Override
    @Transactional/*事物是一组操作，要么都执行，要么都不执行
                   添加了 @Transactional 注解确保事务完整性
                   */

    public boolean AddMallOrder(AddMallOrderParam addMallOrderParam) {
        // 验证参数
        if (addMallOrderParam == null || addMallOrderParam.getUserId() == null) {
            System.err.println("订单参数无效: userId为空");
            return false;
        }
        
        try {
            // 打印出完整的请求参数，帮助调试
            System.out.println("接收到订单请求参数: " + addMallOrderParam);
            
            // 获取商品清单 - 可能来自itemsList或购物车
            List<Cart> cartList = null;
            
            // 计算订单总金额
            BigDecimal totalAmount = new BigDecimal("0");
            
            // 如果提供了itemsList，则首先尝试从中获取商品信息
            String itemsListJson = addMallOrderParam.getItemsList();
            boolean hasItemsList = itemsListJson != null && !itemsListJson.isEmpty();
            
            if (hasItemsList) {
                try {
                    System.out.println("使用前端传递的商品列表: " + itemsListJson);
                    
                    // 使用Jackson解析JSON
                    ObjectMapper objectMapper = new ObjectMapper();
                    cartList = new ArrayList<>();
                    
                    // 简化JSON解析逻辑，统一处理
                    try {
                        // 尝试作为数组解析
                        List<Map<String, Object>> itemsArray;
                        
                        if (itemsListJson.trim().startsWith("[")) {
                            // 是数组格式
                            itemsArray = objectMapper.readValue(itemsListJson, new TypeReference<List<Map<String, Object>>>() {});
                            System.out.println("成功解析商品列表数组，包含" + itemsArray.size() + "个商品项");
                        } else if (itemsListJson.trim().startsWith("{")) {
                            // 单个对象格式，包装成数组
                            Map<String, Object> singleItem = objectMapper.readValue(itemsListJson, new TypeReference<Map<String, Object>>() {});
                            itemsArray = new ArrayList<>();
                            itemsArray.add(singleItem);
                            System.out.println("成功解析单个商品项，并包装为数组");
                        } else {
                            System.err.println("无法识别的JSON格式，既不是数组也不是对象: " + itemsListJson);
                            itemsArray = new ArrayList<>();
                        }
                        
                        // 统一处理商品项
                        for (Map<String, Object> item : itemsArray) {
                            System.out.println("处理商品项: " + item);
                            
                            Cart cart = new Cart();
                            cart.setUserId(addMallOrderParam.getUserId());
                            cart.setSelected(1); // 默认选中
                            
                            // 处理itemId
                            Object itemIdObj = item.get("itemId");
                            if (itemIdObj == null) {
                                itemIdObj = item.get("id");
                            }
                            
                            if (itemIdObj != null) {
                                int itemId;
                                if (itemIdObj instanceof Integer) {
                                    itemId = (Integer) itemIdObj;
                                } else if (itemIdObj instanceof Long) {
                                    itemId = ((Long) itemIdObj).intValue();
                                } else if (itemIdObj instanceof String) {
                                    itemId = Integer.parseInt((String) itemIdObj);
                                } else {
                                    itemId = Integer.parseInt(itemIdObj.toString());
                                }
                                cart.setItemId(itemId);
                                System.out.println("设置商品ID: " + itemId);
                            } else {
                                System.err.println("商品项没有itemId或id字段");
                                continue; // 跳过这个无效的商品项
                            }
                            
                            // 处理数量
                            int quantity = 1; // 默认数量为1
                            Object quantityObj = item.get("quantity");
                            if (quantityObj != null) {
                                if (quantityObj instanceof Integer) {
                                    quantity = (Integer) quantityObj;
                                } else if (quantityObj instanceof String) {
                                    quantity = Integer.parseInt((String) quantityObj);
                                } else {
                                    quantity = Integer.parseInt(quantityObj.toString());
                                }
                            }
                            cart.setQuantity(quantity);
                            System.out.println("设置商品数量: " + quantity);
                            
                            // 添加到购物车列表
                            cartList.add(cart);
                            System.out.println("成功添加虚拟购物车项");
                        }
                    } catch (Exception e) {
                        System.err.println("解析商品JSON失败: " + e.getMessage());
                        e.printStackTrace();
                    }
                    
                    // 如果成功创建了虚拟购物车项，直接使用它
                    if (cartList != null && !cartList.isEmpty()) {
                        System.out.println("成功从itemsList创建了虚拟购物车项，共" + cartList.size() + "个商品");
                    } else {
                        System.err.println("虽然解析了商品列表，但未能创建有效的购物车项");
                    }
                } catch (Exception e) {
                    System.err.println("解析商品列表JSON失败: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("没有提供商品列表JSON，将尝试从购物车获取商品数据");
            }
            
            // 如果没有从itemsList获取到商品，尝试从购物车获取
            if ((cartList == null || cartList.isEmpty()) && !hasItemsList) {
                System.out.println("从购物车获取商品数据");
                cartList = cartService.selectByUserId(addMallOrderParam.getUserId());
                
                if (cartList != null && !cartList.isEmpty()) {
                    System.out.println("从购物车获取到" + cartList.size() + "个商品项");
                } else {
                    System.out.println("购物车为空");
                }
            }
            
            // 仅当从itemsList未获取到商品数据，且购物车数据也为空时，才返回失败
            if (cartList == null || cartList.isEmpty()) {
                System.err.println("购物车为空且没有可用的itemsList，无法创建订单");
                return false;
            }
            
            // 计算订单总金额，同时记录需要更新库存的商品和数量
            Map<Long, Integer> itemQuantities = new HashMap<>();
            
            for (Cart cart : cartList) {
                try {
                    Item item = itemMapper.selectByPrimaryKey(cart.getItemId().longValue());
                    if (item != null) {
                        // 商品单价 * 数量
                        BigDecimal itemTotal = item.getPrice().multiply(new BigDecimal(cart.getQuantity()));
                        totalAmount = totalAmount.add(itemTotal);
                        
                        // 记录商品ID和数量，稍后用于更新库存
                        itemQuantities.put(Long.valueOf(item.getId()), cart.getQuantity());
                    } else {
                        System.err.println("商品不存在，ID: " + cart.getItemId());
                    }
                } catch (Exception e) {
                    System.err.println("计算商品金额时出错: " + e.getMessage());
                }
            }
            
            // 3. 创建订单
            MallOrder mallOrder = new MallOrder();
            mallOrder.setUserId(addMallOrderParam.getUserId());
            mallOrder.setAddressInfo(addMallOrderParam.getAddressInfo()); // 地址信息JSON字符串
            mallOrder.setOrderNo(generateOrderNo()); // 使用正确的字段名
            mallOrder.setStatus(0); // 设置初始状态为待付款
            mallOrder.setTotalAmount(totalAmount); 
            mallOrder.setActualAmount(totalAmount); // 实际支付金额，使用正确的字段名
            mallOrder.setCreateTime(new Date());
            
            // 打印订单信息，帮助调试
            System.out.println("准备创建订单：" + mallOrder);
            
            // 4. 插入订单记录
            int insertRows = mallOrderMapper.insertSelective(mallOrder);
            if (insertRows > 0) {
                System.out.println("订单创建成功，ID: " + mallOrder.getId());
                
                // 5. 如果有订单ID，则更新商品库存和销量
                Long orderId = mallOrder.getId();
                if (orderId != null) {
                    // 更新商品库存和销量
                    updateItemStockAndSales(itemQuantities);
                    
                    // 如果需要，还可以处理优惠券核销等操作
                    
                    // 可以在这里处理订单详情
                    System.out.println("开始添加订单详情，订单ID: " + orderId);
                    // 处理订单详情的逻辑可以在这里添加
                }
                
                return true;
            } else {
                System.err.println("订单创建失败，插入0行");
                return false;
            }
        } catch (Exception e) {
            // 捕获并记录异常，方便排查问题
            System.err.println("创建订单时发生异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新商品库存和销量
     * @param itemQuantities 商品ID和购买数量的映射
     */
    private void updateItemStockAndSales(Map<Long, Integer> itemQuantities) {
        if (itemQuantities == null || itemQuantities.isEmpty()) {
            System.out.println("没有需要更新库存的商品");
            return;
        }
        
        System.out.println("开始更新商品库存和销量...");
        
        for (Map.Entry<Long, Integer> entry : itemQuantities.entrySet()) {
            Long itemId = entry.getKey();
            Integer quantity = entry.getValue();
            
            try {
                // 获取商品信息
                Item item = itemMapper.selectByPrimaryKey(itemId);
                
                if (item != null) {
                    // 更新库存和销量
                    int currentStock = item.getStock();
                    int currentSales = item.getSales() != null ? item.getSales() : 0;
                    
                    // 减少库存
                    item.setStock(Math.max(0, currentStock - quantity));
                    // 增加销量
                    item.setSales(currentSales + quantity);
                    
                    // 更新商品信息
                    itemMapper.updateByPrimaryKeySelective(item);
                    
                    System.out.println("商品 " + itemId + " 库存更新为: " + item.getStock() + 
                                      ", 销量更新为: " + item.getSales());
                } else {
                    System.err.println("无法更新商品库存，商品不存在: " + itemId);
                }
            } catch (Exception e) {
                System.err.println("更新商品 " + itemId + " 库存和销量时出错: " + e.getMessage());
            }
        }
    }

    @Override
    public List<MallOrder> getOrdersByUserId(Integer userId) {
        // 直接调用 Mapper 层的方法查询订单列表
        return mallOrderMapper.selectByUserId(userId);
    }

    @Override
    public List<MallOrder> getAllOrders(Integer pageNum, Integer pageSize) {
        // 实际项目中，这里应该调用Mapper进行分页查询
        // 为了简化，如果pageNum和pageSize不为null，可以构造分页参数传递给Mapper
        // 例如，使用PageHelper:
        // if (pageNum != null && pageSize != null) {
        //     PageHelper.startPage(pageNum, pageSize);
        // }
        // return mallOrderMapper.selectAllOrders(); // 假设有一个selectAllOrders方法
        // 当前mallOrderMapper可能没有selectAllOrders方法，需要添加
        // 暂时返回所有订单，不分页
        return mallOrderMapper.selectAll(); // 假设 mallOrderMapper 有 selectAll 方法
    }

    @Override
    public MallOrder getOrderById(Long orderId) {
        // 调用Mapper根据主键查询订单
        // 还需要考虑是否加载订单详情项
        MallOrder order = mallOrderMapper.selectByPrimaryKey(orderId);
        // if (order != null && orderDetailService != null) {
        //    List<OrderDetail> details = orderDetailService.getOrderDetailsByOrderId(orderId);
        //    order.setOrderDetails(details); // 假设MallOrder有setter，并且需要OrderDetailService和OrderDetailMapper
        // }
        return order;
    }

    @Override
    @Transactional
    public boolean updateOrderStatus(Long orderId, String status, String remarks) {
        MallOrder order = mallOrderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            System.err.println("更新订单状态失败：订单不存在，ID: " + orderId);
            return false;
        }
        // 根据业务逻辑转换status字符串为订单状态码 (int)
        // 这里假设status直接是数据库中状态字段的值，或者需要转换
        // order.setStatus(Integer.parseInt(status)); // 示例，具体类型看MallOrder定义
        // 假设MallOrder中status是String类型
        order.setStatus(Integer.parseInt(status)); // 假设状态是数字，需要根据MallOrder.status的实际类型调整
        // order.setRemarks(remarks); // 如果MallOrder有remarks字段
        order.setUpdateTime(new Date()); // 更新时间

        int updatedRows = mallOrderMapper.updateByPrimaryKeySelective(order);
        return updatedRows > 0;
    }
}
