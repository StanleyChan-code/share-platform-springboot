package cn.com.nabotix.shareplatform.dataset.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * 应用实体类，表示数据共享平台中的应用申请信息
 * @author 陈雍文
 */
@Data
@Entity
@Table(name = "applications")
public class Application {
    /**
     * 应用ID，主键，使用UUID生成策略
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * 数据集版本ID，关联的数据集版本标识符
     */
    @Column(name = "dataset_version_id", nullable = false)
    private UUID datasetVersionId;

    /**
     * 申请人ID，申请使用数据的用户标识符
     */
    @Column(name = "applicant_id", nullable = false)
    private UUID applicantId;

    /**
     * 监督人ID，负责审核申请的管理员标识符
     */
    @Column(name = "supervisor_id")
    private UUID supervisorId;

    /**
     * 申请人角色，枚举类型定义了申请人的身份角色
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "applicant_role", nullable = false)
    private ApplicantRole applicantRole;

    /**
     * 申请人类型，描述申请人的组织或个人类型
     */
    @Column(name = "applicant_type")
    private String applicantType;

    /**
     * 项目标题，申请使用数据的项目名称
     */
    @Column(name = "project_title", nullable = false)
    private String projectTitle;

    /**
     * 项目描述，详细说明项目的背景和用途
     */
    @Column(name = "project_description", nullable = false)
    private String projectDescription;

    /**
     * 资金来源，项目资金支持的来源信息
     */
    @Column(name = "funding_source")
    private String fundingSource;

    /**
     * 使用目的，说明申请使用数据的具体目的
     */
    @Column(nullable = false)
    private String purpose;

    /**
     * 项目负责人，项目负责人的姓名
     */
    @Column(name = "project_leader")
    private String projectLeader;

    /**
     * 审批文件ID，关联的审批文件标识符
     */
    @Column(name = "approval_document_id")
    private UUID approvalDocumentId;

    /**
     * 申请状态，枚举类型表示申请的当前处理状态
     */
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    /**
     * 管理员备注，管理员对申请的内部备注信息
     */
    @Column(name = "admin_notes")
    private String adminNotes;

    /**
     * 提供方备注，数据提供方的备注信息
     */
    @Column(name = "provider_notes")
    private String providerNotes;

    /**
     * 提交时间，记录申请提交的时间戳
     */
    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    /**
     * 数据提供者审核时间，记录申请被数据提供者审核的时间戳
     */
    @Column(name = "provider_reviewed_at")
    private Instant providerReviewedAt;

    /**
     * 机构审核时间，记录申请被机构审核的时间戳
     */
    @Column(name = "institution_reviewed_at")
    private Instant institutionReviewedAt;

    /**
     * 批准时间，记录申请被批准的时间戳
     */
    @Column(name = "approved_at")
    private Instant approvedAt;
}