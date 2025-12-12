package cn.com.nabotix.shareplatform.dataset.statistic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * 数据分析请求DTO
 * 用于接收前端发送的数据分析请求
 */
@AllArgsConstructor
@Data
public class DataAnalysisRequestDto {
    /**
     * 数据文件记录ID
     */
    private UUID dataFileId;

    /**
     * 数据字典文件记录ID
     */
    private UUID dictionaryFileId;
}