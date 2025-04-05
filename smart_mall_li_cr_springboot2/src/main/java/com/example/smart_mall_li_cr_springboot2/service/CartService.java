package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.pojo.Cart;
import com.example.smart_mall_li_cr_springboot2.pojo.CartAddParam;
import com.example.smart_mall_li_cr_springboot2.pojo.UpdateCartParam;

import java.util.List;

public interface CartService {
    boolean addCart(CartAddParam cartAddParam);

    List<Cart>selectByUserId(Integer userId);

    boolean deleteCart(Long id);

    boolean updateCart(UpdateCartParam updateCartParam);
}
