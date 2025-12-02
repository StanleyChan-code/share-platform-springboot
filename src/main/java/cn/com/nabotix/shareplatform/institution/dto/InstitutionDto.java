package cn.com.nabotix.shareplatform.institution.dto;

import cn.com.nabotix.shareplatform.enums.IdType;
import cn.com.nabotix.shareplatform.enums.InstitutionType;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * 机构信息DTO类
 * 用于向前端返回机构信息
 *
 * @author 陈雍文
 */
@Data
public class InstitutionDto {
    private UUID id;
    private String fullName;
    private String shortName;
    private InstitutionType type;
    private String contactPerson;
    private IdType contactIdType;
    private String contactIdNumber;
    private String contactPhone;
    private String contactEmail;
    private Boolean verified = false;
    private Instant createdAt;
    private Instant updatedAt;
}