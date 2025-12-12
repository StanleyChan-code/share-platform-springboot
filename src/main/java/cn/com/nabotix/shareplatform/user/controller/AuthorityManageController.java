package cn.com.nabotix.shareplatform.user.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.security.UserAuthority;
import cn.com.nabotix.shareplatform.security.UserDetailsImpl;
import cn.com.nabotix.shareplatform.user.dto.UserAuthorityUpdateRequestDto;
import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限管理控制器
 * 提供权限查询和管理接口
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/manage/authorities")
public class AuthorityManageController {

    private final UserService userService;

    /**
     * 获取系统所有权限列表
     * 平台管理员可以获取全部权限，机构管理员只能获取除平台管理员外的所有权限
     *
     * @return 权限列表
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR')")
    public ResponseEntity<ApiResponseDto<List<UserAuthority>>> getAllAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<UserAuthority> authorities;

        // 平台管理员可以获取所有权限
        if (authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> "PLATFORM_ADMIN".equals(grantedAuthority.getAuthority()))) {
            authorities = Arrays.asList(UserAuthority.values());
        }
        // 机构管理员只能获取除平台管理员外的所有权限
        else if (authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> "INSTITUTION_SUPERVISOR".equals(grantedAuthority.getAuthority()))) {
            authorities = Arrays.stream(UserAuthority.values())
                    .filter(authority -> authority != UserAuthority.PLATFORM_ADMIN)
                    .collect(Collectors.toList());
        }
        // 其他用户无权访问
        else {
            return ResponseEntity.status(403).body(ApiResponseDto.error("无权限访问"));
        }

        return ResponseEntity.ok(ApiResponseDto.success(authorities, "获取权限列表成功"));
    }

    /**
     * 平台管理员和机构管理员读取指定用户的权限列表
     *
     * @param userId 用户ID
     * @return 指定用户的权限列表
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR')")
    public ResponseEntity<ApiResponseDto<List<UserAuthority>>> getUserAuthorities(@PathVariable UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();

        // 检查机构管理员是否有权限查看该用户权限
        if (authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> "INSTITUTION_SUPERVISOR".equals(grantedAuthority.getAuthority()))) {
            // 获取目标用户信息
            User targetUser = userService.getUserByUserId(userId);
            if (targetUser == null) {
                return ResponseEntity.badRequest().body(ApiResponseDto.error("用户不存在"));
            }

            // 获取当前用户信息
            User currentUserInfo = userService.getUserByUserId(currentUser.getId());
            if (currentUserInfo == null) {
                return ResponseEntity.status(403).body(ApiResponseDto.error("无权限访问"));
            }

            // 检查是否同一机构
            if (!Objects.equals(targetUser.getInstitutionId(), currentUserInfo.getInstitutionId())) {
                return ResponseEntity.status(403).body(ApiResponseDto.error("无权限访问该用户权限信息"));
            }
        }

        List<UserAuthority> authorities = userService.getUserAuthorities(userId);

        return ResponseEntity.ok(ApiResponseDto.success(authorities, "获取用户权限列表成功"));
    }

    /**
     * 更新用户权限
     * 修改时检查机构管理员有没有非法增加平台管理员权限的行为
     * 同时确保平台管理员权限不能被删除
     *
     * @param request 权限更新请求
     * @return 操作结果
     */
    @PutMapping
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR')")
    public ResponseEntity<ApiResponseDto<String>> updateUserAuthorities(@RequestBody UserAuthorityUpdateRequestDto request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();

        // 如果是非平台管理员操作，检查是否有非法添加平台管理员权限的行为
        if (authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> UserAuthority.PLATFORM_ADMIN.name().equals(grantedAuthority.getAuthority()))) {
            Set<UserAuthority> newAuthorities = request.getAuthorities();
            if (newAuthorities.contains(UserAuthority.PLATFORM_ADMIN)) {
                return ResponseEntity.status(403).body(ApiResponseDto.error("无权限添加平台管理员权限"));
            }
        }
        
        // 检查是否尝试非法添加平台管理员权限
        if (authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> "INSTITUTION_SUPERVISOR".equals(grantedAuthority.getAuthority()))) {

            
            // 对于机构管理员，还需验证目标用户是否属于自己机构
            User targetUser = userService.getUserByUserId(request.getUserId());
            if (targetUser == null) {
                return ResponseEntity.badRequest().body(ApiResponseDto.error("用户不存在"));
            }
            
            User currentUserInfo = userService.getUserByUserId(currentUser.getId());
            if (currentUserInfo == null) {
                return ResponseEntity.status(403).body(ApiResponseDto.error("无权限操作"));
            }
            
            // 检查是否同一机构
            if (!Objects.equals(targetUser.getInstitutionId(), currentUserInfo.getInstitutionId())) {
                return ResponseEntity.status(403).body(ApiResponseDto.error("无权限操作该用户"));
            }
            
            // 检查目标用户是否为平台管理员
            List<UserAuthority> targetUserAuthorities = userService.getUserAuthorities(request.getUserId());
            if (targetUserAuthorities.contains(UserAuthority.PLATFORM_ADMIN)) {
                return ResponseEntity.status(403).body(ApiResponseDto.error("无权限修改平台管理员的权限"));
            }
        }

        try {
            // 获取要修改权限的用户
            User targetUser = userService.getUserByUserId(request.getUserId());
            if (targetUser == null) {
                return ResponseEntity.badRequest().body(ApiResponseDto.error("用户不存在"));
            }

            // 检查是否试图删除平台管理员权限
            List<UserAuthority> currentUserAuthorities = userService.getUserAuthorities(request.getUserId());
            boolean isPlatformAdmin = currentUserAuthorities.contains(UserAuthority.PLATFORM_ADMIN);
            
            // 如果目标用户是平台管理员，且新权限列表中不包含平台管理员权限，则拒绝操作
            if (isPlatformAdmin && !request.getAuthorities().contains(UserAuthority.PLATFORM_ADMIN)) {
                return ResponseEntity.status(403).body(ApiResponseDto.error("不允许删除平台管理员权限"));
            }

            // 删除用户当前所有权限
            userService.removeAllUserAuthorities(request.getUserId());

            // 添加新权限
            for (UserAuthority authority : request.getAuthorities()) {
                userService.addUserAuthority(request.getUserId(), authority, currentUser.getId());
            }

            return ResponseEntity.ok(ApiResponseDto.success("权限更新成功", "权限更新成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponseDto.error("权限更新失败: " + e.getMessage()));
        }
    }
}