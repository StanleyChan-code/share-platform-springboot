package cn.com.nabotix.shareplatform.filemanagement.repository;

import cn.com.nabotix.shareplatform.filemanagement.entity.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 文件记录仓储接口
 * 提供文件记录的数据库操作方法
 *
 * @author 陈雍文
 */
@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, UUID> {
    List<FileRecord> findByDeletedFalse();
}