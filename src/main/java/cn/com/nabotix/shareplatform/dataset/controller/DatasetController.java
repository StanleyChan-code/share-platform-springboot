package cn.com.nabotix.shareplatform.dataset.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.dataset.dto.PublicDatasetDto;
import cn.com.nabotix.shareplatform.dataset.service.DatasetService;
import cn.com.nabotix.shareplatform.dataset.entity.DatasetVersion;
import cn.com.nabotix.shareplatform.dataset.service.DatasetVersionService;
import cn.com.nabotix.shareplatform.security.AuthorityUtil;
import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@RestController
@RequestMapping("/api/datasets")
public class DatasetController {

    private final DatasetService datasetService;
    private final DatasetVersionService datasetVersionService;

    @Autowired
    public DatasetController(DatasetService datasetService, DatasetVersionService datasetVersionService) {
        this.datasetService = datasetService;
        this.datasetVersionService = datasetVersionService;
    }

    /**
     * 获取所有公开数据集列表（分页）
     * 匿名用户：只能看到已批准且已发布的数据集
     * 已登录用户：能看到已批准且已发布的数据集 + 已批准但未公开的用户所属机构能够申请的数据集
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<PublicDatasetDto>>> getAllPublicDatasets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        // 检查用户身份
        User authenticationUser = AuthorityUtil.getCurrentUser();

        Page<PublicDatasetDto> datasets;

        // 创建排序对象
        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // 如果用户已认证，进一步判断权限
        if (authenticationUser != null && authenticationUser.getInstitutionId() != null) {
            // 已经登录的用户能够看到已审核的公开数据集和未公开且允许申请的数据集
            datasets = datasetService.getAllInstitutionVisibleTopLevelDatasets(authenticationUser.getInstitutionId(), pageable);
        } else {
            // 匿名用户只能查看审核且公开的数据集
            datasets = datasetService.getAllPublicTopLevelDatasets(pageable);
        }

        return ResponseEntity.ok(ApiResponseDto.success(datasets, "获取公开数据集列表成功"));
    }

    /**
     * 获取所有公开的顶层数据集列表（parentDatasetId为空，时间轴视图）
     * 匿名用户：只能看到已批准且已发布的数据集
     * 已登录用户：能看到已批准且已发布的数据集 + 已批准但未公开的用户所属机构能够申请的数据集
     */
    @GetMapping("/timeline")
    public ResponseEntity<ApiResponseDto<Page<PublicDatasetDto>>> getTimelinePublicDatasets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 检查用户身份
        User authenticationUser = AuthorityUtil.getCurrentUser();
        Page<PublicDatasetDto> datasets;

        // 创建分页对象（时间轴按 startDate 排序）
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").ascending());

        // 如果用户已认证，进一步判断权限
        if (authenticationUser != null && authenticationUser.getInstitutionId() != null) {
            // 用户属于某个机构，可以查看完全公开的时间轴数据集 + 本机构已批准但未公开的时间轴数据集
            datasets = datasetService.getTimelineInstitutionVisibleDatasets(authenticationUser.getInstitutionId(), pageable);
        } else {
            // 匿名用户只能查看完全公开的时间轴数据集
            datasets = datasetService.getTimelinePublicDatasets(pageable);
        }

        return ResponseEntity.ok(ApiResponseDto.success(datasets, "获取时间轴式公开数据集列表成功"));
    }

    /**
     * 根据ID获取特定公开数据集
     * 匿名用户：只能访问已批准且已发布的数据集
     * 已登录用户：能看到已批准且已发布的数据集 + 已批准但未公开的用户所属机构能够申请的数据集
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<PublicDatasetDto>> getPublicDatasetById(@PathVariable UUID id) {
        // 检查用户身份
        User authenticationUser = AuthorityUtil.getCurrentUser();
        UUID userInstitutionId = authenticationUser != null ? authenticationUser.getInstitutionId() : null;

        PublicDatasetDto dataset = datasetService.getDatasetByIdAndUserInstitution(id, userInstitutionId);
        if (dataset != null) {
            return ResponseEntity.ok(ApiResponseDto.success(dataset, "获取公开数据集成功"));
        } else {
            return ResponseEntity.status(404)
                    .body(ApiResponseDto.error("未找到指定的公开数据集"));
        }
    }


    /**
     * 获取特定数据集的时间轴视图（包含该数据集及其子数据集）
     * 匿名用户：只能访问已批准且已发布的数据集
     * 已登录用户：能看到已批准且已发布的数据集 + 已批准但未公开的用户所属机构能够申请的数据集
     */
    @GetMapping("/{id}/timeline")
    public ResponseEntity<ApiResponseDto<PublicDatasetDto>> getDatasetTimelineById(@PathVariable UUID id) {
        // 检查用户身份
        User authenticationUser = AuthorityUtil.getCurrentUser();
        UUID userInstitutionId = authenticationUser != null ? authenticationUser.getInstitutionId() : null;

        PublicDatasetDto dataset = datasetService.getDatasetByIdAndUserInstitution(id, userInstitutionId, true);

        if (dataset != null) {
            return ResponseEntity.ok(ApiResponseDto.success(dataset, "获取数据集时间轴视图成功"));
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
        PublicDatasetDto dataset = datasetService.getDatasetByIdAndUserInstitution(id, userInstitutionId);
        if (dataset == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponseDto.error("未找到指定的数据集"));
        }

        List<DatasetVersion> versions = datasetVersionService.getDatasetVersionsByDatasetId(id);
        return ResponseEntity.ok(ApiResponseDto.success(versions, "获取数据集版本信息成功"));
    }
}