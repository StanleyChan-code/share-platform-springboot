package cn.com.nabotix.shareplatform.dataset.repository;

import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    List<Dataset> findByProviderId(UUID providerId);

    List<Dataset> findByParentDatasetId(UUID parentDatasetId);

    /**
     * 查询公开可见且没有父数据集的数据集（已批准且已发布的顶层数据集）- 分页版本
     * @param pageable 分页参数
     * @return 公开可见的顶层数据集分页结果
     */
    @Query("SELECT d FROM Dataset d WHERE d.approved = true AND d.published = true AND d.parentDatasetId IS NULL")
    Page<Dataset> findPublicVisibleTopLevelDatasets(Pageable pageable);

    /**
     * 查询公开可见或者指定机构内可见且没有父数据集的数据集（已批准的顶层数据集）- 分页版本
     * @param institutionId 机构ID
     * @param pageable 分页参数
     * @return 公开可见或机构内可见的顶层数据集分页结果
     */
    @Query("SELECT d FROM Dataset d WHERE d.approved = true AND d.parentDatasetId IS NULL AND " +
           "(d.published = true OR (d.published = false AND d.institutionId = :institutionId))")
    Page<Dataset> findPublicOrInstitutionVisibleTopLevelDatasets(@Param("institutionId") UUID institutionId, Pageable pageable);

}