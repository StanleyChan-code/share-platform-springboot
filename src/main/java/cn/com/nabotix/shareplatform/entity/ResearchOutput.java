package cn.com.nabotix.shareplatform.entity;

import cn.com.nabotix.shareplatform.enums.OutputType;
import lombok.Data;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "research_outputs")
public class ResearchOutput {
    @Id
    private UUID id;

    @Column(name = "dataset_id", nullable = false)
    private UUID datasetId;

    @Column(name = "submitter_id", nullable = false)
    private UUID submitterId;

    @Enumerated(EnumType.STRING)
    private OutputType type;

    @Column(nullable = false)
    private String title;

    private String abstractText;

    @Column(name = "patent_number")
    private String patentNumber;

    @Column(name = "citation_count")
    private Integer citationCount = 0;

    @Column(name = "publication_url")
    private String publicationUrl;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private Boolean approved = false;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    private String journal;

}