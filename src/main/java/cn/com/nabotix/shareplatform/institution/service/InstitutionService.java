package cn.com.nabotix.shareplatform.institution.service;

import cn.com.nabotix.shareplatform.institution.entity.Institution;
import cn.com.nabotix.shareplatform.institution.dto.InstitutionCreateRequestDto;
import cn.com.nabotix.shareplatform.institution.dto.InstitutionDto;
import cn.com.nabotix.shareplatform.institution.repository.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * 机构服务类
 * 提供机构相关的业务逻辑处理
 *
 * @author 陈雍文
 */
@Service
@RequiredArgsConstructor
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    /**
     * 获取所有机构列表（分页）
     *
     * @return 机构列表
     */
    public Page<InstitutionDto> getAllInstitutions(Pageable pageable) {
        Page<Institution> institutionPage = institutionRepository.findAll(pageable);
        return institutionPage.map(this::convertToDto);
    }

    /**
     * 获取所有已验证的机构列表（分页）
     *
     * @return 已验证的机构列表
     */
    public Page<InstitutionDto> getVerifiedInstitutions(Pageable pageable) {
        Page<Institution> institutionPage = institutionRepository.findByVerifiedTrue(pageable);
        return institutionPage.map(this::convertToDto);
    }

    /**
     * 根据ID获取特定机构
     *
     * @param id 机构ID
     * @return 机构信息
     */
    public InstitutionDto getInstitutionById(UUID id) {
        Institution institution = institutionRepository.findById(id).orElse(null);
        return institution != null ? convertToDto(institution) : null;
    }

    /**
     * 创建新机构
     *
     * @param institutionDto 机构信息
     * @return 创建后的机构信息
     */
    public InstitutionDto createInstitution(InstitutionCreateRequestDto institutionDto) {
        Institution institution = new Institution();
        institution.setFullName(institutionDto.getFullName());
        institution.setShortName(institutionDto.getShortName());
        institution.setType(institutionDto.getType());
        institution.setContactPerson(institutionDto.getContactPerson());
        institution.setContactIdType(institutionDto.getContactIdType());
        institution.setContactIdNumber(institutionDto.getContactIdNumber());
        institution.setContactPhone(institutionDto.getContactPhone());
        institution.setContactEmail(institutionDto.getContactEmail());
        institution.setVerified(false);
        institution.setCreatedAt(Instant.now());
        institution.setUpdatedAt(Instant.now());

        Institution savedInstitution = institutionRepository.save(institution);
        return convertToDto(savedInstitution);
    }

    /**
     * 更新机构信息
     *
     * @param id 机构ID
     * @param institutionDto 新的机构信息
     * @return 更新后的机构信息
     */
    public InstitutionDto updateInstitution(UUID id, InstitutionCreateRequestDto institutionDto) {
        Institution existingInstitution = institutionRepository.findById(id).orElse(null);
        if (existingInstitution == null) {
            return null;
        }

        existingInstitution.setFullName(institutionDto.getFullName());
        existingInstitution.setShortName(institutionDto.getShortName());
        existingInstitution.setType(institutionDto.getType());
        existingInstitution.setContactPerson(institutionDto.getContactPerson());
        existingInstitution.setContactIdType(institutionDto.getContactIdType());
        existingInstitution.setContactIdNumber(institutionDto.getContactIdNumber());
        existingInstitution.setContactPhone(institutionDto.getContactPhone());
        existingInstitution.setContactEmail(institutionDto.getContactEmail());
        existingInstitution.setUpdatedAt(Instant.now());

        Institution savedInstitution = institutionRepository.save(existingInstitution);
        return convertToDto(savedInstitution);
    }

    /**
     * 删除机构
     *
     * @param id 机构ID
     * @return 删除是否成功
     */
    public boolean deleteInstitution(UUID id) {
        if (institutionRepository.existsById(id)) {
            institutionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * 验证通过机构
     *
     * @param id 机构ID
     * @return 验证通过后的机构信息
     */
    public InstitutionDto verifyInstitution(UUID id) {
        Institution existingInstitution = institutionRepository.findById(id).orElse(null);
        if (existingInstitution == null) {
            return null;
        }

        existingInstitution.setVerified(true);
        existingInstitution.setUpdatedAt(Instant.now());

        Institution savedInstitution = institutionRepository.save(existingInstitution);
        return convertToDto(savedInstitution);
    }

    /**
     * 将实体转换为DTO
     *
     * @param institution 实体对象
     * @return DTO对象
     */
    private InstitutionDto convertToDto(Institution institution) {
        InstitutionDto dto = new InstitutionDto();
        dto.setId(institution.getId());
        dto.setFullName(institution.getFullName());
        dto.setShortName(institution.getShortName());
        dto.setType(institution.getType());
        dto.setContactPerson(institution.getContactPerson());
        dto.setContactIdType(institution.getContactIdType());
        dto.setContactIdNumber(institution.getContactIdNumber());
        dto.setContactPhone(institution.getContactPhone());
        dto.setContactEmail(institution.getContactEmail());
        dto.setVerified(institution.getVerified());
        dto.setCreatedAt(institution.getCreatedAt());
        dto.setUpdatedAt(institution.getUpdatedAt());
        return dto;
    }
}