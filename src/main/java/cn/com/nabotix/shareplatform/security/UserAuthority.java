package cn.com.nabotix.shareplatform.security;

/**
 * 用户权限枚举类
 * 定义了系统中不同用户角色的权限等级
 * @author 陈雍文
 */
public enum UserAuthority {
    INSTITUTION_SUPERVISOR,
    PLATFORM_ADMIN,
    DATASET_UPLOADER,
    DATASET_APPROVER,
    RESEARCH_OUTPUT_APPROVER
}