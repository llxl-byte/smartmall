package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.pojo.Category;
import com.example.smart_mall_li_cr_springboot2.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@CrossOrigin
@RestController
public class CategoryController {
    @Autowired
    CategoryService categoryService;
    @RequestMapping("/selectAll")
    public List<Category>selectAll(){
        List<Category>categoryList=categoryService.selectAll();
        return categoryList;
    }

    @RequestMapping("/selectByParentId")
    public List<Category> selectByParentId(Integer parentId) {
        return categoryService.selectByParentId(parentId);
    }

    @RequestMapping("/searchCategories")
    public List<Category> searchCategoriesByItemName(String itemName) {
        return categoryService.searchCategoriesByItemName(itemName);
    }
}
