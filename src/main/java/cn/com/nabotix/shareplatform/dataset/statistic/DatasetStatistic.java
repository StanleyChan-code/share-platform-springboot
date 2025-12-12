package cn.com.nabotix.shareplatform.dataset.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 数据集统计信息实体类
 * 用于存储数据集中各个变量的统计信息
 *
 * @author 陈雍文
 */
@Data
@Entity
@Table(name = "dataset_statistics",  uniqueConstraints = @UniqueConstraint(columnNames = {"dataset_version_id"}))
public class DatasetStatistic {
    /**
     * 主键ID，使用UUID生成策略
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * 关联的数据集版本ID
     */
    @Column(name = "dataset_version_id", nullable = false, unique = true)
    private UUID datasetVersionId;

    // 统计的协议版本
    @Column(name = "version", nullable = false)
    private String version;

    /**
     * 变量分类信息
     * 格式: [{"type":"categorical", "variables":["性别","具体情况", "fileIndex": 0]}, 
     *        {"type":"numeric", "variables":["xx程度","xx评估", "fileIndex": 1]}]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "variables", nullable = false, columnDefinition = "jsonb")
    private List<VariableInfo> variables;

    /**
     * 统计数据文件（压缩后的字节数据）
     * 对应关系：variables 中的 fileIndex 指向此数组的索引
     * 从DTO的字符串数据压缩而来，读取时需要解压
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "statistical_files", nullable = false, columnDefinition = "jsonb")
    private List<byte[]> statisticalFiles = new ArrayList<>();

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Data
    @AllArgsConstructor
    public static class VariableInfo {
        private String type;
        private List<String> variables;
        private Integer fileIndex;
    }
}