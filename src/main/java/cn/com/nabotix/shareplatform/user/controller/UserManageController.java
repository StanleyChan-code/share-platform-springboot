package cn.com.nabotix.shareplatform.user.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.security.UserDetailsImpl;
import cn.com.nabotix.shareplatform.user.dto.UserCreateRequestDto;
import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.service.UserService;
import cn.com.nabotix.shareplatform.security.UserAuthority;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 * 提供平台管理员管理用户的接口
 *
 * @author 陈雍文
 */
@RestController
@RequestMapping("/api/manage/users")
public class UserManageController {

    private final UserService userService;

    public UserManageController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 平台管理员创建用户
     *
     * @param userCreateRequest 用户创建请求
     * @return 创建的用户信息
     */
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponseDto<User>> createUser(@RequestBody UserCreateRequestDto userCreateRequest) {
        try {
            if (userService.getUserByPhone(userCreateRequest.getPhone()) != null) {
                return ResponseEntity.status(400).body(ApiResponseDto.error("手机号已存在"));
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // 创建用户
            User user = new User();
            user.setUsername(userCreateRequest.getUsername());
            user.setRealName(userCreateRequest.getRealName());
            user.setPhone(userCreateRequest.getPhone());
            user.setEmail(userCreateRequest.getEmail());
            user.setPassword(userCreateRequest.getPassword());
            user.setInstitutionId(userCreateRequest.getInstitutionId());
            user.setSupervisorId(userDetails.getId());

            User createdUser = userService.createUser(user);

            // 为用户添加机构管理员权限

            userService.addUserAuthority(
                    createdUser.getId(), 
                    UserAuthority.INSTITUTION_SUPERVISOR,
                    userDetails.getId());

            // 清除敏感信息
            createdUser.setPassword(null);

            return ResponseEntity.ok(ApiResponseDto.success(createdUser, "用户创建成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponseDto.error("用户创建失败: " + e.getMessage()));
        }
    }

    // todo: 这里需要增加用户权限控制，去除掉上面那个方法的默认添加机构管理员权限
}