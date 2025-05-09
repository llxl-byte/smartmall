package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.mapper.CartMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.Cart;
import com.example.smart_mall_li_cr_springboot2.pojo.CartAddParam;
import com.example.smart_mall_li_cr_springboot2.pojo.UpdateCartParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    CartMapper cartMapper;
    @Override
    public boolean addCart(CartAddParam cartAddParam) {
        Cart cart = new Cart();
        cart.setUserId(cartAddParam.getUserId());
        cart.setItemId(cartAddParam.getItemId());
        cart.setQuantity(cartAddParam.getQuantity());

        // 设置选中状态，默认为选中状态(1)
        if (cartAddParam.getSelected() != null) {
            cart.setSelected(cartAddParam.getSelected());
        } else {
            cart.setSelected(1); // 默认选中
        }

        int insertRows = cartMapper.insertSelective(cart);//这里参数类型是cart record，因此需要将addCartParam参数构造成cart实体类
        if(insertRows>0){
            return true;
        }
            return false;
    }

    @Override
    public List<Cart> selectByUserId(Integer userId) {
        return cartMapper.selectByUserId(userId);
    }

    @Override
    public boolean deleteCart(Long id) {
        int deleteRows = cartMapper.deleteByPrimaryKey(id);
        if(deleteRows>0){
            return true;
            }else{
            return false;
        }
    }

    @Override
    public boolean updateCart(UpdateCartParam updateCartParam) {
        Cart cart = new Cart();
        cart.setId(updateCartParam.getId());

        // 设置数量（如果有）
        if (updateCartParam.getQuantity() != null) {
            cart.setQuantity(updateCartParam.getQuantity());
        }

        // 设置选中状态（如果有）
        if (updateCartParam.getSelected() != null) {
            cart.setSelected(updateCartParam.getSelected());
        }

        int updateRows = cartMapper.updateByPrimaryKeySelective(cart);
        if(updateRows>0){
            return true;
        }
        return false;
    }
}
