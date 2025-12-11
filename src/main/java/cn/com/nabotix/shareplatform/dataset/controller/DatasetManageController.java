package cn.com.nabotix.shareplatform.dataset.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.common.service.AuditLogService;
import cn.com.nabotix.shareplatform.dataset.dto.DatasetApprovalRequestDto;
import cn.com.nabotix.shareplatform.dataset.dto.DatasetCreateRequestDto;
import cn.com.nabotix.shareplatform.dataset.dto.DatasetVersionDto;
import cn.com.nabotix.shareplatform.dataset.dto.PublicDatasetDto;
import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import cn.com.nabotix.shareplatform.dataset.entity.DatasetVersion;
import cn.com.nabotix.shareplatform.dataset.service.DatasetService;
import cn.com.nabotix.shareplatform.dataset.service.DatasetVersionService;
import cn.com.nabotix.shareplatform.security.UserAuthority;
import cn.com.nabotix.shareplatform.security.AuthorityUtil;
import cn.com.nabotix.shareplatform.security.UserDetailsImpl;
import cn.com.nabotix.shareplatform.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.util.*;

/**
 * 数据集管理控制器
 * 提供数据集的管理接口，包括创建、更新、删除等操作
 *
 * @author 陈雍文
 */
@Slf4j
@RestController
@RequestMapping("/api/manage/datasets")
public class DatasetManageController {

    private final DatasetService datasetService;
    private final DatasetVersionService datasetVersionService;
    private final UserService userService;
    private final AuditLogService auditLogService;

    @Autowired
    public DatasetManageController(DatasetService datasetService, DatasetVersionService datasetVersionService, UserService userService, AuditLogService auditLogService) {
        this.datasetService = datasetService;
        this.datasetVersionService = datasetVersionService;
        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    /**
     * 获取所有管理的数据集列表
     * 平台管理员和机构管理员可访问所有数据集，数据集上传员只能看到自己上传的数据集
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'DATASET_UPLOADER')")
    public ResponseEntity<ApiResponseDto<List<PublicDatasetDto>>> getAllDatasets() {
        List<Dataset> datasets = new ArrayList<>();
        AuthorityUtil.checkBuilder().whenHasAuthority(() -> {
            // 平台管理员可以看到所有数据集
            datasets.addAll(datasetService.getAllDatasets());
        }, UserAuthority.PLATFORM_ADMIN).whenHasAuthority(() -> {
            // 机构管理员可以看到本机构所有数据集
            UUID institutionId = getCurrentUserInstitutionId();
            if (institutionId != null) {
                datasets.addAll(datasetService.getDatasetsByInstitutionId(institutionId));
            }
        }, UserAuthority.INSTITUTION_SUPERVISOR).whenHasAuthority(() -> {
            // 数据集上传员只能看到自己上传的数据集
            datasets.addAll(datasetService.getDatasetsByProviderId(AuthorityUtil.getCurrentUserId()));
        }, UserAuthority.DATASET_UPLOADER).execute();

        // 转换为 DTO 并包含版本信息
        List<PublicDatasetDto> dtos = datasets.stream()
                .map(datasetService::convertToPublicDto)
                .toList();

        return ResponseEntity.ok(ApiResponseDto.success(dtos, "获取数据集列表成功"));
    }

    /**
     * 根据ID获取特定管理的数据集
     * 平台管理员和机构管理员可访问所有数据集，数据集上传员只能看到自己上传数据集
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'DATASET_UPLOADER')")
    public ResponseEntity<ApiResponseDto<PublicDatasetDto>> getDatasetById(@PathVariable UUID id) {
        Dataset dataset = datasetService.getDatasetById(id);

        if (dataset == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的数据集"));
        }

        // 检查权限
        if (!hasPermissionToManageDataset(dataset)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error("无权访问该数据集"));
        }

        // 获取版本信息
        PublicDatasetDto dto = datasetService.convertToPublicDto(dataset);

        return ResponseEntity.ok(ApiResponseDto.success(dto, "获取数据集成功"));
    }

    /**
     * 创建新的数据集
     * 平台管理员、机构管理员和数据集上传员可创建数据集
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'DATASET_UPLOADER')")
    public ResponseEntity<ApiResponseDto<PublicDatasetDto>> createDataset(@RequestBody DatasetCreateRequestDto datasetDto) {
        Dataset dataset = new Dataset();
        // 将 DTO 转换为实体
        dataset.setTitleCn(datasetDto.getTitleCn());
        dataset.setDescription(datasetDto.getDescription());
        dataset.setType(datasetDto.getType());
        dataset.setDatasetLeader(datasetDto.getDatasetLeader());
        dataset.setPrincipalInvestigator(datasetDto.getPrincipalInvestigator());
        dataset.setDataCollectionUnit(datasetDto.getDataCollectionUnit());
        dataset.setStartDate(datasetDto.getStartDate());
        dataset.setEndDate(datasetDto.getEndDate());
        dataset.setRecordCount(datasetDto.getRecordCount());
        dataset.setVariableCount(datasetDto.getVariableCount());
        dataset.setKeywords(datasetDto.getKeywords());
        dataset.setSubjectAreaId(datasetDto.getSubjectAreaId());
        dataset.setCategory(datasetDto.getCategory());
        dataset.setSamplingMethod(datasetDto.getSamplingMethod());
        dataset.setPublished(datasetDto.getPublished());
        dataset.setShareAllData(datasetDto.getShareAllData());
        dataset.setContactPerson(datasetDto.getContactPerson());
        dataset.setContactInfo(datasetDto.getContactInfo());
        dataset.setDemographicFields(datasetDto.getDemographicFields());
        dataset.setOutcomeFields(datasetDto.getOutcomeFields());
        dataset.setParentDatasetId(datasetDto.getParentDatasetId());
        dataset.setApplicationInstitutionIds(datasetDto.getApplicationInstitutionIds());

        Instant now = Instant.now();
        dataset.setFirstPublishedDate(now);
        dataset.setCurrentVersionDate(now);

        // 设置创建者和所属机构
        dataset.setProviderId(AuthorityUtil.getCurrentUserId());
        dataset.setInstitutionId(getCurrentUserInstitutionId());

        // 创建最初数据集版本记录
        DatasetVersion firstVersion = getFirstVersion(datasetDto);

        Dataset createdDataset = datasetService.createDataset(dataset, firstVersion);

        PublicDatasetDto dto = datasetService.convertToPublicDto(createdDataset);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(dto, "创建数据集成功"));
    }

    private static DatasetVersion getFirstVersion(DatasetCreateRequestDto datasetDto) {
        DatasetVersion firstVersion = new DatasetVersion();
        firstVersion.setVersionNumber(datasetDto.getVersionNumber());
        firstVersion.setDescription(datasetDto.getVersionDescription());
        firstVersion.setFileRecordId(datasetDto.getFileRecordId());
        firstVersion.setDataDictRecordId(datasetDto.getDataDictRecordId());
        firstVersion.setTermsAgreementRecordId(datasetDto.getTermsAgreementRecordId());
        firstVersion.setDataSharingRecordId(datasetDto.getDataSharingRecordId());
        firstVersion.setApproved(null);
        firstVersion.setSupervisorId(null);
        firstVersion.setPublishedDate(null);
        return firstVersion;
    }

    /**
     * 更新现有数据集基本信息，包括：描述、关键字、是否公开、共享数据、联系人、联系方式、人口统计、结果字段、采样方法、限制的申请机构
     * 平台管理员可更新任意数据集，机构管理员只能更新自己机构，数据集上传员只能更新自己的数据集
     */
    @PutMapping("/{id}/basic-info")
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'DATASET_UPLOADER')")
    public ResponseEntity<ApiResponseDto<PublicDatasetDto>> updateDatasetBasicInfo(@PathVariable UUID id, @RequestBody DatasetCreateRequestDto datasetDto) {
        Dataset dataset = datasetService.getDatasetById(id);

        if (dataset == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的数据集"));
        }

        // 检查权限
        if (!hasPermissionToManageDataset(dataset)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error("无权更新该数据集"));
        }
        // 更新基础字段
        dataset.setDescription(datasetDto.getDescription());
        dataset.setKeywords(datasetDto.getKeywords());
        dataset.setPublished(datasetDto.getPublished());
        dataset.setShareAllData(datasetDto.getShareAllData());
        dataset.setContactPerson(datasetDto.getContactPerson());
        dataset.setContactInfo(datasetDto.getContactInfo());
        dataset.setDemographicFields(datasetDto.getDemographicFields());
        dataset.setOutcomeFields(datasetDto.getOutcomeFields());
        dataset.setSamplingMethod(datasetDto.getSamplingMethod());
        dataset.setApplicationInstitutionIds(datasetDto.getApplicationInstitutionIds());

        Dataset updatedDataset = datasetService.updateDataset(id, dataset);

        PublicDatasetDto dto = datasetService.convertToPublicDto(updatedDataset);
        return ResponseEntity.ok(ApiResponseDto.success(dto, "更新数据集基本信息成功"));
    }

    /**
     * 为现有数据集添加新版本
     * 平台管理员可为任意数据集添加版本，机构管理员只能为自己机构的数据集添加版本，数据集上传员只能为自己的数据集添加版本
     */
    @PostMapping("/{id}/versions")
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'DATASET_UPLOADER')")
    public ResponseEntity<ApiResponseDto<DatasetVersionDto>> addDatasetVersion(@PathVariable UUID id, @RequestBody DatasetCreateRequestDto datasetDto) {
        Dataset dataset = datasetService.getDatasetById(id);

        if (dataset == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的数据集"));
        }

        // 检查权限
        if (!hasPermissionToManageDataset(dataset)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error("无权为该数据集添加新版本"));
        }

        // 创建新的版本记录
        DatasetVersion newVersion = new DatasetVersion();
        newVersion.setDatasetId(dataset.getId());
        newVersion.setVersionNumber(datasetDto.getVersionNumber());
        newVersion.setDescription(datasetDto.getDescription());
        newVersion.setFileRecordId(datasetDto.getFileRecordId());
        newVersion.setDataDictRecordId(datasetDto.getDataDictRecordId());
        newVersion.setTermsAgreementRecordId(datasetDto.getTermsAgreementRecordId());
        newVersion.setDataSharingRecordId(datasetDto.getDataSharingRecordId());
        newVersion.setCreatedAt(Instant.now());

        // 设置新版本为待审核状态
        newVersion.setApproved(null);
        newVersion.setSupervisorId(null);

        // 保存新版本
        DatasetVersion savedVersion = datasetVersionService.save(newVersion);
        DatasetVersionDto datasetVersionDto = datasetVersionService.convertToDto(savedVersion);

        return ResponseEntity.ok(ApiResponseDto.success(datasetVersionDto, "添加数据集新版本成功"));
    }

    /**
     * 修改数据集审核状态（通过、驳回）
     * 平台管理员、机构管理员和数据集审核员可修改任意数据集的审核状态
     */
    @PutMapping("/{id}/{datasetVersionId}/approval")
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'DATASET_APPROVER')")
    public ResponseEntity<ApiResponseDto<DatasetVersionDto>> updateDatasetApprovalStatus(
            @PathVariable UUID id,
            @PathVariable UUID datasetVersionId,
            @RequestBody DatasetApprovalRequestDto approvalRequest,
            HttpServletRequest request) {

        Dataset dataset = datasetService.getDatasetById(id);

        if (dataset == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的数据集"));
        }
        if (datasetVersionId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error("数据集版本ID不能为空"));
        }

        // 检查权限
        if (!hasPermissionToApproveDataset(dataset)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error("无权修改该数据集的审核状态"));
        }

        DatasetVersion datasetVersion = datasetVersionService.getById(datasetVersionId);
        if (datasetVersion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的数据集版本"));
        } else if (!datasetVersion.getDatasetId().equals(dataset.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error("数据集版本ID无效"));
        }

        // 更新审核状态
        DatasetVersion updatedDatasetVersion = datasetVersionService.updateDatasetApprovalStatus(
                datasetVersion.getId(),
                AuthorityUtil.getCurrentUserId(),
                approvalRequest.getApproved(),
                approvalRequest.getRejectionReason());

        // 如果审核通过，则更新数据集的信息
        if (approvalRequest.getApproved() != null && approvalRequest.getApproved()) {
            dataset.setCurrentVersionDate(updatedDatasetVersion.getCreatedAt());
            datasetService.updateDataset(dataset.getId(), dataset);
        }

        // 记录审核操作到审计日志
        try {
            String action = approvalRequest.getApproved() == null ?
                    "RESET_DATASET_APPROVAL_STATUS" :
                    approvalRequest.getApproved() ? "APPROVE_DATASET" : "REJECT_DATASET";

            Map<String, Object> additionalParams = new HashMap<>();
            if (approvalRequest.getRejectionReason() != null) {
                additionalParams.put("rejectionReason", approvalRequest.getRejectionReason());
            }
            auditLogService.logApprovalAction(action, datasetVersionId, dataset.getTitleCn(), additionalParams, request.getRemoteAddr());
        } catch (Exception e) {
            // 日志记录失败不应该影响主要业务流程
            log.error("记录审计日志失败", e);
        }
        if (updatedDatasetVersion == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("更新数据集审核状态失败"));
        }

        // 获取版本信息
        DatasetVersionDto datasetVersionDto = datasetVersionService.convertToDto(updatedDatasetVersion);

        String message = approvalRequest.getApproved() == null ?
                "数据集审核状态重置" :
                approvalRequest.getApproved() ? "数据集审核通过" : "数据集审核驳回";
        return ResponseEntity.ok(ApiResponseDto.success(datasetVersionDto, message));
    }

    /**
     * 检查用户是否有权限更新指定数据集的审核状态
     *
     * @param dataset 数据集
     * @return 是否有权限
     */
    private boolean hasPermissionToApproveDataset(Dataset dataset) {
        return AuthorityUtil.checkBuilder()
                .withAllowedAuthorities(UserAuthority.INSTITUTION_SUPERVISOR, UserAuthority.DATASET_APPROVER)
                .withTargetInstitutionId(dataset.getInstitutionId())
                .execute();
    }

    /**
     * 检查用户是否有权限更新指定数据集
     *
     * @param dataset 数据集
     * @return 是否有权限
     */
    private boolean hasPermissionToManageDataset(Dataset dataset) {
        return AuthorityUtil.checkBuilder()
                .withAllowedAuthorities(UserAuthority.INSTITUTION_SUPERVISOR, UserAuthority.DATASET_UPLOADER)
                .withTargetInstitutionId(dataset.getInstitutionId())
                .withTargetUserId(dataset.getProviderId())
                .execute();
    }

    /**
     * 获取当前认证用户的机构ID
     *
     * @return 机构ID
     */
    private UUID getCurrentUserInstitutionId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // 获取用户信息及机构ID
        var user = userService.getUserByUserId(userDetails.getId());
        return user != null ? user.getInstitutionId() : null;
    }
}