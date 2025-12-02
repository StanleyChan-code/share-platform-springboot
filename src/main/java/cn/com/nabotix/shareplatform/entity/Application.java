package cn.com.nabotix.shareplatform.entity;

import cn.com.nabotix.shareplatform.enums.ApplicantRole;
import cn.com.nabotix.shareplatform.enums.ApplicationStatus;
import lombok.Data;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "dataset_id", nullable = false)
    private UUID datasetId;

    @Column(name = "applicant_id", nullable = false)
    private UUID applicantId;

    @Column(name = "supervisor_id")
    private UUID supervisorId;

    @Column(name = "project_title", nullable = false)
    private String projectTitle;

    @Column(name = "project_description", nullable = false)
    private String projectDescription;

    @Column(name = "funding_source")
    private String fundingSource;

    @Column(nullable = false)
    private String purpose;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(name = "admin_notes")
    private String adminNotes;

    @Column(name = "provider_notes")
    private String providerNotes;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "approval_document_url")
    private String approvalDocumentUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "applicant_role", nullable = false)
    private ApplicantRole applicantRole;

    @Column(name = "applicant_type")
    private String applicantType;
}