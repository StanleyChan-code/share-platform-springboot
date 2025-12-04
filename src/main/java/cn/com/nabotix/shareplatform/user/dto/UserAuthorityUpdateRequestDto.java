package cn.com.nabotix.shareplatform.user.dto;

import cn.com.nabotix.shareplatform.enums.UserAuthority;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

/**
 * 用户权限更新请求DTO
 * 用于接收前端传来的用户权限更新请求
 */
@Data
public class UserAuthorityUpdateRequestDto {
    private UUID userId;
    private Set<UserAuthority> authorities;
}