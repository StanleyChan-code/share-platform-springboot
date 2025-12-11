package cn.com.nabotix.shareplatform.filemanagement.repository;

import cn.com.nabotix.shareplatform.filemanagement.entity.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
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
    /**
     * 查询未删除且在filePath目录下且上传时间早于指定时间的文件记录
     * @param filePath 文件路径
     * @param timeThreshold 时间阈值
     * @return 文件记录列表
     */
    @Query("SELECT fr FROM FileRecord fr WHERE fr.deleted = false AND " +
            "fr.filePath LIKE CONCAT(:filePath, '%') AND " +
            ":nowTime - fr.uploadTime > :timeThreshold")
    List<FileRecord> findOldFiles(@Param("filePath") String filePath, @Param("timeThreshold") Duration timeThreshold, @Param("nowTime") Instant compareTime);

}