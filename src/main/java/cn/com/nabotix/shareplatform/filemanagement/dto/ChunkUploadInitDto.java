package cn.com.nabotix.shareplatform.filemanagement.dto;

import lombok.Data;

import java.util.UUID;

/**
 * 分片上传初始化DTO
 * 用于封装分片上传初始化接口的响应数据
 *
 * @author 陈雍文
 */
@Data
public class ChunkUploadInitDto {
    private UUID uploadId;
    private String fileName;
    private Long fileSize;
    private Integer totalChunks;

    public ChunkUploadInitDto(UUID uploadId, String fileName, Long fileSize, Integer totalChunks) {
        this.uploadId = uploadId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.totalChunks = totalChunks;
    }
}