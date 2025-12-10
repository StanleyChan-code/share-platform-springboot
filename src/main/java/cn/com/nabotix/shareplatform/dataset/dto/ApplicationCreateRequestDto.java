package cn.com.nabotix.shareplatform.dataset.dto;

import cn.com.nabotix.shareplatform.dataset.entity.ApplicantRole;
import lombok.Data;

import java.util.UUID;

/**
 * 数据集申请创建请求DTO
 */
@Data
public class ApplicationCreateRequestDto {
    /**
     * 数据集版本ID
     */
    private UUID datasetVersionId;

    /**
     * 申请人角色
     */
    private ApplicantRole applicantRole;

    /**
     * 申请人类型
     */
    private String applicantType;

    /**
     * 项目标题
     */
    private String projectTitle;

    /**
     * 项目描述
     */
    private String projectDescription;

    /**
     * 资金来源
     */
    private String fundingSource;

    /**
     * 使用目的
     */
    private String purpose;

    /**
     * 项目负责人
     */
    private String projectLeader;

    /**
     * 审批文件ID
     */
    private UUID approvalDocumentId;
}