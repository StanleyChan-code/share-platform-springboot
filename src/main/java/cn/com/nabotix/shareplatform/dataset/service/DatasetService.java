package cn.com.nabotix.shareplatform.dataset.service;

import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import cn.com.nabotix.shareplatform.dataset.repository.DatasetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class DatasetService {

    private final DatasetRepository datasetRepository;

    @Autowired
    public DatasetService(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
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
     * 创建新数据集
     */
    public Dataset createDataset(Dataset dataset) {
        // 设置创建和更新时间
        dataset.setCreatedAt(Instant.now());
        dataset.setUpdatedAt(Instant.now());
        
        // 如果没有设置ID，则生成一个新的UUID
        if (dataset.getId() == null) {
            dataset.setId(UUID.randomUUID());
        }
        
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
}