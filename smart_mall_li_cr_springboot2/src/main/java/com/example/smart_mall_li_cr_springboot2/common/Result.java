// 位置：com.example.smart_mall_li_cr_springboot2.common包下
package com.example.smart_mall_li_cr_springboot2.common;

public class Result {
    private boolean success;   // 操作是否成功
    private String message;    // 提示信息
    private Object data;       // 数据（可存放用户对象等）

    // 无参构造器（可选）
    public Result() {}

    // 成功时的构造器
    public Result(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // 失败时的构造器
    public Result(boolean success, String message) {
        this(success, message, null);
    }

    // Getter 和 Setter（可使用 Lombok 的 @Data 注解简化）
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
