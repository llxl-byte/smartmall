package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.pojo.Item;
import com.example.smart_mall_li_cr_springboot2.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@CrossOrigin
@RestController
public class ItemController {
    @Autowired
    ItemService itemService;
    @RequestMapping("/selectByCategoryId")
    public List<Item>selectByCategoryId(Integer categoryId){
        return itemService.selectByCategoryId(categoryId);
    }

    @RequestMapping("/searchItemByName")
    public List<Item>selectByItemName(String itemName){
        return itemService.selectByItemName(itemName);
    }

    @RequestMapping("/selectByItemId")
    public Item selectByItemId(Long itemId){
        return itemService.selectByItemId(itemId);
    }
    
    @PostMapping("/add")
    public Result addItem(@RequestBody Item item) {
        int result = itemService.addItem(item);
        if (result > 0) {
            return Result.success("商品添加成功");
        } else {
            return Result.error("商品添加失败");
        }
    }
    
    @PutMapping("/update")
    public Result updateItem(@RequestBody Item item) {
        int result = itemService.updateItem(item);
        if (result > 0) {
            return Result.success("商品更新成功");
        } else {
            return Result.error("商品更新失败");
        }
    }
    
    @DeleteMapping("/delete/{itemId}")
    public Result deleteItem(@PathVariable Long itemId) {
        int result = itemService.deleteItem(itemId);
        if (result > 0) {
            return Result.success("商品删除成功");
        } else {
            return Result.error("商品删除失败");
        }
    }
    
    @PostMapping("/uploadImage")
    public Result uploadItemImage(@RequestParam("file") MultipartFile file, @RequestParam("itemId") Long itemId) {
        String imageUrl = itemService.uploadItemImage(file, itemId);
        if (imageUrl != null) {
            return Result.success(imageUrl);
        } else {
            return Result.error("图片上传失败");
        }
    }
}
