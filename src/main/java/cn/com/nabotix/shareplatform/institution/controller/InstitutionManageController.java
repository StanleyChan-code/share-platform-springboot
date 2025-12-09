package cn.com.nabotix.shareplatform.institution.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.institution.dto.InstitutionCreateRequestDto;
import cn.com.nabotix.shareplatform.institution.dto.InstitutionDto;
import cn.com.nabotix.shareplatform.institution.service.InstitutionService;
import cn.com.nabotix.shareplatform.security.UserDetailsImpl;
import cn.com.nabotix.shareplatform.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 机构管理控制器
 * 提供机构信息的管理接口，包括创建、更新、删除等操作
 *
 * @author 陈雍文
 */
@RestController
@RequestMapping("/api/manage/institutions")
public class InstitutionManageController {

    private final InstitutionService institutionService;
    private final UserService userService;

    @Autowired
    public InstitutionManageController(InstitutionService institutionService, UserService userService) {
        this.institutionService = institutionService;
        this.userService = userService;
    }

    /**
     * 获取所有机构列表
     * 仅平台管理员可访问
     */
    @GetMapping
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<InstitutionDto>>> getAllInstitutions() {
        List<InstitutionDto> institutions = institutionService.getAllInstitutions();
        return ResponseEntity.ok(ApiResponseDto.success(institutions, "获取机构列表成功"));
    }

    /**
     * 根据ID获取特定机构
     * 仅平台管理员可访问
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponseDto<InstitutionDto>> getInstitutionById(@PathVariable UUID id) {
        InstitutionDto institution = institutionService.getInstitutionById(id);
        if (institution != null) {
            return ResponseEntity.ok(ApiResponseDto.success(institution, "获取机构成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的机构"));
        }
    }

    /**
     * 创建新的机构
     * 仅平台管理员可访问
     */
    @PostMapping
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponseDto<InstitutionDto>> createInstitution(@RequestBody InstitutionCreateRequestDto institutionDto) {
        InstitutionDto createdInstitution = institutionService.createInstitution(institutionDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(createdInstitution, "创建机构成功"));
    }

    /**
     * 更新现有机构
     * 平台管理员可更新任意机构，机构管理员只能更新自己所属的机构
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR')")
    public ResponseEntity<ApiResponseDto<InstitutionDto>> updateInstitution(@PathVariable UUID id, @RequestBody InstitutionCreateRequestDto institutionDto) {
        // 检查权限
        if (!hasPermissionToUpdateInstitution(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error("无权更新该机构"));
        }

        InstitutionDto updatedInstitution = institutionService.updateInstitution(id, institutionDto);
        if (updatedInstitution != null) {
            return ResponseEntity.ok(ApiResponseDto.success(updatedInstitution, "更新机构成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的机构"));
        }
    }

    /**
     * 删除机构
     * 仅平台管理员可访问
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponseDto<Void>> deleteInstitution(@PathVariable UUID id) {
        boolean deleted = institutionService.deleteInstitution(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponseDto.success(null, "删除机构成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的机构"));
        }
    }

    /**
     * 获取当前机构管理员所属机构的信息
     * 仅机构管理员可访问
     */
    @GetMapping("/own")
    @PreAuthorize("hasAuthority('INSTITUTION_SUPERVISOR')")
    public ResponseEntity<ApiResponseDto<InstitutionDto>> getOwnInstitution() {
        UUID institutionId = getCurrentUserInstitutionId();
        InstitutionDto institution = institutionService.getInstitutionById(institutionId);

        if (institution != null) {
            return ResponseEntity.ok(ApiResponseDto.success(institution, "获取机构信息成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的机构"));
        }
    }

    /**
     * 更新当前机构管理员所属机构的信息
     * 仅机构管理员可访问
     */
    @PutMapping("/own")
    @PreAuthorize("hasAuthority('INSTITUTION_SUPERVISOR')")
    public ResponseEntity<ApiResponseDto<InstitutionDto>> updateOwnInstitution(@RequestBody InstitutionCreateRequestDto institutionDto) {
        UUID institutionId = getCurrentUserInstitutionId();

        InstitutionDto updatedInstitution = institutionService.updateInstitution(institutionId, institutionDto);

        if (updatedInstitution != null) {
            return ResponseEntity.ok(ApiResponseDto.success(updatedInstitution, "更新机构信息成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的机构"));
        }
    }

    /**
     * 验证通过机构
     * 平台管理员可验证任意机构，机构管理员只能验证自己所属的机构
     */
    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR')")
    public ResponseEntity<ApiResponseDto<InstitutionDto>> verifyInstitution(@PathVariable UUID id) {
        // 检查权限
        if (!hasPermissionToUpdateInstitution(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error("无权验证该机构"));
        }

        InstitutionDto verifiedInstitution = institutionService.verifyInstitution(id);
        if (verifiedInstitution != null) {
            return ResponseEntity.ok(ApiResponseDto.success(verifiedInstitution, "机构验证通过成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的机构"));
        }
    }

    /**
     * 检查用户是否有权限更新指定机构
     *
     * @param institutionId 机构ID
     * @return 是否有权限
     */
    private boolean hasPermissionToUpdateInstitution(UUID institutionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // 平台管理员可以更新任何机构
        if (authentication.getAuthorities().stream()
                .anyMatch(authority -> "PLATFORM_ADMIN".equals(authority.getAuthority()))) {
            return true;
        }

        // 机构管理员只能更新自己所属的机构
        if (authentication.getAuthorities().stream()
                .anyMatch(authority -> "INSTITUTION_SUPERVISOR".equals(authority.getAuthority()))) {
            var user = userService.getUserByUserId(userDetails.getId());
            return user != null && user.getInstitutionId() != null && user.getInstitutionId().equals(institutionId);
        }

        return false;
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