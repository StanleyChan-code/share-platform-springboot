package cn.com.nabotix.shareplatform.researchoutput.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.researchoutput.entity.ResearchOutput;
import cn.com.nabotix.shareplatform.researchoutput.service.ResearchOutputService;
import cn.com.nabotix.shareplatform.researchoutput.dto.ResearchOutputApprovalRequestDto;
import cn.com.nabotix.shareplatform.researchoutput.dto.ResearchOutputDto;
import cn.com.nabotix.shareplatform.security.UserAuthority;
import cn.com.nabotix.shareplatform.security.AuthorityUtil;
import cn.com.nabotix.shareplatform.security.UserDetailsImpl;
import cn.com.nabotix.shareplatform.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 研究成果管理控制器
 * 提供研究成果的管理接口，包括审核等操作
 *
 * @author 陈雍文
 */
@Slf4j
@RestController
@RequestMapping("/api/manage/research-outputs")
public class ResearchOutputManageController {

    private final ResearchOutputService researchOutputService;
    private final UserService userService;

    @Autowired
    public ResearchOutputManageController(ResearchOutputService researchOutputService, UserService userService) {
        this.researchOutputService = researchOutputService;
        this.userService = userService;
    }

    /**
     * 获取所有管理的研究成果列表
     * 平台管理员可以查看所有研究成果
     * 机构管理员和研究成果审核员可以查看本机构成员提交的研究成果
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'RESEARCH_OUTPUT_APPROVER')")
    public ResponseEntity<ApiResponseDto<Page<ResearchOutputDto>>> getAllResearchOutputs(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        List<Page<ResearchOutput>> outputsList = new ArrayList<>();
        boolean hasPermission;
        if (status == null || "all".equals(status)) {
            // 查询全部成果
            hasPermission = AuthorityUtil.checkBuilder()
                    .whenHasAuthority(() -> {
                        // 获取所有成果
                        outputsList.add(researchOutputService.getAllResearchOutputs(pageable));
                    }, UserAuthority.PLATFORM_ADMIN)
                    .whenHasAuthority(() -> {
                        // 获取本机构所有成果
                        UUID institutionId = getCurrentUserInstitutionId();
                        if (institutionId != null) {
                            outputsList.add(researchOutputService.getResearchOutputsByInstitutionId(institutionId, pageable));
                        }
                    }, UserAuthority.INSTITUTION_SUPERVISOR, UserAuthority.RESEARCH_OUTPUT_APPROVER)
                    .execute();

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

            hasPermission = AuthorityUtil.checkBuilder()
                    .whenHasAuthority(() -> {
                        // 平台管理员可以看到所有研究成果
                        outputsList.add(researchOutputService.getAllResearchOutputs(approved, pageable));
                    }, UserAuthority.PLATFORM_ADMIN)
                    .whenHasAuthority(() -> {
                        // 机构管理员和成果审核员可以看到本机构所有研究成果
                        UUID institutionId = getCurrentUserInstitutionId();
                        if (institutionId != null) {
                            outputsList.add(researchOutputService.getResearchOutputsByInstitutionId(institutionId, approved, pageable));
                        }
                    }, UserAuthority.INSTITUTION_SUPERVISOR, UserAuthority.RESEARCH_OUTPUT_APPROVER)
                    .execute();
        }

        if (!hasPermission || outputsList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error("无权访问研究成果列表"));
        }
        Page<ResearchOutput> outputs = outputsList.getFirst();

        // 转换为 DTO
        List<ResearchOutputDto> dtos = outputs.getContent().stream()
                .map(researchOutputService::convertToDto)
                .toList();
        Page<ResearchOutputDto> dtosPage = new PageImpl<>(dtos, pageable, outputs.getTotalElements());

        return ResponseEntity.ok(ApiResponseDto.success(dtosPage, "获取研究成果列表成功"));
    }

    /**
     * 根据ID获取特定管理的研究成果
     * 平台管理员可以查看所有研究成果
     * 机构管理员和研究成果审核员可以查看本机构成员提交的研究成果
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'RESEARCH_OUTPUT_APPROVER')")
    public ResponseEntity<ApiResponseDto<ResearchOutputDto>> getResearchOutputById(@PathVariable UUID id) {
        ResearchOutput output = researchOutputService.getResearchOutputById(id);

        if (output == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的研究成果"));
        }

        // 检查权限
        if (!hasPermissionToManageResearchOutput(output)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error("无权访问该研究成果"));
        }

        ResearchOutputDto dto = researchOutputService.convertToDto(output);

        return ResponseEntity.ok(ApiResponseDto.success(dto, "获取研究成果成功"));
    }

    /**
     * 修改研究成果审核状态（通过、驳回）
     * 平台管理员、机构管理员和研究成果审核员可修改任意研究成果的审核状态
     */
    @PutMapping("/{id}/approval")
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'RESEARCH_OUTPUT_APPROVER')")
    public ResponseEntity<ApiResponseDto<ResearchOutputDto>> updateResearchOutputApprovalStatus(
            @PathVariable UUID id,
            @RequestBody ResearchOutputApprovalRequestDto approvalRequest) {

        ResearchOutput output = researchOutputService.getResearchOutputById(id);

        if (output == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的研究成果"));
        }

        // 检查权限
        if (!hasPermissionToApproveResearchOutput(output)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error("无权修改该研究成果的审核状态"));
        }

        try {
            ResearchOutput updatedOutput = researchOutputService.updateResearchOutputApprovalStatus(
                    output.getId(),
                    AuthorityUtil.getCurrentUserId(),
                    approvalRequest.getApproved(),
                    approvalRequest.getRejectionReason());

            if (updatedOutput == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseDto.error("更新研究成果审核状态失败"));
            }

            ResearchOutputDto dto = researchOutputService.convertToDto(updatedOutput);

            String message = approvalRequest.getApproved() == null ?
                    "研究成果审核状态重置" :
                    approvalRequest.getApproved() ? "研究成果审核通过" : "研究成果审核驳回";
            return ResponseEntity.ok(ApiResponseDto.success(dto, message));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error(e.getMessage()));
        }
    }

    /**
     * 检查用户是否有权限审核指定研究成果
     *
     * @param output 研究成果
     * @return 是否有权限
     */
    private boolean hasPermissionToApproveResearchOutput(ResearchOutput output) {
        return AuthorityUtil.checkBuilder()
                .withAllowedAuthorities(UserAuthority.INSTITUTION_SUPERVISOR, UserAuthority.RESEARCH_OUTPUT_APPROVER)
                .withTargetInstitutionId(researchOutputService.getInstitutionIdByResearchOutput(output))
                .execute();
    }

    /**
     * 检查用户是否有权限管理指定研究成果
     *
     * @param output 研究成果
     * @return 是否有权限
     */
    private boolean hasPermissionToManageResearchOutput(ResearchOutput output) {
        return AuthorityUtil.checkBuilder()
                .withAllowedAuthorities(UserAuthority.INSTITUTION_SUPERVISOR, UserAuthority.RESEARCH_OUTPUT_APPROVER)
                .withTargetInstitutionId(researchOutputService.getInstitutionIdByResearchOutput(output))
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