// 位置：com.example.smart_mall_li_cr_springboot2.common包下
package com.example.smart_mall_li_cr_springboot2.common;

public class Result<T> {
    private boolean success;   // 操作是否成功
    private String message;    // 提示信息
    private T data;       // 数据（可存放用户对象等）

    // 无参构造器（可选）
    public Result() {}

    // 成功时的构造器
    public Result(boolean success, String message, T data) {
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
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    // 静态成功方法
    public static <T> Result<T> success(T data) {
        return new Result<>(true, "操作成功", data);
    }
    
    // 带自定义消息的静态成功方法
    public static <T> Result<T> success(T data, String message) {
        return new Result<>(true, message, data);
    }

    // 静态失败方法
    public static <T> Result<T> error(String message) {
        return new Result<>(false, message);
    }
    
    // 带数据的静态失败方法
    public static <T> Result<T> error(String message, T data) {
        return new Result<>(false, message, data);
    }
}
