package cn.com.nabotix.shareplatform.dataset.service;

import cn.com.nabotix.shareplatform.dataset.dto.DatasetVersionDto;
import cn.com.nabotix.shareplatform.dataset.dto.PublicDatasetDto;
import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import cn.com.nabotix.shareplatform.dataset.repository.DatasetRepository;
import cn.com.nabotix.shareplatform.researchsubject.entity.ResearchSubject;
import cn.com.nabotix.shareplatform.researchsubject.repository.ResearchSubjectRepository;
import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.repository.UserRepository;
import cn.com.nabotix.shareplatform.dataset.entity.DatasetVersion;
import jakarta.persistence.Transient;
import lombok.NonNull;
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
 *
 * @author 陈雍文
 */
@Slf4j
@Service
public class DatasetService {

    private final DatasetRepository datasetRepository;
    private final ResearchSubjectRepository researchSubjectRepository;
    private final UserRepository userRepository;
    private final DatasetVersionService datasetVersionService;

    @Autowired
    public DatasetService(DatasetRepository datasetRepository,
                          ResearchSubjectRepository researchSubjectRepository,
                          UserRepository userRepository,
                          DatasetVersionService datasetVersionService) {
        this.datasetRepository = datasetRepository;
        this.researchSubjectRepository = researchSubjectRepository;
        this.userRepository = userRepository;
        this.datasetVersionService = datasetVersionService;
    }


    /**
     * 根据ID和用户机构ID获取数据集
     *
     * @param id                数据集ID
     * @param userInstitutionId 用户机构ID，当未空时则滤出公开可见的数据集
     */
    public PublicDatasetDto getDatasetByIdAndUserInstitution(UUID id, UUID userInstitutionId) {
        return getDatasetByIdAndUserInstitution(id, userInstitutionId, false);
    }

    public PublicDatasetDto getDatasetByIdAndUserInstitution(UUID id, UUID userInstitutionId, boolean loadTimeline) {
        return datasetRepository.findById(id)
                .filter(d -> checkPublicDatasetAssessPermission(d, userInstitutionId))
                .map(dataset -> loadTimeline ?
                        convertToTimelinePublicDto(dataset, userInstitutionId) :
                        convertToPublicDto(dataset))
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
     * 返回完全公开的顶层数据集 + 已批准但未公开的用户机构能申请的顶层数据集
     */
    public Page<PublicDatasetDto> getAllInstitutionVisibleTopLevelDatasets(UUID institutionId, Pageable pageable) {
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
                .map(dataset -> convertToTimelinePublicDto(dataset, null))
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
                .map(dataset -> convertToTimelinePublicDto(dataset, institutionId))
                .collect(Collectors.toList());
        return new PageImpl<>(timelineDtoList, pageable, datasetPage.getTotalElements());
    }

    /**
     * 将Dataset实体转换为PublicDatasetDto（时间轴版本，包含子数据集）
     */
    public PublicDatasetDto convertToTimelinePublicDto(Dataset dataset, UUID userInstitutionId) {
        PublicDatasetDto dto = convertToPublicDto(dataset);

        // 获取该数据集的子数据集（随访数据集）
        if (dto.getId() != null) {
            List<PublicDatasetDto> children = datasetRepository.findByParentDatasetId(dto.getId()).stream()
                    .filter(child -> checkPublicDatasetAssessPermission(child, userInstitutionId))
                    .sorted(Comparator.comparing(Dataset::getStartDate))
                    .map(this::convertToPublicDto)
                    .toList();

            dto.setFollowUpDatasets(children);
        }

        return dto;
    }

    /**
     * 将Dataset实体转换为PublicDatasetDto
     */
    public PublicDatasetDto convertToPublicDto(@NonNull Dataset dataset) {
        // 设置学科领域信息
        ResearchSubject subjectArea = dataset.getSubjectAreaId() != null ?
                researchSubjectRepository.findById(dataset.getSubjectAreaId()).orElse(null) : null;

        // 获取提供者信息
        User provider = dataset.getProviderId() != null ?
                userRepository.findById(dataset.getProviderId()).orElse(null) : null;

        // 获取版本信息
        List<DatasetVersionDto> versionsDtos = dataset.getId() == null ?
                null :
                datasetVersionService.findAllVersionsByDatasetId(dataset.getId()).stream()
                        .map(datasetVersionService::convertToDto)
                        .toList();

        return PublicDatasetDto.fromEntity(dataset, subjectArea, provider, versionsDtos);
    }


    /**
     * 查看公开数据集的权限控制
     */
    public boolean checkPublicDatasetAssessPermission(Dataset dataset, UUID userInstitutionId) {
        if (dataset == null || dataset.getId() == null) {
            return false;
        }

        // 获取数据集的机构ID
        UUID datasetInstitutionId = dataset.getInstitutionId();

        // 如果数据集的机构ID为空，或者未审核，则返回false
        if (datasetInstitutionId == null) {
            return false;
        }
        if (datasetVersionService.findLatestApprovedVersionByDatasetId(dataset.getId()) == null) {
            return false;
        }

        // 如果公开访问，则返回true；如果非公开访问就必须检查用户机构id
        if (dataset.getPublished()) {
            return true;
        } else if (userInstitutionId == null) {
            return false;
        }

        // 查阅数据集时，数据集所属机构与用户所属机构没有关系，同一机构也不代表用户能够查看数据集
        // 如果不是公开访问，则判断用户当前机构id是否在允许申请的机构列表中
        List<UUID> applicationInstitutionIds = dataset.getApplicationInstitutionIds();
        return applicationInstitutionIds != null && applicationInstitutionIds.contains(userInstitutionId);
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
        if (id == null) {
            return null;
        }
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
    public Dataset createDataset(Dataset dataset, DatasetVersion firstVersion) {
        // 设置创建和更新时间
        dataset.setCreatedAt(Instant.now());
        dataset.setUpdatedAt(Instant.now());

        Dataset savedDataset = datasetRepository.save(dataset);

        // 创建第一个版本
        firstVersion.setDatasetId(savedDataset.getId());
        firstVersion.setCreatedAt(Instant.now());
        datasetVersionService.save(firstVersion);

        return savedDataset;
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
            dataset.setDatasetLeader(datasetDetails.getDatasetLeader());
            dataset.setPrincipalInvestigator(datasetDetails.getPrincipalInvestigator());
            dataset.setDataCollectionUnit(datasetDetails.getDataCollectionUnit());
            dataset.setStartDate(datasetDetails.getStartDate());
            dataset.setEndDate(datasetDetails.getEndDate());
            dataset.setRecordCount(datasetDetails.getRecordCount());
            dataset.setVariableCount(datasetDetails.getVariableCount());
            dataset.setKeywords(datasetDetails.getKeywords());
            dataset.setSubjectAreaId(datasetDetails.getSubjectAreaId());
            dataset.setSamplingMethod(datasetDetails.getSamplingMethod());
            dataset.setPublished(datasetDetails.getPublished());
            dataset.setSearchCount(datasetDetails.getSearchCount());
            dataset.setShareAllData(datasetDetails.getShareAllData());
            dataset.setContactPerson(datasetDetails.getContactPerson());
            dataset.setContactInfo(datasetDetails.getContactInfo());
            dataset.setDemographicFields(datasetDetails.getDemographicFields());
            dataset.setOutcomeFields(datasetDetails.getOutcomeFields());
            dataset.setFirstPublishedDate(datasetDetails.getFirstPublishedDate());
            dataset.setCurrentVersionDate(datasetDetails.getCurrentVersionDate());
            dataset.setParentDatasetId(datasetDetails.getParentDatasetId());
            dataset.setInstitutionId(datasetDetails.getInstitutionId());
            dataset.setApplicationInstitutionIds(datasetDetails.getApplicationInstitutionIds());

            // 更新修改时间
            dataset.setUpdatedAt(Instant.now());

            // 保存更新后的数据集
            return datasetRepository.save(dataset);
        }
        return null;
    }
}