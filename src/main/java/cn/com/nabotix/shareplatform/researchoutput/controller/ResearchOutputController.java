package cn.com.nabotix.shareplatform.researchoutput.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.researchoutput.entity.ResearchOutput;
import cn.com.nabotix.shareplatform.researchoutput.service.ResearchOutputService;
import cn.com.nabotix.shareplatform.researchoutput.dto.ResearchOutputCreateRequestDto;
import cn.com.nabotix.shareplatform.researchoutput.dto.ResearchOutputDto;
import cn.com.nabotix.shareplatform.security.AuthorityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 研究成果公共访问控制器
 * 提供研究成果的提交和用户查询自己提交记录的接口
 *
 * @author 陈雍文
 */
@Slf4j
@RestController
@RequestMapping("/api/research-outputs")
public class ResearchOutputController {

    private final ResearchOutputService researchOutputService;

    @Autowired
    public ResearchOutputController(ResearchOutputService researchOutputService) {
        this.researchOutputService = researchOutputService;
    }

    /**
     * 用户提交新的研究成果
     * 已登录用户可以提交研究成果
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<ResearchOutputDto>> createResearchOutput(@RequestBody ResearchOutputCreateRequestDto outputDto) {
        UUID userId = AuthorityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.error("用户未登录"));
        }

        ResearchOutput output = new ResearchOutput();
        // 将 DTO 转换为实体
        output.setDatasetId(outputDto.getDatasetId());
        output.setType(outputDto.getType());
        output.setOtherType(outputDto.getOtherType());
        output.setTitle(outputDto.getTitle());
        output.setAbstractText(outputDto.getAbstractText());
        output.setOutputNumber(outputDto.getOutputNumber());
        output.setCitationCount(outputDto.getCitationCount());
        output.setPublicationUrl(outputDto.getPublicationUrl());
        output.setFileId(outputDto.getFileId());
        output.setOtherInfo(outputDto.getOtherInfo());

        // 设置提交者
        output.setSubmitterId(userId);

        ResearchOutput createdOutput = researchOutputService.createResearchOutput(output);

        ResearchOutputDto dto = researchOutputService.convertToDto(createdOutput);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(dto, "提交研究成果成功"));
    }

    /**
     * 用户查询自己提交的研究成果列表
     * 已登录用户可以查看自己提交的所有研究成果
     */
    @GetMapping("/my-submissions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<Page<ResearchOutputDto>>> getMyResearchOutputs(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // 获取当前用户ID
        UUID currentUserId = AuthorityUtil.getCurrentUserId();

        Page<ResearchOutput> outputsPage;
        if (status == null || "all".equals(status)) {
            // 获取用户提交的所有研究成果
            outputsPage = researchOutputService.getResearchOutputsBySubmitterId(currentUserId, pageable);
        } else {
            Boolean approved;
            switch (status) {
                case "pending":
                    approved = null;
                    break;
                case "processed":
                    approved = true;
                    break;
                case "denied":
                    approved = false;
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponseDto.error("无效的参数"));
            }

            outputsPage = researchOutputService.getResearchOutputsBySubmitterIdWithApprovedStatus(currentUserId, approved, pageable);
        }

        // 转换为 DTO
        List<ResearchOutputDto> dtos = outputsPage.getContent().stream()
                .map(researchOutputService::convertToDto)
                .collect(Collectors.toList());

        Page<ResearchOutputDto> dtosPage = new PageImpl<>(dtos, pageable, outputsPage.getTotalElements());

        return ResponseEntity.ok(ApiResponseDto.success(dtosPage, "获取我的研究成果列表成功"));
    }

    /**
     * 用户查询自己提交的特定研究成果
     * 已登录用户可以查看自己提交的特定研究成果
     */
    @GetMapping("/my-submissions/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<ResearchOutputDto>> getMyResearchOutputById(@PathVariable UUID id) {
        ResearchOutput output = researchOutputService.getResearchOutputById(id);

        if (output == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的研究成果"));
        }

        // 检查是否是当前用户提交的
        if (!output.getSubmitterId().equals(AuthorityUtil.getCurrentUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error("无权访问该研究成果"));
        }

        ResearchOutputDto dto = researchOutputService.convertToDto(output);

        return ResponseEntity.ok(ApiResponseDto.success(dto, "获取研究成果成功"));
    }
}