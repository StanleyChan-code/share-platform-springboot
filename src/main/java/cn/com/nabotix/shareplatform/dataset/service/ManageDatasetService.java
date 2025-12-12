package cn.com.nabotix.shareplatform.dataset.service;

import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import cn.com.nabotix.shareplatform.dataset.entity.DatasetVersion;
import cn.com.nabotix.shareplatform.dataset.repository.DatasetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * 数据集管理服务类
 * 提供数据集管理相关业务逻辑，供管理控制器使用
 *
 * @author 陈雍文
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ManageDatasetService {

    private final DatasetRepository datasetRepository;
    private final DatasetVersionService datasetVersionService;

    /**
     * 获取所有数据集（分页）
     */
    public Page<Dataset> getAllDatasets(Pageable pageable) {
        return datasetRepository.findAll(pageable);
    }
    
    /**
     * 根据机构ID获取数据集（分页）
     */
    public Page<Dataset> getDatasetsByInstitutionId(UUID institutionId, Pageable pageable) {
        return datasetRepository.findByInstitutionId(institutionId, pageable);
    }
    
    /**
     * 根据提供者ID获取数据集（分页）
     */
    public Page<Dataset> getDatasetsByProviderId(UUID providerId, Pageable pageable) {
        return datasetRepository.findByProviderId(providerId, pageable);
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
     * 创建新数据集
     */
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