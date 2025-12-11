package cn.com.nabotix.shareplatform.researchoutput.dto;

import cn.com.nabotix.shareplatform.researchoutput.entity.OutputType;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

/**
 * 研究成果创建请求DTO
 * 用于创建新的研究成果的请求参数
 *
 * @author 陈雍文
 */
@Data
public class ResearchOutputCreateRequestDto {
    private UUID datasetId;
    private OutputType type;
    private String otherType;
    private String title;
    private String abstractText;
    private String outputNumber;
    private Integer citationCount;
    private String publicationUrl;
    private UUID fileId;
    private Map<String, Object> otherInfo;
}