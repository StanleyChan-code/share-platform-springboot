package cn.com.nabotix.shareplatform.dataset.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.dataset.dto.ApplicationDto;
import cn.com.nabotix.shareplatform.dataset.dto.ApplicationReviewRequestDto;
import cn.com.nabotix.shareplatform.dataset.service.ApplicationService;
import cn.com.nabotix.shareplatform.dataset.entity.Application;
import cn.com.nabotix.shareplatform.dataset.entity.ApplicationStatus;
import cn.com.nabotix.shareplatform.security.UserDetailsImpl;
import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * 数据集申请管理控制器
 *
 * @author 陈雍文
 */
@RestController
@RequestMapping("/api/manage/applications")
public class ApplicationManageController {

    private final ApplicationService applicationService;
    private final UserService userService;

    @Autowired
    public ApplicationManageController(ApplicationService applicationService, UserService userService) {
        this.applicationService = applicationService;
        this.userService = userService;
    }

    /**
     * 申请审核员审核申请
     */
    @PutMapping("/{id}/approver-review")
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'DATASET_APPROVER')")
    public ResponseEntity<ApiResponseDto<ApplicationDto>> reviewByApprover(
            @PathVariable UUID id,
            @Valid @RequestBody ApplicationReviewRequestDto reviewRequest) {
        try {
            // 获取当前用户ID
            UUID reviewerId = getCurrentUserId();

            // 审核申请
            Application application = applicationService.reviewByApprover(
                    id, reviewerId, reviewRequest.getNotes(), reviewRequest.getApproved());

            // 转换为DTO并返回
            ApplicationDto resultDto = applicationService.getApplicationDtoById(application.getId());
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
            UUID institutionId = getCurrentUserInstitutionId();

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
            UUID institutionId = getCurrentUserInstitutionId();

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

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    private UUID getCurrentUserInstitutionId() {
        UUID userId = getCurrentUserId();
        if (userId == null) {
            return null;
        }
        User user = userService.getUserByUserId(userId);
        return user != null ? user.getInstitutionId() : null;
    }
}