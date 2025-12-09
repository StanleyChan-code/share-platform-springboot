package cn.com.nabotix.shareplatform.dataset.entity;
/**
 * 应用状态枚举类
 * 
 * 定义了应用程序在整个审批流程中的各种状态
 * 包括提交、待审核、批准和拒绝等状态
 *
 * @author 陈雍文
 */
public enum ApplicationStatus {
    SUBMITTED,
    PENDING_PROVIDER_REVIEW,
    PENDING_INSTITUTION_REVIEW,
    APPROVED,
    DENIED
}