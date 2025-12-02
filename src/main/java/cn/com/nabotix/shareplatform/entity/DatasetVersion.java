package cn.com.nabotix.shareplatform.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "dataset_versions")
public class DatasetVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "dataset_id", nullable = false)
    private UUID datasetId;

    @Column(name = "version_number", nullable = false)
    private String versionNumber;

    @Column(name = "published_date", nullable = false)
    private Instant publishedDate;

    @Column(name = "changes_description")
    private String changesDescription;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "data_dict_url")
    private String dataDictUrl;

    @Column(name = "terms_agreement_url")
    private String termsAgreementUrl;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}