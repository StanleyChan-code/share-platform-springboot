package cn.com.nabotix.shareplatform.dataset.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.dataset.dto.PublicDatasetDto;
import cn.com.nabotix.shareplatform.dataset.service.DatasetService;
import cn.com.nabotix.shareplatform.security.UserDetailsImpl;
import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 数据集控制器
 * 提供公开数据集的查询接口
 * 支持匿名用户和认证用户的不同访问权限
 *
 * @author 陈雍文
 */
@RestController
@RequestMapping("/api/datasets")
public class DatasetController {

    private final DatasetService datasetService;
    private final UserService userService;

    @Autowired
    public DatasetController(DatasetService datasetService, UserService userService) {
        this.datasetService = datasetService;
        this.userService = userService;
    }

    /**
     * 获取所有公开的顶层数据集列表（parentDatasetId为空）
     * 匿名用户：只能看到已批准且已发布的顶层数据集
     * 机构内用户：能看到已批准且已发布的顶层数据集 + 已批准但未公开的本机构顶层数据集
     */
    @GetMapping()
    public ResponseEntity<ApiResponseDto<Page<PublicDatasetDto>>> getAllPublicDatasets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        // 检查用户身份
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Page<PublicDatasetDto> datasets;
        
        // 创建分页和排序对象
        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // 如果用户已认证，进一步判断权限
        if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userService.getUserByUserId(userDetails.getId());
            
            if (user != null && user.getInstitutionId() != null) {
                // 用户属于某个机构，可以查看完全公开的顶层数据集 + 本机构已批准但未公开的顶层数据集
                datasets = datasetService.getInstitutionVisibleTopLevelDatasets(user.getInstitutionId(), pageable);
            } else {
                // 用户不属于任何机构，只能查看完全公开的顶层数据集
                datasets = datasetService.getAllPublicTopLevelDatasets(pageable);
            }
        } else {
            // 匿名用户只能查看完全公开的顶层数据集
            datasets = datasetService.getAllPublicTopLevelDatasets(pageable);
        }
        
        return ResponseEntity.ok(ApiResponseDto.success(datasets, "获取公开顶层数据集列表成功"));
    }

    /**
     * 获取时间轴式公开数据集列表（仅包含没有父数据集的数据集，并附带其子数据集）
     * 匿名用户：只能看到已批准且已发布的数据集
     * 机构内用户：能看到已批准且已发布的数据集 + 已批准但未公开的本机构数据集
     */
    @GetMapping("/timeline")
    public ResponseEntity<ApiResponseDto<Page<PublicDatasetDto>>> getTimelinePublicDatasets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 检查用户身份
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Page<PublicDatasetDto> datasets;
        
        // 创建分页对象（时间轴按 startDate 排序）
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").ascending());
        
        // 如果用户已认证，进一步判断权限
        if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userService.getUserByUserId(userDetails.getId());
            
            if (user != null && user.getInstitutionId() != null) {
                // 用户属于某个机构，可以查看完全公开的时间轴数据集 + 本机构已批准但未公开的时间轴数据集
                datasets = datasetService.getTimelineInstitutionVisibleDatasets(user.getInstitutionId(), pageable);
            } else {
                // 用户不属于任何机构，只能查看完全公开的时间轴数据集
                datasets = datasetService.getTimelinePublicDatasets(pageable);
            }
        } else {
            // 匿名用户只能查看完全公开的时间轴数据集
            datasets = datasetService.getTimelinePublicDatasets(pageable);
        }
        
        return ResponseEntity.ok(ApiResponseDto.success(datasets, "获取时间轴式公开数据集列表成功"));
    }

    /**
     * 根据ID获取特定公开数据集
     * 匿名用户：只能访问已批准且已发布的数据集
     * 机构内用户：能访问已批准且已发布的数据集 + 已批准但未发布的本机构数据集
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<PublicDatasetDto>> getPublicDatasetById(@PathVariable UUID id) {
        // 检查用户身份
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PublicDatasetDto dataset;
        
        // 如果用户已认证，进一步判断权限
        if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userService.getUserByUserId(userDetails.getId());
            
            if (user != null && user.getInstitutionId() != null) {
                // 用户属于某个机构，可以访问完全公开的数据集 + 本机构已批准但未公开的数据集
                dataset = datasetService.getDatasetByIdAndUserInstitution(id, user.getInstitutionId());
            } else {
                // 用户不属于任何机构，只能访问完全公开的数据集
                dataset = datasetService.getPublicDatasetById(id);
            }
        } else {
            // 匿名用户只能访问完全公开的数据集
            dataset = datasetService.getPublicDatasetById(id);
        }
        
        if (dataset != null) {
            return ResponseEntity.ok(ApiResponseDto.success(dataset, "获取公开数据集成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的公开数据集"));
        }
    }
}