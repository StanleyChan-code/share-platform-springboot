package cn.com.nabotix.shareplatform.dataset.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.dataset.dto.PublicDatasetDto;
import cn.com.nabotix.shareplatform.dataset.service.DatasetService;
import cn.com.nabotix.shareplatform.security.UserDetailsImpl;
import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
     * 获取所有公开数据集列表
     * 匿名用户：只能看到已批准且已发布的数据集
     * 机构内用户：能看到已批准且已发布的数据集 + 已批准但未发布的本机构数据集
     */
    @GetMapping()
    public ResponseEntity<ApiResponseDto<List<PublicDatasetDto>>> getAllPublicDatasets() {
        // 检查用户身份
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<PublicDatasetDto> datasets;
        
        // 如果用户已认证，进一步判断权限
        if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userService.getUserByUserId(userDetails.getId());
            
            if (user != null && user.getInstitutionId() != null) {
                // 用户属于某个机构，可以查看完全公开的数据集 + 本机构已批准但未公开的数据集
                datasets = datasetService.getInstitutionVisibleDatasets(user.getInstitutionId());
            } else {
                // 用户不属于任何机构，只能查看完全公开的数据集
                datasets = datasetService.getAllPublicDatasets();
            }
        } else {
            // 匿名用户只能查看完全公开的数据集
            datasets = datasetService.getAllPublicDatasets();
        }
        
        return ResponseEntity.ok(ApiResponseDto.success(datasets, "获取公开数据集列表成功"));
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
        PublicDatasetDto dataset = null;
        
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