package cn.com.nabotix.shareplatform.security;

import lombok.Data;

/**
 * 登录请求数据传输对象
 *
 * @author 陈雍文
 */
@Data
public class LoginRequest {
    // 可以是phone或UUID
    private String identifier;
    // 密码
    private String password;
    // 用于验证码登录
    private String phone;
    // 验证码登录时使用
    private String verificationCode;
    // 登录类型：PASSWORD 或 VERIFICATION_CODE
    private String loginType;
}