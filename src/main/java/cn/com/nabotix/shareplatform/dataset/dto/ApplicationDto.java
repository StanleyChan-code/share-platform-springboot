package cn.com.nabotix.shareplatform.dataset.dto;

import cn.com.nabotix.shareplatform.dataset.entity.Application;
import cn.com.nabotix.shareplatform.dataset.entity.ApplicantRole;
import cn.com.nabotix.shareplatform.dataset.entity.ApplicationStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * 数据集申请DTO
 */
@Data
public class ApplicationDto {
    /**
     * 应用ID
     */
    private UUID id;

    /**
     * 数据集版本ID
     */
    private UUID datasetVersionId;

    /**
     * 数据集标题
     */
    private String datasetTitle;

    /**
     * 申请人ID
     */
    private UUID applicantId;

    /**
     * 申请人姓名
     */
    private String applicantName;

    /**
     * 监督人ID
     */
    private UUID supervisorId;

    /**
     * 监督人姓名
     */
    private String supervisorName;

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

    /**
     * 申请状态
     */
    private ApplicationStatus status;

    /**
     * 管理员备注
     */
    private String adminNotes;

    /**
     * 提供方备注
     */
    private String providerNotes;

    /**
     * 提交时间
     */
    private Instant submittedAt;

    /**
     * 数据提供者审核时间
     */
    private Instant providerReviewedAt;

    /**
     * 机构审核时间
     */
    private Instant institutionReviewedAt;

    /**
     * 批准时间
     */
    private Instant approvedAt;

    public static ApplicationDto fromEntity(Application application) {
        if (application == null) {
            return null;
        }

        ApplicationDto dto = new ApplicationDto();
        dto.setId(application.getId());
        dto.setDatasetVersionId(application.getDatasetVersionId());
        dto.setApplicantId(application.getApplicantId());
        dto.setSupervisorId(application.getSupervisorId());
        dto.setApplicantRole(application.getApplicantRole());
        dto.setApplicantType(application.getApplicantType());
        dto.setProjectTitle(application.getProjectTitle());
        dto.setProjectDescription(application.getProjectDescription());
        dto.setFundingSource(application.getFundingSource());
        dto.setPurpose(application.getPurpose());
        dto.setProjectLeader(application.getProjectLeader());
        dto.setApprovalDocumentId(application.getApprovalDocumentId());
        dto.setStatus(application.getStatus());
        dto.setAdminNotes(application.getAdminNotes());
        dto.setProviderNotes(application.getProviderNotes());
        dto.setSubmittedAt(application.getSubmittedAt());
        dto.setProviderReviewedAt(application.getProviderReviewedAt());
        dto.setInstitutionReviewedAt(application.getInstitutionReviewedAt());
        dto.setApprovedAt(application.getApprovedAt());

        return dto;
    }
}