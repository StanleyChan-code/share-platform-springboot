package cn.com.nabotix.shareplatform.user.dto;

import lombok.Data;

/**
 * 用户注册请求DTO
 * 用于Controller接收前端注册请求数据
 */
@Data
public class RegistrationRequestDto {
    private String phoneNumber;
    private String verificationCode;
    private String username;
    private String realName;
    private String email;
    private String password;
}