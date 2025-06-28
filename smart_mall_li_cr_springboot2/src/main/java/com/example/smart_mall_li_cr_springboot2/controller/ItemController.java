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

    /**
     * 后台管理：获取所有商品列表
     * @return 包含所有商品的Result对象
     */
    @GetMapping("/admin/items")
    public Result<List<Item>> getAllItems() {
        try {
            List<Item> itemList = itemService.getAllItems();
            return Result.success(itemList);
        } catch (Exception e) {
            return Result.error("获取商品列表失败: " + e.getMessage());
        }
    }

    /**
     * 后台管理：添加商品
     * @param item 商品对象
     * @return 操作结果
     */
    @PostMapping("/admin/items")
    public Result<Void> addItemAdmin(@RequestBody Item item) {
        try {
            int result = itemService.addItem(item);
            if (result > 0) {
                return Result.success(null, "商品添加成功");
            } else {
                return Result.error("商品添加失败");
            }
        } catch (Exception e) {
            return Result.error("商品添加失败: " + e.getMessage());
        }
    }

    /**
     * 后台管理：更新商品
     * @param itemId 商品ID
     * @param item 商品对象
     * @return 操作结果
     */
    @PutMapping("/admin/items/{itemId}")
    public Result<Void> updateItemAdmin(@PathVariable Integer itemId, @RequestBody Item item) {
        try {
            item.setId(itemId); // 确保ID一致
            int result = itemService.updateItem(item);
            if (result > 0) {
                return Result.success(null, "商品更新成功");
            } else {
                return Result.error("商品更新失败");
            }
        } catch (Exception e) {
            return Result.error("商品更新失败: " + e.getMessage());
        }
    }

    /**
     * 后台管理：删除商品
     * @param itemId 商品ID
     * @return 操作结果
     */
    @DeleteMapping("/admin/items/{itemId}")
    public Result<Void> deleteItemAdmin(@PathVariable Long itemId) {
        try {
            int result = itemService.deleteItem(itemId);
            if (result > 0) {
                return Result.success(null, "商品删除成功");
            } else {
                return Result.error("商品删除失败");
            }
        } catch (Exception e) {
            return Result.error("商品删除失败: " + e.getMessage());
        }
    }

    /**
     * 后台管理：上传商品图片
     * @param file 图片文件
     * @return 操作结果
     */
    @PostMapping("/admin/items/upload")
    public Result<String> uploadItemImageAdmin(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = itemService.uploadItemImage(file, null);
            if (imageUrl != null) {
                return Result.success(imageUrl, "图片上传成功");
            } else {
                return Result.error("图片上传失败");
            }
        } catch (Exception e) {
            return Result.error("图片上传失败: " + e.getMessage());
        }
    }
}
