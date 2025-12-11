package cn.com.nabotix.shareplatform.dataset.dto;

import cn.com.nabotix.shareplatform.dataset.entity.DatasetVersion;
import cn.com.nabotix.shareplatform.user.dto.UserDto;
import cn.com.nabotix.shareplatform.user.entity.User;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * 数据集版本DTO
 * 用于展示数据集的版本信息
 *
 * @author 陈雍文
 */
@Data
public class DatasetVersionDto {
    private UUID id;
    private UUID datasetId;
    private String versionNumber;
    private Instant createdAt;
    private Instant publishedDate;
    private String description;

    private UUID fileRecordId;
    private UUID dataDictRecordId;
    private UUID termsAgreementRecordId;
    private UUID dataSharingRecordId;
    private Boolean approved;
    private String rejectReason;
    private Instant approvedAt;
    private UserDto supervisor;

    public static DatasetVersionDto fromEntity(DatasetVersion version, User supervisor) {
        if (version == null) {
            return null;
        }
        DatasetVersionDto dto = new DatasetVersionDto();
        dto.id = version.getId();
        dto.datasetId = version.getDatasetId();
        dto.versionNumber = version.getVersionNumber();
        dto.createdAt = version.getCreatedAt();
        dto.publishedDate = version.getPublishedDate();
        dto.description = version.getDescription();

        dto.fileRecordId = version.getFileRecordId();
        dto.dataDictRecordId = version.getDataDictRecordId();
        dto.termsAgreementRecordId = version.getTermsAgreementRecordId();
        dto.dataSharingRecordId = version.getDataSharingRecordId();
        dto.approved = version.getApproved();
        dto.approvedAt = version.getApprovedAt();
        dto.rejectReason = version.getRejectReason();

        dto.supervisor = UserDto.fromEntity(supervisor);

        return dto;
    }
}