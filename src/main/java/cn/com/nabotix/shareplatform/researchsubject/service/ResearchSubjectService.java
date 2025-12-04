package cn.com.nabotix.shareplatform.researchsubject.service;

import cn.com.nabotix.shareplatform.researchsubject.entity.ResearchSubject;
import cn.com.nabotix.shareplatform.researchsubject.dto.ResearchSubjectCreateRequestDto;
import cn.com.nabotix.shareplatform.researchsubject.dto.ResearchSubjectDto;
import cn.com.nabotix.shareplatform.researchsubject.repository.ResearchSubjectRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResearchSubjectService {

    private final ResearchSubjectRepository researchSubjectRepository;

    public ResearchSubjectService(ResearchSubjectRepository researchSubjectRepository) {
        this.researchSubjectRepository = researchSubjectRepository;
    }

    /**
     * 获取所有研究学科
     *
     * @return 研究学科列表
     */
    public List<ResearchSubjectDto> getAllResearchSubjects() {
        return researchSubjectRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有激活的研究学科
     * 用于公开查询接口
     *
     * @return 激活的研究学科列表
     */
    public List<ResearchSubjectDto> getActiveResearchSubjects() {
        return researchSubjectRepository.findByActiveTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有未激活的研究学科
     * 仅平台管理员使用
     *
     * @return 未激活的研究学科列表
     */
    public List<ResearchSubjectDto> getInactiveResearchSubjects() {
        return researchSubjectRepository.findByActiveFalse().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取研究学科
     *
     * @param id 学科ID
     * @return 研究学科DTO
     */
    public ResearchSubjectDto getResearchSubjectById(UUID id) {
        return researchSubjectRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    /**
     * 创建新的研究学科
     *
     * @param requestDto 创建请求DTO
     * @return 创建的研究学科
     */
    public ResearchSubjectDto createResearchSubject(ResearchSubjectCreateRequestDto requestDto) {
        ResearchSubject subject = new ResearchSubject();
        subject.setName(requestDto.getName());
        subject.setNameEn(requestDto.getNameEn());
        subject.setDescription(requestDto.getDescription());
        subject.setActive(requestDto.getActive());
        // 注意：createdAt字段在实体类中已经设置了默认值为Instant.now()

        ResearchSubject savedSubject = researchSubjectRepository.save(subject);
        return convertToDto(savedSubject);
    }

    /**
     * 更新研究学科
     *
     * @param id 学科ID
     * @param requestDto 更新请求DTO
     * @return 更新后的研究学科
     */
    public ResearchSubjectDto updateResearchSubject(UUID id, ResearchSubjectCreateRequestDto requestDto) {
        return researchSubjectRepository.findById(id)
                .map(subject -> {
                    subject.setName(requestDto.getName());
                    subject.setNameEn(requestDto.getNameEn());
                    subject.setDescription(requestDto.getDescription());
                    subject.setActive(requestDto.getActive());
                    
                    ResearchSubject updatedSubject = researchSubjectRepository.save(subject);
                    return convertToDto(updatedSubject);
                })
                .orElse(null);
    }

    /**
     * 删除研究学科
     *
     * @param id 学科ID
     * @return 是否删除成功
     */
    public boolean deleteResearchSubject(UUID id) {
        return researchSubjectRepository.findById(id)
                .map(subject -> {
                    researchSubjectRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }

    /**
     * 将实体转换为DTO
     *
     * @param subject 研究学科实体
     * @return 研究学科DTO
     */
    private ResearchSubjectDto convertToDto(ResearchSubject subject) {
        ResearchSubjectDto dto = new ResearchSubjectDto();
        BeanUtils.copyProperties(subject, dto);
        return dto;
    }
}