package com.example.smart_mall_li_cr_springboot2.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 管理员实体类
 */
@Data
@TableName("backend_admin") // 指向后台管理员表
public class Admin {

    @TableId(type = IdType.AUTO) // 主键自增
    private Integer id;

    private String username; // 用户名

    private String password; // 密码 (实际应用中应存储加密后的密码)

    private String role; // 角色，例如 "admin", "editor"

    private Timestamp createTime; // 创建时间

    private Timestamp updateTime; // 更新时间
}