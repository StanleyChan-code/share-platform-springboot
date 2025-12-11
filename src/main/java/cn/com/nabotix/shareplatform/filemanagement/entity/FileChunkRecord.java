package cn.com.nabotix.shareplatform.filemanagement.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * 文件分片记录实体类
 * 用于存储大文件分片上传过程中的分片信息
 *
 * @author 陈雍文
 */
@Data
@Entity
@Table(name = "file_chunk_records")
public class FileChunkRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID uploadId;

    @Column(nullable = false)
    private Integer chunkNumber;

    @Column(nullable = false)
    private Integer totalChunks;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String chunkPath;

    @Column(nullable = false)
    private Long chunkSize;

    @Column(nullable = false)
    private UUID uploaderId;

    @Column(nullable = false)
    private Instant uploadTime;

    // Constructors
    public FileChunkRecord() {}

    public FileChunkRecord(UUID uploadId, Integer chunkNumber, Integer totalChunks, 
                          String fileName, String chunkPath, Long chunkSize, UUID uploaderId) {
        this.uploadId = uploadId;
        this.chunkNumber = chunkNumber;
        this.totalChunks = totalChunks;
        this.fileName = fileName;
        this.chunkPath = chunkPath;
        this.chunkSize = chunkSize;
        this.uploaderId = uploaderId;
        this.uploadTime = Instant.now();
    }
}