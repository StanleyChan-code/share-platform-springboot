package cn.com.nabotix.shareplatform.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 通用API响应DTO
 * 用于Controller统一返回数据格式
 * 
 * @param <T> 返回数据的类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto<T> {
    /**
     * 请求是否成功
     */
    private boolean success;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 响应时间戳
     */
    private Instant timestamp = Instant.now();
    
    /**
     * 创建成功的响应
     * 
     * @param data 响应数据
     * @param <T> 数据类型
     * @return ApiResponseDto实例
     */
    public static <T> ApiResponseDto<T> success(T data) {
        return create(true, "操作成功", data);
    }
    
    /**
     * 创建成功的响应（无数据）
     * 
     * @param message 响应消息
     * @param <T> 数据类型
     * @return ApiResponseDto实例
     */
    public static <T> ApiResponseDto<T> success(String message) {
        return create(true, message, null);
    }
    
    /**
     * 创建带消息的成功响应
     * 
     * @param data 响应数据
     * @param message 响应消息
     * @param <T> 数据类型
     * @return ApiResponseDto实例
     */
    public static <T> ApiResponseDto<T> success(T data, String message) {
        return create(true, message, data);
    }
    
    /**
     * 创建失败的响应
     * 
     * @param message 错误消息
     * @param <T> 数据类型
     * @return ApiResponseDto实例
     */
    public static <T> ApiResponseDto<T> error(String message) {
        return create(false, message, null);
    }
    
    /**
     * 创建失败的响应
     * 
     * @param message 错误消息
     * @param throwable 异常对象
     * @param <T> 数据类型
     * @return ApiResponseDto实例
     */
    public static <T> ApiResponseDto<T> error(String message, Throwable throwable) {
        return create(false, message + ": " + throwable.getMessage(), null);
    }
    
    /**
     * 创建自定义的响应
     * 
     * @param success 是否成功
     * @param message 响应消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return ApiResponseDto实例
     */
    public static <T> ApiResponseDto<T> custom(boolean success, String message, T data) {
        return create(success, message, data);
    }
    
    /**
     * 创建响应对象的私有方法
     * 
     * @param success 是否成功
     * @param message 响应消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return ApiResponseDto实例
     */
    private static <T> ApiResponseDto<T> create(boolean success, String message, T data) {
        ApiResponseDto<T> response = new ApiResponseDto<>();
        response.setSuccess(success);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
}