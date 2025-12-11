package cn.com.nabotix.shareplatform.filemanagement.dto;

import lombok.Data;

import java.util.UUID;

/**
 * 分片上传响应DTO
 * 用于封装分片上传接口的响应数据
 *
 * @author 陈雍文
 */
@Data
public class ChunkUploadResponseDto {
    private UUID uploadId;
    private Integer chunkNumber;
    private Boolean uploaded;
    private String message;


    public ChunkUploadResponseDto(UUID uploadId, Integer chunkNumber, Boolean uploaded, String message) {
        this.uploadId = uploadId;
        this.chunkNumber = chunkNumber;
        this.uploaded = uploaded;
        this.message = message;
    }

}