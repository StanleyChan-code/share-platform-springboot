package cn.com.nabotix.shareplatform.dataset.entity;

import cn.com.nabotix.shareplatform.enums.DatasetType;
import lombok.Data;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.JdbcTypeCode;

@Data
@Entity
@Table(name = "datasets")
public class Dataset {
    @Id
    private UUID id;

    @Column(name = "title_cn", nullable = false)
    private String titleCn;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private DatasetType type;

    private String category;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "supervisor_id")
    private UUID supervisorId;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "record_count")
    private Integer recordCount;

    @Column(name = "variable_count")
    private Integer variableCount;

    @Column(name = "keywords", columnDefinition = "text[]")
    private String[] keywords;

    @Column(name = "subject_area_id")
    private UUID subjectAreaId;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "data_dict_url")
    private String dataDictUrl;

    private Boolean approved = false;

    private Boolean published = false;

    @Column(name = "search_count")
    private Integer searchCount = 0;

    @Column(name = "share_all_data")
    private Boolean shareAllData = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "dataset_leader")
    private String datasetLeader;

    @Column(name = "data_collection_unit")
    private String dataCollectionUnit;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "contact_info")
    private String contactInfo;

    @Column(name = "demographic_fields", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String demographicFields;

    @Column(name = "outcome_fields", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String outcomeFields;

    @Column(name = "terms_agreement_url")
    private String termsAgreementUrl;

    @Column(name = "sampling_method")
    private String samplingMethod;

    @Column(name = "version_number")
    private String versionNumber;

    @Column(name = "first_published_date")
    private Instant firstPublishedDate;

    @Column(name = "current_version_date")
    private Instant currentVersionDate;

    @Column(name = "parent_dataset_id")
    private UUID parentDatasetId;

    @Column(name = "principal_investigator")
    private String principalInvestigator;

}