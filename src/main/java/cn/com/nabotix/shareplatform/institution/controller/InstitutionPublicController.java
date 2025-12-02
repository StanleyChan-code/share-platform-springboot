package cn.com.nabotix.shareplatform.institution.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.institution.dto.InstitutionDto;
import cn.com.nabotix.shareplatform.institution.service.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 机构公共访问控制器
 * 提供机构信息的公开查询接口
 *
 * @author 陈雍文
 */
@RestController
@RequestMapping("/api/institutions")
public class InstitutionPublicController {

    private final InstitutionService institutionService;

    @Autowired
    public InstitutionPublicController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    /**
     * 获取所有机构列表
     * 所有用户均可访问
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<InstitutionDto>>> getAllInstitutions() {
        List<InstitutionDto> institutions = institutionService.getAllInstitutions();
        return ResponseEntity.ok(ApiResponseDto.success(institutions, "获取机构列表成功"));
    }

    /**
     * 根据ID获取特定机构
     * 所有用户均可访问
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<InstitutionDto>> getInstitutionById(@PathVariable UUID id) {
        InstitutionDto institution = institutionService.getInstitutionById(id);
        if (institution != null) {
            return ResponseEntity.ok(ApiResponseDto.success(institution, "获取机构成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的机构"));
        }
    }
}