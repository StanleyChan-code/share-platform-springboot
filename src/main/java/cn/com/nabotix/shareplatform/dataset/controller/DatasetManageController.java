package cn.com.nabotix.shareplatform.dataset.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.common.service.AuditLogService;
import cn.com.nabotix.shareplatform.dataset.dto.DatasetApprovalRequestDto;
import cn.com.nabotix.shareplatform.dataset.dto.DatasetBaseDto;
import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import cn.com.nabotix.shareplatform.dataset.service.DatasetService;
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
    private final UserService userService;
    private final AuditLogService auditLogService;

    @Autowired
    public DatasetManageController(DatasetService datasetService, UserService userService, AuditLogService auditLogService) {
        this.datasetService = datasetService;
        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    /**
     * 获取所有管理的数据集列表
     * 平台管理员和机构管理员可访问所有数据集，数据集上传员只能看到自己上传的数据集
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'DATASET_UPLOADER')")
    public ResponseEntity<ApiResponseDto<List<Dataset>>> getAllDatasets() {
        List<Dataset> datasets = new ArrayList<>();
        AuthorityUtil.checkBuilder().whenHasAuthority(UserAuthority.PLATFORM_ADMIN, () -> {
            // 平台管理员可以看到所有数据集
            datasets.addAll(datasetService.getAllDatasets());
        }).whenHasAuthority(UserAuthority.INSTITUTION_SUPERVISOR, () -> {
            // 机构管理员可以看到本机构所有数据集
            UUID institutionId = getCurrentUserInstitutionId();
            if (institutionId != null) {
                datasets.addAll(datasetService.getDatasetsByInstitutionId(institutionId));
            }
        }).whenHasAuthority(UserAuthority.DATASET_UPLOADER, () -> {
            // 数据集上传员只能看到自己上传的数据集
            datasets.addAll(datasetService.getDatasetsByProviderId(getCurrentUserId()));
        }).execute();

        return ResponseEntity.ok(ApiResponseDto.success(datasets, "获取数据集列表成功"));
    }

    /**
     * 根据ID获取特定管理的数据集
     * 平台管理员和机构管理员可访问所有数据集，数据集上传员只能看到自己上传数据集
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'DATASET_UPLOADER')")
    public ResponseEntity<ApiResponseDto<Dataset>> getDatasetById(@PathVariable UUID id) {
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

        return ResponseEntity.ok(ApiResponseDto.success(dataset, "获取数据集成功"));
    }

    /**
     * 创建新的数据集
     * 平台管理员、机构管理员和数据集上传员可创建数据集
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'DATASET_UPLOADER')")
    public ResponseEntity<ApiResponseDto<Dataset>> createDataset(@RequestBody DatasetBaseDto datasetDto) {
        Dataset dataset = new Dataset();
        // 将 DTO 转换为实体
        dataset.setTitleCn(datasetDto.getTitleCn());
        dataset.setDescription(datasetDto.getDescription());
        dataset.setType(datasetDto.getType());
        dataset.setCategory(datasetDto.getCategory());
        dataset.setSupervisorId(datasetDto.getSupervisorId());
        dataset.setStartDate(datasetDto.getStartDate());
        dataset.setEndDate(datasetDto.getEndDate());
        dataset.setRecordCount(datasetDto.getRecordCount());
        dataset.setVariableCount(datasetDto.getVariableCount());
        dataset.setKeywords(datasetDto.getKeywords());
        dataset.setSubjectAreaId(datasetDto.getSubjectAreaId());
        dataset.setFileRecordId(datasetDto.getFileRecordId());
        dataset.setDataDictRecordId(datasetDto.getDataDictRecordId());
        dataset.setPublished(datasetDto.getPublished());
        dataset.setShareAllData(datasetDto.getShareAllData());
        dataset.setDatasetLeader(datasetDto.getDatasetLeader());
        dataset.setDataCollectionUnit(datasetDto.getDataCollectionUnit());
        dataset.setContactPerson(datasetDto.getContactPerson());
        dataset.setContactInfo(datasetDto.getContactInfo());
        dataset.setDemographicFields(datasetDto.getDemographicFields());
        dataset.setOutcomeFields(datasetDto.getOutcomeFields());
        dataset.setTermsAgreementRecordId(datasetDto.getTermsAgreementRecordId());
        dataset.setSamplingMethod(datasetDto.getSamplingMethod());
        dataset.setVersionNumber(datasetDto.getVersionNumber());
        dataset.setFirstPublishedDate(datasetDto.getFirstPublishedDate());
        dataset.setCurrentVersionDate(datasetDto.getCurrentVersionDate());
        dataset.setParentDatasetId(datasetDto.getParentDatasetId());
        dataset.setPrincipalInvestigator(datasetDto.getPrincipalInvestigator());
        dataset.setApplicationInstitutionIds(datasetDto.getApplicationInstitutionIds());

        // 设置创建者和所属机构
        dataset.setProviderId(getCurrentUserId());
        dataset.setInstitutionId(getCurrentUserInstitutionId());
        // 默认为待审核状态
        dataset.setApproved(null);
        dataset.setSupervisorId(null);

        Dataset createdDataset = datasetService.createDataset(dataset);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(createdDataset, "创建数据集成功"));
    }

    /**
     * 更新现有数据集
     * 平台管理员可更新任意数据集，机构管理员和数据集上传员只能更新自己机构的数据集
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'DATASET_UPLOADER')")
    public ResponseEntity<ApiResponseDto<Dataset>> updateDataset(@PathVariable UUID id, @RequestBody DatasetBaseDto datasetDto) {
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
        // 修改过的数据集状态改为待审核
        dataset.setApproved(null);
        dataset.setSupervisorId(null);

        // 更新字段
        dataset.setTitleCn(datasetDto.getTitleCn());
        dataset.setDescription(datasetDto.getDescription());
        dataset.setType(datasetDto.getType());
        dataset.setCategory(datasetDto.getCategory());
        dataset.setProviderId(datasetDto.getProviderId());
        dataset.setStartDate(datasetDto.getStartDate());
        dataset.setEndDate(datasetDto.getEndDate());
        dataset.setRecordCount(datasetDto.getRecordCount());
        dataset.setVariableCount(datasetDto.getVariableCount());
        dataset.setKeywords(datasetDto.getKeywords());
        dataset.setSubjectAreaId(datasetDto.getSubjectAreaId());
        dataset.setFileRecordId(datasetDto.getFileRecordId());
        dataset.setDataDictRecordId(datasetDto.getDataDictRecordId());
        dataset.setPublished(datasetDto.getPublished());
        dataset.setShareAllData(datasetDto.getShareAllData());
        dataset.setDatasetLeader(datasetDto.getDatasetLeader());
        dataset.setDataCollectionUnit(datasetDto.getDataCollectionUnit());
        dataset.setContactPerson(datasetDto.getContactPerson());
        dataset.setContactInfo(datasetDto.getContactInfo());
        dataset.setDemographicFields(datasetDto.getDemographicFields());
        dataset.setOutcomeFields(datasetDto.getOutcomeFields());
        dataset.setTermsAgreementRecordId(datasetDto.getTermsAgreementRecordId());
        dataset.setSamplingMethod(datasetDto.getSamplingMethod());
        dataset.setVersionNumber(datasetDto.getVersionNumber());
        dataset.setFirstPublishedDate(datasetDto.getFirstPublishedDate());
        dataset.setCurrentVersionDate(datasetDto.getCurrentVersionDate());
        dataset.setParentDatasetId(datasetDto.getParentDatasetId());
        dataset.setPrincipalInvestigator(datasetDto.getPrincipalInvestigator());
        dataset.setApplicationInstitutionIds(datasetDto.getApplicationInstitutionIds());

        Dataset updatedDataset = datasetService.updateDataset(id, dataset);
        return ResponseEntity.ok(ApiResponseDto.success(updatedDataset, "更新数据集成功"));
    }

    /**
     * 修改数据集审核状态（通过、驳回）
     * 平台管理员、机构管理员和数据集审核员可修改任意数据集的审核状态
     */
    @PutMapping("/{id}/approval")
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'DATASET_APPROVER')")
    public ResponseEntity<ApiResponseDto<Dataset>> updateDatasetApprovalStatus(
            @PathVariable UUID id,
            @RequestBody DatasetApprovalRequestDto approvalRequest,
            HttpServletRequest request) {

        Dataset dataset = datasetService.getDatasetById(id);

        if (dataset == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的数据集"));
        }

        // 检查权限
        if (!hasPermissionToApproveDataset(dataset)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error("无权修改该数据集的审核状态"));
        }

        // 更新审核状态
        Dataset updatedDataset = datasetService.updateDatasetApprovalStatus(
                id,
                approvalRequest.getApproved(),
                getCurrentUserId());

        if (updatedDataset == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("更新数据集审核状态失败"));
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

            auditLogService.logApprovalAction(action, id, dataset.getTitleCn(), additionalParams, request.getRemoteAddr());
        } catch (Exception e) {
            // 日志记录失败不应该影响主要业务流程
            log.error("记录审计日志失败", e);
        }

        String message = approvalRequest.getApproved() == null ?
                "数据集审核状态重置" :
                approvalRequest.getApproved() ? "数据集审核通过" : "数据集审核驳回";
        return ResponseEntity.ok(ApiResponseDto.success(updatedDataset, message));
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

    /**
     * 获取当前认证用户的ID
     *
     * @return 用户ID
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}