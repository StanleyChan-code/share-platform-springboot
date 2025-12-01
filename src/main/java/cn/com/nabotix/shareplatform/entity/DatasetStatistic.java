package cn.com.nabotix.shareplatform.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "dataset_statistics")
public class DatasetStatistic {
    @Id
    private UUID id;

    @Column(name = "dataset_id", nullable = false)
    private UUID datasetId;

    @Column(name = "variable_name", nullable = false)
    private String variableName;

    @Column(name = "variable_type", nullable = false)
    private String variableType;

    @Column(name = "mean_value")
    private Double meanValue;

    @Column(name = "std_deviation")
    private Double stdDeviation;

    private Double percentage;

    @Column(name = "missing_count")
    private Integer missingCount;

    @Column(name = "total_count")
    private Integer totalCount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}