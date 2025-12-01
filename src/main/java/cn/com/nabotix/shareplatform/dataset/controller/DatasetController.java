package cn.com.nabotix.shareplatform.dataset.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.dataset.dto.DatasetBaseDto;

import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import cn.com.nabotix.shareplatform.dataset.service.DatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/datasets")
public class DatasetController {

    private final DatasetService datasetService;

    @Autowired
    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    /**
     * 获取所有数据集列表
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<Dataset>>> getAllDatasets() {
        List<Dataset> datasets = datasetService.getAllDatasets();
        return ResponseEntity.ok(ApiResponseDto.success(datasets, "获取数据集列表成功"));
    }

    /**
     * 根据ID获取特定数据集
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Dataset>> getDatasetById(@PathVariable UUID id) {
        Dataset dataset = datasetService.getDatasetById(id);
        if (dataset != null) {
            return ResponseEntity.ok(ApiResponseDto.success(dataset, "获取数据集成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的数据集"));
        }
    }

    /**
     * 创建新的数据集
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<Dataset>> createDataset(@RequestBody DatasetBaseDto datasetDto) {
        Dataset dataset = new Dataset();
        // 将 DTO 转换为实体
        dataset.setTitleCn(datasetDto.getTitleCn());
        dataset.setDescription(datasetDto.getDescription());
        dataset.setType(datasetDto.getType());
        dataset.setCategory(datasetDto.getCategory());
        dataset.setProviderId(datasetDto.getProviderId());
        dataset.setSupervisorId(datasetDto.getSupervisorId());
        dataset.setStartDate(datasetDto.getStartDate());
        dataset.setEndDate(datasetDto.getEndDate());
        dataset.setRecordCount(datasetDto.getRecordCount());
        dataset.setVariableCount(datasetDto.getVariableCount());
        dataset.setKeywords(datasetDto.getKeywords());
        dataset.setSubjectAreaId(datasetDto.getSubjectAreaId());
        dataset.setFileUrl(datasetDto.getFileUrl());
        dataset.setDataDictUrl(datasetDto.getDataDictUrl());
        dataset.setApproved(datasetDto.getApproved());
        dataset.setPublished(datasetDto.getPublished());
        dataset.setShareAllData(datasetDto.getShareAllData());
        dataset.setDatasetLeader(datasetDto.getDatasetLeader());
        dataset.setDataCollectionUnit(datasetDto.getDataCollectionUnit());
        dataset.setContactPerson(datasetDto.getContactPerson());
        dataset.setContactInfo(datasetDto.getContactInfo());
        dataset.setDemographicFields(datasetDto.getDemographicFields());
        dataset.setOutcomeFields(datasetDto.getOutcomeFields());
        dataset.setTermsAgreementUrl(datasetDto.getTermsAgreementUrl());
        dataset.setSamplingMethod(datasetDto.getSamplingMethod());
        dataset.setVersionNumber(datasetDto.getVersionNumber());
        dataset.setFirstPublishedDate(datasetDto.getFirstPublishedDate());
        dataset.setCurrentVersionDate(datasetDto.getCurrentVersionDate());
        dataset.setParentDatasetId(datasetDto.getParentDatasetId());
        dataset.setPrincipalInvestigator(datasetDto.getPrincipalInvestigator());
        
        Dataset createdDataset = datasetService.createDataset(dataset);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(createdDataset, "创建数据集成功"));
    }

    /**
     * 更新现有数据集
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Dataset>> updateDataset(@PathVariable UUID id, @RequestBody DatasetBaseDto datasetDto) {
        Dataset dataset = new Dataset();
        // 将 DTO 转换为实体
        dataset.setTitleCn(datasetDto.getTitleCn());
        dataset.setDescription(datasetDto.getDescription());
        dataset.setType(datasetDto.getType());
        dataset.setCategory(datasetDto.getCategory());
        dataset.setProviderId(datasetDto.getProviderId());
        dataset.setSupervisorId(datasetDto.getSupervisorId());
        dataset.setStartDate(datasetDto.getStartDate());
        dataset.setEndDate(datasetDto.getEndDate());
        dataset.setRecordCount(datasetDto.getRecordCount());
        dataset.setVariableCount(datasetDto.getVariableCount());
        dataset.setKeywords(datasetDto.getKeywords());
        dataset.setSubjectAreaId(datasetDto.getSubjectAreaId());
        dataset.setFileUrl(datasetDto.getFileUrl());
        dataset.setDataDictUrl(datasetDto.getDataDictUrl());
        dataset.setApproved(datasetDto.getApproved());
        dataset.setPublished(datasetDto.getPublished());
        dataset.setShareAllData(datasetDto.getShareAllData());
        dataset.setDatasetLeader(datasetDto.getDatasetLeader());
        dataset.setDataCollectionUnit(datasetDto.getDataCollectionUnit());
        dataset.setContactPerson(datasetDto.getContactPerson());
        dataset.setContactInfo(datasetDto.getContactInfo());
        dataset.setDemographicFields(datasetDto.getDemographicFields());
        dataset.setOutcomeFields(datasetDto.getOutcomeFields());
        dataset.setTermsAgreementUrl(datasetDto.getTermsAgreementUrl());
        dataset.setSamplingMethod(datasetDto.getSamplingMethod());
        dataset.setVersionNumber(datasetDto.getVersionNumber());
        dataset.setFirstPublishedDate(datasetDto.getFirstPublishedDate());
        dataset.setCurrentVersionDate(datasetDto.getCurrentVersionDate());
        dataset.setParentDatasetId(datasetDto.getParentDatasetId());
        dataset.setPrincipalInvestigator(datasetDto.getPrincipalInvestigator());
        
        Dataset updatedDataset = datasetService.updateDataset(id, dataset);
        if (updatedDataset != null) {
            return ResponseEntity.ok(ApiResponseDto.success(updatedDataset, "更新数据集成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的数据集"));
        }
    }

    /**
     * 删除数据集
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteDataset(@PathVariable UUID id) {
        boolean deleted = datasetService.deleteDataset(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponseDto.success(null, "删除数据集成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的数据集"));
        }
    }
}