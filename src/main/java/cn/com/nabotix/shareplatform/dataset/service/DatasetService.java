package cn.com.nabotix.shareplatform.dataset.service;

import cn.com.nabotix.shareplatform.dataset.dto.PublicDatasetDto;
import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import cn.com.nabotix.shareplatform.dataset.repository.DatasetRepository;
import cn.com.nabotix.shareplatform.researchsubject.entity.ResearchSubject;
import cn.com.nabotix.shareplatform.researchsubject.repository.ResearchSubjectRepository;
import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DatasetService {

    private final DatasetRepository datasetRepository;
    private final ResearchSubjectRepository researchSubjectRepository;
    private final UserRepository userRepository;

    @Autowired
    public DatasetService(DatasetRepository datasetRepository,
                          ResearchSubjectRepository researchSubjectRepository,
                          UserRepository userRepository) {
        this.datasetRepository = datasetRepository;
        this.researchSubjectRepository = researchSubjectRepository;
        this.userRepository = userRepository;
    }

    /**
     * 获取所有公开数据集
     */
    public List<PublicDatasetDto> getAllPublicDatasets() {
        return datasetRepository.findPublicVisibleDatasets().stream()
                .map(this::convertToPublicDto)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取公开数据集
     */
    public PublicDatasetDto getPublicDatasetById(UUID id) {
        Dataset dataset = datasetRepository.findPublicVisibleById(id).orElse(null);
        return dataset != null ? convertToPublicDto(dataset) : null;
    }

    /**
     * 根据ID和用户机构ID获取数据集（用于机构内用户访问检查）
     */
    public PublicDatasetDto getDatasetByIdAndUserInstitution(UUID id, UUID userInstitutionId) {
        // 首先尝试查找完全公开的数据集
        Dataset dataset = datasetRepository.findPublicVisibleById(id).orElse(null);
        
        // 如果找不到完全公开的数据集，再尝试查找本机构已批准但未公开的数据集
        if (dataset == null) {
            dataset = datasetRepository.findApprovedByIdAndInstitutionId(id, userInstitutionId).orElse(null);
        }
        
        return dataset != null ? convertToPublicDto(dataset) : null;
    }

    /**
     * 获取机构内可见的数据集列表
     * 返回完全公开的数据集 + 本机构已批准但未公开的数据集
     */
    public List<PublicDatasetDto> getInstitutionVisibleDatasets(UUID institutionId) {
        // 获取完全公开的数据集
        List<PublicDatasetDto> publicDatasets = getAllPublicDatasets();
        
        // 获取本机构已批准但未公开的数据集
        List<Dataset> institutionApprovedDatasets = datasetRepository.findInstitutionVisibleByInstitutionId(institutionId);
        
        // 合并两个列表并去重
        List<PublicDatasetDto> result = publicDatasets.stream().collect(Collectors.toList());
        
        // 添加本机构已批准但未公开的数据集（排除已在公开列表中的）
        for (Dataset dataset : institutionApprovedDatasets) {
            // 检查是否已经在结果中（通过ID判断）
            boolean exists = result.stream().anyMatch(dto -> dto.getId().equals(dataset.getId()));
            if (!exists) {
                result.add(convertToPublicDto(dataset));
            }
        }
        
        return result;
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
    public Dataset createDataset(Dataset dataset) {
        // 设置创建和更新时间
        dataset.setCreatedAt(Instant.now());
        dataset.setUpdatedAt(Instant.now());
        return datasetRepository.save(dataset);
    }

    /**
     * 更新现有数据集
     */
    public Dataset updateDataset(UUID id, Dataset datasetDetails) {
        Dataset dataset = datasetRepository.findById(id).orElse(null);
        if (dataset != null) {
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
            dataset.setFileUrl(datasetDetails.getFileUrl());
            dataset.setDataDictUrl(datasetDetails.getDataDictUrl());
            dataset.setApproved(datasetDetails.getApproved());
            dataset.setPublished(datasetDetails.getPublished());
            dataset.setShareAllData(datasetDetails.getShareAllData());
            dataset.setDatasetLeader(datasetDetails.getDatasetLeader());
            dataset.setDataCollectionUnit(datasetDetails.getDataCollectionUnit());
            dataset.setContactPerson(datasetDetails.getContactPerson());
            dataset.setContactInfo(datasetDetails.getContactInfo());
            dataset.setDemographicFields(datasetDetails.getDemographicFields());
            dataset.setOutcomeFields(datasetDetails.getOutcomeFields());
            dataset.setTermsAgreementUrl(datasetDetails.getTermsAgreementUrl());
            dataset.setSamplingMethod(datasetDetails.getSamplingMethod());
            dataset.setVersionNumber(datasetDetails.getVersionNumber());
            dataset.setFirstPublishedDate(datasetDetails.getFirstPublishedDate());
            dataset.setCurrentVersionDate(datasetDetails.getCurrentVersionDate());
            dataset.setParentDatasetId(datasetDetails.getParentDatasetId());
            dataset.setPrincipalInvestigator(datasetDetails.getPrincipalInvestigator());
            dataset.setInstitutionId(datasetDetails.getInstitutionId());
            
            // 更新修改时间
            dataset.setUpdatedAt(Instant.now());
            
            return datasetRepository.save(dataset);
        }
        return null;
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
     * @param id 数据集ID
     * @param approved 审核状态
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