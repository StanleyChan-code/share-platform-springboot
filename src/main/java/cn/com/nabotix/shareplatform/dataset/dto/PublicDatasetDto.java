package cn.com.nabotix.shareplatform.dataset.dto;

import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import cn.com.nabotix.shareplatform.dataset.entity.DatasetType;
import cn.com.nabotix.shareplatform.researchsubject.entity.ResearchSubject;
import cn.com.nabotix.shareplatform.user.entity.User;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * 公共数据集DTO
 * 用于向公众展示数据集基本信息
 *
 * @author 陈雍文
 */
@Data
public class PublicDatasetDto {
    private UUID id;
    private UUID parentDatasetId;

    private String titleCn;
    private String description;
    private DatasetType type;
    private UserDto provider;
    private UserDto supervisor;

    private Instant startDate;
    private Instant endDate;

    private String datasetLeader;
    private String principalInvestigator;
    private String dataCollectionUnit;

    private Integer recordCount;
    private Integer variableCount;
    private String[] keywords;
    private SubjectAreaDto subjectArea;
    private String category;

    private Boolean approved = false;
    private Boolean published = false;
    private Integer searchCount = 0;
    private Boolean shareAllData = false;

    private String versionNumber;
    private Instant firstPublishedDate;
    private Instant currentVersionDate;

    private Instant createdAt;
    private Instant updatedAt;
    
    // 申请机构ID列表
    private UUID[] applicationInstitutionIds;
    
    // 添加子数据集字段（随访数据集）
    private PublicDatasetDto[] followUpDatasets;

    /**
     * 数据集监督者DTO
     * 用于向公众展示数据集监督者的基本信息
     */
    @Data
    static class UserDto {
        private UUID id;
        private String username;
        private String realName;
        private String title;

        public static UserDto fromEntity(User user) {
            if (user == null) {
                return null;
            }
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setUsername(user.getUsername());
            userDto.setRealName(user.getRealName());
            userDto.setTitle(user.getTitle());
            return userDto;
        }
    }


    /**
     * 学科领域DTO
     * 用于向公众展示学科领域的基本信息
     */
    @Data
    static class SubjectAreaDto {
        private UUID id;
        private String name;
        private String nameEn;
        private String description;

        public static SubjectAreaDto fromEntity(ResearchSubject subjectArea) {
            if (subjectArea == null) {
                return null;
            }
            SubjectAreaDto subjectAreaDto = new SubjectAreaDto();
            subjectAreaDto.setId(subjectArea.getId());
            subjectAreaDto.setName(subjectArea.getName());
            subjectAreaDto.setNameEn(subjectArea.getNameEn());
            subjectAreaDto.setDescription(subjectArea.getDescription());
            return subjectAreaDto;
        }
    }

    public static PublicDatasetDto fromEntity(Dataset dataset, ResearchSubject subjectArea, User supervisor, User provider) {
        if (dataset == null){
            return null;
        }

        PublicDatasetDto publicDatasetDto = new PublicDatasetDto();
        publicDatasetDto.setId(dataset.getId());
        publicDatasetDto.setParentDatasetId(dataset.getParentDatasetId());
        publicDatasetDto.setTitleCn(dataset.getTitleCn());
        publicDatasetDto.setDescription(dataset.getDescription());
        publicDatasetDto.setType(dataset.getType());
        publicDatasetDto.setCategory(dataset.getCategory());
        publicDatasetDto.setProvider(UserDto.fromEntity(provider));
        publicDatasetDto.setSupervisor(UserDto.fromEntity(supervisor));
        publicDatasetDto.setStartDate(dataset.getStartDate());
        publicDatasetDto.setEndDate(dataset.getEndDate());
        publicDatasetDto.setDatasetLeader(dataset.getDatasetLeader());
        publicDatasetDto.setPrincipalInvestigator(dataset.getPrincipalInvestigator());
        publicDatasetDto.setDataCollectionUnit(dataset.getDataCollectionUnit());
        publicDatasetDto.setRecordCount(dataset.getRecordCount());
        publicDatasetDto.setVariableCount(dataset.getVariableCount());
        publicDatasetDto.setKeywords(dataset.getKeywords());
        publicDatasetDto.setSubjectArea(SubjectAreaDto.fromEntity(subjectArea));
        publicDatasetDto.setApproved(dataset.getApproved());
        publicDatasetDto.setPublished(dataset.getPublished());
        publicDatasetDto.setSearchCount(dataset.getSearchCount());
        publicDatasetDto.setShareAllData(dataset.getShareAllData());
        publicDatasetDto.setVersionNumber(dataset.getVersionNumber());
        publicDatasetDto.setFirstPublishedDate(dataset.getFirstPublishedDate());
        publicDatasetDto.setCurrentVersionDate(dataset.getCurrentVersionDate());
        publicDatasetDto.setCreatedAt(dataset.getCreatedAt());
        publicDatasetDto.setUpdatedAt(dataset.getUpdatedAt());
        publicDatasetDto.setApplicationInstitutionIds(dataset.getApplicationInstitutionIds());
        return publicDatasetDto;
    }
}