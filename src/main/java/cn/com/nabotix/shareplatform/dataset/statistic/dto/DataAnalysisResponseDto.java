package cn.com.nabotix.shareplatform.dataset.statistic.dto;

import lombok.Data;

import java.util.List;

/**
 * 数据分析服务响应DTO
 * 用于接收数据分析服务返回的结果
 */
@Data
public class DataAnalysisResponseDto {
    /**
     * 变量分类信息
     */
    private List<VariableInfoDto> variables;

    /**
     * 协议版本
     */
    private String version;

    /**
     * 统计文件内容（CSV格式的字符串列表）
     */
    private List<String> statisticalFiles;
}