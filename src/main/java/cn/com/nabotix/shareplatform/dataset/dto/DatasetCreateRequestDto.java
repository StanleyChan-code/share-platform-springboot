package cn.com.nabotix.shareplatform.dataset.dto;

import cn.com.nabotix.shareplatform.dataset.entity.DatasetType;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * 数据集创建请求DTO
 * 用于创建新数据集的请求参数
 *
 * @author 陈雍文
 */
@Data
public class DatasetCreateRequestDto {
    /**
     * 数据集标题
     */
    private String titleCn;

    /**
     * 数据集描述
     */
    private String description;

    /**
     * 研究类型
     */
    private DatasetType type;

    /**
     * 数据集上传提供者ID
     */
    private UUID providerId;

    /**
     * 数据集负责人
     */
    private String datasetLeader;

    /**
     * 首席研究员
     */
    private String principalInvestigator;

    /**
     * 数据收集单位
     */
    private String dataCollectionUnit;

    /**
     * 研究开始日期
     */
    private Instant startDate;

    /**
     * 研究结束日期
     */
    private Instant endDate;

    /**
     * 记录数量
     */
    private Integer recordCount;

    /**
     * 变量数量
     */
    private Integer variableCount;

    /**
     * 关键词数组
     */
    private String[] keywords;

    /**
     * 学科领域ID(关联学科领域表)
     */
    private UUID subjectAreaId;

    /**
     * 学科领域文本（可以与关联的学科领域表表述不同）
     */
    private String category;

    /**
     * 抽样方法描述
     */
    private String samplingMethod;

    /**
     * 对外发布状态(true:已发布, false:未发布)
     */
    private Boolean published = false;

    /**
     * 是否共享所有数据(true:共享全部, false:部分共享)
     */
    private Boolean shareAllData = false;

    /**
     * 联系人姓名
     */
    private String contactPerson;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 人口统计学字段信息(JSON格式存储)
     */
    private String demographicFields;

    /**
     * 结果字段信息(JSON格式存储)
     */
    private String outcomeFields;

    /**
     * 父数据集ID(用于随访关联基线数据集)
     */
    private UUID parentDatasetId;

    /**
     * 所属机构ID
     */
    private UUID institutionId;

    /**
     * 申请机构ID列表
     * null表示允许所有人申请
     * []表示不允许任何人申请
     * [UUID1, UUID2, ...]表示只允许这些机构的人申请
     */
    private UUID[] applicationInstitutionIds;

    /**
     * 版本号
     */
    private String versionNumber;

    /**
     * 数据文件记录ID
     */
    private UUID fileRecordId;

    /**
     * 数据字典文件记录ID
     */
    private UUID dataDictRecordId;

    /**
     * 条款协议文件记录ID
     */
    private UUID termsAgreementRecordId;

    /**
     * 数据集版本描述
     */
    private String versionDescription;

}