package cn.com.nabotix.shareplatform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "analysis_results")
public class AnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "dataset_id", nullable = false)
    private UUID datasetId;

    @Column(name = "total_rows", nullable = false)
    private Integer totalRows;

    @Column(name = "total_columns", nullable = false)
    private Integer totalColumns;

    @Column(name = "analysis_date", nullable = false)
    private Instant analysisDate;

    @Column(name = "overall_missing_rate")
    private Double overallMissingRate;

    @Column(name = "memory_usage_mb")
    private Double memoryUsageMb;

    @Column(name = "correlations", columnDefinition = "jsonb")
    private String correlations;

    @Column(name = "field_mappings", columnDefinition = "jsonb")
    private String fieldMappings;

    @Column(name = "unit_conversions", columnDefinition = "jsonb")
    private String unitConversions;

    @Column(name = "analysis_metadata", columnDefinition = "jsonb")
    private String analysisMetadata;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}