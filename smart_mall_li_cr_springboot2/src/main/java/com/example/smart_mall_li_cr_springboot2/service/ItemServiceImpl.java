package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.mapper.ItemMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // 新增导入

import java.io.File; // 新增导入
import java.io.IOException; // 新增导入
import java.util.List;
import java.util.UUID; // 新增导入
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    ItemMapper itemMapper;

    @Override
    public List<Item> getAllItems() {
        // 调用 Mapper 获取所有商品
        return itemMapper.selectAll();
    }
    @Override
    @Cacheable(value = "category", key = "#categoryId")
    public List<Item> selectByCategoryId(Integer categoryId) {
        return itemMapper.selectByCategoryId(categoryId);
    }

    @Override
    public List<Item> selectByItemName(String itemName) {
        return itemMapper.selectByItemName(itemName);
    }

    @Override
    @Cacheable(value = "item", key = "#itemId")
    public Item selectByItemId(Long itemId) {
        return itemMapper.selectByPrimaryKey(itemId);
    }

    @Override
    public int addItem(Item item) {
        // 在这里可以添加一些业务逻辑，例如参数校验等
        return itemMapper.insertSelective(item);
    }

    @Override
    public int updateItem(Item item) {
        // 在这里可以添加一些业务逻辑，例如参数校验等
        return itemMapper.updateByPrimaryKeySelective(item);
    }

    @Override
    public int deleteItem(Long itemId) {
        return itemMapper.deleteByPrimaryKey(itemId);
    }

    @Override
    public String uploadItemImage(MultipartFile file, Long itemId) {
        // 实现图片上传逻辑
        // 1. 校验文件是否为空，文件类型，大小等
        if (file.isEmpty()) {
            return null; // 或者抛出异常
        }
        // 2. 生成新的文件名，避免文件名冲突
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + suffix;

        // 3. 定义文件保存路径 (这个路径需要根据实际配置来确定)
        //    通常会在 application.yml 或专门的配置类中定义
        //    例如： String uploadPath = "D:/uploads/smartmall/items/";
        //    为了演示，这里暂时硬编码一个路径，实际项目中应通过配置获取
        String uploadDir = "uploads/items/"; // 相对路径，需要配置静态资源映射
        File uploadPathDir = new File(uploadDir);
        if (!uploadPathDir.exists()) {
            uploadPathDir.mkdirs();
        }
        File dest = new File(uploadPathDir.getAbsolutePath() + File.separator + newFileName);

        try {
            // 4. 保存文件
            file.transferTo(dest);

            // 5. 更新商品的图片URL到数据库 (如果需要的话)
            //    这里假设 Item 实体类有 imageUrl 字段
            //    Item item = itemMapper.selectByPrimaryKey(itemId);
            //    if (item != null) {
            //        item.setImageUrl("/" + uploadDir + newFileName); // 返回相对URL给前端
            //        itemMapper.updateByPrimaryKeySelective(item);
            //    }

            // 6. 返回图片的访问URL (相对路径)
            return "/" + uploadDir + newFileName;
        } catch (IOException e) {
            // 处理文件保存异常
            e.printStackTrace();
            return null; // 或者抛出自定义异常
        }
    }
}
