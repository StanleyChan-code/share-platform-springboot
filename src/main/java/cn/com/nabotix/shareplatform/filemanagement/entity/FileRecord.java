package cn.com.nabotix.shareplatform.filemanagement.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * 文件记录实体类
 * 用于存储上传文件的相关信息，包括文件路径、上传者、上传时间等
 *
 * @author 陈雍文
 */
@Data
@Entity
@Table(name = "file_records")
public class FileRecord {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

    @Column
    private String fileType;

    @Column(nullable = false)
    private UUID uploaderId;

    @Column(nullable = false)
    private Instant uploadTime;

    @Column(nullable = false)
    private Boolean deleted = false;

    @Column
    private Instant deleteTime;

    // Constructors
    public FileRecord() {}

    public FileRecord(UUID id, String fileName, String filePath, Long fileSize, String fileType, UUID uploaderId) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.uploaderId = uploaderId;
        this.uploadTime = Instant.now();
        this.deleted = false;
    }
}