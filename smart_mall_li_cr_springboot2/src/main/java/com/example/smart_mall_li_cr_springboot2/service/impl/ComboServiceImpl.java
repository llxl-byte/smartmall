package com.example.smart_mall_li_cr_springboot2.service.impl;

import com.example.smart_mall_li_cr_springboot2.mapper.ComboItemMapper; // 假设有 ComboItemMapper
import com.example.smart_mall_li_cr_springboot2.mapper.ComboMapper; // 假设有 ComboMapper
import com.example.smart_mall_li_cr_springboot2.pojo.Combo; // 假设有 Combo 实体类
import com.example.smart_mall_li_cr_springboot2.pojo.ComboItem; // 假设有 ComboItem 实体类
import com.example.smart_mall_li_cr_springboot2.service.ComboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 可能需要事务管理

import java.util.List;

/**
 * 套餐管理服务实现类
 */
@Service
public class ComboServiceImpl implements ComboService {

    @Autowired
    private ComboMapper comboMapper; // 假设已注入

    @Autowired(required = false) // 如果套餐包含商品项，可能需要注入 ComboItemMapper
    private ComboItemMapper comboItemMapper;

    @Override
    @Transactional // 添加套餐可能涉及多个数据库操作，使用事务
    public boolean addCombo(Combo combo) {
        // 1. 校验套餐数据
        if (combo == null || combo.getName() == null || combo.getPrice() == null) {
            return false;
        }
        // 2. 插入套餐主表记录
        // 注意：insertSelective会返回插入的行数，并且如果配置了useGeneratedKeys，会将生成的主键设置回combo对象
        int comboRows = comboMapper.insertSelective(combo);
        if (comboRows <= 0) {
            return false;
        }
        // 3. 如果套餐包含商品项，插入套餐商品项记录
        List<ComboItem> comboItems = combo.getComboItems();
        if (comboItems != null && !comboItems.isEmpty()) {
            for (ComboItem item : comboItems) {
                item.setComboId(combo.getId()); // 设置套餐ID
            }
            comboItemMapper.batchInsert(comboItems); // 使用批量插入
        }
        return true;
    }

    @Override
    @Transactional // 编辑套餐可能涉及多个数据库操作，使用事务
    public boolean updateCombo(Combo combo) {
        // 1. 校验套餐数据和ID
        if (combo == null || combo.getId() == null || combo.getName() == null || combo.getPrice() == null) {
            return false;
        }
        // 2. 更新套餐主表记录
        int comboRows = comboMapper.updateByPrimaryKeySelective(combo);
        if (comboRows <= 0) {
            // 可以选择抛出异常或者返回false，表示更新失败或记录不存在
            return false;
        }
        // 3. 更新套餐商品项：先删除旧的，再插入新的
        comboItemMapper.deleteByComboId(combo.getId().longValue()); // 注意ID类型转换
        List<ComboItem> comboItems = combo.getComboItems();
        if (comboItems != null && !comboItems.isEmpty()) {
            for (ComboItem item : comboItems) {
                item.setComboId(combo.getId()); // 设置套餐ID
            }
            comboItemMapper.batchInsert(comboItems); // 使用批量插入
        }
        return true;
    }

    @Override
    @Transactional // 删除套餐可能涉及多个数据库操作，使用事务
    public boolean deleteCombo(Integer comboId) {
        // 1. 校验套餐ID
        if (comboId == null) {
            return false;
        }
        // 2. 删除套餐商品项记录
        comboItemMapper.deleteByComboId(comboId.longValue()); // 注意ID类型转换
        // 3. 删除套餐主表记录
        int comboRows = comboMapper.deleteByPrimaryKey(comboId);
        return comboRows > 0;
    }

    @Override
    public List<Combo> getAllCombos() {
        // TODO: 实现获取套餐列表逻辑
        // 调用 Mapper 方法查询所有套餐
        return comboMapper.selectAll(); // 假设有 selectAll 方法
    }

    // 可以根据需要添加获取单个套餐详情的方法实现
    // @Override
    // public Combo getComboById(Integer comboId) {
    //     return comboMapper.selectByPrimaryKey(comboId); // 假设有 selectByPrimaryKey 方法
    // }
}