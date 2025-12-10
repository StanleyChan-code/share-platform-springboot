package cn.com.nabotix.shareplatform.dataset.repository;

import cn.com.nabotix.shareplatform.dataset.entity.Application;
import cn.com.nabotix.shareplatform.dataset.entity.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    
    /**
     * 根据申请人ID分页查询申请记录
     *
     * @param applicantId 申请人ID
     * @param pageable    分页参数
     * @return 申请记录分页结果
     */
    Page<Application> findAllByApplicantId(UUID applicantId, Pageable pageable);
    
    /**
     * 根据数据集提供者ID分页查询申请记录
     *
     * @param providerId 数据集提供者ID
     * @param pageable   分页参数
     * @return 申请记录分页结果
     */
    @Query("SELECT a FROM Application a JOIN DatasetVersion dv ON a.datasetVersionId = dv.id JOIN Dataset d ON dv.datasetId = d.id WHERE d.providerId = :providerId")
    Page<Application> findAllByProviderId(@Param("providerId") UUID providerId, Pageable pageable);
    
    /**
     * 根据机构ID和申请状态分页查询申请记录
     *
     * @param institutionId 机构ID
     * @param status        申请状态
     * @param pageable      分页参数
     * @return 申请记录分页结果
     */
    @Query("SELECT a FROM Application a JOIN DatasetVersion dv ON a.datasetVersionId = dv.id JOIN Dataset d ON dv.datasetId = d.id WHERE d.institutionId = :institutionId AND a.status = :status")
    Page<Application> findAllByInstitutionIdAndStatus(
            @Param("institutionId") UUID institutionId,
            @Param("status") ApplicationStatus status,
            Pageable pageable);
}