package cn.com.nabotix.shareplatform.researchsubject.dto;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class ResearchSubjectDto {
    private UUID id;
    private String name;
    private String nameEn;
    private String description;
    private Boolean active = true;
    private Instant createdAt;
}