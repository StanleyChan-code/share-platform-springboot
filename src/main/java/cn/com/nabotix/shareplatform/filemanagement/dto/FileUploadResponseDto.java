package cn.com.nabotix.shareplatform.filemanagement.dto;

import java.util.UUID;

/**
 * 文件上传响应DTO
 * 用于封装文件上传接口的响应数据
 *
 * @author 陈雍文
 */
public class FileUploadResponseDto {
    private UUID fileId;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private UUID uploaderId;

    public FileUploadResponseDto() {
    }

    public FileUploadResponseDto(UUID fileId, String fileName, Long fileSize, String fileType, UUID uploaderId) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.uploaderId = uploaderId;
    }

    // Getters and Setters
    public UUID getFileId() {
        return fileId;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public UUID getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(UUID uploaderId) {
        this.uploaderId = uploaderId;
    }
}