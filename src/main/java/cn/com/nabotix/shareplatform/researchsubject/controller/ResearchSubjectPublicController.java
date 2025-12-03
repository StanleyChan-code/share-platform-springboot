package cn.com.nabotix.shareplatform.researchsubject.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.researchsubject.dto.ResearchSubjectDto;
import cn.com.nabotix.shareplatform.researchsubject.service.ResearchSubjectService;
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
@RestController
@RequestMapping("/api/research-subjects")
public class ResearchSubjectPublicController {

    private final ResearchSubjectService researchSubjectService;

    public ResearchSubjectPublicController(ResearchSubjectService researchSubjectService) {
        this.researchSubjectService = researchSubjectService;
    }

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
     * 根据ID获取特定激活的研究学科
     * 所有用户均可访问
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<ResearchSubjectDto>> getActiveResearchSubjectById(@PathVariable UUID id) {
        ResearchSubjectDto subject = researchSubjectService.getResearchSubjectById(id);
        if (subject != null) {
            // 检查学科是否激活
            if (!Boolean.TRUE.equals(subject.getActive())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.error("未找到指定的研究学科"));
            }
            return ResponseEntity.ok(ApiResponseDto.success(subject, "获取研究学科成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的研究学科"));
        }
    }
}