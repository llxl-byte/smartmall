package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.pojo.Cart;
import com.example.smart_mall_li_cr_springboot2.pojo.CartAddParam;
import com.example.smart_mall_li_cr_springboot2.pojo.UpdateCartParam;
import com.example.smart_mall_li_cr_springboot2.service.CartService;
import com.example.smart_mall_li_cr_springboot2.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(originPatterns = {"https://*.app.github.dev", "http://localhost:*", "http://127.0.0.1:*"}, allowCredentials = "false")
@RestController
public class CartController {
    @Autowired
    CartService cartService;

    @PostMapping("/addCart")
    public Result<Boolean> addCart(@RequestBody CartAddParam cartAddParam) {
        try {
            boolean success = cartService.addCart(cartAddParam);
            if (success) {
                return Result.success(true, "添加购物车成功");
            } else {
                return Result.error("添加购物车失败");
            }
        } catch (Exception e) {
            return Result.error("添加购物车失败: " + e.getMessage());
        }
    }

    @RequestMapping("/selectByUserId")
    public List<Cart> selectByUserId(Integer userId){
        return cartService.selectByUserId(userId);
    }

    @RequestMapping("/deleteCart")
    public Result<Boolean> deleteCart(Long id){
        try {
            if (id == null) {
                return Result.error("购物车ID不能为空");
            }
            boolean success = cartService.deleteCart(id);
            if (success) {
                return Result.success(true, "删除购物车成功");
            } else {
                return Result.error("删除购物车失败");
            }
        } catch (Exception e) {
            return Result.error("删除购物车失败: " + e.getMessage());
        }
    }

    @PostMapping("/updateCart")
    public Result<Boolean> updateCart(@RequestBody UpdateCartParam updateCartParam){
        try {
            boolean success = cartService.updateCart(updateCartParam);
            if (success) {
                return Result.success(true, "更新购物车成功");
            } else {
                return Result.error("更新购物车失败");
            }
        } catch (Exception e) {
            return Result.error("更新购物车失败: " + e.getMessage());
        }
    }

    @PostMapping("/batchUpdateCartSelected")
    public Result<Boolean> batchUpdateCartSelected(@RequestBody UpdateCartParam updateCartParam){
        try {
            // 确保选中状态字段存在
            if (updateCartParam.getSelected() == null) {
                return Result.error("选中状态不能为空");
            }

            // 获取用户ID和选中状态
            Integer userId = updateCartParam.getUserId();
            Integer selected = updateCartParam.getSelected();

            if (userId == null) {
                return Result.error("用户ID不能为空");
            }

            // 获取用户的所有购物车项
            List<Cart> cartList = cartService.selectByUserId(userId);
            if (cartList == null || cartList.isEmpty()) {
                return Result.success(true, "购物车为空");
            }

            // 更新所有购物车项的选中状态
            boolean success = true;
            for (Cart cart : cartList) {
                UpdateCartParam param = new UpdateCartParam();
                param.setId(cart.getId());
                param.setSelected(selected);
                if (!cartService.updateCart(param)) {
                    success = false;
                }
            }

            if (success) {
                return Result.success(true, "批量更新购物车成功");
            } else {
                return Result.error("批量更新购物车部分失败");
            }
        } catch (Exception e) {
            return Result.error("批量更新购物车失败: " + e.getMessage());
        }
    }
}
