package cn.com.nabotix.shareplatform.dataset.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.common.service.AuditLogService;
import cn.com.nabotix.shareplatform.dataset.dto.ApplicationDto;
import cn.com.nabotix.shareplatform.dataset.dto.ApplicationReviewRequestDto;
import cn.com.nabotix.shareplatform.dataset.service.ApplicationService;
import cn.com.nabotix.shareplatform.dataset.entity.Application;
import cn.com.nabotix.shareplatform.dataset.entity.ApplicationStatus;
import cn.com.nabotix.shareplatform.security.AuthorityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 数据集申请管理控制器
 *
 * @author 陈雍文
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/manage/applications")
public class ApplicationManageController {

    private final ApplicationService applicationService;
    private final AuditLogService auditLogService;

    /**
     * 申请审核员审核申请
     */
    @PutMapping("/{id}/approver-review")
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'DATASET_APPROVER')")
    public ResponseEntity<ApiResponseDto<ApplicationDto>> reviewByApprover(
            @PathVariable UUID id,
            @Valid @RequestBody ApplicationReviewRequestDto reviewRequest,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            UUID reviewerId = AuthorityUtil.getCurrentUserId();

            // 审核申请
            Application application = applicationService.reviewByApprover(
                    id, reviewerId, reviewRequest.getNotes(), reviewRequest.getApproved());

            // 转换为DTO并返回
            ApplicationDto resultDto = applicationService.getApplicationDtoById(application.getId());
            
            // 记录审核操作到审计日志
            String action = reviewRequest.getApproved() == null ?
                    "RESET_APPLICATION_APPROVAL_STATUS" :
                    reviewRequest.getApproved() ? "APPROVE_APPLICATION" : "REJECT_APPLICATION";
            
            Map<String, Object> additionalParams = new HashMap<>();
            if (reviewRequest.getNotes() != null) {
                additionalParams.put("reviewNotes", reviewRequest.getNotes());
            }
            
            auditLogService.logApprovalAction(action, application.getId(), 
                    "Application-" + application.getId(), additionalParams, request.getRemoteAddr());
            
            return ResponseEntity.ok(ApiResponseDto.success(resultDto, "审核完成"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("审核失败"));
        }
    }

    /**
     * 申请审核员查看待审核申请记录列表
     */
    @GetMapping("/pending-applications")
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'DATASET_APPROVER')")
    public ResponseEntity<ApiResponseDto<Page<ApplicationDto>>> getPendingApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            UUID institutionId = AuthorityUtil.getCurrentUserInstitutionId();

            // 创建分页和排序对象
            Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            // 查询待审核申请记录
            Page<ApplicationDto> applications = applicationService.getApplicationsByInstitutionIdAndStatus(
                    institutionId, ApplicationStatus.PENDING_INSTITUTION_REVIEW, pageable);
            return ResponseEntity.ok(ApiResponseDto.success(applications, "查询成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("查询失败"));
        }
    }

    /**
     * 申请审核员查看已处理申请记录列表
     */
    @GetMapping("/processed-applications")
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'DATASET_APPROVER')")
    public ResponseEntity<ApiResponseDto<Map<String, Page<ApplicationDto>>>> getProcessedApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            UUID institutionId = AuthorityUtil.getCurrentUserInstitutionId();

            // 创建分页和排序对象
            Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            // 查询已处理申请记录（已批准或已拒绝）
            Page<ApplicationDto> applications = applicationService.getApplicationsByInstitutionIdAndStatus(
                    institutionId, ApplicationStatus.APPROVED, pageable);

            Page<ApplicationDto> deniedApplications = applicationService.getApplicationsByInstitutionIdAndStatus(
                    institutionId, ApplicationStatus.DENIED, pageable);

            return ResponseEntity.ok(ApiResponseDto.success(
                    Map.of("approvedApplications", applications, "deniedApplications", deniedApplications),
                    "查询成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("查询失败"));
        }
    }


}