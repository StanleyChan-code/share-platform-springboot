package cn.com.nabotix.shareplatform.institution.entity;

import cn.com.nabotix.shareplatform.enums.IdType;
import cn.com.nabotix.shareplatform.enums.InstitutionType;
import lombok.Data;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * 机构实体类
 * 用于表示平台上的各类机构信息
 *
 * @author 陈雍文
 */
@Data
@Entity
@Table(name = "institutions")
public class Institution {
    @Id
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "short_name")
    private String shortName;

    @Enumerated(EnumType.STRING)
    private InstitutionType type;

    @Column(name = "contact_person", nullable = false)
    private String contactPerson;

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_id_type", nullable = false)
    private IdType contactIdType;

    @Column(name = "contact_id_number", nullable = false)
    private String contactIdNumber;

    @Column(name = "contact_phone", nullable = false)
    private String contactPhone;

    @Column(name = "contact_email", nullable = false)
    private String contactEmail;

    private Boolean verified = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}