package cn.com.nabotix.shareplatform.dataset.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.dataset.dto.ApplicationCreateRequestDto;
import cn.com.nabotix.shareplatform.dataset.dto.ApplicationDto;
import cn.com.nabotix.shareplatform.dataset.dto.ApplicationReviewRequestDto;
import cn.com.nabotix.shareplatform.dataset.service.ApplicationService;
import cn.com.nabotix.shareplatform.dataset.entity.Application;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 数据集申请控制器
 *
 * @author 陈雍文
 */
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserService userService;

    @Autowired
    public ApplicationController(ApplicationService applicationService, UserService userService) {
        this.applicationService = applicationService;
        this.userService = userService;
    }

    /**
     * 用户申请数据集
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<ApplicationDto>> applyForDataset(
            @Valid @RequestBody ApplicationCreateRequestDto requestDto) {
        try {
            // 获取当前用户ID
            UUID applicantId = getCurrentUserId();
            if (applicantId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("用户未登录"));
            }

            // 构造ApplicationDto
            ApplicationDto applicationDto = new ApplicationDto();
            applicationDto.setDatasetId(requestDto.getDatasetId());
            applicationDto.setApplicantRole(requestDto.getApplicantRole());
            applicationDto.setApplicantType(requestDto.getApplicantType());
            applicationDto.setProjectTitle(requestDto.getProjectTitle());
            applicationDto.setProjectDescription(requestDto.getProjectDescription());
            applicationDto.setFundingSource(requestDto.getFundingSource());
            applicationDto.setPurpose(requestDto.getPurpose());
            applicationDto.setProjectLeader(requestDto.getProjectLeader());
            applicationDto.setApprovalDocumentId(requestDto.getApprovalDocumentId());

            // 创建申请
            Application application = applicationService.createApplication(applicationDto, applicantId);

            // 转换为DTO并返回
            ApplicationDto resultDto = applicationService.getApplicationById(application.getId());
            return ResponseEntity.ok(ApiResponseDto.success(resultDto, "申请提交成功"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("申请提交失败"));
        }
    }

    /**
     * 数据集提供者审核申请，这里不加入方法级权限限制是为了避免上传者的当前权限修改过导致错误
     */
    @PutMapping("/{id}/provider-review")
    public ResponseEntity<ApiResponseDto<ApplicationDto>> reviewByProvider(
            @PathVariable UUID id,
            @Valid @RequestBody ApplicationReviewRequestDto reviewRequest) {
        try {
            // 获取当前用户ID
            UUID providerId = getCurrentUserId();
            if (providerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("用户未登录"));
            }

            // 审核申请
            Application application = applicationService.reviewByProvider(
                    id, providerId, reviewRequest.getNotes(), reviewRequest.getApproved());

            // 转换为DTO并返回
            ApplicationDto resultDto = applicationService.getApplicationById(application.getId());
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
     * 申请者查询自己的申请记录
     */
    @GetMapping("/my-applications")
    public ResponseEntity<ApiResponseDto<Page<ApplicationDto>>> getMyApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            // 获取当前用户ID
            UUID applicantId = getCurrentUserId();
            if (applicantId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("用户未登录"));
            }

            // 创建分页和排序对象
            Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            // 查询申请记录
            Page<ApplicationDto> applications = applicationService.getApplicationsByApplicantId(applicantId, pageable);
            return ResponseEntity.ok(ApiResponseDto.success(applications, "查询成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("查询失败"));
        }
    }

    /**
     * 数据集提供者查看申请记录列表
     */
    @GetMapping("/provider-applications")
    public ResponseEntity<ApiResponseDto<Page<ApplicationDto>>> getProviderApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            // 获取当前用户ID
            UUID providerId = getCurrentUserId();
            if (providerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("用户未登录"));
            }

            // 创建分页和排序对象
            Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            // 查询申请记录
            Page<ApplicationDto> applications = applicationService.getApplicationsByProviderId(providerId, pageable);
            return ResponseEntity.ok(ApiResponseDto.success(applications, "查询成功"));
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