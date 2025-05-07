package com.example.smart_mall_li_cr_springboot2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.example.smart_mall_li_cr_springboot2.mapper")  //mybatis扫描mapper接口所在的包
@EnableCaching  // 开启缓存支持
public class SmartMallLiCrSpringboot2Application {

    public static void main(String[] args) {
        SpringApplication.run(SmartMallLiCrSpringboot2Application.class, args);
    }

}
