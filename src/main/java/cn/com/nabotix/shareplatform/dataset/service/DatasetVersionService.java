package cn.com.nabotix.shareplatform.dataset.service;

import cn.com.nabotix.shareplatform.dataset.dto.DatasetVersionDto;
import cn.com.nabotix.shareplatform.dataset.entity.DatasetVersion;
import cn.com.nabotix.shareplatform.dataset.repository.DatasetVersionRepository;
import cn.com.nabotix.shareplatform.dataset.statistic.DatasetStatisticDto;
import cn.com.nabotix.shareplatform.dataset.statistic.DatasetStatisticService;
import cn.com.nabotix.shareplatform.dataset.statistic.dto.DataAnalysisRequestDto;
import cn.com.nabotix.shareplatform.dataset.statistic.dto.DataAnalysisResponseDto;
import cn.com.nabotix.shareplatform.filemanagement.service.FileManagementService;
import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

/**
 * 数据集版本服务类
 * 提供数据集版本相关的业务逻辑处理
 *
 * @author 陈雍文
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatasetVersionService {

    private final DatasetVersionRepository datasetVersionRepository;
    private final UserService userService;
    private final FileManagementService fileManagementService;
    private final DatasetStatisticService datasetStatisticService;

    /**
     * 根据数据集ID获取所有版本
     */
    public List<DatasetVersion> getDatasetVersionsByDatasetId(UUID datasetId) {
        return datasetVersionRepository.findByDatasetIdOrderByCreatedAtDesc(datasetId);
    }

    /**
     * 查询指定数据集的最新已审核版本
     *
     * @param datasetId 数据集ID
     * @return 最新已审核版本，如果不存在则返回null
     */
    public DatasetVersion findLatestApprovedVersionByDatasetId(UUID datasetId) {
        // 查找指定数据集的所有已审核版本，并按创建时间倒序排列
        List<DatasetVersion> approvedVersions = datasetVersionRepository
                .findByDatasetIdAndApprovedTrueOrderByCreatedAtDesc(datasetId);

        // 返回最新版本（第一个元素），如果没有则返回null
        return approvedVersions.isEmpty() ? null : approvedVersions.getFirst();
    }

    /**
     * 获取数据集的所有版本
     */
    public List<DatasetVersion> findAllVersionsByDatasetId(UUID datasetId) {
        return datasetVersionRepository.findByDatasetIdOrderByCreatedAtDesc(datasetId);
    }


    public DatasetVersion getById(UUID id) {
        Optional<DatasetVersion> optional = datasetVersionRepository.findById(id);
        return optional.orElse(null);
    }


    /**
     * 更新数据集审核状态
     *
     * @param datasetVersionId 数据集版本ID
     * @param approved         审核状态
     * @param reviewerId       审核人ID
     * @return 更新后的数据集
     */
    public DatasetVersion updateDatasetApprovalStatus(UUID datasetVersionId, UUID reviewerId, Boolean approved, String rejectReason) {
        DatasetVersion datasetVersion = getById(datasetVersionId);
        if (datasetVersion != null) {
            datasetVersion.setApproved(approved);
            datasetVersion.setSupervisorId(reviewerId);
            datasetVersion.setRejectReason(rejectReason);
            datasetVersion.setApprovedAt(Instant.now());

            if (approved != null && approved) {
                datasetVersion.setPublishedDate(Instant.now());
            } else {
                datasetVersion.setPublishedDate(null);
            }
            return datasetVersionRepository.save(datasetVersion);
        }
        return null;
    }


    public DatasetVersionDto convertToDto(DatasetVersion version) {
        if (version == null) {
            return null;
        }

        User supervisor = null;
        if (version.getSupervisorId() != null) {
            supervisor = userService.getUserByUserId(version.getSupervisorId());
        }
        return DatasetVersionDto.fromEntity(version, supervisor);
    }

    /**
     * 保存数据集版本
     *
     * @param version 数据集版本实体
     * @return 保存后的数据集版本
     */
    public DatasetVersion save(DatasetVersion version) {
        DatasetVersion save = datasetVersionRepository.save(version);

        // 将数据文件分析
        DataAnalysisRequestDto dataAnalysisRequestDto = new DataAnalysisRequestDto(
                version.getFileRecordId(),
                version.getDataDictRecordId()
        );
        DataAnalysisResponseDto dataAnalysisResponseDto = datasetStatisticService.analyzeData(dataAnalysisRequestDto);
        DatasetStatisticDto datasetStatisticDto = new DatasetStatisticDto();
        datasetStatisticDto.setDatasetVersionId(save.getId());
        datasetStatisticDto.setVersion(dataAnalysisResponseDto.getVersion());
        datasetStatisticDto.setVariables(dataAnalysisResponseDto.getVariables());
        datasetStatisticDto.setStatisticalFiles(dataAnalysisResponseDto.getStatisticalFiles());
        datasetStatisticService.saveDatasetStatistic(datasetStatisticDto);

        moveDatasetFiles(save);
        return save;
    }

    /**
     * 将数据集相关文件移动到指定目录
     *
     * @param datasetVersion 数据集版本实体
     */
    private void moveDatasetFiles(DatasetVersion datasetVersion) {
        // 构建数据集文件存储路径
        String basePath = "dataset/" + datasetVersion.getDatasetId() + "/" + datasetVersion.getId() + "/";

        // 移动数据文件
        if (datasetVersion.getFileRecordId() != null) {
            try {
                fileManagementService.moveFileToDirectory(datasetVersion.getFileRecordId(), basePath);
            } catch (Exception e) {
                // 记录日志，但不中断操作
                log.error("Failed to move data file: {}", e.getMessage());
            }
        }

        // 移动数据字典文件
        if (datasetVersion.getDataDictRecordId() != null) {
            try {
                fileManagementService.moveFileToDirectory(datasetVersion.getDataDictRecordId(), basePath);
            } catch (Exception e) {
                // 记录日志，但不中断操作
                log.error("Failed to move data dictionary file: {}", e.getMessage());
            }
        }

        // 移动条款协议文件
        if (datasetVersion.getTermsAgreementRecordId() != null) {
            try {
                fileManagementService.moveFileToDirectory(datasetVersion.getTermsAgreementRecordId(), basePath);
            } catch (Exception e) {
                // 记录日志，但不中断操作
                log.error("Failed to move terms agreement file: {}", e.getMessage());
            }
        }

        // 移动数据分享文件
        if (datasetVersion.getDataSharingRecordId() != null) {
            try {
                fileManagementService.moveFileToDirectory(datasetVersion.getDataSharingRecordId(), basePath);
            } catch (Exception e) {
                // 记录日志，但不中断操作
                log.error("Failed to move data sharing file: {}", e.getMessage());
            }
        }
    }
}