package cn.com.nabotix.shareplatform.dataset.service;

import cn.com.nabotix.shareplatform.dataset.dto.DatasetVersionDto;
import cn.com.nabotix.shareplatform.dataset.dto.PublicDatasetDto;
import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import cn.com.nabotix.shareplatform.dataset.repository.DatasetRepository;
import cn.com.nabotix.shareplatform.researchsubject.entity.ResearchSubject;
import cn.com.nabotix.shareplatform.researchsubject.repository.ResearchSubjectRepository;
import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.repository.UserRepository;
import cn.com.nabotix.shareplatform.popularity.service.PopularityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
@RequiredArgsConstructor
public class DatasetService {

    private final DatasetRepository datasetRepository;
    private final ResearchSubjectRepository researchSubjectRepository;
    private final UserRepository userRepository;
    private final DatasetVersionService datasetVersionService;
    private final PopularityService popularityService;

    /**
     * 根据ID和用户机构ID获取数据集
     *
     * @param id                数据集ID
     * @param userInstitutionId 用户机构ID，当未空时则滤出公开可见的数据集
     * @param loadTimeline      是否加载随访数据集
     */
    public PublicDatasetDto getDatasetByIdAndUserInstitution(UUID id, UUID userInstitutionId, boolean loadTimeline) {
        return datasetRepository.findById(id)
                .filter(d -> checkPublicDatasetAssessPermission(d, userInstitutionId))
                .map(dataset -> loadTimeline ?
                        convertToTimelinePublicDto(dataset, userInstitutionId) :
                        convertToPublicDto(dataset))
                .orElse(null);
    }


    /**
     * 获取公开和机构内可见的顶层数据集列表（parentDatasetId为空）
     * 机构内用户：能看到已批准且已发布数据集 + 已批准但未公开且数据集允许申请的机构用户
     * 匿名用户（userInstitutionId为null）：只能看到已批准且已发布数据集
     */
    public Page<PublicDatasetDto> getAllPublicTopLevelDatasets(UUID institutionId, Pageable pageable) {
        // 使用新的查询方法，直接在数据库层面合并查询并分页
        Page<Dataset> datasetPage;
        if (institutionId == null) {
            datasetPage = datasetRepository.findPublicVisibleTopLevelDatasets(pageable);
        } else {
            datasetPage = datasetRepository.findPublicOrInstitutionVisibleTopLevelDatasets(institutionId, pageable);
        }
        List<PublicDatasetDto> dtoList = datasetPage.getContent().stream()
                .map(this::convertToPublicDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, datasetPage.getTotalElements());
    }


    /**
     * 根据学科领域ID获取数据集（分页）
     * 机构内用户：能看到已批准且已发布数据集 + 已批准但未公开且数据集允许申请的机构用户
     * 匿名用户（userInstitutionId为null）：只能看到已批准且已发布数据集
     */
    public Page<PublicDatasetDto> getAllDatasetsBySubjectAreaId(UUID subjectAreaId, UUID institutionId, Pageable pageable) {
        Page<Dataset> datasetPage;
        if (institutionId == null) {
            datasetPage = datasetRepository.findPublicVisibleTopLevelDatasetsBySubjectAreaId(subjectAreaId, pageable);
        } else {
            datasetPage = datasetRepository.findPublicVisibleTopLevelDatasetsBySubjectAreaId(subjectAreaId, institutionId, pageable);
        }
        List<PublicDatasetDto> dtoList = datasetPage.getContent().stream()
                .map(this::convertToPublicDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, datasetPage.getTotalElements());
    }

    /**
     * 将Dataset实体转换为PublicDatasetDto（时间轴版本，包含子数据集）
     */
    public PublicDatasetDto convertToTimelinePublicDto(Dataset dataset, UUID userInstitutionId) {
        PublicDatasetDto dto = convertToPublicDto(dataset);

        // 获取该数据集的子数据集（随访数据集）
        if (dto.getId() != null) {
            dto.setFollowUpDatasets(getAllDatasetsByParentDatasetId(dto.getId(), userInstitutionId));
        }

        return dto;
    }

    /**
     * 获取随访数据集（不含父数据集）
     * 机构内用户：能看到已批准且已发布数据集 + 已批准但未公开且数据集允许申请的机构用户
     * 匿名用户（userInstitutionId为null）：只能看到已批准且已发布数据集
     *
     * @param parentDatasetId   父数据集ID
     * @param userInstitutionId 用户机构ID，当未空时则滤出公开可见的数据集
     */
    public List<PublicDatasetDto> getAllDatasetsByParentDatasetId(UUID parentDatasetId, UUID userInstitutionId) {
        return datasetRepository.findByParentDatasetId(parentDatasetId).stream()
                .filter(child -> checkPublicDatasetAssessPermission(child, userInstitutionId))
                .sorted(Comparator.comparing(Dataset::getStartDate))
                .map(this::convertToPublicDto)
                .toList();
    }

    /**
     * 将Dataset实体转换为PublicDatasetDto
     */
    public PublicDatasetDto convertToPublicDto(Dataset dataset) {
        if (dataset == null || dataset.getId() == null) {
            return null;
        }

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

        // 从Redis获取实时热度数据
        Long realTimeSearchCount = popularityService.getDatasetPopularity(dataset.getId());
        if (realTimeSearchCount != null) {
            dataset.setSearchCount(realTimeSearchCount.intValue());
        }

        return PublicDatasetDto.fromEntity(dataset, subjectArea, provider, versionsDtos);
    }


    /**
     * 查看公开数据集的权限控制
     * 机构内用户：能看到已批准且已发布数据集 + 已批准但未公开且数据集允许申请的机构用户
     * 匿名用户（userInstitutionId为null）：只能看到已批准且已发布数据集
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
        // 未审核，则返回false
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
}