package cn.com.nabotix.shareplatform.dataset.dto;

import lombok.Data;

/**
 * 数据集审核状态更新请求DTO
 * 用于平台管理员或机构管理员更新数据集的审核状态
 *
 * @author 陈雍文
 */
@Data
public class DatasetApprovalRequestDto {
    /**
     * 审核状态(true:通过, false:驳回)
     */
    private Boolean approved;
    
    /**
     * 驳回原因（当approved为false时使用）
     */
    private String rejectionReason;
}