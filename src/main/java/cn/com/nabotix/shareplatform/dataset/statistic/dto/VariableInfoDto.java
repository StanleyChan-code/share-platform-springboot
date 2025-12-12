package cn.com.nabotix.shareplatform.dataset.statistic.dto;

import lombok.Data;

import java.util.List;

@Data
public class VariableInfoDto {
    private String type;
    private List<String> variables;
    private Integer fileIndex;
}
