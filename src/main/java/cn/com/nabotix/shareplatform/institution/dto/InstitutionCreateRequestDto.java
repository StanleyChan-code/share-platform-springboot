package cn.com.nabotix.shareplatform.institution.dto;

import cn.com.nabotix.shareplatform.enums.IdType;
import cn.com.nabotix.shareplatform.enums.InstitutionType;
import lombok.Data;

/**
 * 机构创建请求DTO类
 * 用于接收前端传来的机构创建请求数据
 *
 * @author 陈雍文
 */
@Data
public class InstitutionCreateRequestDto {
    private String fullName;
    private String shortName;
    private InstitutionType type;
    private String contactPerson;
    private IdType contactIdType;
    private String contactIdNumber;
    private String contactPhone;
    private String contactEmail;
}