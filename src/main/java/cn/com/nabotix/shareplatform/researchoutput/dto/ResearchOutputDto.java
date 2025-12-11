package cn.com.nabotix.shareplatform.researchoutput.dto;

import cn.com.nabotix.shareplatform.dataset.dto.PublicDatasetDto;
import cn.com.nabotix.shareplatform.researchoutput.entity.OutputType;
import cn.com.nabotix.shareplatform.researchoutput.entity.ResearchOutput;
import cn.com.nabotix.shareplatform.user.dto.UserDto;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
public class ResearchOutputDto {
    private UUID id;
    private PublicDatasetDto dataset;
    private UserDto submitter;
    private OutputType type;
    private String otherType;
    private String title;
    private String abstractText;
    private String outputNumber;
    private Integer citationCount;
    private String publicationUrl;
    private UUID fileId;
    private Instant createdAt;
    private Boolean approved;
    private UserDto approver;
    private Instant approvedAt;
    private String rejectionReason;
    private Map<String, Object> otherInfo;

    public static ResearchOutputDto fromEntity(ResearchOutput researchOutput, PublicDatasetDto dataset, UserDto submitter, UserDto approver) {
        ResearchOutputDto dto = new ResearchOutputDto();
        dto.setId(researchOutput.getId());
        dto.setDataset(dataset);
        dto.setSubmitter(submitter);
        dto.setType(researchOutput.getType());
        dto.setOtherType(researchOutput.getOtherType());
        dto.setTitle(researchOutput.getTitle());
        dto.setAbstractText(researchOutput.getAbstractText());
        dto.setOutputNumber(researchOutput.getOutputNumber());
        dto.setCitationCount(researchOutput.getCitationCount());
        dto.setPublicationUrl(researchOutput.getPublicationUrl());
        dto.setFileId(researchOutput.getFileId());
        dto.setCreatedAt(researchOutput.getCreatedAt());
        dto.setApproved(researchOutput.getApproved());
        dto.setApprover(approver);
        dto.setApprovedAt(researchOutput.getApprovedAt());
        dto.setRejectionReason(researchOutput.getRejectionReason());
        dto.setOtherInfo(researchOutput.getOtherInfo());
        return dto;
    }
}