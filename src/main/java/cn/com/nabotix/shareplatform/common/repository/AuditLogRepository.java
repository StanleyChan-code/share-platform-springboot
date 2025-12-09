package cn.com.nabotix.shareplatform.common.repository;

import cn.com.nabotix.shareplatform.common.entry.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    // 可以在这里添加自定义查询方法
}