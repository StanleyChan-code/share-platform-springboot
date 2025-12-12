package cn.com.nabotix.shareplatform.common.entry;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * 审计日志实体类
 * 用于记录系统操作日志信息
 *
 * @author 陈雍文
 */
@Data
@Entity
@Table(name = "audit_log")
public class AuditLog {
    /**
     * 日志唯一标识符
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * 实例ID，关联到具体业务实例
     */
    @Column(name = "instance_id")
    private UUID instanceId;

    /**
     * 日志负载数据，存储JSON格式的详细信息
     */
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Payload payload;

    /**
     * 日志创建时间
     */
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * 请求来源IP地址
     */
    @Column(name = "ip_address", nullable = false, length = 64)
    private String ipAddress = "";


    /**
     * 审计日志载荷数据类
     */
    @Data
    public static class Payload {
        /**
         * 操作行为
         */
        private String action;

        /**
         * 资源ID
         */
        private UUID resourceId;

        /**
         * 资源标题
         */
        private String resourceTitle;

        /**
         * 额外参数
         */
        private Map<String, Object> additionalParams = Map.of();
    }
}