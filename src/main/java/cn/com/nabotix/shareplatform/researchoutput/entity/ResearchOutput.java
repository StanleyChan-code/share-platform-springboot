package cn.com.nabotix.shareplatform.researchoutput.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * 科研成果实体类
 * 用于存储和管理科研成果信息
 *
 * @author 陈雍文
 */
@Data
@Entity
@Table(name = "research_outputs")
public class ResearchOutput {
    /**
     * 科研成果唯一标识符
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * 数据集ID，关联到对应的数据集
     */
    @Column(name = "dataset_id", nullable = false)
    private UUID datasetId;

    /**
     * 提交者ID，标识该科研成果的提交用户
     */
    @Column(name = "submitter_id", nullable = false)
    private UUID submitterId;

    /**
     * 成果类型，使用枚举类型定义
     */
    @Enumerated(EnumType.STRING)
    private OutputType type;

    /**
     * 其他类型说明，当type为OTHER时的具体描述
     */
    @Column(name = "other_type")
    private String otherType;

    /**
     * 科研成果标题，不能为空
     */
    @Column(nullable = false)
    private String title;

    /**
     * 科研成果摘要
     */
    private String abstractText;

    /**
     * 成果编号
     */
    @Column(name = "output_number")
    private String outputNumber;

    /**
     * 引用次数，默认为0
     */
    @Column(name = "citation_count")
    private Integer citationCount = 0;

    /**
     * 发表链接或URL地址
     */
    @Column(name = "publication_url")
    private String publicationUrl;

    /**
     * 文件ID，关联到对应的文件存储
     */
    @Column(name = "file_id")
    private UUID fileId;

    /**
     * 创建时间，不能为空
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /**
     * 审核状态，null表示待审核，true表示已审核通过，false表示审核未通过
     */
    private Boolean approved;

    /**
     * 审核人ID，标识审核该科研成果的用户
     */
    @Column(name = "approved_by")
    private UUID approvedBy;

    /**
     * 审核时间
     */
    @Column(name = "approved_at")
    private Instant approvedAt;

    /**
     * 拒绝原因，当审核不通过时填写
     */
    @Column(name = "rejection_reason")
    private String rejectionReason;

    /**
     * 其他信息，以JSON格式存储扩展字段
     */
    @Column(name = "other_info", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> otherInfo;
}