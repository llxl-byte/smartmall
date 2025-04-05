package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.mapper.CategoryMapper;
import com.example.smart_mall_li_cr_springboot2.mapper.ItemMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    ItemMapper itemMapper;

    @Override
    public List<Category> selectAll() {
        return categoryMapper.selectAll();
    }

    @Override
    public List<Category> selectByParentId(Integer parentId) {
        List<Category> categories = categoryMapper.selectByParentId(parentId);
        return categories;
    }

    @Override
    public List<Category> searchCategoriesByItemName(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return categoryMapper.selectByItemName(itemName.trim());
    }
}
