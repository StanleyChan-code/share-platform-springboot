package cn.com.nabotix.shareplatform.dataset.entity;

import lombok.Data;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * 数据集实体类
 *
 * @author 陈雍文
 */
@Data
@Entity
@Table(name = "datasets")
public class Dataset {
    /**
     * 数据集唯一标识符
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * 数据集标题
     */
    @Column(name = "title_cn", nullable = false)
    private String titleCn;

    /**
     * 数据集描述
     */
    @Column(nullable = false)
    private String description;

    /**
     * 研究类型
     */
    @Enumerated(EnumType.STRING)
    private DatasetType type;

    /**
     * 数据集上传提供者ID
     */
    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    /**
     * 数据集负责人
     */
    @Column(name = "dataset_leader")
    private String datasetLeader;

    /**
     * 首席研究员
     */
    @Column(name = "principal_investigator")
    private String principalInvestigator;

    /**
     * 数据收集单位
     */
    @Column(name = "data_collection_unit")
    private String dataCollectionUnit;

    /**
     * 研究开始日期
     */
    @Column(name = "start_date")
    private Instant startDate;

    /**
     * 研究结束日期
     */
    @Column(name = "end_date")
    private Instant endDate;

    /**
     * 记录数量
     */
    @Column(name = "record_count")
    private Integer recordCount;

    /**
     * 变量数量
     */
    @Column(name = "variable_count")
    private Integer variableCount;

    /**
     * 关键词数组
     */
    @Column(name = "keywords", columnDefinition = "text[]")
    private String[] keywords;

    /**
     * 学科领域ID(关联学科领域表)
     */
    @Column(name = "subject_area_id")
    private UUID subjectAreaId;

    /**
     * 学科领域文本（可以与关联的学科领域表表述不同）
     */
    private String category;

    /**
     * 抽样方法描述
     */
    @Column(name = "sampling_method")
    private String samplingMethod;

    /**
     * 是否允许公开查看
     */
    private Boolean published = false;

    /**
     * 查看次数统计
     */
    @Column(name = "search_count")
    private Integer searchCount = 0;

    /**
     * 是否共享所有数据(true:共享全部, false:部分共享)
     */
    @Column(name = "share_all_data")
    private Boolean shareAllData = false;

    /**
     * 联系人姓名
     */
    @Column(name = "contact_person")
    private String contactPerson;

    /**
     * 联系方式
     */
    @Column(name = "contact_info")
    private String contactInfo;

    /**
     * 人口统计学字段信息(JSON格式存储)
     */
    @Column(name = "demographic_fields", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String demographicFields;

    /**
     * 结果字段信息(JSON格式存储)
     */
    @Column(name = "outcome_fields", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String outcomeFields;

    /**
     * 首次发布时间
     */
    @Column(name = "first_published_date")
    private Instant firstPublishedDate;

    /**
     * 当前版本日期
     */
    @Column(name = "current_version_date")
    private Instant currentVersionDate;

    /**
     * 父数据集ID(用于随访关联基线数据集)
     */
    @Column(name = "parent_dataset_id")
    private UUID parentDatasetId;

    /**
     * 所属机构ID
     */
    @Column(name = "institution_id")
    private UUID institutionId;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * 申请机构ID列表
     * null表示允许所有人申请
     * []表示不允许任何人申请
     * [UUID1, UUID2, ...]表示只允许这些机构的人申请
     */
    @Column(name = "application_institution_ids", columnDefinition = "uuid[]")
    private UUID[] applicationInstitutionIds;

}