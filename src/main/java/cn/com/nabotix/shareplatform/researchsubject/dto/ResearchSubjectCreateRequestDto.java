package cn.com.nabotix.shareplatform.researchsubject.dto;

import lombok.Data;

@Data
public class ResearchSubjectCreateRequestDto {
    private String name;
    private String nameEn;
    private String description;
    private Boolean active = true;
}