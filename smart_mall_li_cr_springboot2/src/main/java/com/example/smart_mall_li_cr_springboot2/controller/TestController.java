package com.example.smart_mall_li_cr_springboot2.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.smart_mall_li_cr_springboot2.common.Result;

@RestController
@CrossOrigin
public class TestController {

    @GetMapping("/test")
    public Result test() {
        return new Result(true, "测试成功", null);
    }
}