package cn.com.nabotix.shareplatform.dataset.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.dataset.dto.PublicDatasetDto;
import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import cn.com.nabotix.shareplatform.dataset.service.ApplicationService;
import cn.com.nabotix.shareplatform.dataset.service.DatasetService;
import cn.com.nabotix.shareplatform.dataset.entity.DatasetVersion;
import cn.com.nabotix.shareplatform.dataset.service.DatasetVersionService;
import cn.com.nabotix.shareplatform.dataset.service.ManageDatasetService;
import cn.com.nabotix.shareplatform.filemanagement.dto.FileDownloadDto;
import cn.com.nabotix.shareplatform.filemanagement.service.FileManagementService;
import cn.com.nabotix.shareplatform.security.AuthorityUtil;
import cn.com.nabotix.shareplatform.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 数据集公共控制器
 * 提供数据集的公开查询接口
 *
 * @author 陈雍文
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/datasets")
public class DatasetController {

    private final DatasetService datasetService;
    private final DatasetVersionService datasetVersionService;
    private final FileManagementService fileManagementService;
    private final ApplicationService applicationService;
    private final ManageDatasetService manageDatasetService;

    /**
     * 获取所有公开数据集列表（分页），不含随访数据集的信息
     * 匿名用户：只能看到已批准且已发布的数据集
     * 已登录用户：能看到已批准且已发布的数据集 + 已批准但未公开的用户所属机构能够申请的数据集
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<PublicDatasetDto>>> getAllPublicDatasets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        // 创建排序对象
        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PublicDatasetDto> datasets = datasetService.getAllPublicTopLevelDatasets(
                AuthorityUtil.getCurrentUserInstitutionId(),
                pageable
        );
        return ResponseEntity.ok(ApiResponseDto.success(datasets, "获取公开数据集列表成功"));
    }

    /**
     * 获取指定id数据集的所有随访数据集，（parentDatasetId = id，时间轴视图）
     * 匿名用户：只能看到已批准且已发布的数据集
     * 已登录用户：能看到已批准且已发布的数据集 + 已批准但未公开的用户所属机构能够申请的数据集
     */
    @GetMapping("/{id}/timeline")
    public ResponseEntity<ApiResponseDto<List<PublicDatasetDto>>> getTimelinePublicDatasets(@PathVariable UUID id) {
        // 检查用户身份
        Dataset parentDataset = manageDatasetService.getDatasetById(id);
        UUID currentUserInstitutionId = AuthorityUtil.getCurrentUserInstitutionId();
        boolean assessPermission = datasetService.checkPublicDatasetAssessPermission(parentDataset, currentUserInstitutionId);
        if (!assessPermission) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponseDto.error("无权限访问"));
        }

        List<PublicDatasetDto> publicDatasetDtos = datasetService.getAllDatasetsByParentDatasetId(id, currentUserInstitutionId);
        return ResponseEntity.ok(ApiResponseDto.success(publicDatasetDtos, "获取时间轴式公开数据集列表成功"));
    }

    /**
     * 根据ID获取特定公开数据集
     * 匿名用户：只能访问已批准且已发布的数据集
     * 已登录用户：能看到已批准且已发布的数据集 + 已批准但未公开的用户所属机构能够申请的数据集
     *
     * @param id           数据集ID
     * @param loadTimeline 是否加载随访信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<PublicDatasetDto>> getPublicDatasetById(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean loadTimeline) {
        PublicDatasetDto dataset = datasetService.getDatasetByIdAndUserInstitution(
                id, AuthorityUtil.getCurrentUserInstitutionId(), loadTimeline
        );
        if (dataset != null) {
            return ResponseEntity.ok(ApiResponseDto.success(dataset, "获取公开数据集成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的数据集"));
        }
    }

    /**
     * 根据数据集ID获取所有版本信息
     */
    @GetMapping("/{id}/versions")
    public ResponseEntity<ApiResponseDto<List<DatasetVersion>>> getDatasetVersions(@PathVariable UUID id) {
        User authenticationUser = AuthorityUtil.getCurrentUser();
        UUID userInstitutionId = authenticationUser != null ? authenticationUser.getInstitutionId() : null;

        // 检查数据集是否存在
        PublicDatasetDto dataset = datasetService.getDatasetByIdAndUserInstitution(id, userInstitutionId, false);
        if (dataset == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponseDto.error("未找到指定的数据集"));
        }

        List<DatasetVersion> versions = datasetVersionService.getDatasetVersionsByDatasetId(id);
        return ResponseEntity.ok(ApiResponseDto.success(versions, "获取数据集版本信息成功"));
    }

    /**
     * 下载数据集版本的数据字典文件
     * 需要用户登录才能下载
     */
    @GetMapping("/{datasetId}/versions/{versionId}/data-dictionary")
    public ResponseEntity<Resource> downloadDataDictionary(
            @PathVariable UUID datasetId,
            @PathVariable UUID versionId) {
        // 检查用户是否登录
        User currentUser = AuthorityUtil.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 检查数据集版本是否存在
        DatasetVersion datasetVersion = datasetVersionService.getById(versionId);
        if (datasetVersion == null || !datasetVersion.getDatasetId().equals(datasetId)) {
            return ResponseEntity.notFound().build();
        }

        // 检查数据字典文件是否存在
        if (datasetVersion.getDataDictRecordId() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            // 获取文件资源
            FileDownloadDto fileDownloadDto = fileManagementService.downloadFile(datasetVersion.getDataDictRecordId());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileDownloadDto.getFileName() + "\"")
                    .body(fileDownloadDto.getFile());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 下载数据集版本的使用协议文件
     * 需要用户登录才能下载
     */
    @GetMapping("/{datasetId}/versions/{versionId}/terms-agreement")
    public ResponseEntity<Resource> downloadTermsAgreement(
            @PathVariable UUID datasetId,
            @PathVariable UUID versionId) {
        // 检查用户是否登录
        User currentUser = AuthorityUtil.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 检查数据集版本是否存在
        DatasetVersion datasetVersion = datasetVersionService.getById(versionId);
        if (datasetVersion == null || !datasetVersion.getDatasetId().equals(datasetId)) {
            return ResponseEntity.notFound().build();
        }

        // 检查条款协议文件是否存在
        if (datasetVersion.getTermsAgreementRecordId() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            // 获取文件资源
            FileDownloadDto fileDownloadDto = fileManagementService.downloadFile(datasetVersion.getTermsAgreementRecordId());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileDownloadDto.getFileName() + "\"")
                    .body(fileDownloadDto.getFile());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 下载数据集版本的数据分享文件
     * 需要用户登录并且有相应的申请审批通过记录，或者自己是提供者
     */
    @GetMapping("/{datasetId}/versions/{versionId}/data-sharing")
    public ResponseEntity<Resource> downloadDataSharing(
            @PathVariable UUID datasetId,
            @PathVariable UUID versionId) {
        // 检查用户是否登录
        User currentUser = AuthorityUtil.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 检查数据集版本是否存在
        DatasetVersion datasetVersion = datasetVersionService.getById(versionId);
        if (datasetVersion == null || !datasetVersion.getDatasetId().equals(datasetId)) {
            return ResponseEntity.notFound().build();
        }

        // 如果检查数据集不是自己就要检查是否有申请
        if (!manageDatasetService.getDatasetById(datasetId).getProviderId().equals(currentUser.getId())) {
            // 检查用户是否有对该数据集版本的访问权限（需要有审批通过的申请记录）
            if (!applicationService.checkUserAccessToDatasetVersion(versionId, currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        // 检查数据分享文件是否存在
        if (datasetVersion.getDataSharingRecordId() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            // 获取文件资源
            FileDownloadDto fileDownloadDto = fileManagementService.downloadFile(datasetVersion.getDataSharingRecordId());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileDownloadDto.getFileName() + "\"")
                    .body(fileDownloadDto.getFile());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}