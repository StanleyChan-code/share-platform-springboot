package cn.com.nabotix.shareplatform.dataset.repository;

import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, UUID> {
    List<Dataset> findByInstitutionId(UUID institutionId);
    
    /**
     * 根据提供者ID查询数据集
     * @param providerId 提供者ID
     * @return 该提供者上传的数据集列表
     */
    List<Dataset> findByProviderId(UUID providerId);
    
    /**
     * 查询公开可见的数据集（已批准且已发布的数据集）
     * @return 公开可见的数据集列表
     */
    @Query("SELECT d FROM Dataset d WHERE d.approved = true AND d.published = true")
    List<Dataset> findPublicVisibleDatasets();
    
    /**
     * 根据ID查询公开可见的数据集（已批准且已发布的数据集）
     * @param id 数据集ID
     * @return 公开可见的数据集
     */
    @Query("SELECT d FROM Dataset d WHERE d.id = :id AND d.approved = true AND d.published = true")
    Optional<Dataset> findPublicVisibleById(@Param("id") UUID id);
    
    /**
     * 根据机构ID查询该机构内可见的数据集（已批准的数据集）
     * @param institutionId 机构ID
     * @return 该机构内可见的数据集列表
     */
    @Query("SELECT d FROM Dataset d WHERE d.approved = true AND d.institutionId = :institutionId")
    List<Dataset> findInstitutionVisibleByInstitutionId(@Param("institutionId") UUID institutionId);
    
    /**
     * 根据ID和机构ID查询数据集（用于机构内用户访问检查）
     * @param id 数据集ID
     * @param institutionId 机构ID
     * @return 数据集
     */
    @Query("SELECT d FROM Dataset d WHERE d.id = :id AND d.institutionId = :institutionId AND d.approved = true")
    Optional<Dataset> findApprovedByIdAndInstitutionId(@Param("id") UUID id, @Param("institutionId") UUID institutionId);
}