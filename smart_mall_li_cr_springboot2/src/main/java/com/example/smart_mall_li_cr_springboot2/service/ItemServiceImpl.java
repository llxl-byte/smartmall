package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.mapper.ItemMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.util.List;
import java.util.UUID;
@Service
public class ItemServiceImpl implements ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);
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
    @CacheEvict(value = {"category","item"},allEntries = true)
    public int addItem(Item item) {
        // 在这里可以添加一些业务逻辑，例如参数校验等
        return itemMapper.insertSelective(item);
    }

    @Override
    @CacheEvict(value = {"category", "item"}, allEntries = true)
    public int updateItem(Item item) {
        // 在这里可以添加一些业务逻辑，例如参数校验等
        return itemMapper.updateByPrimaryKeySelective(item);
    }

    @Override
    @CacheEvict(value = {"category", "item"}, allEntries = true)
    public int deleteItem(Long itemId) {
        return itemMapper.deleteByPrimaryKey(itemId);
    }

    @Override
    public String uploadItemImage(MultipartFile file, Long itemId) {
        // 实现图片上传逻辑
        logger.info("开始上传商品图片, 商品ID: {}", itemId);

        // 1. 校验文件是否为空，文件类型，大小等
        if (file.isEmpty()) {
            logger.error("上传的文件为空");
            return null; // 或者抛出异常
        }

        // 2. 生成新的文件名，避免文件名冲突
        String originalFilename = file.getOriginalFilename();
        logger.info("原始文件名: {}", originalFilename);

        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + suffix;
        logger.info("新文件名: {}", newFileName);

        // 3. 定义文件保存路径
        // 注意：这里需要使用与 FileUploadConfig 中相同的路径
        String baseUploadDir = "uploads/"; // 基础上传目录，与 FileUploadConfig 中保持一致
        String itemsDir = baseUploadDir + "items/"; // 商品图片子目录

        // 创建基础上传目录
        File baseDir = new File(baseUploadDir);
        if (!baseDir.exists()) {
            boolean created = baseDir.mkdirs();
            logger.info("创建基础上传目录 {} {}", baseUploadDir, created ? "成功" : "失败");
        }

        // 创建商品图片子目录
        File itemsUploadDir = new File(itemsDir);
        if (!itemsUploadDir.exists()) {
            boolean created = itemsUploadDir.mkdirs();
            logger.info("创建商品图片目录 {} {}", itemsDir, created ? "成功" : "失败");
        }

        String absolutePath = itemsUploadDir.getAbsolutePath();
        logger.info("上传目录绝对路径: {}", absolutePath);

        File dest = new File(absolutePath + File.separator + newFileName);
        logger.info("目标文件路径: {}", dest.getAbsolutePath());

        try {
            // 4. 保存文件
            file.transferTo(dest);
            logger.info("文件保存成功: {}", dest.getAbsolutePath());

            // 5. 更新商品的图片URL到数据库 (如果需要的话)
            if (itemId != null) {
                Item item = itemMapper.selectByPrimaryKey(itemId);
                if (item != null) {
                    String imageUrl = "/uploads/items/" + newFileName;
                    item.setMainImage(imageUrl);
                    itemMapper.updateByPrimaryKeySelective(item);
                    logger.info("更新商品图片URL成功, 商品ID: {}, URL: {}", itemId, imageUrl);
                }
            }

            // 6. 返回图片的访问URL (相对路径)
            String imageUrl = "/uploads/items/" + newFileName;
            logger.info("返回图片URL: {}", imageUrl);
            return imageUrl;
        } catch (IOException e) {
            // 处理文件保存异常
            logger.error("保存文件失败: {}", e.getMessage(), e);
            return null; // 或者抛出自定义异常
        }
    }
}
