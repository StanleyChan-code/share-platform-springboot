package cn.com.nabotix.shareplatform.dataset.dto;

import lombok.Data;

/**
 * 数据集申请审核请求DTO
 */
@Data
public class ApplicationReviewRequestDto {
    /**
     * 审核意见
     */
    private String notes;

    /**
     * 是否批准申请
     */
    private Boolean approved;
}