package cn.com.nabotix.shareplatform.filemanagement.dto;

import lombok.Data;

import java.util.UUID;

/**
 * 文件上传响应DTO
 * 用于封装文件上传接口的响应数据
 *
 * @author 陈雍文
 */
@Data
public class FileUploadResponseDto {
    private UUID fileId;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private UUID uploaderId;

    public FileUploadResponseDto(UUID fileId, String fileName, Long fileSize, String fileType, UUID uploaderId) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.uploaderId = uploaderId;
    }
}