package cn.com.nabotix.shareplatform.user.dto;

import lombok.Data;

/**
 * 修改密码请求DTO
 * 用于Controller接收前端修改密码请求数据
 *
 * @author 陈雍文
 */
@Data
public class UpdatePasswordRequestDto {
    private String phoneNumber;
    private String verificationCode;
    private String newPassword;
}