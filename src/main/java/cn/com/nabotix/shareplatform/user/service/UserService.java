package cn.com.nabotix.shareplatform.user.service;

import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.entity.UserAuthorityEntity;
import cn.com.nabotix.shareplatform.enums.UserAuthority;
import cn.com.nabotix.shareplatform.user.repository.UserRepository;
import cn.com.nabotix.shareplatform.user.repository.UserAuthorityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 用户服务类
 *
 * @author 陈雍文
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserAuthorityRepository userAuthorityRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * 保存用户
     *
     * @param user 用户对象
     * @return 保存后的用户对象
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * 根据用户ID获取用户的所有角色
     *
     * @param userId 用户ID
     * @return 用户的所有角色列表
     */
    public List<UserAuthority> getUserAuthorities(UUID userId) {
        List<UserAuthorityEntity> userAuthorityEntities = userAuthorityRepository.findByUserId(userId);
        List<UserAuthority> authorities = new ArrayList<>();
        
        for (UserAuthorityEntity entity : userAuthorityEntities) {
            authorities.add(entity.getAuthority());
        }
        
        return authorities;
    }

    /**
     * 为用户添加角色
     *
     * @param userId 用户ID
     * @param authority 要添加的角色
     * @param createdBy 创建者ID
     */
    public void addUserAuthority(UUID userId, UserAuthority authority, UUID createdBy) {
        UserAuthorityEntity userAuthorityEntity = new UserAuthorityEntity();
        userAuthorityEntity.setUserId(userId);
        userAuthorityEntity.setAuthority(authority);
        userAuthorityEntity.setCreatedBy(createdBy);
        userAuthorityEntity.setCreatedAt(Instant.now());
        
        userAuthorityRepository.save(userAuthorityEntity);
    }

    /**
     * 移除用户的指定角色
     *
     * @param userId 用户ID
     * @param authority 要移除的角色
     */
    public void removeUserAuthority(UUID userId, UserAuthority authority) {
        List<UserAuthorityEntity> authorityEntityList = userAuthorityRepository.findByUserId(userId);
        
        for (UserAuthorityEntity entity : authorityEntityList) {
            if (entity.getAuthority() == authority) {
                userAuthorityRepository.delete(entity);
            }
        }
    }

    /**
     * 获取用户信息及其所有角色
     *
     * @param userId 用户ID
     * @return 包含用户基本信息和角色列表的用户对象
     */
    public User getUserByUserId(UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null){
            user.setPassword(null);
        }
        return user;
    }
    
    /**
     * 根据手机号获取用户
     *
     * @param phone 手机号
     * @return 用户对象
     */
    public User getUserByPhone(String phone) {
        User user = userRepository.findByPhone(phone).orElse(null);
        if (user != null){
            user.setPassword(null);
        }
        return user;
    }

    /**
     * 修改用户密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 更新后的用户对象
     */
    public User updatePassword(UUID userId, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }
    
    /**
     * 创建用户（加密密码）
     *
     * @param user 用户对象
     * @return 保存后的用户对象
     */
    public User createUser(User user) {
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }
}