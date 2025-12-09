package cn.com.nabotix.shareplatform.dataset.service;

import cn.com.nabotix.shareplatform.dataset.dto.PublicDatasetDto;
import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import cn.com.nabotix.shareplatform.dataset.repository.DatasetRepository;
import cn.com.nabotix.shareplatform.filemanagement.service.FileManagementService;
import cn.com.nabotix.shareplatform.researchsubject.entity.ResearchSubject;
import cn.com.nabotix.shareplatform.researchsubject.repository.ResearchSubjectRepository;
import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.repository.UserRepository;
import jakarta.persistence.Transient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据集服务类
 * 提供数据集的增删改查、审批、发布等相关业务逻辑
 */
@Slf4j
@Service
public class DatasetService {

    private final DatasetRepository datasetRepository;
    private final ResearchSubjectRepository researchSubjectRepository;
    private final UserRepository userRepository;
    private final FileManagementService fileManagementService;

    @Autowired
    public DatasetService(DatasetRepository datasetRepository,
                          ResearchSubjectRepository researchSubjectRepository,
                          UserRepository userRepository,
                          FileManagementService fileManagementService) {
        this.datasetRepository = datasetRepository;
        this.researchSubjectRepository = researchSubjectRepository;
        this.userRepository = userRepository;
        this.fileManagementService = fileManagementService;
    }

    /**
     * 根据ID获取公开数据集（适用于匿名用户）
     */
    public PublicDatasetDto getPublicDatasetById(UUID id) {
        return datasetRepository.findById(id)
                .filter(d -> d.getApproved() != null && d.getPublished() != null &&
                        d.getApproved() && d.getPublished())
                .map(this::convertToPublicDto)
                .orElse(null);
    }

    /**
     * 根据ID和用户机构ID获取数据集（用于机构内用户访问）
     */
    public PublicDatasetDto getDatasetByIdAndUserInstitution(UUID id, UUID userInstitutionId) {
        return datasetRepository.findById(id)
                .filter(d -> d.getApproved() != null && d.getApproved() &&
                        d.getPublished() != null && (d.getPublished() || d.getInstitutionId().equals(userInstitutionId)))
                .map(this::convertToPublicDto)
                .orElse(null);
    }

    /**
     * 获取所有公开的顶层数据集（parentDatasetId为空）
     */
    public Page<PublicDatasetDto> getAllPublicTopLevelDatasets(Pageable pageable) {
        Page<Dataset> datasetPage = datasetRepository.findPublicVisibleTopLevelDatasets(pageable);
        List<PublicDatasetDto> dtoList = datasetPage.getContent().stream()
                .map(this::convertToPublicDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, datasetPage.getTotalElements());
    }

    /**
     * 获取机构内可见的顶层数据集列表（parentDatasetId为空）
     * 返回完全公开的顶层数据集 + 本机构已批准但未公开的顶层数据集
     */
    public Page<PublicDatasetDto> getInstitutionVisibleTopLevelDatasets(UUID institutionId, Pageable pageable) {
        // 使用新的查询方法，直接在数据库层面合并查询并分页
        Page<Dataset> datasetPage = datasetRepository.findPublicOrInstitutionVisibleTopLevelDatasets(institutionId, pageable);
        List<PublicDatasetDto> dtoList = datasetPage.getContent().stream()
                .map(this::convertToPublicDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, datasetPage.getTotalElements());
    }

    /**
     * 获取时间轴形式的公开数据集（仅包含没有父数据集的数据集）
     * 并附带其子数据集（随访数据集）
     * 匿名用户：只能看到已批准且已发布的数据集
     * 机构内用户：能看到已批准且已发布的数据集 + 已批准但未公开的本机构数据集
     */
    public Page<PublicDatasetDto> getTimelinePublicDatasets(Pageable pageable) {
        // 获取公开可见的顶层数据集（分页）
        Page<Dataset> datasetPage = datasetRepository.findPublicVisibleTopLevelDatasets(pageable);

        // 转换为时间轴DTO（包含子数据集）
        List<PublicDatasetDto> timelineDtoList = datasetPage.getContent().stream()
                .map(this::convertToTimelinePublicDto)
                .sorted(Comparator.comparing(PublicDatasetDto::getStartDate))
                .collect(Collectors.toList());

        return new PageImpl<>(timelineDtoList, pageable, datasetPage.getTotalElements());
    }

    /**
     * 获取时间轴形式的机构可见数据集（仅包含没有父数据集的数据集）
     * 并附带其子数据集（随访数据集）
     * 机构内用户：能看到已批准且已发布的数据集 + 已批准但未公开的本机构数据集
     */
    public Page<PublicDatasetDto> getTimelineInstitutionVisibleDatasets(UUID institutionId, Pageable pageable) {
        Page<Dataset> datasetPage = datasetRepository.findPublicOrInstitutionVisibleTopLevelDatasets(institutionId, pageable);
        List<PublicDatasetDto> timelineDtoList = datasetPage.getContent().stream()
                .map(dataset -> convertToTimelineInstitutionDto(dataset, institutionId))
                .collect(Collectors.toList());
        return new PageImpl<>(timelineDtoList, pageable, datasetPage.getTotalElements());
    }

    /**
     * 将Dataset实体转换为PublicDatasetDto（时间轴版本，包含子数据集）
     */
    public PublicDatasetDto convertToTimelinePublicDto(Dataset dataset) {
        PublicDatasetDto dto = convertToPublicDto(dataset);

        // 获取该数据集的子数据集（随访数据集）
        if (dto.getId() != null) {
            PublicDatasetDto[] children = datasetRepository.findByParentDatasetId(dto.getId()).stream()
                    .filter(child -> child.getApproved() != null && child.getApproved() &&
                            child.getPublished() != null && child.getPublished())
                    .sorted(Comparator.comparing(Dataset::getStartDate))
                    .map(this::convertToPublicDto)
                    .toArray(PublicDatasetDto[]::new);

            dto.setFollowUpDatasets(children);
        }

        return dto;
    }

    /**
     * 将Dataset实体转换为PublicDatasetDto（针对机构用户的版本，包含子数据集）
     */
    public PublicDatasetDto convertToTimelineInstitutionDto(Dataset dataset, UUID userInstitutionId) {
        PublicDatasetDto dto = convertToPublicDto(dataset);

        // 获取该数据集的子数据集（随访数据集）
        if (dto.getId() != null) {

            PublicDatasetDto[] children = datasetRepository.findByParentDatasetId(dto.getId()).stream()
                    .filter(child -> child.getApproved() != null && child.getApproved() &&
                            child.getPublished() != null && (child.getPublished() || child.getInstitutionId().equals(userInstitutionId)))
                    .sorted(Comparator.comparing(Dataset::getStartDate))
                    .map(this::convertToPublicDto)
                    .toArray(PublicDatasetDto[]::new);

            dto.setFollowUpDatasets(children);
        }

        return dto;
    }

    /**
     * 将Dataset实体转换为PublicDatasetDto
     */
    public PublicDatasetDto convertToPublicDto(Dataset dataset) {
        // 设置学科领域信息
        ResearchSubject subjectArea = dataset.getSubjectAreaId() != null ?
                researchSubjectRepository.findById(dataset.getSubjectAreaId()).orElse(null) : null;

        // 设置用户信息
        User supervisor = dataset.getSupervisorId() != null ?
                userRepository.findById(dataset.getSupervisorId()).orElse(null) : null;

        User provider = dataset.getProviderId() != null ?
                userRepository.findById(dataset.getProviderId()).orElse(null) : null;

        return PublicDatasetDto.fromEntity(dataset, subjectArea, supervisor, provider);
    }

    /**
     * 获取所有数据集
     */
    public List<Dataset> getAllDatasets() {
        return datasetRepository.findAll();
    }

    /**
     * 根据ID获取数据集
     */
    public Dataset getDatasetById(UUID id) {
        return datasetRepository.findById(id).orElse(null);
    }

    /**
     * 根据机构ID获取数据集
     */
    public List<Dataset> getDatasetsByInstitutionId(UUID institutionId) {
        return datasetRepository.findByInstitutionId(institutionId);
    }

    /**
     * 根据提供者ID获取数据集
     */
    public List<Dataset> getDatasetsByProviderId(UUID providerId) {
        return datasetRepository.findByProviderId(providerId);
    }

    /**
     * 创建新数据集
     */
    @Transient
    public Dataset createDataset(Dataset dataset) {
        // 设置创建和更新时间
        dataset.setCreatedAt(Instant.now());
        dataset.setUpdatedAt(Instant.now());

        Dataset saveDateset = datasetRepository.save(dataset);

        if (dataset.getFileRecordId() == null) {
            return null;
        }

        // 移动相关文件到指定目录
        moveDatasetFiles(saveDateset);
        
        return saveDateset;
    }

    /**
     * 更新现有数据集
     */
    public Dataset updateDataset(UUID id, Dataset datasetDetails) {
        Dataset dataset = datasetRepository.findById(id).orElse(null);
        if (dataset != null) {
            // 保存旧的文件记录ID，以便后续比较
            UUID oldFileRecordId = dataset.getFileRecordId();
            UUID oldDataDictRecordId = dataset.getDataDictRecordId();
            UUID oldTermsAgreementRecordId = dataset.getTermsAgreementRecordId();

            // 更新字段
            dataset.setTitleCn(datasetDetails.getTitleCn());
            dataset.setDescription(datasetDetails.getDescription());
            dataset.setType(datasetDetails.getType());
            dataset.setCategory(datasetDetails.getCategory());
            dataset.setProviderId(datasetDetails.getProviderId());
            dataset.setSupervisorId(datasetDetails.getSupervisorId());
            dataset.setStartDate(datasetDetails.getStartDate());
            dataset.setEndDate(datasetDetails.getEndDate());
            dataset.setRecordCount(datasetDetails.getRecordCount());
            dataset.setVariableCount(datasetDetails.getVariableCount());
            dataset.setKeywords(datasetDetails.getKeywords());
            dataset.setSubjectAreaId(datasetDetails.getSubjectAreaId());
            dataset.setFileRecordId(datasetDetails.getFileRecordId());
            dataset.setDataDictRecordId(datasetDetails.getDataDictRecordId());
            dataset.setApproved(datasetDetails.getApproved());
            dataset.setPublished(datasetDetails.getPublished());
            dataset.setShareAllData(datasetDetails.getShareAllData());
            dataset.setDatasetLeader(datasetDetails.getDatasetLeader());
            dataset.setDataCollectionUnit(datasetDetails.getDataCollectionUnit());
            dataset.setContactPerson(datasetDetails.getContactPerson());
            dataset.setContactInfo(datasetDetails.getContactInfo());
            dataset.setDemographicFields(datasetDetails.getDemographicFields());
            dataset.setOutcomeFields(datasetDetails.getOutcomeFields());
            dataset.setTermsAgreementRecordId(datasetDetails.getTermsAgreementRecordId());
            dataset.setSamplingMethod(datasetDetails.getSamplingMethod());
            dataset.setVersionNumber(datasetDetails.getVersionNumber());
            dataset.setFirstPublishedDate(datasetDetails.getFirstPublishedDate());
            dataset.setCurrentVersionDate(datasetDetails.getCurrentVersionDate());
            dataset.setParentDatasetId(datasetDetails.getParentDatasetId());
            dataset.setPrincipalInvestigator(datasetDetails.getPrincipalInvestigator());
            dataset.setInstitutionId(datasetDetails.getInstitutionId());
            dataset.setApplicationInstitutionIds(datasetDetails.getApplicationInstitutionIds());

            // 更新修改时间
            dataset.setUpdatedAt(Instant.now());
            
            // 移动相关文件到指定目录
            moveDatasetFiles(dataset);

            // 保存更新后的数据集
            Dataset updatedDataset = datasetRepository.save(dataset);

            // 检查是否有文件记录ID变更，如果有则标记删除旧文件
            handleFileRecordChanges(oldFileRecordId, dataset.getFileRecordId());
            handleFileRecordChanges(oldDataDictRecordId, dataset.getDataDictRecordId());
            handleFileRecordChanges(oldTermsAgreementRecordId, dataset.getTermsAgreementRecordId());

            return updatedDataset;
        }
        return null;
    }
    
    /**
     * 将数据集相关文件移动到指定目录
     * @param dataset 数据集实体
     */
    private void moveDatasetFiles(Dataset dataset) {
        // 构建数据集文件存储路径
        String basePath = "dataset/" + dataset.getId() + "/";
        
        // 移动数据文件
        if (dataset.getFileRecordId() != null) {
            try {
                fileManagementService.moveFileToDirectory(dataset.getFileRecordId(), basePath);
            } catch (Exception e) {
                // 记录日志，但不中断操作
                log.error("Failed to move data file: {}", e.getMessage());
            }
        }
        
        // 移动数据字典文件
        if (dataset.getDataDictRecordId() != null) {
            try {
                fileManagementService.moveFileToDirectory(dataset.getDataDictRecordId(), basePath);
            } catch (Exception e) {
                // 记录日志，但不中断操作
                log.error("Failed to move data dictionary file: {}", e.getMessage());
            }
        }
        
        // 移动条款协议文件
        if (dataset.getTermsAgreementRecordId() != null) {
            try {
                fileManagementService.moveFileToDirectory(dataset.getTermsAgreementRecordId(), basePath);
            } catch (Exception e) {
                // 记录日志，但不中断操作
                log.error("Failed to move terms agreement file: {}", e.getMessage());
            }
        }
    }

    /**
     * 处理文件记录变更，如果文件记录ID发生变化，则标记删除旧文件
     * @param oldFileRecordId 旧文件记录ID
     * @param newFileRecordId 新文件记录ID
     */
    private void handleFileRecordChanges(UUID oldFileRecordId, UUID newFileRecordId) {
        // 如果旧文件记录ID不为空且与新文件记录ID不同，则标记删除旧文件
        if (oldFileRecordId != null && !oldFileRecordId.equals(newFileRecordId)) {
            try {
                fileManagementService.markFileAsDeleted(oldFileRecordId);
            } catch (Exception e) {
                // 记录日志，但不中断操作
                log.error("Failed to mark old file as deleted: {}", e.getMessage());
            }
        }
    }

    /**
     * 删除数据集
     */
    public boolean deleteDataset(UUID id) {
        if (datasetRepository.existsById(id)) {
            datasetRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * 更新数据集审核状态
     *
     * @param id         数据集ID
     * @param approved   审核状态
     * @param reviewerId 审核人ID
     * @return 更新后的数据集
     */
    public Dataset updateDatasetApprovalStatus(UUID id, Boolean approved, UUID reviewerId) {
        Dataset dataset = datasetRepository.findById(id).orElse(null);
        if (dataset != null) {
            dataset.setApproved(approved);
            dataset.setSupervisorId(reviewerId);
            dataset.setUpdatedAt(Instant.now());

            return datasetRepository.save(dataset);
        }
        return null;
    }
}