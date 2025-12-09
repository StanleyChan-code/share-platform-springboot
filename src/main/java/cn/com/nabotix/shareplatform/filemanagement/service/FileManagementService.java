package cn.com.nabotix.shareplatform.filemanagement.service;

import cn.com.nabotix.shareplatform.filemanagement.entity.FileRecord;
import cn.com.nabotix.shareplatform.filemanagement.repository.FileRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 文件管理服务类
 * 提供文件上传、移动、下载等核心业务逻辑
 *
 * @author 陈雍文
 */
@Service
public class FileManagementService {

    private static final Logger logger = LoggerFactory.getLogger(FileManagementService.class);
    
    private final FileRecordRepository fileRecordRepository;
    
    // 定义临时文件夹路径
    private static final String TEMP_DIR = System.getProperty("user.home") + "/.clinical-research-data-sharing-platform/tmp/";
    
    // 定义基础文件夹路径
    private static final String BASE_DIR = System.getProperty("user.home") + "/.clinical-research-data-sharing-platform/";

    @Autowired
    public FileManagementService(FileRecordRepository fileRecordRepository) {
        this.fileRecordRepository = fileRecordRepository;
        
        // 创建必要的目录
        createDirectories();
    }
    
    /**
     * 创建必要的目录
     */
    private void createDirectories() {
        try {
            Files.createDirectories(Paths.get(TEMP_DIR));
            Files.createDirectories(Paths.get(BASE_DIR));
        } catch (IOException e) {
            throw new RuntimeException("无法创建必要的目录", e);
        }
    }
    
    /**
     * 根据原始文件名提取文件扩展名
     * @param originalFileName 原始文件名
     * @return 文件扩展名（包括点号）
     */
    private String getFileExtension(String originalFileName) {
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return fileExtension;
    }
    
    /**
     * 根据文件ID和原始文件名构造实际文件名
     * @param fileId 文件ID
     * @param originalFileName 原始文件名
     * @return 实际文件名
     */
    private String getActualFileName(UUID fileId, String originalFileName) {
        return fileId + getFileExtension(originalFileName);
    }
    
    /**
     * 构造完整文件路径
     *
     * @param filePath         文件相对路径
     * @param fileId           文件ID
     * @param originalFileName 原始文件名
     * @return 完整文件路径
     */
    private Path getCompleteFilePath(String filePath, UUID fileId, String originalFileName) {
        String actualFileName = getActualFileName(fileId, originalFileName);
        return Paths.get(BASE_DIR, filePath, actualFileName);
    }
    
    /**
     * 上传文件到临时目录
     * @param file 上传的文件
     * @param uploaderId 上传者ID
     * @return 文件记录ID
     */
    public UUID uploadFile(MultipartFile file, UUID uploaderId) {
        try {
            // 预先生成UUID
            UUID fileId = UUID.randomUUID();
            
            // 生成基于UUID的新文件名
            String originalFileName = file.getOriginalFilename();
            String newFileName = getActualFileName(fileId, originalFileName);
            
            // 构建文件路径
            Path filePath = Paths.get(TEMP_DIR, newFileName);
            
            // 保存文件到临时目录
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // 创建并保存文件记录
            FileRecord fileRecord = new FileRecord(
                    fileId,
                originalFileName,
                "tmp/",
                file.getSize(),
                file.getContentType(),
                uploaderId
            );
            FileRecord savedRecord = fileRecordRepository.save(fileRecord);
            
            return savedRecord.getId();
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }
    
    /**
     * 将文件从临时目录移动到指定目录
     * @param fileId 文件记录ID
     * @param relativePath 相对路径（如 /dataset/1/）
     * @return 移动后的文件路径
     */
    public String moveFileToDirectory(UUID fileId, String relativePath) {
        Optional<FileRecord> fileRecordOpt = fileRecordRepository.findById(fileId);
        
        if (fileRecordOpt.isEmpty()) {
            throw new RuntimeException("文件记录不存在");
        }
        
        FileRecord fileRecord = fileRecordOpt.get();
        
        if (fileRecord.getDeleted()) {
            throw new RuntimeException("文件已被删除");
        }
        
        try {
            // 确保路径以斜杠结尾
            if (!relativePath.endsWith("/")) {
                relativePath = relativePath + "/";
            }
            
            // 构建目标目录路径
            Path targetDir = Paths.get(BASE_DIR, relativePath);
            
            // 创建目标目录（如果不存在）
            Files.createDirectories(targetDir);
            
            // 生成基于UUID的文件名
            String originalFileName = fileRecord.getFileName();
            String newFileName = getActualFileName(fileId, originalFileName);
            
            // 构建源文件路径
            String sourceFileName = getActualFileName(fileId, fileRecord.getFileName());
            Path sourcePath = Paths.get(BASE_DIR, fileRecord.getFilePath(), sourceFileName);
            
            // 构建目标文件路径
            Path targetPath = targetDir.resolve(newFileName);

            // 如果移动地址前后相同，则不需要移动文件
            if (sourcePath.equals(targetPath)) {
                return relativePath + newFileName;
            }
            
            // 移动文件
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // 更新文件记录中的路径，只保存相对目录路径
            fileRecord.setFilePath(relativePath);
            fileRecordRepository.save(fileRecord);
            
            return relativePath + newFileName;
        } catch (IOException e) {
            throw new RuntimeException("文件移动失败", e);
        }
    }
    
    /**
     * 下载文件（供后端使用）
     * @param fileId 文件记录ID
     * @return Resource对象
     */
    public Resource downloadFile(UUID fileId) {
        Optional<FileRecord> fileRecordOpt = fileRecordRepository.findById(fileId);
        
        if (fileRecordOpt.isEmpty()) {
            throw new RuntimeException("文件记录不存在");
        }
        
        FileRecord fileRecord = fileRecordOpt.get();
        
        if (fileRecord.getDeleted()) {
            throw new RuntimeException("文件已被删除");
        }
        
        try {
            // 构建完整文件路径
            Path filePath = getCompleteFilePath(fileRecord.getFilePath(), fileId, fileRecord.getFileName());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("无法读取文件");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("文件路径错误", e);
        }
    }
    
    /**
     * 标记文件为已删除（软删除）
     * @param fileId 文件记录ID
     */
    public void markFileAsDeleted(UUID fileId) {
        Optional<FileRecord> fileRecordOpt = fileRecordRepository.findById(fileId);
        
        if (fileRecordOpt.isEmpty()) {
            throw new RuntimeException("文件记录不存在");
        }
        
        FileRecord fileRecord = fileRecordOpt.get();
        fileRecord.setDeleted(true);
        fileRecord.setDeleteTime(LocalDateTime.now());
        fileRecordRepository.save(fileRecord);
        
        // 删除实际文件
        try {
            // 构建完整文件路径
            Path filePath = getCompleteFilePath(fileRecord.getFilePath(), fileId, fileRecord.getFileName());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // 文件可能已经不存在，忽略异常
        }
    }

    /**
     * 清理临时文件（定时任务调用）
     * 只清理上传时间超过1小时且位于tmp/目录下的文件
     */
    public void cleanUpTemporaryFiles() {
        List<FileRecord> temporaryFiles = fileRecordRepository.findByDeletedFalse();
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        
        for (FileRecord fileRecord : temporaryFiles) {
            // 检查文件是否在临时目录中且上传时间超过1小时
            if (fileRecord.getFilePath().startsWith("tmp/") && fileRecord.getUploadTime().isBefore(oneHourAgo)) {
                try {
                    // 构建完整文件路径
                    Path filePath = getCompleteFilePath(fileRecord.getFilePath(), fileRecord.getId(), fileRecord.getFileName());
                    // 删除实际文件
                    Files.deleteIfExists(filePath);
                    
                    // 标记为已删除
                    fileRecord.setDeleted(true);
                    fileRecord.setDeleteTime(LocalDateTime.now());
                    fileRecordRepository.save(fileRecord);
                } catch (IOException e) {
                    // 记录日志或处理异常
                    logger.error("清理临时文件时发生错误，文件ID: {}", fileRecord.getId(), e);
                }
            }
        }
    }
    
    /**
     * 根据文件ID获取文件记录
     * @param fileId 文件记录ID
     * @return 文件记录
     */
    public FileRecord getFileRecordById(UUID fileId) {
        Optional<FileRecord> fileRecordOpt = fileRecordRepository.findById(fileId);
        return fileRecordOpt.orElse(null);
    }
}