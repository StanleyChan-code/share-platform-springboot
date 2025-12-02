package cn.com.nabotix.shareplatform.security.jwt;

import lombok.Data;

import java.util.List;

/**
 * JWT响应实体类
 * 用于封装JWT认证成功后的响应数据
 *
 * @author 陈雍文
 */
@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private List<String> roles;

    public JwtResponse(String accessToken, String username, List<String> roles) {
        this.token = accessToken;
        this.username = username;
        this.roles = roles;
    }
}