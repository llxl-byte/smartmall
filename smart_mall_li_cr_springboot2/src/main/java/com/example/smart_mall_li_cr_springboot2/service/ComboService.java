package com.example.smart_mall_li_cr_springboot2.service;

import com.example.smart_mall_li_cr_springboot2.pojo.Combo; // 假设有 Combo 实体类

import java.util.List;

/**
 * 套餐管理服务接口
 */
public interface ComboService {

    /**
     * 添加套餐
     * @param combo 套餐对象
     * @return 是否添加成功
     */
    boolean addCombo(Combo combo);

    /**
     * 编辑套餐
     * @param combo 套餐对象
     * @return 是否编辑成功
     */
    boolean updateCombo(Combo combo);

    /**
     * 删除套餐
     * @param comboId 套餐ID
     * @return 是否删除成功
     */
    boolean deleteCombo(Integer comboId);

    /**
     * 获取所有套餐列表
     * @return 套餐列表
     */
    List<Combo> getAllCombos();

    // 可以根据需要添加获取单个套餐详情的方法声明
    // Combo getComboById(Integer comboId);
}