package cn.com.nabotix.shareplatform.dataset.entity;

import lombok.Data;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * 数据集版本实体类
 * 用于存储数据集的历史版本信息
 *
 * @author 陈雍文
 */
@Data
@Entity
@Table(name = "dataset_versions")
public class DatasetVersion {
    /**
     * 版本唯一标识符
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * 关联的数据集ID
     */
    @Column(name = "dataset_id", nullable = false)
    private UUID datasetId;

    /**
     * 版本号
     */
    @Column(name = "version_number", nullable = false)
    private String versionNumber;

    /**
     * 版本创建时间
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /**
     * 版本发布日期
     */
    @Column(name = "published_date")
    private Instant publishedDate;

    /**
     * 版本描述
     */
    @Column(name = "description")
    private String description;

    /**
     * 数据文件记录ID
     */
    @Column(name = "file_record_id")
    private UUID fileRecordId;

    /**
     * 数据字典文件记录ID
     */
    @Column(name = "data_dict_record_id")
    private UUID dataDictRecordId;

    /**
     * 条款协议文件记录ID
     */
    @Column(name = "terms_agreement_record_id")
    private UUID termsAgreementRecordId;

    /**
     * 审核状态(true:通过, false:拒绝, null:未审核)
     */
    private Boolean approved;

    /**
     * 审批时间
     */
    @Column(name = "approved_at")
    private Instant approvedAt;

    /**
     * 拒绝理由
     */
    @Column(name = "reject_reason")
    private String rejectReason;

    /**
     * 数据集审批者ID
     */
    @Column(name = "supervisor_id")
    private UUID supervisorId;
}