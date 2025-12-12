package cn.com.nabotix.shareplatform.security;

import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.entity.UserAuthorityEntity;
import cn.com.nabotix.shareplatform.user.repository.UserRepository;
import cn.com.nabotix.shareplatform.user.repository.UserAuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/**
 * 用户详情服务实现类
 *
 * @author 陈雍文
 */
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserAuthorityRepository userAuthorityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = findUserByIdentifier(username);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found with identifier: " + username);
        }

        User user = userOptional.get();
        List<UserAuthorityEntity> authorityEntityList = userAuthorityRepository.findByUserId(user.getId());

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (UserAuthorityEntity authorityEntity : authorityEntityList) {
            authorities.add(new SimpleGrantedAuthority(authorityEntity.getAuthority().name().toUpperCase()));
        }

        return UserDetailsImpl.build(user, authorities);
    }

    /**
     * 根据标识符查找用户（支持phone或UUID）
     * @param identifier 用户标识符
     * @return 用户对象Optional
     */
    public Optional<User> findUserByIdentifier(String identifier) {
        // 尝试按UUID查找
        try {
            UUID userId = UUID.fromString(identifier);
            return userRepository.findById(userId);
        } catch (IllegalArgumentException e) {
            // 不是有效的UUID格式
        }

        // 尝试按手机号查找
        if (isPhoneNumber(identifier)) {
            return userRepository.findByPhone(identifier);
        }

        return Optional.empty();
    }
    
    /**
     * 判断字符串是否为手机号格式
     * @param str 待判断字符串
     * @return 是否为手机号
     */
    private boolean isPhoneNumber(String str) {
        if (!StringUtils.hasText(str)) {
            return false;
        }
        // 简单的手机号验证（11位数字，以1开头）
        return str.matches("^1[3-9]\\d{9}$");
    }
}