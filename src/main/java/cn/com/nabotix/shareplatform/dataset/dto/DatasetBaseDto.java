package cn.com.nabotix.shareplatform.dataset.dto;

import cn.com.nabotix.shareplatform.dataset.entity.DatasetType;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * 数据集基础DTO
 * 包含数据集的公共字段
 *
 * @author 陈雍文
 */
@Data
public class DatasetBaseDto {
    private String titleCn;
    private String description;
    private DatasetType type;
    private String category;
    private UUID providerId;
    private UUID supervisorId;
    private Instant startDate;
    private Instant endDate;
    private Integer recordCount;
    private Integer variableCount;
    private String[] keywords;
    private UUID subjectAreaId;
    private UUID fileRecordId;
    private UUID dataDictRecordId;
    private Boolean approved = false;
    private Boolean published = false;
    private Boolean shareAllData = false;
    private String datasetLeader;
    private String dataCollectionUnit;
    private String contactPerson;
    private String contactInfo;
    private String demographicFields;
    private String outcomeFields;
    private UUID termsAgreementRecordId;
    private String samplingMethod;
    private String versionNumber;
    private Instant firstPublishedDate;
    private Instant currentVersionDate;
    private UUID parentDatasetId;
    private String principalInvestigator;
    private UUID[] applicationInstitutionIds;
}