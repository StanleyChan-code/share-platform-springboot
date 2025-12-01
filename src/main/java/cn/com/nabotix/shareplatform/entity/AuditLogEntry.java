package cn.com.nabotix.shareplatform.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "audit_log_entries")
public class AuditLogEntry {
    @Id
    private UUID id;

    @Column(name = "instance_id")
    private UUID instanceId;

    @Column(columnDefinition = "json")
    private String payload;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "ip_address", nullable = false, length = 64)
    private String ipAddress = "";
}