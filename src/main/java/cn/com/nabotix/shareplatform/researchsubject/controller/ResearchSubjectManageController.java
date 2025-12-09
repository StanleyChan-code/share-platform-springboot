package cn.com.nabotix.shareplatform.researchsubject.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.researchsubject.dto.ResearchSubjectCreateRequestDto;
import cn.com.nabotix.shareplatform.researchsubject.dto.ResearchSubjectDto;
import cn.com.nabotix.shareplatform.researchsubject.service.ResearchSubjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 研究学科管理控制器
 * 提供研究学科的管理接口，包括创建、更新、删除等操作
 * 仅平台管理员可访问
 *
 * @author 陈雍文
 */
@RestController
@RequestMapping("/api/manage/research-subjects")
@PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
public class ResearchSubjectManageController {

    private final ResearchSubjectService researchSubjectService;

    public ResearchSubjectManageController(ResearchSubjectService researchSubjectService) {
        this.researchSubjectService = researchSubjectService;
    }

    /**
     * 获取所有研究学科列表（包括激活和非激活的）
     * 仅平台管理员可访问
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ResearchSubjectDto>>> getAllResearchSubjects() {
        List<ResearchSubjectDto> subjects = researchSubjectService.getAllResearchSubjects();
        return ResponseEntity.ok(ApiResponseDto.success(subjects, "获取研究学科列表成功"));
    }

    /**
     * 根据ID获取特定研究学科（无论是否激活）
     * 仅平台管理员可访问
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
     * 创建新的研究学科
     * 仅平台管理员可访问
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<ResearchSubjectDto>> createResearchSubject(@RequestBody ResearchSubjectCreateRequestDto subjectDto) {
        ResearchSubjectDto createdSubject = researchSubjectService.createResearchSubject(subjectDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(createdSubject, "创建研究学科成功"));
    }

    /**
     * 更新现有研究学科
     * 仅平台管理员可访问
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<ResearchSubjectDto>> updateResearchSubject(@PathVariable UUID id, @RequestBody ResearchSubjectCreateRequestDto subjectDto) {
        ResearchSubjectDto updatedSubject = researchSubjectService.updateResearchSubject(id, subjectDto);
        if (updatedSubject != null) {
            return ResponseEntity.ok(ApiResponseDto.success(updatedSubject, "更新研究学科成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的研究学科"));
        }
    }

    /**
     * 删除研究学科
     * 仅平台管理员可访问
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteResearchSubject(@PathVariable UUID id) {
        boolean deleted = researchSubjectService.deleteResearchSubject(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponseDto.success(null, "删除研究学科成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的研究学科"));
        }
    }
}