package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.pojo.Cart;
import com.example.smart_mall_li_cr_springboot2.pojo.CartAddParam;
import com.example.smart_mall_li_cr_springboot2.pojo.UpdateCartParam;
import com.example.smart_mall_li_cr_springboot2.service.CartService;
import com.example.smart_mall_li_cr_springboot2.service.CartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@CrossOrigin
@RestController
public class CartController {
    @Autowired
    CartService cartService;
    @RequestMapping("/addCart")
    public boolean addCart(CartAddParam cartAddParam) {
        return cartService.addCart(cartAddParam);
    }

    @RequestMapping("/selectByUserId")
    public List<Cart> selectByUserId(Integer userId){
        return cartService.selectByUserId(userId);
    }

    @RequestMapping("/deleteCart")
    public boolean deleteCart(Long id){
        return cartService.deleteCart(id);
    }

    @RequestMapping("/updateCart")
    public boolean updateCart(UpdateCartParam updateCartParam){
        return cartService.updateCart(updateCartParam);
    }
}
