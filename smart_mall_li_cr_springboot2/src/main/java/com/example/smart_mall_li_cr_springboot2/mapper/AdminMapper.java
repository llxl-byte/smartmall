package com.example.smart_mall_li_cr_springboot2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smart_mall_li_cr_springboot2.pojo.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员 Mapper 接口
 */
@Mapper // 标记为 MyBatis Mapper 接口
public interface AdminMapper extends BaseMapper<Admin> {
    // 继承 BaseMapper 后，自动拥有了常用的 CRUD 方法
    // 如果有特殊的 SQL 查询需求，可以在这里定义方法，并在对应的 XML 文件中编写 SQL
    // 例如：根据用户名查询管理员
    // Admin selectByUsername(String username);
}
