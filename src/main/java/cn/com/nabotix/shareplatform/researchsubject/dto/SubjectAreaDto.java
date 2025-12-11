package cn.com.nabotix.shareplatform.researchsubject.dto;

import cn.com.nabotix.shareplatform.researchsubject.entity.ResearchSubject;
import lombok.Data;

import java.util.UUID;

/**
 * 学科领域DTO
 * 用于向公众展示学科领域的基本信息
 *
 * @author 陈雍文
 */
@Data
public class SubjectAreaDto {
    private UUID id;
    private String name;
    private String nameEn;
    private String description;

    public static SubjectAreaDto fromEntity(ResearchSubject subjectArea) {
        if (subjectArea == null) {
            return null;
        }
        SubjectAreaDto subjectAreaDto = new SubjectAreaDto();
        subjectAreaDto.setId(subjectArea.getId());
        subjectAreaDto.setName(subjectArea.getName());
        subjectAreaDto.setNameEn(subjectArea.getNameEn());
        subjectAreaDto.setDescription(subjectArea.getDescription());
        return subjectAreaDto;
    }
}
