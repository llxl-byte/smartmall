package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.pojo.Category;

import java.util.List;

public interface CategoryService {
    public List<Category> selectAll();
    public List<Category> selectByParentId(Integer parentId);
    public List<Category> searchCategoriesByItemName(String itemName);
}
