package cn.com.nabotix.shareplatform.researchoutput.dto;

import lombok.Data;

/**
 * 研究成果审批请求数据传输对象
 * 用于处理研究成果的审批流程相关数据
 *
 * @author 陈雍文
 */
@Data
public class ResearchOutputApprovalRequestDto {
    /**
     * 审批状态
     * true表示通过，false表示拒绝
     */
    private Boolean approved;
    
    /**
     * 拒绝原因
     * 当approved为false时，需要填写拒绝的具体原因
     */
    private String rejectionReason;
}