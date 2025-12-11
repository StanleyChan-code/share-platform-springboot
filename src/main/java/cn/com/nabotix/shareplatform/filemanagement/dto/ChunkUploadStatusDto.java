package cn.com.nabotix.shareplatform.filemanagement.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * 分片上传状态DTO
 * 用于封装分片上传状态查询接口的响应数据
 *
 * @author 陈雍文
 */
@Data
public class ChunkUploadStatusDto {
    private UUID uploadId;
    private String fileName;
    private Long fileSize;
    private Integer totalChunks;
    private List<Integer> uploadedChunks;
    private Boolean completed;
}