package cn.com.nabotix.shareplatform.researchsubject.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.dataset.dto.PublicDatasetDto;
import cn.com.nabotix.shareplatform.dataset.service.DatasetService;
import cn.com.nabotix.shareplatform.researchsubject.dto.ResearchSubjectDto;
import cn.com.nabotix.shareplatform.researchsubject.service.ResearchSubjectService;
import cn.com.nabotix.shareplatform.security.AuthorityUtil;
import lombok.RequiredArgsConstructor;
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
 * 研究学科公共访问控制器
 * 提供研究学科信息的公开查询接口
 *
 * @author 陈雍文
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/research-subjects")
public class ResearchSubjectPublicController {

    private final ResearchSubjectService researchSubjectService;
    private final DatasetService datasetService;


    /**
     * 获取所有激活的研究学科列表
     * 所有用户均可访问
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ResearchSubjectDto>>> getAllActiveResearchSubjects() {
        List<ResearchSubjectDto> subjects = researchSubjectService.getActiveResearchSubjects();
        return ResponseEntity.ok(ApiResponseDto.success(subjects, "获取研究学科列表成功"));
    }

    /**
     * 根据ID获取特定研究学科（无论是否激活）
     * 所有用户均可访问
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<ResearchSubjectDto>> getResearchSubjectById(@PathVariable UUID id) {
        ResearchSubjectDto subject = researchSubjectService.getResearchSubjectById(id);
        if (subject != null) {
            return ResponseEntity.ok(ApiResponseDto.success(subject, "获取研究学科成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的研究学科"));
        }
    }
    
    /**
     * 根据研究学科ID分页获取相关的数据集列表
     * 所有用户均可访问
     *
     * @param subjectAreaId 研究学科ID
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向（asc/desc）
     * @return 数据集分页列表
     */
    @GetMapping("/{subjectAreaId}/datasets")
    public ResponseEntity<ApiResponseDto<Page<PublicDatasetDto>>> getDatasetsByResearchSubjectId(
            @PathVariable UUID subjectAreaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PublicDatasetDto> datasets = datasetService.getAllDatasetsBySubjectAreaId(
                subjectAreaId,
                AuthorityUtil.getCurrentUserInstitutionId(),
                pageable);
        return ResponseEntity.ok(ApiResponseDto.success(datasets, "获取数据集列表成功"));
    }
}