package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.pojo.Item;
import com.example.smart_mall_li_cr_springboot2.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
