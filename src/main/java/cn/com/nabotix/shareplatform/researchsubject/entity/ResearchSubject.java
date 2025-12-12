package cn.com.nabotix.shareplatform.researchsubject.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * 研究主题实体类
 * 用于存储研究主题的基本信息
 *
 * @author 陈雍文
 */
@Data
@Entity
@Table(name = "research_subjects")
public class ResearchSubject {
    /**
     * 主键ID，使用UUID生成策略
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * 研究主题名称（中文）
     * 不允许为空
     */
    @Column(nullable = false)
    private String name;

    /**
     * 研究主题名称（英文）
     * 可为空
     */
    @Column(name = "name_en")
    private String nameEn;

    /**
     * 研究主题描述信息
     * 可为空
     */
    private String description;

    /**
     * 是否激活状态
     * 默认为true（激活）
     */
    private Boolean active = true;

    /**
     * 创建时间
     * 不允许为空，默认为当前时间
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
    
    /**
     * 搜索次数统计
     * 默认为0
     */
    @Column(name = "search_count")
    private Long searchCount = 0L;
}