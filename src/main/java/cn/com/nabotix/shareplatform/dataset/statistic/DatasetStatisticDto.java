package cn.com.nabotix.shareplatform.dataset.statistic;

import cn.com.nabotix.shareplatform.dataset.statistic.dto.VariableInfoDto;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 数据集统计信息DTO
 * 用于接收和传输数据集统计信息
 */
@Data
public class DatasetStatisticDto {
    /**
     * 主键ID
     */
    private UUID id;

    /**
     * 关联的数据集版本ID
     */
    private UUID datasetVersionId;

    // 统计的协议版本
    private String version;

    /**
     * 变量分类信息
     */
    private List<VariableInfoDto> variables = new ArrayList<>();

    /**
     * 统计数据文件内容（原始字符串格式）
     * 对应关系：variables 中的 fileIndex 指向此数组的索引
     * 保存时会被压缩为字节数组存储
     */
    private List<String> statisticalFiles = new ArrayList<>();

    /**
     * 创建时间
     */
    private Instant createdAt;

}