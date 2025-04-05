package com.example.smart_mall_li_cr_springboot2.controller;

import com.example.smart_mall_li_cr_springboot2.pojo.AddMallOrderParam;
import com.example.smart_mall_li_cr_springboot2.service.MallOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class MallOrderController {
    @Autowired
    MallOrderService mallOrderService;
    @RequestMapping("/addMallOrder")
    public boolean AddMallOrder(AddMallOrderParam addMallOrderParam) {
        return mallOrderService.AddMallOrder(addMallOrderParam);
    }
}
