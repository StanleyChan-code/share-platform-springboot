package cn.com.nabotix.shareplatform.user.dto;

import lombok.Data;

import java.util.UUID;

/**
 * 用户创建请求DTO
 * 用于平台管理员创建用户请求数据
 */
@Data
public class UserCreateRequestDto {
    private String phone;
    private String username;
    private String realName;
    private String email;
    private String password;
    private UUID institutionId;
}