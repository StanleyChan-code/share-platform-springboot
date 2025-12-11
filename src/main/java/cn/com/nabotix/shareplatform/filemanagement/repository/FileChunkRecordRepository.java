package cn.com.nabotix.shareplatform.filemanagement.repository;

import cn.com.nabotix.shareplatform.filemanagement.entity.FileChunkRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 文件分片记录仓储接口
 * 提供文件分片记录的数据库操作方法
 *
 * @author 陈雍文
 */
@Repository
public interface FileChunkRecordRepository extends JpaRepository<FileChunkRecord, UUID> {
    List<FileChunkRecord> findByUploadIdOrderByChunkNumber(UUID uploadId);
    Optional<FileChunkRecord> findByUploadIdAndChunkNumber(UUID uploadId, Integer chunkNumber);
    void deleteByUploadId(UUID uploadId);
}