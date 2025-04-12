package com.example.smart_mall_li_cr_springboot2.pojo;

/**
 * 添加订单的参数类
 */
public class AddMallOrderParam {
    private Integer userId;
    private String addressInfo;
    // 可选字段，用于接收前端传递的其他参数
    private String itemsList; // 商品列表JSON字符串，可能不使用

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
    
    public String getItemsList() {
        return itemsList;
    }

    public void setItemsList(String itemsList) {
        this.itemsList = itemsList;
    }

    @Override
    public String toString() {
        return "AddMallOrderParam{" +
                "userId=" + userId +
                ", addressInfo='" + addressInfo + '\'' +
                ", itemsList='" + itemsList + '\'' +
                '}';
    }
}
