package cn.com.nabotix.shareplatform.dataset.service;

import cn.com.nabotix.shareplatform.dataset.dto.ApplicationDto;
import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import cn.com.nabotix.shareplatform.dataset.entity.DatasetVersion;
import cn.com.nabotix.shareplatform.dataset.repository.DatasetRepository;
import cn.com.nabotix.shareplatform.dataset.entity.Application;
import cn.com.nabotix.shareplatform.dataset.entity.ApplicationStatus;
import cn.com.nabotix.shareplatform.filemanagement.service.FileManagementService;
import cn.com.nabotix.shareplatform.security.UserAuthority;
import cn.com.nabotix.shareplatform.dataset.repository.ApplicationRepository;
import cn.com.nabotix.shareplatform.security.AuthorityUtil;
import cn.com.nabotix.shareplatform.user.entity.User;
import cn.com.nabotix.shareplatform.user.repository.UserRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 数据集申请服务类
 *
 * @author 陈雍文
 */
@Slf4j
@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final DatasetRepository datasetRepository;
    private final UserRepository userRepository;
    private final DatasetVersionService datasetVersionService;
    private final FileManagementService fileManagementService;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository,
                              DatasetRepository datasetRepository,
                              UserRepository userRepository,
                              DatasetVersionService datasetVersionService,
                              FileManagementService fileManagementService) {
        this.applicationRepository = applicationRepository;
        this.datasetRepository = datasetRepository;
        this.userRepository = userRepository;
        this.datasetVersionService = datasetVersionService;
        this.fileManagementService = fileManagementService;
    }

    /**
     * 创建数据集申请
     *
     * @param applicationDto 申请信息
     * @param userId         申请人ID
     * @return 创建的申请
     */
    public Application createApplication(ApplicationDto applicationDto, UUID userId) {
        // 检查数据集版本是否存在
        DatasetVersion datasetVersion = datasetVersionService.getById(applicationDto.getDatasetVersionId());
        if (datasetVersion == null) {
            throw new IllegalArgumentException("数据集版本不存在");
        }

        // 检查数据集是否存在且可申请
        Dataset dataset = datasetRepository.findById(datasetVersion.getDatasetId()).orElse(null);
        if (dataset == null) {
            throw new IllegalArgumentException("数据集不存在");
        }

        // 检查用户是否有权限申请该数据集
        if (!canUserApplyForDataset(userId, dataset, datasetVersion)) {
            throw new IllegalStateException("您无权限申请该数据集");
        }

        // 创建申请记录
        Application application = new Application();
        application.setDatasetVersionId(applicationDto.getDatasetVersionId());
        application.setApplicantId(applicationDto.getApplicantId());
        application.setApplicantRole(applicationDto.getApplicantRole());
        application.setApplicantType(applicationDto.getApplicantType());
        application.setProjectTitle(applicationDto.getProjectTitle());
        application.setProjectDescription(applicationDto.getProjectDescription());
        application.setFundingSource(applicationDto.getFundingSource());
        application.setPurpose(applicationDto.getPurpose());
        application.setProjectLeader(applicationDto.getProjectLeader());
        application.setApprovalDocumentId(applicationDto.getApprovalDocumentId());
        application.setStatus(ApplicationStatus.PENDING_PROVIDER_REVIEW);
        application.setSubmittedAt(Instant.now());

        Application savedApplication = applicationRepository.save(application);
        moveApprovalDocumentFile(savedApplication);

        return savedApplication;
    }

    /**
     * 检查用户是否有权限申请指定数据集
     *
     * @param userId         用户ID
     * @param dataset        数据集
     * @param datasetVersion 数据集版本
     * @return 是否有权限申请
     */
    public boolean canUserApplyForDataset(@NonNull UUID userId, @NonNull Dataset dataset, @NonNull DatasetVersion datasetVersion) {
        // 检查数据集是否已审核通过
        if (datasetVersion.getApproved() == null || !datasetVersion.getApproved()) {
            return false;
        }

        // 获取用户信息
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }

        // 检查applicationInstitutionIds字段
        // null且数据集公开访问表示允许所有人申请
        if (dataset.getPublished() == true && dataset.getApplicationInstitutionIds() == null) {
            return true;
        }

        // []表示不允许任何人申请
        if (dataset.getApplicationInstitutionIds().isEmpty()) {
            return false;
        }

        // [UUID1, UUID2, ...]表示只允许这些机构的人申请
        if (user.getInstitutionId() != null) {
            for (UUID institutionId : dataset.getApplicationInstitutionIds()) {
                if (institutionId.equals(user.getInstitutionId())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 数据集提供者审核申请
     *
     * @param applicationId 申请ID
     * @param providerId    提供者ID
     * @param notes         审核意见
     * @param approved      是否通过
     * @return 更新后的申请
     */
    public Application reviewByProvider(UUID applicationId, UUID providerId, String notes, Boolean approved) {
        Application application = applicationRepository.findById(applicationId).orElse(null);
        if (application == null) {
            throw new IllegalArgumentException("申请记录不存在");
        }

        // 检查是否处于待数据提供者审核状态
        if (application.getStatus() != ApplicationStatus.PENDING_PROVIDER_REVIEW) {
            throw new IllegalStateException("申请状态不正确，当前申请不处于待数据提供者审核状态");
        }

        // 检查是否有权限审核
        DatasetVersion datasetVersion = datasetVersionService.getById(application.getDatasetVersionId());
        Dataset dataset = datasetRepository.findById(datasetVersion.getDatasetId()).orElse(null);
        if (dataset == null || !dataset.getProviderId().equals(providerId)) {
            throw new IllegalStateException("无权限审核该申请");
        }

        // 更新审核信息
        application.setProviderNotes(notes);
        application.setProviderReviewedAt(Instant.now());

        // 更新状态
        if (approved) {
            application.setStatus(ApplicationStatus.PENDING_INSTITUTION_REVIEW);
        } else {
            application.setStatus(ApplicationStatus.DENIED);
            application.setApprovedAt(Instant.now());
        }

        return applicationRepository.save(application);
    }

    /**
     * 申请审核员审核申请
     *
     * @param applicationId 申请ID
     * @param reviewerId    审核员ID
     * @param notes         审核意见
     * @param approved      是否通过
     * @return 更新后的申请
     */
    public Application reviewByApprover(UUID applicationId, UUID reviewerId, String notes, Boolean approved) {
        Application application = applicationRepository.findById(applicationId).orElse(null);
        if (application == null) {
            throw new IllegalArgumentException("申请记录不存在");
        }

        // 检查申请状态是否为待机构审核
        if (application.getStatus() != ApplicationStatus.PENDING_INSTITUTION_REVIEW) {
            throw new IllegalStateException("申请状态不正确，当前申请不处于待机构审核状态");
        }

        // 检查是否有权限审核
        DatasetVersion datasetVersion = datasetVersionService.getById(application.getDatasetVersionId());
        Dataset dataset = datasetRepository.findById(datasetVersion.getDatasetId()).orElse(null);
        User reviewer = userRepository.findById(reviewerId).orElse(null);
        if (dataset == null || reviewer == null) {
            throw new IllegalStateException("数据异常");
        }

        // 检查审核员权限（平台管理员、机构管理员或数据集申请审核员）
        AuthorityUtil.checkBuilder()
                .withAllowedAuthorities(UserAuthority.PLATFORM_ADMIN, UserAuthority.INSTITUTION_SUPERVISOR, UserAuthority.DATASET_APPROVER)
                .whenCheckFalse(() -> {
                    throw new IllegalStateException("无权限审核该申请");
                })
                .whenCheckTrue(() -> {
                    // 更新审核信息
                    application.setAdminNotes(notes);
                    application.setSupervisorId(reviewerId);
                    application.setInstitutionReviewedAt(Instant.now());

                    // 更新状态
                    if (approved) {
                        application.setStatus(ApplicationStatus.APPROVED);
                        application.setApprovedAt(Instant.now());
                    } else {
                        application.setStatus(ApplicationStatus.DENIED);
                    }
                })
                .execute();

        return applicationRepository.save(application);
    }

    /**
     * 根据ID获取申请详情
     *
     * @param id 申请ID
     * @return 申请详情
     */
    public ApplicationDto getApplicationDtoById(UUID id) {
        Application application = applicationRepository.findById(id).orElse(null);
        if (application == null) {
            return null;
        }

        return enrichApplicationDto(application);
    }

    /**
     * 获取申请者自己的申请记录列表（分页）
     *
     * @param applicantId 申请人ID
     * @param pageable    分页参数
     * @return 申请记录列表
     */
    public Page<ApplicationDto> getApplicationsByApplicantId(UUID applicantId, Pageable pageable) {
        Page<Application> applicationPage = applicationRepository.findAllByApplicantId(applicantId, pageable);
        List<ApplicationDto> dtoList = applicationPage.getContent().stream()
                .map(this::enrichApplicationDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, applicationPage.getTotalElements());
    }

    /**
     * 获取数据集提供者的申请记录列表（分页）
     *
     * @param providerId 数据集提供者ID
     * @param pageable   分页参数
     * @return 申请记录列表
     */
    public Page<ApplicationDto> getApplicationsByProviderId(UUID providerId, Pageable pageable) {
        Page<Application> applicationPage = applicationRepository.findAllByProviderId(providerId, pageable);
        List<ApplicationDto> dtoList = applicationPage.getContent().stream()
                .map(this::enrichApplicationDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, applicationPage.getTotalElements());
    }

    /**
     * 获取指定状态的申请记录列表（供审核员查看）
     *
     * @param institutionId 机构ID
     * @param status        申请状态
     * @param pageable      分页参数
     * @return 申请记录列表
     */
    public Page<ApplicationDto> getApplicationsByInstitutionIdAndStatus(
            UUID institutionId, ApplicationStatus status, Pageable pageable) {
        Page<Application> applicationPage = applicationRepository
                .findAllByInstitutionIdAndStatus(institutionId, status, pageable);
        List<ApplicationDto> dtoList = applicationPage.getContent().stream()
                .map(this::enrichApplicationDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, applicationPage.getTotalElements());
    }

    /**
     * 补充申请DTO的额外信息
     *
     * @param application 申请实体
     * @return 补充后的申请DTO
     */
    private ApplicationDto enrichApplicationDto(Application application) {
        ApplicationDto dto = ApplicationDto.fromEntity(application);

        // 补充数据集标题
        DatasetVersion datasetVersion = datasetVersionService.getById(application.getDatasetVersionId());
        datasetRepository.findById(datasetVersion.getDatasetId()).ifPresent(dataset -> dto.setDatasetTitle(dataset.getTitleCn()));

        // 补充申请人姓名
        userRepository.findById(application.getApplicantId()).ifPresent(applicant -> dto.setApplicantName(applicant.getRealName()));

        // 补充监督人姓名
        if (application.getSupervisorId() != null) {
            userRepository.findById(application.getSupervisorId()).ifPresent(supervisor -> dto.setSupervisorName(supervisor.getRealName()));
        }

        return dto;
    }

    /**
     * 将申请相关的审批文件移动到指定目录
     *
     * @param application 申请实体
     */
    private void moveApprovalDocumentFile(Application application) {
        // 构建申请文件存储路径
        String basePath = "application/" + application.getId() + "/";

        // 移动审批文件
        if (application.getApprovalDocumentId() != null) {
            try {
                fileManagementService.moveFileToDirectory(application.getApprovalDocumentId(), basePath);
            } catch (Exception e) {
                // 记录日志，但不中断操作
                log.error("Failed to move approval document file: {}", e.getMessage());
            }
        }
    }
    
    /**
     * 检查用户是否对指定数据集版本有访问权限（即有审批通过的申请记录）
     * 
     * @param datasetVersionId 数据集版本ID
     * @param userId 用户ID
     * @return 是否有访问权限
     */
    public boolean checkUserAccessToDatasetVersion(UUID datasetVersionId, UUID userId) {
        // 查找用户对该数据集版本的申请记录，状态为已批准
        return applicationRepository.existsByDatasetVersionIdAndApplicantIdAndStatus(
                datasetVersionId, userId, ApplicationStatus.APPROVED);
    }
}