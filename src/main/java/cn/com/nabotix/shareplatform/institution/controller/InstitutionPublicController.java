package cn.com.nabotix.shareplatform.institution.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.institution.dto.InstitutionDto;
import cn.com.nabotix.shareplatform.institution.service.InstitutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 机构公共访问控制器
 * 提供机构信息的公开查询接口
 *
 * @author 陈雍文
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/institutions")
public class InstitutionPublicController {

    private final InstitutionService institutionService;

    /**
     * 获取所有机构列表（分页）
     * 所有用户均可访问
     * 只返回已验证的机构
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<InstitutionDto>>> getAllInstitutions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InstitutionDto> institutions = institutionService.getVerifiedInstitutions(pageable);
        return ResponseEntity.ok(ApiResponseDto.success(institutions, "获取机构列表成功"));
    }

    /**
     * 根据ID获取特定机构
     * 所有用户均可访问
     * 只能获取已验证的机构
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<InstitutionDto>> getInstitutionById(@PathVariable UUID id) {
        InstitutionDto institution = institutionService.getInstitutionById(id);
        if (institution != null) {
            // 检查机构是否已验证
            if (!Boolean.TRUE.equals(institution.getVerified())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.error("未找到指定的机构"));
            }
            return ResponseEntity.ok(ApiResponseDto.success(institution, "获取机构成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的机构"));
        }
    }
}