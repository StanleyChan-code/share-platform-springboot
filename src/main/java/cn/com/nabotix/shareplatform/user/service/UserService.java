package cn.com.nabotix.shareplatform.user.service;

import cn.com.nabotix.shareplatform.config.SecurityConfig;
import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.entity.UserRoleEntity;
import cn.com.nabotix.shareplatform.enums.UserRole;
import cn.com.nabotix.shareplatform.user.repository.UserRepository;
import cn.com.nabotix.shareplatform.user.repository.UserRoleRepository;
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
    private final UserRoleRepository userRoleRepository;
    private final SecurityConfig.PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, SecurityConfig.PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
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
    public List<UserRole> getUserRoles(UUID userId) {
        List<UserRoleEntity> userRoleEntities = userRoleRepository.findByUserId(userId);
        List<UserRole> roles = new ArrayList<>();
        
        for (UserRoleEntity entity : userRoleEntities) {
            roles.add(entity.getRole());
        }
        
        return roles;
    }

    /**
     * 为用户添加角色
     *
     * @param userId 用户ID
     * @param role 要添加的角色
     * @param createdBy 创建者ID
     */
    public void addUserRole(UUID userId, UserRole role, UUID createdBy) {
        UserRoleEntity userRoleEntity = new UserRoleEntity();
        userRoleEntity.setUserId(userId);
        userRoleEntity.setRole(role);
        userRoleEntity.setCreatedBy(createdBy);
        userRoleEntity.setCreatedAt(Instant.now());
        
        userRoleRepository.save(userRoleEntity);
    }

    /**
     * 移除用户的指定角色
     *
     * @param userId 用户ID
     * @param role 要移除的角色
     */
    public void removeUserRole(UUID userId, UserRole role) {
        List<UserRoleEntity> userRoleEntities = userRoleRepository.findByUserId(userId);
        
        for (UserRoleEntity entity : userRoleEntities) {
            if (entity.getRole() == role) {
                userRoleRepository.delete(entity);
            }
        }
    }

    /**
     * 获取用户信息及其所有角色
     *
     * @param userId 用户ID
     * @return 包含用户基本信息和角色列表的用户对象
     */
    public User getUserWithRoles(UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        // 注意：这里返回的User对象不包含role字段，因为用户现在可以有多个角色
        // 需要调用getUserRoles方法单独获取用户的所有角色
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