package cn.com.nabotix.shareplatform.security;

import cn.com.nabotix.shareplatform.user.entity.User;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 用户详细信息实现类
 * 该类实现了Spring Security的UserDetails接口，用于提供用户认证和授权所需的信息。
 * 包含用户的基本信息如用户名、密码、手机号等，以及用户的权限集合。
 *
 * @author 陈雍文
 */
@Getter
public class UserDetailsImpl implements UserDetails {
    private final String username;
    private final String password;
    private final String phone;
    private final UUID id;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(UUID id, String username, String phone, String password,
                          Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.password = password;
        this.authorities = authorities;
    }

    public static @NonNull UserDetailsImpl build(@NonNull User user, List<GrantedAuthority> authorities) {
        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPhone(),
                user.getPassword(),
                authorities);
    }
}