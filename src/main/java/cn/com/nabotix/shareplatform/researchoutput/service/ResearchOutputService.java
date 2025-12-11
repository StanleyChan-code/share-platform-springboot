package cn.com.nabotix.shareplatform.researchoutput.service;

import cn.com.nabotix.shareplatform.dataset.dto.PublicDatasetDto;
import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import cn.com.nabotix.shareplatform.dataset.service.DatasetService;
import cn.com.nabotix.shareplatform.filemanagement.service.FileManagementService;
import cn.com.nabotix.shareplatform.researchoutput.entity.ResearchOutput;
import cn.com.nabotix.shareplatform.researchoutput.repository.ResearchOutputRepository;
import cn.com.nabotix.shareplatform.researchoutput.dto.ResearchOutputDto;
import cn.com.nabotix.shareplatform.user.dto.UserDto;
import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * 研究成果服务类
 * 
 * 提供研究成果的增删改查、审批、转换等业务逻辑处理
 * 
 * @author 陈雍文
 */
@Slf4j
@Service
public class ResearchOutputService {

    private final ResearchOutputRepository researchOutputRepository;
    private final UserService userService;
    private final DatasetService datasetService;
    private final FileManagementService fileManagementService;

    @Autowired
    public ResearchOutputService(ResearchOutputRepository researchOutputRepository, UserService userService, DatasetService datasetService, FileManagementService fileManagementService) {
        this.researchOutputRepository = researchOutputRepository;
        this.userService = userService;
        this.datasetService = datasetService;
        this.fileManagementService = fileManagementService;
    }

    public Page<ResearchOutput> getAllResearchOutputs(Pageable pageable) {
        return researchOutputRepository.findAll(pageable);
    }

    public Page<ResearchOutput> getAllResearchOutputs(Boolean approved, Pageable pageable) {
        // 直接在数据库中查询这些用户提交的所有研究成果
        return researchOutputRepository.findByApproved(approved, pageable);
    }

    public Page<ResearchOutput> getResearchOutputsByInstitutionId(UUID institutionId, Pageable pageable) {
        // 直接在数据库中查询这些用户提交的所有研究成果
        return researchOutputRepository.findByInstitutionId(institutionId, pageable);
    }


    public Page<ResearchOutput> getResearchOutputsByInstitutionId(UUID institutionId, Boolean approved, Pageable pageable) {
        // 直接在数据库中查询这些用户提交的所有研究成果
        return researchOutputRepository.findByInstitutionIdWithApproved(institutionId, approved, pageable);
    }

    public Page<ResearchOutput> getResearchOutputsBySubmitterId(UUID submitterId, Pageable pageable) {
        return researchOutputRepository.findBySubmitterId(submitterId, pageable);
    }

    public Page<ResearchOutput> getResearchOutputsBySubmitterIdWithApprovedStatus(UUID submitterId, Boolean approved, Pageable pageable) {
        return researchOutputRepository.findBySubmitterIdAndApproved(submitterId, approved, pageable);
    }

    /**
     * 获取研究成果所属机构ID
     *
     * @param output 研究成果
     * @return 所属机构ID
     */
    public UUID getInstitutionIdByResearchOutput(ResearchOutput output) {
        User submitter = userService.getUserByUserId(output.getSubmitterId());
        return submitter != null ? submitter.getInstitutionId() : null;
    }

    public ResearchOutput getResearchOutputById(UUID id) {
        return researchOutputRepository.findById(id).orElse(null);
    }

    public ResearchOutput createResearchOutput(ResearchOutput researchOutput) {
        researchOutput.setCreatedAt(Instant.now());
        
        // 保存研究成果以获取ID
        ResearchOutput savedResearchOutput = researchOutputRepository.save(researchOutput);
        
        // 如果有文件，将其从临时目录移动到正式目录
        if (savedResearchOutput.getFileId() != null) {
            try {
                String basePath = "research-output/" + savedResearchOutput.getId() + "/";
                fileManagementService.moveFileToDirectory(savedResearchOutput.getFileId(), basePath);
            } catch (Exception e) {
                log.error("Failed to move research output file: {}", e.getMessage());
            }
        }
        
        return savedResearchOutput;
    }

    public ResearchOutput updateResearchOutputApprovalStatus(UUID id, UUID approverId, Boolean approved, String rejectionReason) {
        ResearchOutput researchOutput = getResearchOutputById(id);
        if (researchOutput == null) {
            return null;
        }

        // 检查是否处于待审核状态
        if (researchOutput.getApproved() != null) {
            throw new IllegalStateException("该研究成果已审核，不能重复审核");
        }

        researchOutput.setApproved(approved);
        researchOutput.setApprovedBy(approverId);
        researchOutput.setApprovedAt(Instant.now());
        researchOutput.setRejectionReason(rejectionReason);

        return researchOutputRepository.save(researchOutput);
    }

    public ResearchOutputDto convertToDto(ResearchOutput researchOutput) {
        if (researchOutput == null) {
            return null;
        }

        Dataset dataset = datasetService.getDatasetById(researchOutput.getDatasetId());
        PublicDatasetDto datasetDto = datasetService.convertToPublicDto(dataset);

        User submitter = userService.getUserByUserId(researchOutput.getSubmitterId());
        UserDto submitterDto = UserDto.fromEntity(submitter);

        User approver = userService.getUserByUserId(researchOutput.getApprovedBy());
        UserDto approverDto = UserDto.fromEntity(approver);

        return ResearchOutputDto.fromEntity(researchOutput, datasetDto, submitterDto, approverDto);
    }

}