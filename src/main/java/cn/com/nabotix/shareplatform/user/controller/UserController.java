package cn.com.nabotix.shareplatform.user.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.user.dto.RegistrationRequestDto;
import cn.com.nabotix.shareplatform.user.dto.UpdatePasswordRequestDto;

import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.common.service.SmsVerificationService;
import cn.com.nabotix.shareplatform.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用户相关接口控制器
 *
 * @author 陈雍文
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final SmsVerificationService smsVerificationService;
    private final UserService userService;

    public UserController(SmsVerificationService smsVerificationService, UserService userService) {
        this.smsVerificationService = smsVerificationService;
        this.userService = userService;
    }

    /**
     * 请求发送手机验证码
     *
     * @param phoneNumber 手机号码
     * @param businessType 业务类型
     * @return 发送结果
     */
    @PostMapping("/send-verification-code")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> sendVerificationCode(
            @RequestParam String phoneNumber,
            @RequestParam String businessType) {
        try {
            // 生成并发送验证码
            String code = smsVerificationService.generateAndSendVerificationCode(phoneNumber, businessType);
            
            // 在日志中打印验证码，仅用于开发和调试，生产环境应删除此行
            logger.info("Verification code for phone number {}: {}", phoneNumber, code);

            Map<String, Object> data = new HashMap<>();
            // 不再向客户端返回验证码
            
            return ResponseEntity.ok(ApiResponseDto.success(data, "验证码已发送"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponseDto.error("发送验证码失败", e));
        }
    }

    /**
     * 用户注册接口，需要验证手机号和验证码
     *
     * @param registrationRequest 注册请求数据
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<Object>> register(@RequestBody RegistrationRequestDto registrationRequest) {
        try {
            // 验证手机号和验证码（注册业务类型）
            boolean isValid = smsVerificationService.verifyCode(
                    registrationRequest.getPhoneNumber(), 
                    registrationRequest.getVerificationCode(),
                    "REGISTER");

            if (!isValid) {
                return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("验证码无效或已过期"));
            }

            // 验证通过，创建用户
            User user = new User();
            user.setId(UUID.randomUUID());
            user.setUsername(registrationRequest.getUsername());
            user.setPassword(registrationRequest.getPassword());
            user.setRealName(registrationRequest.getRealName());
            if (registrationRequest.getEmail() != null && !registrationRequest.getEmail().isEmpty()) {
                user.setEmail(registrationRequest.getEmail());
            }
            user.setPhone(registrationRequest.getPhoneNumber());

            // 使用加密方式保存用户
            User savedUser = userService.createUser(user);

            return ResponseEntity.ok(ApiResponseDto.success(savedUser, "注册成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponseDto.error("注册失败", e));
        }
    }

    
    /**
     * 修改用户密码接口，需要验证手机验证码（通过请求体）
     *
     * @param userId 用户ID
     * @param updatePasswordRequest 修改密码请求数据
     * @return 修改结果
     */
    @PutMapping("/{userId}/password/body")
    public ResponseEntity<ApiResponseDto<User>> updatePassword(
            @PathVariable UUID userId,
            @RequestBody UpdatePasswordRequestDto updatePasswordRequest) {
        try {
            // 验证手机号和验证码（修改密码业务类型）
            boolean isValid = smsVerificationService.verifyCode(
                    updatePasswordRequest.getPhoneNumber(), 
                    updatePasswordRequest.getVerificationCode(),
                    "UPDATE_PASSWORD");
            
            if (!isValid) {
                return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("验证码无效或已过期"));
            }
            
            User updatedUser = userService.updatePassword(userId, updatePasswordRequest.getNewPassword());
            return ResponseEntity.ok(ApiResponseDto.success(updatedUser, "密码修改成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("用户不存在"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponseDto.error("密码修改失败", e));
        }
    }
    
    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<User>> getUserById(@PathVariable UUID userId) {
        try {
            User user = userService.getUserWithRoles(userId);
            if (user == null) {
                return ResponseEntity.badRequest().body(ApiResponseDto.error("用户不存在"));
            }
            return ResponseEntity.ok(ApiResponseDto.success(user, "获取用户信息成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponseDto.error("获取用户信息失败", e));
        }
    }
}