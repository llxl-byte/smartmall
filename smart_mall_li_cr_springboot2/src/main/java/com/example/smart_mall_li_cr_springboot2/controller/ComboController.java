package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.common.Result;
import com.example.smart_mall_li_cr_springboot2.pojo.Combo; // 假设有 Combo 实体类
import com.example.smart_mall_li_cr_springboot2.service.ComboService; // 稍后创建
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理控制器
 */
@RestController
@RequestMapping("/admin/combos") // 后台管理端的套餐接口
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"}, allowCredentials = "true") // 允许本地前端访问
public class ComboController {

    @Autowired // 自动注入 ComboService 实例
    private ComboService comboService; // 稍后创建

    /**
     * 添加套餐
     * @param combo 套餐对象
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> addCombo(@RequestBody Combo combo) {
        try {
            boolean success = comboService.addCombo(combo);
            if (success) {
                return Result.success(null, "套餐添加成功");
            } else {
                return Result.error("套餐添加失败");
            }
        } catch (Exception e) {
            return Result.error("套餐添加失败: " + e.getMessage());
        }
    }

    /**
     * 编辑套餐
     * @param comboId 套餐ID
     * @param combo 套餐对象
     * @return 操作结果
     */
    @PutMapping("/{comboId}")
    public Result<Void> updateCombo(@PathVariable Integer comboId, @RequestBody Combo combo) {
        // 确保路径参数和请求体中的ID一致
        combo.setId(comboId);
        try {
            boolean success = comboService.updateCombo(combo);
            if (success) {
                return Result.success(null, "套餐编辑成功");
            } else {
                return Result.error("套餐编辑失败");
            }
        } catch (Exception e) {
            return Result.error("套餐编辑失败: " + e.getMessage());
        }
    }

    /**
     * 删除套餐
     * @param comboId 套餐ID
     * @return 操作结果
     */
    @DeleteMapping("/{comboId}")
    public Result<Void> deleteCombo(@PathVariable Integer comboId) {
        try {
            boolean success = comboService.deleteCombo(comboId);
            if (success) {
                return Result.success(null, "套餐删除成功");
            } else {
                return Result.error("套餐删除失败");
            }
        } catch (Exception e) {
            return Result.error("套餐删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取套餐列表
     * @return 套餐列表
     */
    @GetMapping
    public Result<List<Combo>> getAllCombos() {
        try {
            List<Combo> comboList = comboService.getAllCombos();
            return Result.success(comboList);
        } catch (Exception e) {
            return Result.error("获取套餐列表失败: " + e.getMessage());
        }
    }

    // 可以根据需要添加获取单个套餐详情的接口
    // @GetMapping("/{comboId}")
    // public Result<Combo> getComboById(@PathVariable Integer comboId) { ... }
}