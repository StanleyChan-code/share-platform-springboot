package cn.com.nabotix.shareplatform.filemanagement.controller;

import cn.com.nabotix.shareplatform.filemanagement.dto.*;
import cn.com.nabotix.shareplatform.filemanagement.entity.FileRecord;
import cn.com.nabotix.shareplatform.filemanagement.service.FileManagementService;
import cn.com.nabotix.shareplatform.security.AuthorityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 文件管理控制器
 * 提供文件上传和移动相关的API接口
 *
 * @author 陈雍文
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
@PreAuthorize("isAuthenticated()")
public class FileManagementController {

    private final FileManagementService fileManagementService;

    /**
     * 上传文件接口
     *
     * @param file 上传的文件
     * @return 文件记录信息
     */
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponseDto> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            UUID fileId = fileManagementService.uploadFile(file, AuthorityUtil.getCurrentUserId());

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

    /**
     * 初始化分片上传接口
     *
     * @param initDto 分片上传初始化信息
     * @return 上传ID和其他相关信息
     */
    @PostMapping("/chunked-upload/init")
    public ResponseEntity<ChunkUploadInitDto> initChunkedUpload(@RequestBody ChunkUploadInitDto initDto) {
        // 获取当前认证用户ID
        try {
            UUID uploadId = fileManagementService.initChunkedUpload(
                    initDto.getFileName(),
                    initDto.getFileSize(),
                    AuthorityUtil.getCurrentUserId()
            );

            ChunkUploadInitDto responseDto = new ChunkUploadInitDto(
                    uploadId,
                    initDto.getFileName(),
                    initDto.getFileSize(),
                    initDto.getTotalChunks()
            );

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 上传文件分片接口
     *
     * @param file        上传的文件分片
     * @param uploadId    上传ID
     * @param chunkNumber 分片编号
     * @param totalChunks 总分片数
     * @param fileName    original文件名
     * @return 分片上传结果
     */
    @PostMapping("/chunked-upload/chunk")
    public ResponseEntity<ChunkUploadResponseDto> uploadChunk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("uploadId") UUID uploadId,
            @RequestParam("chunkNumber") Integer chunkNumber,
            @RequestParam("totalChunks") Integer totalChunks,
            @RequestParam("fileName") String fileName) {
        try {
            fileManagementService.uploadChunk(file, uploadId, chunkNumber, totalChunks, fileName, AuthorityUtil.getCurrentUserId());

            ChunkUploadResponseDto responseDto = new ChunkUploadResponseDto(
                    uploadId,
                    chunkNumber,
                    true,
                    "分片上传成功"
            );

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ChunkUploadResponseDto(
                    uploadId,
                    chunkNumber,
                    false,
                    "分片上传失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 查询分片上传状态接口
     *
     * @param uploadId 上传ID
     * @return 上传状态信息
     */
    @GetMapping("/chunked-upload/status")
    public ResponseEntity<ChunkUploadStatusDto> getChunkedUploadStatus(@RequestParam("uploadId") UUID uploadId) {
        try {
            // 验证用户是否有权限查看此上传状态
            if (!fileManagementService.isUploaderOfUploadId(uploadId, AuthorityUtil.getCurrentUserId())) {
                return ResponseEntity.status(403).body(null);
            }

            ChunkUploadStatusDto statusDto = fileManagementService.getChunkedUploadStatus(uploadId);
            return ResponseEntity.ok(statusDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 合并分片接口
     *
     * @param uploadId 上传ID
     * @return 合并后的文件路径
     */
    @PostMapping("/chunked-upload/merge")
    public ResponseEntity<FileUploadResponseDto> mergeChunks(
            @RequestParam("uploadId") UUID uploadId) {
        try {
            // 验证用户是否有权限合并此上传
            if (!fileManagementService.isUploaderOfUploadId(uploadId, AuthorityUtil.getCurrentUserId())) {
                return ResponseEntity.status(403).body(null);
            }

            FileRecord fileRecord = fileManagementService.mergeChunks(uploadId);

            FileUploadResponseDto responseDto = new FileUploadResponseDto(
                    fileRecord.getId(),
                    fileRecord.getFileName(),
                    fileRecord.getFileSize(),
                    fileRecord.getFileType(),
                    fileRecord.getUploaderId()
            );

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("合并分片失败: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
}