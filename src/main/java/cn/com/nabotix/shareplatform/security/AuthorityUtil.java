package cn.com.nabotix.shareplatform.security;

import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限检查帮助工具类
 * 提供统一的权限检查机制，使用Builder模式构建权限检查条件
 *
 * @author 陈雍文
 */
@Slf4j
@Component
public class AuthorityUtil {

    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        AuthorityUtil.userService = userService;
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }

    public static User getCurrentUser() {
        if (!isAuthenticated()) {
            return null;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userService.getUserByUserId(userDetails.getId());
    }

    /**
     * 创建权限检查器Builder
     *
     * @return AuthorityCheckerBuilder实例
     */
    public static AuthorityCheckerBuilder checkBuilder() {
        return new AuthorityCheckerBuilder();
    }

    /**
     * 权限检查器Builder类
     */
    public static class AuthorityCheckerBuilder {
        private UUID targetInstitutionId;
        private UUID targetUserId;
        private boolean allowPlatformAdminOverride = true;

        private Set<UserAuthority> allowedAuthorities = new HashSet<>();
        private final List<Map.Entry<UserAuthority, Runnable>> orderedAuthorityActions = new ArrayList<>();
        private Runnable checkTrueAction;
        private Runnable checkFalseAction;
        private Runnable anonymousAction;


        /**
         * 设置允许的权限列表
         *
         * @param authorities 允许的权限
         * @return Builder实例
         */
        public AuthorityCheckerBuilder withAllowedAuthorities(UserAuthority... authorities) {
            this.allowedAuthorities = Set.of(authorities);
            return this;
        }

        /**
         * 设置为不允许平台管理员覆盖权限检查
         *
         * @return Builder实例
         */
        public AuthorityCheckerBuilder withoutPlatformAdminOverride() {
            this.allowPlatformAdminOverride = false;
            return this;
        }

        /**
         * 设置目标机构ID（用于机构范围权限检查）
         *
         * @param institutionId 目标机构ID
         * @return Builder实例
         */
        public AuthorityCheckerBuilder withTargetInstitutionId(UUID institutionId) {
            this.targetInstitutionId = institutionId;
            return this;
        }

        /**
         * 设置目标用户ID（用于用户相关权限检查）
         *
         * @param userId 目标用户ID
         * @return Builder实例
         */
        public AuthorityCheckerBuilder withTargetUserId(UUID userId) {
            this.targetUserId = userId;
            return this;
        }

        /**
         * 添加权限和对应操作的映射关系，当具有对应权限时执行相应的操作
         * 按添加顺序存储，检查时只执行第一个匹配的权限操作
         *
         * @param authority 权限
         * @param action    对应的操作
         * @return Builder实例
         * @throws IllegalArgumentException 如果权限或操作为空或者权限操作已设置。
         *                                  同时使用了whenHasAuthority和withAllowedAuthorities也会导致错误
         */
        public AuthorityCheckerBuilder whenHasAuthority(UserAuthority authority, Runnable action) {
            if (authority == null || action == null) {
                throw new IllegalArgumentException("权限和操作不能为空");
            } else if (allowedAuthorities.contains(authority)) {
                throw new IllegalArgumentException("权限设置已存在");
            } else if (orderedAuthorityActions.size() != allowedAuthorities.size()) {
                throw new UnsupportedOperationException("权限和操作数量不匹配，这可能是同时使用了whenHasAuthority和withAllowedAuthorities导致的");
            }
            orderedAuthorityActions.add(Map.entry(authority, action));
            allowedAuthorities.add(authority);
            return this;
        }

        public AuthorityCheckerBuilder whenAnonymous(Runnable action) {
            if (action == null) {
                throw new IllegalArgumentException("操作不能为空");
            }
            this.anonymousAction = action;
            return this;
        }

        public AuthorityCheckerBuilder whenCheckFalse(Runnable action) {
            if (action == null) {
                throw new IllegalArgumentException("操作不能为空");
            }
            this.checkFalseAction = action;
            return this;
        }

        public AuthorityCheckerBuilder whenCheckTrue(Runnable action) {
            if (action == null) {
                throw new IllegalArgumentException("操作不能为空");
            }
            this.checkTrueAction = action;
            return this;
        }

        /**
         * 执行权限检查并执行相应的操作，具体是检查是否有设置的权限，再按照设置的操作顺序来核对后执行相应的操作
         *
         * @return 是否具有相应权限
         */
        public boolean execute() {
            boolean checkResult = checkAndExecute();
            if (checkResult) {
                if (checkTrueAction != null) {
                    checkTrueAction.run();
                }
            } else {
                if (checkFalseAction != null) {
                    checkFalseAction.run();
                }
            }
            return checkResult;
        }

        /**
         * 执行权限检查
         *
         * @return 是否具有相应权限
         */
        private boolean checkAndExecute() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
                if (anonymousAction != null) {
                    anonymousAction.run();
                }
                return false;
            }

            // 获取当前用户权限
            Set<String> currentUserAuthorities = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            // 检查平台管理员权限（可以访问所有资源）
            if (checkPlatformAdminAccess(currentUserAuthorities)) {
                executeOrderedAuthorityActions(currentUserAuthorities);
                return true;
            }

            // 检查是否具有直接允许的权限
            if (!checkDirectPermission(currentUserAuthorities)) {
                return false;
            }

            // 如果没有特定的机构或用户限制，则权限检查通过
            if (targetInstitutionId == null && targetUserId == null) {
                executeOrderedAuthorityActions(currentUserAuthorities);
                return true;
            }

            // 获取当前用户信息
            UUID currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return false;
            }
            User currentUser = userService.getUserByUserId(currentUserId);
            if (currentUser == null) {
                return false;
            }

            // 如果指定了目标用户ID，检查当前用户是否本人
            if (checkSelfAccess(currentUserId)) {
                executeOrderedAuthorityActions(currentUserAuthorities);
                return true;
            }

            // 如果指定了目标机构ID，检查当前用户是否属于指定机构
            if (checkInstitutionAccess(currentUser, currentUserAuthorities)) {
                executeOrderedAuthorityActions(currentUserAuthorities);
                return true;
            }

            return false;
        }

        /**
         * 按顺序检查并执行第一个匹配的权限操作
         *
         * @param currentUserAuthorities 当前用户权限集合
         */
        private void executeOrderedAuthorityActions(Set<String> currentUserAuthorities) {
            for (Map.Entry<UserAuthority, Runnable> entry : orderedAuthorityActions) {
                if (currentUserAuthorities.contains(entry.getKey().name())) {
                    entry.getValue().run();
                    break; // 只执行第一个匹配的权限操作
                }
            }
        }

        /**
         * 检查平台管理员权限
         *
         * @param currentUserAuthorities 当前用户权限集合
         * @return 是否具有平台管理员权限
         */
        private boolean checkPlatformAdminAccess(Set<String> currentUserAuthorities) {
            return allowPlatformAdminOverride && currentUserAuthorities.contains(UserAuthority.PLATFORM_ADMIN.name());
        }

        /**
         * 检查是否有直接允许的权限
         *
         * @param currentUserAuthorities 当前用户权限集合
         * @return 是否具有直接允许的权限
         */
        private boolean checkDirectPermission(Set<String> currentUserAuthorities) {
            return allowedAuthorities != null && allowedAuthorities.stream()
                    .map(Enum::name)
                    .anyMatch(currentUserAuthorities::contains);
        }

        /**
         * 检查自我访问权限（用户访问自己的资源）
         *
         * @param currentUserId 当前用户ID
         * @return 是否允许访问自己的资源
         */
        private boolean checkSelfAccess(UUID currentUserId) {
            return targetUserId != null && targetUserId.equals(currentUserId);
        }

        /**
         * 检查机构访问权限
         *
         * @param currentUser            当前用户对象
         * @param currentUserAuthorities 当前用户权限集合
         * @return 是否具有机构访问权限
         */
        private boolean checkInstitutionAccess(User currentUser, Set<String> currentUserAuthorities) {
            if (targetInstitutionId == null || currentUser.getInstitutionId() == null) {
                return false;
            }

            // 机构管理员可以访问本机构的所有资源
            if (currentUserAuthorities.contains(UserAuthority.INSTITUTION_SUPERVISOR.name()) &&
                    targetInstitutionId.equals(currentUser.getInstitutionId())) {
                return true;
            }

            // 检查其他需要匹配同一机构的权限
            for (UserAuthority authority : allowedAuthorities) {
                switch (authority) {
                    case DATASET_UPLOADER:
                        // 数据上传者只能管理自己的数据
                        if (targetUserId == null || !targetUserId.equals(currentUser.getId())) {
                            if (targetUserId ==  null) {
                                log.warn("DATASET_UPLOADER权限检查需要配置目标用户ID，请检查权限配置");
                            }
                            return false;
                        }
                        // 此外还需要检查是否在同一个机构
                    case DATASET_APPROVER:
                        if (targetInstitutionId.equals(currentUser.getInstitutionId())) {
                            return true;
                        }
                        break;
                    default:
                        // 其他权限已在前面处理
                        break;
                }
            }

            return false;
        }

        /**
         * 获取当前认证用户的ID
         *
         * @return 用户ID
         */
        private UUID getCurrentUserId() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
                return userDetails.getId();
            }
            return null;
        }
    }
}