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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
        // 1. 获取用户购物车信息
        List<Cart> cartList = cartService.selectByUserId(addMallOrderParam.getUserId());
        if (cartList == null || cartList.isEmpty()) {
            return false;
        }

        // 2. 计算订单总金额
        BigDecimal totalAmount = new BigDecimal("0");
        for (Cart cart : cartList) {
            Item item = itemMapper.selectByPrimaryKey(cart.getItemId().longValue());
            if (item != null) {
                // 商品单价 * 数量
                BigDecimal itemTotal = item.getPrice().multiply(new BigDecimal(cart.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
            }
        }

        // 3. 创建订单
        MallOrder mallOrder = new MallOrder();
        mallOrder.setUserId(addMallOrderParam.getUserId());
        mallOrder.setAddressInfo(addMallOrderParam.getAddressInfo());
        mallOrder.setOrderNo(generateOrderNo());
        mallOrder.setStatus(0); // 设置初始状态为待付款
        mallOrder.setTotalAmount(totalAmount);
        mallOrder.setActualAmount(totalAmount); // 实际支付金额，如果有优惠可以在这里计算
        mallOrder.setCreateTime(new Date());
        int insertRows = mallOrderMapper.insertSelective(mallOrder);
        if (insertRows > 0) {
            return true;
        }
        return false;
    }
}
