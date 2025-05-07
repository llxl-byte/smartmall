package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.pojo.Item;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {
    List<Item> selectByCategoryId(Integer categoryId);

    List<Item> selectByItemName(String itemName);

    Item selectByItemId(Long itemId);
    
    int addItem(Item item);
    
    int updateItem(Item item);
    
    int deleteItem(Long itemId);
    
    String uploadItemImage(MultipartFile file, Long itemId);
}
