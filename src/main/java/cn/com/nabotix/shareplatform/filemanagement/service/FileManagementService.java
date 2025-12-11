package cn.com.nabotix.shareplatform.filemanagement.service;

import cn.com.nabotix.shareplatform.filemanagement.dto.ChunkUploadStatusDto;
import cn.com.nabotix.shareplatform.filemanagement.dto.FileDownloadDto;
import cn.com.nabotix.shareplatform.filemanagement.entity.FileChunkRecord;
import cn.com.nabotix.shareplatform.filemanagement.entity.FileRecord;
import cn.com.nabotix.shareplatform.filemanagement.repository.FileChunkRecordRepository;
import cn.com.nabotix.shareplatform.filemanagement.repository.FileRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
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
    private final FileChunkRecordRepository fileChunkRecordRepository;

    // 定义基础文件夹路径
    private static final String BASE_DIR = System.getProperty("user.home") + "/.clinical-research-data-sharing-platform/";

    private static final String TEMP_RELATED_PATH = "tmp/";
    private static final String CHUNKS_RELATED_PATH = "chunks/";

    @Autowired
    public FileManagementService(FileRecordRepository fileRecordRepository, FileChunkRecordRepository fileChunkRecordRepository) {
        this.fileRecordRepository = fileRecordRepository;
        this.fileChunkRecordRepository = fileChunkRecordRepository;

        // 创建必要的目录
        createDirectories();
    }

    /**
     * 创建必要的目录
     */
    private void createDirectories() {
        try {
            Files.createDirectories(Paths.get(BASE_DIR));
            Files.createDirectories(Paths.get(BASE_DIR, TEMP_RELATED_PATH));
            Files.createDirectories(Paths.get(BASE_DIR, CHUNKS_RELATED_PATH));
        } catch (IOException e) {
            throw new RuntimeException("无法创建必要的目录", e);
        }
    }

    /**
     * 根据原始文件名提取文件扩展名
     *
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
     *
     * @param fileId           文件ID
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
     *
     * @param file       上传的文件
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
            Path filePath = Paths.get(BASE_DIR, TEMP_RELATED_PATH, newFileName);

            // 保存文件到临时目录
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 创建并保存文件记录
            FileRecord fileRecord = new FileRecord(
                    fileId,
                    originalFileName,
                    TEMP_RELATED_PATH,
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
     *
     * @param fileId       文件记录ID
     * @param relativePath 相对路径（如 /dataset/1/）
     * @return 移动后的文件路径
     */
    public FileRecord moveFileToDirectory(UUID fileId, String relativePath) {
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
                return fileRecord;
            }

            // 移动文件
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 更新文件记录中的路径，只保存相对目录路径
            fileRecord.setFilePath(relativePath);

            return fileRecordRepository.save(fileRecord);
        } catch (IOException e) {
            throw new RuntimeException("文件移动失败", e);
        }
    }

    /**
     * 下载文件（供后端使用）
     *
     * @param fileId 文件记录ID
     * @return Resource对象
     */
    public FileDownloadDto downloadFile(UUID fileId) {
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
                FileDownloadDto dto = new FileDownloadDto();
                dto.setFile(resource);
                dto.setFileName(fileRecord.getFileName());
                dto.setFileSize(fileRecord.getFileSize());
                dto.setFileType(fileRecord.getFileType());
                return dto;
            } else {
                throw new RuntimeException("无法读取文件");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("文件路径错误", e);
        }
    }

    /**
     * 标记文件为已删除（软删除）
     *
     * @param fileId 文件记录ID
     */
    public void markFileAsDeleted(UUID fileId) {
        Optional<FileRecord> fileRecordOpt = fileRecordRepository.findById(fileId);

        if (fileRecordOpt.isEmpty()) {
            throw new RuntimeException("文件记录不存在");
        }

        FileRecord fileRecord = fileRecordOpt.get();
        fileRecord.setDeleted(true);
        fileRecord.setDeleteTime(Instant.now());
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
     * 清理tmp/目录下上传超过1小时的文件和chunks/目录下上传超过48小时的文件
     */
    public void cleanUpTemporaryFiles() {
        Instant now = Instant.now();
        Duration oneHour = Duration.ofHours(1);
        Duration fortyEightHours = Duration.ofHours(48);

        // 查询需要清理的分片文件
        List<FileRecord> oldChunkFiles = fileRecordRepository.findOldFiles(CHUNKS_RELATED_PATH, fortyEightHours, now);
        for (FileRecord fileRecord : oldChunkFiles) {
            // 查询所有分片并删除
            List<FileChunkRecord> chunkFiles = fileChunkRecordRepository.findByUploadIdOrderByChunkNumber(fileRecord.getId());
            for (FileChunkRecord chunkFile : chunkFiles) {
                try {
                    // 构建完整文件路径
                    Path chunkFilePath = getCompleteFilePath(chunkFile.getChunkPath(), chunkFile.getId(), chunkFile.getFileName());
                    // 删除实际文件
                    Files.deleteIfExists(chunkFilePath);
                } catch (IOException e) {
                    // 记录日志或处理异常
                    logger.error("清理临时文件时发生错误，文件ID: {}", chunkFile.getId(), e);
                }
            }
        }


        // 查询需要清理的临时文件
        List<FileRecord> oldTempFiles = fileRecordRepository.findOldFiles(TEMP_RELATED_PATH, oneHour, now);

        // 合并两个列表
        List<FileRecord> filesToClean = new ArrayList<>();
        filesToClean.addAll(oldTempFiles);
        filesToClean.addAll(oldChunkFiles);

        // 清理文件
        for (FileRecord fileRecord : filesToClean) {
            try {
                // 构建完整文件路径
                Path filePath = getCompleteFilePath(fileRecord.getFilePath(), fileRecord.getId(), fileRecord.getFileName());
                // 删除实际文件
                Files.deleteIfExists(filePath);

                // 标记为已删除
                fileRecord.setDeleted(true);
                fileRecord.setDeleteTime(now);
                fileRecordRepository.save(fileRecord);
            } catch (IOException e) {
                // 记录日志或处理异常
                logger.error("清理临时文件时发生错误，文件ID: {}", fileRecord.getId(), e);
            }
        }
    }

    /**
     * 根据文件ID获取文件记录
     *
     * @param fileId 文件记录ID
     * @return 文件记录
     */
    public FileRecord getFileRecordById(UUID fileId) {
        Optional<FileRecord> fileRecordOpt = fileRecordRepository.findById(fileId);
        return fileRecordOpt.orElse(null);
    }

    /**
     * 检查指定的uploadId是否属于指定的上传者
     *
     * @param uploadId   上传ID
     * @param uploaderId 上传者ID
     * @return 是否属于同一上传者
     */
    public boolean isUploaderOfUploadId(UUID uploadId, UUID uploaderId) {
        // 首先检查分片记录
        List<FileChunkRecord> chunks = fileChunkRecordRepository.findByUploadIdOrderByChunkNumber(uploadId);
        if (!chunks.isEmpty()) {
            return chunks.getFirst().getUploaderId().equals(uploaderId);
        }

        // 如果没有分片记录，检查完整文件记录
        Optional<FileRecord> record = fileRecordRepository.findById(uploadId);
        return record.map(fileRecord ->
                        fileRecord.getUploaderId().equals(uploaderId))
                .orElse(false);

    }

    /**
     * 初始化分片上传
     *
     * @param fileName    文件名
     * @param fileSize    文件大小
     * @param uploaderId  上传者ID
     * @return 上传ID
     */
    public UUID initChunkedUpload(String fileName, Long fileSize, UUID uploaderId) {
        FileRecord fileRecord = new FileRecord();
        fileRecord.setId(UUID.randomUUID());
        fileRecord.setFileName(fileName);
        fileRecord.setFileSize(fileSize);
        fileRecord.setFilePath("chunks/");
        fileRecord.setUploaderId(uploaderId);
        fileRecord.setUploadTime(Instant.now());
        fileRecord = fileRecordRepository.save(fileRecord);
        return fileRecord.getId();
    }

    /**
     * 检查分片是否已上传
     *
     * @param uploadId    上传ID
     * @param chunkNumber 分片编号
     * @return 是否已上传
     */
    private boolean isChunkUploaded(UUID uploadId, Integer chunkNumber) {
        return fileChunkRecordRepository.findByUploadIdAndChunkNumber(uploadId, chunkNumber).isPresent();
    }

    /**
     * 上传文件分片
     *
     * @param file        文件分片
     * @param uploadId    上传ID
     * @param chunkNumber 分片编号
     * @param totalChunks 总分片数
     * @param fileName    原始文件名
     * @param uploaderId  上传者ID
     */
    public void uploadChunk(MultipartFile file, UUID uploadId, Integer chunkNumber, Integer totalChunks,
                            String fileName, UUID uploaderId) {
        try {
            // 检查分片是否已存在
            if (isChunkUploaded(uploadId, chunkNumber)) {
                // 分片已存在，无需重复上传
                return;
            }

            // 检查uploadId是否已经有关联的上传者，并验证是否一致
            List<FileChunkRecord> existingChunks = fileChunkRecordRepository.findByUploadIdOrderByChunkNumber(uploadId);
            if (!existingChunks.isEmpty() && !existingChunks.getFirst().getUploaderId().equals(uploaderId)) {
                throw new RuntimeException("操作者ID与记录的上传者ID不一致");
            }

            // 生成分片文件名
            String chunkFileName = uploadId.toString() + "_" + chunkNumber;

            // 构建分片文件路径
            Path chunkPath = Paths.get(BASE_DIR, CHUNKS_RELATED_PATH, chunkFileName);

            // 保存分片文件
            Files.copy(file.getInputStream(), chunkPath, StandardCopyOption.REPLACE_EXISTING);

            // 创建并保存分片记录
            FileChunkRecord chunkRecord = new FileChunkRecord(
                    uploadId,
                    chunkNumber,
                    totalChunks,
                    fileName,
                    "chunks/" + chunkFileName,
                    file.getSize(),
                    uploaderId
            );

            fileChunkRecordRepository.save(chunkRecord);
        } catch (IOException e) {
            throw new RuntimeException("分片上传失败", e);
        }
    }

    /**
     * 获取分片上传状态
     *
     * @param uploadId 上传ID
     * @return 上传状态信息
     */
    public ChunkUploadStatusDto getChunkedUploadStatus(UUID uploadId) {
        // 查询已上传的分片
        List<FileChunkRecord> uploadedChunks = fileChunkRecordRepository.findByUploadIdOrderByChunkNumber(uploadId);

        // 提取分片编号列表
        List<Integer> chunkNumbers = uploadedChunks.stream()
                .map(FileChunkRecord::getChunkNumber)
                .toList();

        // 获取第一个分片记录作为基本信息来源
        FileChunkRecord firstChunk = uploadedChunks.isEmpty() ? null : uploadedChunks.getFirst();

        // 检查是否已完成
        boolean completed = firstChunk != null && chunkNumbers.size() == firstChunk.getTotalChunks();

        // 构造状态DTO
        ChunkUploadStatusDto statusDto = new ChunkUploadStatusDto();
        statusDto.setUploadId(uploadId);
        statusDto.setFileName(firstChunk != null ? firstChunk.getFileName() : "");
        statusDto.setFileSize(firstChunk != null ? firstChunk.getChunkSize() : 0L);
        statusDto.setTotalChunks(firstChunk != null ? firstChunk.getTotalChunks() : 0);
        statusDto.setUploadedChunks(chunkNumbers);
        statusDto.setCompleted(completed);

        return statusDto;
    }

    /**
     * 合并分片
     *
     * @param uploadId 上传ID
     * @return 合并后的文件ID
     */
    @Transactional
    public FileRecord mergeChunks(UUID uploadId) {
        try {
            // 查找上传记录
            Optional<FileRecord> optionalFileRecord = fileRecordRepository.findById(uploadId);
            if (optionalFileRecord.isEmpty()) {
                throw new RuntimeException("上传记录不存在");
            }
            FileRecord fileRecord = optionalFileRecord.get();

            // 查询所有分片记录
            List<FileChunkRecord> chunks = fileChunkRecordRepository.findByUploadIdOrderByChunkNumber(uploadId);

            if (chunks.isEmpty()) {
                throw new RuntimeException("没有找到任何分片");
            }

            // 获取文件基本信息
            FileChunkRecord firstChunk = chunks.getFirst();
            String fileName = firstChunk.getFileName();
            long totalSize = chunks.stream().mapToLong(FileChunkRecord::getChunkSize).sum();

            // 检查是否所有分片都已上传
            if (chunks.size() != firstChunk.getTotalChunks()) {
                throw new RuntimeException("分片不完整，已上传" + chunks.size() + "个分片，总共" + firstChunk.getTotalChunks() + "个分片");
            }

            // 构建目标目录路径，生成目标文件名
            String targetFileName = getActualFileName(uploadId, fileName);
            Path targetPath = Paths.get(BASE_DIR, TEMP_RELATED_PATH).resolve(targetFileName);

            // 创建目标文件
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(targetPath.toFile(), "rw")) {
                randomAccessFile.setLength(totalSize);

                // 按顺序合并分片
                for (FileChunkRecord chunk : chunks) {
                    Path chunkPath = Paths.get(BASE_DIR, chunk.getChunkPath());
                    byte[] chunkBytes = Files.readAllBytes(chunkPath);
                    randomAccessFile.write(chunkBytes);
                }
            }

            // 更新文件记录
            fileRecord.setFileType(Files.probeContentType(targetPath));
            fileRecord.setFileSize(targetPath.toFile().length());
            fileRecord.setUploaderId(firstChunk.getUploaderId());
            fileRecord.setUploadTime(Instant.now());
            fileRecord.setDeleted(false);
            fileRecord.setDeleteTime(null);
            fileRecord.setFilePath("tmp/");
            fileRecord = fileRecordRepository.save(fileRecord);

            // 删除分片文件和记录
            for (FileChunkRecord chunk : chunks) {
                Path chunkPath = Paths.get(BASE_DIR, chunk.getChunkPath());
                Files.deleteIfExists(chunkPath);
            }
            fileChunkRecordRepository.deleteByUploadId(uploadId);

            return fileRecord;
        } catch (IOException e) {
            throw new RuntimeException("分片合并失败", e);
        }
    }
}