package cn.com.nabotix.shareplatform.security;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.common.service.SmsVerificationService;
import cn.com.nabotix.shareplatform.user.dto.RegistrationRequestDto;
import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.service.UserService;
import cn.com.nabotix.shareplatform.user.repository.UserRepository;
import cn.com.nabotix.shareplatform.security.jwt.JwtUtils;
import cn.com.nabotix.shareplatform.security.jwt.JwtResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 认证控制器
 *
 * @author 陈雍文
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final SmsVerificationService smsVerificationService;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public AuthController(AuthenticationManager authenticationManager, UserService userService,
                          SmsVerificationService smsVerificationService, UserRepository userRepository,
                          JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.smsVerificationService = smsVerificationService;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<JwtResponse>> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        UserDetailsImpl loginUser;
        // 验证码登录
        if ("VERIFICATION_CODE".equals(loginRequest.getLoginType())) {
            if (loginRequest.getPhone() == null || loginRequest.getVerificationCode() == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("手机号和验证码不能为空"));
            }

            // 手机号+验证验证码
            boolean isValid = smsVerificationService.verifyCode(
                    loginRequest.getPhone(),
                    loginRequest.getVerificationCode(),
                    "LOGIN");

            if (!isValid) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("验证码无效或已过期"));
            }

            // 验证通过，获取用户信息
            Optional<User> userOptional = userDetailsServiceImpl.findUserByIdentifier(loginRequest.getPhone());
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("手机号不存在"));
            }

            loginUser = (UserDetailsImpl) userDetailsServiceImpl.loadUserByUsername(loginRequest.getPhone());

            // 手动设置认证信息
            authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        }
        // 密码登录
        else {
            if (loginRequest.getPhone() == null || loginRequest.getPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("手机号和密码不能为空"));
            }

            if (!userRepository.existsByPhone(loginRequest.getPhone())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("手机号不存在"));
            }

            try {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getPhone(), loginRequest.getPassword()));
            } catch (Exception e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("手机号或密码错误"));
            }
            loginUser = (UserDetailsImpl) authentication.getPrincipal();
        }

        // 登录成功，记录并返回jwt
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(ApiResponseDto.success(
                new JwtResponse(jwt, loginUser.getPhone(),
                        loginUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()),
                "登录成功"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<Object>> register(@RequestBody RegistrationRequestDto registrationRequest) {
        try {
            // 验证手机号和验证码（注册业务类型）
            boolean isValid = smsVerificationService.verifyCode(
                    registrationRequest.getPhone(),
                    registrationRequest.getVerificationCode(),
                    "REGISTER");
            if (!isValid) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("验证码无效或已过期"));
            }

            // 检查手机号是否已存在
            if (userRepository.existsByPhone(registrationRequest.getPhone())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("手机号已被注册"));
            }

            // 验证通过，创建用户
            User user = new User();
            user.setUsername(registrationRequest.getUsername());
            user.setPassword(registrationRequest.getPassword());
            user.setRealName(registrationRequest.getRealName());
            if (registrationRequest.getEmail() != null && !registrationRequest.getEmail().isEmpty()) {
                user.setEmail(registrationRequest.getEmail());
            }
            user.setPhone(registrationRequest.getPhone());

            // 使用加密方式保存用户
            User savedUser = userService.createUser(user);

            return ResponseEntity.ok(ApiResponseDto.success(savedUser, "注册成功"));
        } catch (Exception e) {
            logger.error("注册失败", e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDto.error("注册失败"));
        }
    }

    @PostMapping("/send-verification-code")
    public ResponseEntity<ApiResponseDto<String>> sendVerificationCode(@RequestParam String phone,
                                                                       @RequestParam(required = false, defaultValue = "LOGIN") String businessType) {
        try {
            String code = smsVerificationService.generateAndSendVerificationCode(phone, businessType);
            // 在日志中打印验证码，仅用于开发和调试，生产环境应删除此行
            logger.info("Verification code for phone number {}: {}", phone, code);
            return ResponseEntity.ok(ApiResponseDto.success("验证码已发送"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("发送验证码失败", e);
            return ResponseEntity.badRequest().body(ApiResponseDto.error("发送验证码失败"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto<String>> logout() {
        // JWT是无状态的，不需要服务器端登出操作
        // 客户端只需删除本地存储的token即可
        return ResponseEntity.ok(ApiResponseDto.success("登出成功"));
    }
}