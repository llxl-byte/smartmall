package com.example.smart_mall_li_cr_springboot2.pojo;

public class AddMallOrderParam {
    Integer userId;
    String addressInfo;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(String addressInfo) {
        this.addressInfo = addressInfo;
    }
}
