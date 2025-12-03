package cn.com.nabotix.shareplatform.researchsubject.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "research_subjects")
public class ResearchSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "name_en")
    private String nameEn;

    private String description;

    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}