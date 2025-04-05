package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.mapper.ItemMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    ItemMapper itemMapper;
    @Override
    public List<Item> selectByCategoryId(Integer categoryId) {
        return itemMapper.selectByCategoryId(categoryId);
    }

    @Override
    public List<Item> selectByItemName(String itemName) {
        return itemMapper.selectByItemName(itemName);
    }

    @Override
    public Item selectByItemId(Long itemId) {
        return itemMapper.selectByPrimaryKey(itemId);
    }
}
