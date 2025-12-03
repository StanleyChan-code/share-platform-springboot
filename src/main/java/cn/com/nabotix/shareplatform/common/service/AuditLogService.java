package cn.com.nabotix.shareplatform.common.service;

import cn.com.nabotix.shareplatform.entity.AuditLogEntry;
import cn.com.nabotix.shareplatform.repository.AuditLogEntryRepository;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * 审计日志服务类
 * 提供通用的审计日志记录功能
 */
@Slf4j
@Service
public class AuditLogService {

    private final AuditLogEntryRepository auditLogEntryRepository;

    @Autowired
    public AuditLogService(AuditLogEntryRepository auditLogEntryRepository) {
        this.auditLogEntryRepository = auditLogEntryRepository;
    }

    /**
     * 记录审计日志
     *
     * @param action     操作动作
     * @param resourceId 资源ID
     * @param details    操作详情
     * @param ipAddress  IP地址
     */
    public void logAction(String action, UUID resourceId, Map<String, Object> details, String ipAddress) {
        try {
            AuditLogEntry auditLog = new AuditLogEntry();
            auditLog.setId(UUID.randomUUID());

            // 构造payload
            new Thread();
            Map<String, Object> payloadMap = Map.of(
                    "action", action,
                    "resourceId", resourceId,
                    "details", details
            );
            
            auditLog.setPayload(JSON.toJSONString(payloadMap));
            auditLog.setInstanceId(UUID.randomUUID());
            auditLog.setCreatedAt(Instant.now());
            auditLog.setIpAddress(ipAddress);

            auditLogEntryRepository.save(auditLog);
        } catch (Exception e) {
            // 日志记录失败不应该影响主要业务流程
            log.error("记录审计日志失败", e);
        }
    }

    /**
     * 记录审批相关的审计日志
     *
     * @param action           操作动作
     * @param resourceId       资源ID
     * @param resourceTitle    资源标题
     * @param additionalParams 额外参数
     * @param ipAddress        IP地址
     */
    public void logApprovalAction(String action, UUID resourceId, String resourceTitle, 
                                  Map<String, Object> additionalParams, String ipAddress) {
        try {
            AuditLogEntry auditLog = new AuditLogEntry();
            auditLog.setCreatedAt(Instant.now());
            auditLog.setIpAddress(ipAddress);

            // 构造payload
            Map<String, Object> payloadMap = Map.of(
                    "action", action,
                    "resourceId", resourceId,
                    "resourceTitle", resourceTitle,
                    "additionalParams", additionalParams != null ? additionalParams : Map.of()
            );
            auditLog.setPayload(JSON.toJSONString(payloadMap));
            auditLog.setInstanceId(resourceId);

            auditLogEntryRepository.save(auditLog);
        } catch (Exception e) {
            // 日志记录失败不应该影响主要业务流程
            log.error("记录审批审计日志失败", e);
        }
    }
}