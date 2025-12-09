package cn.com.nabotix.shareplatform.filemanagement.controller;

import cn.com.nabotix.shareplatform.filemanagement.dto.FileUploadResponseDto;
import cn.com.nabotix.shareplatform.filemanagement.entity.FileRecord;
import cn.com.nabotix.shareplatform.filemanagement.service.FileManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 文件管理控制器
 * 提供文件上传和移动相关的API接口
 *
 * @author 陈雍文
 */
@RestController
@RequestMapping("/api/files")
public class FileManagementController {

    private final FileManagementService fileManagementService;

    @Autowired
    public FileManagementController(FileManagementService fileManagementService) {
        this.fileManagementService = fileManagementService;
    }

    /**
     * 上传文件接口
     * @param file 上传的文件
     * @param uploaderId 上传者ID
     * @return 文件记录信息
     */
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponseDto> uploadFile(@RequestParam("file") MultipartFile file,
                                           @RequestParam("uploaderId") UUID uploaderId) {
        try {
            UUID fileId = fileManagementService.uploadFile(file, uploaderId);
            
            // 获取文件记录详情
            FileRecord fileRecord = fileManagementService.getFileRecordById(fileId);
            
            FileUploadResponseDto responseDto = new FileUploadResponseDto(
                fileRecord.getId(),
                fileRecord.getFileName(),
                fileRecord.getFileSize(),
                fileRecord.getFileType(),
                fileRecord.getUploaderId()
            );
            
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}