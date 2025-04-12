package com.example.smart_mall_li_cr_springboot2.service.impl;

import com.example.smart_mall_li_cr_springboot2.dto.CreateOrderRequestDTO;
import com.example.smart_mall_li_cr_springboot2.dto.CreateOrderResponseDTO;
import com.example.smart_mall_li_cr_springboot2.mapper.*; // 引入所有 Mapper
import com.example.smart_mall_li_cr_springboot2.pojo.*; // 引入所有 Pojo
import com.example.smart_mall_li_cr_springboot2.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ItemMapper itemMapper; // 需要操作商品库存
    @Autowired
    private CouponMapper couponMapper; // 需要更新优惠券状态
    @Autowired
    private CartMapper cartMapper; // 可能需要清空购物车
    @Autowired
    private UserAddressMapper addressMapper; // 需要验证地址有效性

    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务，并在任何异常时回滚
    public CreateOrderResponseDTO createOrder(CreateOrderRequestDTO requestDTO) throws Exception {
        Long userId = requestDTO.getUserId();
        Long addressId = requestDTO.getAddressId();
        List<CreateOrderRequestDTO.OrderItemDTO> itemsDTO = requestDTO.getItems();
        Long couponId = requestDTO.getCouponId();

        // 1. 验证地址有效性
        UserAddress address = addressMapper.findByIdAndUserId(addressId, userId);
        if (address == null) {
            throw new Exception("收货地址无效或不属于当前用户");
        }

        // 2. 验证并锁定优惠券 (如果使用了)
        Coupon coupon = null;
        BigDecimal couponDiscount = BigDecimal.ZERO;
        if (couponId != null) {
            // 需要在 CouponMapper 中添加根据 ID 和用户 ID 查询的方法，并考虑加锁
            coupon = couponMapper.findByIdAndUserIdForUpdate(couponId, userId);
            if (coupon == null || coupon.getStatus() != 0 || coupon.getExpiryDate().before(new Date())) {
                throw new Exception("优惠券无效或已使用/过期");
            }
            couponDiscount = coupon.getDiscountAmount();
            // 验证优惠券是否满足门槛 (理论上前端已经过滤，后端再确认一次)
            // ... (需要先计算商品总价)
        }

        // 3. 验证商品及库存，并计算总价
        BigDecimal totalItemAmount = BigDecimal.ZERO;
        for (CreateOrderRequestDTO.OrderItemDTO itemDTO : itemsDTO) {
            // 需要在 ItemMapper 中添加根据 ID 查询并锁定库存的方法 (for update)
            Item item = itemMapper.findByIdForUpdate(itemDTO.getItemId());
            if (item == null || item.getStock() < itemDTO.getQuantity()) {
                throw new Exception("商品[" + (item != null ? item.getName() : itemDTO.getItemId()) + "]不存在或库存不足");
            }
            // 累加商品总价
            totalItemAmount = totalItemAmount.add(item.getPrice().multiply(new BigDecimal(itemDTO.getQuantity())));
        }

        // 4. (如果使用了优惠券) 再次验证优惠券门槛
        if (coupon != null && totalItemAmount.compareTo(coupon.getThreshold()) < 0) {
            throw new Exception("订单金额未达到优惠券使用门槛");
        }

        // 5. 计算最终支付金额
        BigDecimal finalAmount = totalItemAmount.subtract(couponDiscount).max(BigDecimal.ZERO);

        // 6. 创建订单主表记录 (mall_order)
        MallOrder order = new MallOrder();
        order.setOrderId("ORD-" + UUID.randomUUID().toString().replace("-", "")); // 生成唯一订单号
        order.setUserId(userId);
        order.setTotalAmount(totalItemAmount); // 商品总金额
        order.setDiscountAmount(couponDiscount); // 优惠金额
        order.setPaymentAmount(finalAmount); // 实付金额
        order.setStatus(0); // 0: 待支付 (或其他初始状态)
        order.setCreateTime(new Date());
        order.setRemark(requestDTO.getRemark());
        order.setAddressId(addressId);
        // 将地址信息冗余存储到订单表，方便查询
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getAddress());

        orderMapper.insert(order); // 插入订单主表

        // 7. 创建订单详情记录 (order_detail) 并扣减库存
        for (CreateOrderRequestDTO.OrderItemDTO itemDTO : itemsDTO) {
            Item item = itemMapper.findById(itemDTO.getItemId()); // 再次获取商品信息用于详情
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(order.getOrderId());
            detail.setItemId(itemDTO.getItemId());
            detail.setItemName(item.getName());
            detail.setItemImage(item.getMainImage()); // 获取主图
            detail.setQuantity(itemDTO.getQuantity());
            detail.setPrice(item.getPrice());

            orderDetailMapper.insert(detail); // 插入订单详情

            // 扣减库存 (需要 ItemMapper 提供 updateStock 方法)
            int updatedRows = itemMapper.decreaseStock(itemDTO.getItemId(), itemDTO.getQuantity());
            if (updatedRows == 0) {
                // 如果更新行数为 0，说明在并发情况下库存被其他事务修改了
                throw new Exception("商品[" + item.getName() + "]库存不足(并发)");
            }
        }

        // 8. 更新优惠券状态 (如果使用了)
        if (coupon != null) {
            // 需要 CouponMapper 提供 updateStatus 方法
            couponMapper.updateStatus(couponId, 1, new Date(), order.getOrderId()); // 1: 已使用
        }

        // 9. (可选) 清空购物车
        // cartMapper.deleteByUserAndItems(userId, itemsDTO.stream().map(CreateOrderRequestDTO.OrderItemDTO::getItemId).collect(Collectors.toList()));

        // 10. 准备并返回响应 DTO
        CreateOrderResponseDTO responseDTO = new CreateOrderResponseDTO();
        responseDTO.setOrderId(order.getOrderId());
        responseDTO.setPaymentAmount(finalAmount);

        return responseDTO;
    }
} 