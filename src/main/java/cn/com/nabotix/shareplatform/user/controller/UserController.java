package cn.com.nabotix.shareplatform.user.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.security.UserAuthority;
import cn.com.nabotix.shareplatform.security.UserDetailsImpl;

import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 用户相关接口控制器
 *
 * @author 陈雍文
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    
    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<User>> getUserById(@PathVariable UUID userId) {
        try {
            User user = userService.getUserByUserId(userId);
            if (user == null) {
                return ResponseEntity.badRequest().body(ApiResponseDto.error("用户不存在"));
            }
            return ResponseEntity.ok(ApiResponseDto.success(user, "获取用户信息成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponseDto.error("获取用户信息失败", e));
        }
    }
    
    /**
     * 获取当前认证用户的信息
     *
     * @return 当前用户信息
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponseDto<User>> getCurrentUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(ApiResponseDto.error("未认证"));
            }

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userService.getUserByPhone(userDetails.getPhone());

            if (user == null) {
                return ResponseEntity.badRequest().body(ApiResponseDto.error("用户不存在"));
            }
            
            // 清除敏感信息
            user.setPassword(null);
            
            return ResponseEntity.ok(ApiResponseDto.success(user, "获取用户信息成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponseDto.error("获取用户信息失败", e));
        }
    }

    /**
     * 获取当前用户（自己）的权限列表
     * 任意已认证用户都可以访问此接口
     *
     * @return 当前用户的权限列表
     */
    @GetMapping("/authorities")
    public ResponseEntity<ApiResponseDto<List<UserAuthority>>> getCurrentUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<UserAuthority> authorities = userService.getUserAuthorities(userDetails.getId());

        return ResponseEntity.ok(ApiResponseDto.success(authorities, "获取当前用户权限列表成功"));
    }
}