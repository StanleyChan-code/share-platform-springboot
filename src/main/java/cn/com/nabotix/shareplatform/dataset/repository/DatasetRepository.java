package cn.com.nabotix.shareplatform.dataset.repository;

import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 数据集仓库接口
 * 提供数据集的CRUD操作以及特定查询方法
 * 继承自JpaRepository，支持基本的增删改查功能
 * 
 * @author 陈雍文
 */
@Repository
public interface DatasetRepository extends JpaRepository<Dataset, UUID> {
    Page<Dataset> findByInstitutionId(UUID institutionId, Pageable pageable);

    Page<Dataset> findByProviderId(UUID providerId, Pageable pageable);

    Page<Dataset> findBySubjectAreaId(UUID subjectAreaId, Pageable pageable);

    List<Dataset> findByParentDatasetId(UUID parentDatasetId);

    /**
     * 查询公开可见且没有父数据集的数据集（已批准且已发布的顶层数据集）- 分页版本
     *
     * @param pageable 分页参数
     * @return 公开可见的顶层数据集分页结果
     */
    @Query("SELECT d FROM Dataset d WHERE d.published = true AND d.parentDatasetId IS NULL AND " +
            "EXISTS (SELECT 1 FROM DatasetVersion dv WHERE dv.datasetId = d.id AND dv.approved = true)")
    Page<Dataset> findPublicVisibleTopLevelDatasets(Pageable pageable);

    /**
     * 查询公开可见或者指定机构内可见且没有父数据集的数据集（已批准的顶层数据集）- 分页版本
     *
     * @param institutionId 机构ID
     * @param pageable      分页参数
     * @return 公开可见或指定机构可见的顶层数据集分页结果
     */
    @Query("SELECT d FROM Dataset d WHERE " +
            "d.parentDatasetId IS NULL AND " +
            "(d.published = true OR (d.published = false AND :institutionId IN (d.applicationInstitutionIds))) AND " +
            "EXISTS (SELECT 1 FROM DatasetVersion dv WHERE dv.datasetId = d.id AND dv.approved = true)")
    Page<Dataset> findPublicOrInstitutionVisibleTopLevelDatasets(@Param("institutionId") UUID institutionId, Pageable pageable);

    /**
     * 查询公开可见且没有父数据集的数据集（已批准且已发布的顶层数据集） - 按照研究学科筛选 - 分页版本
     *
     * @param subjectAreaId 研究学科ID
     * @param pageable 分页参数
     * @return 公开可见的指定研究学科的顶层数据集分页结果
     */
    @Query("SELECT d FROM Dataset d WHERE d.published = true AND d.parentDatasetId IS NULL AND " +
            "d.subjectAreaId = :subjectAreaId AND " +
            "EXISTS (SELECT 1 FROM DatasetVersion dv WHERE dv.datasetId = d.id AND dv.approved = true)")
    Page<Dataset> findPublicVisibleTopLevelDatasetsBySubjectAreaId(@Param("subjectAreaId") UUID subjectAreaId, Pageable pageable);


    /**
     * 查询公开可见且没有父数据集的数据集（已批准且已发布的顶层数据集） - 按照研究学科筛选 - 分页版本
     *
     * @param subjectAreaId 研究学科ID
     * @param institutionId 机构ID
     * @param pageable 分页参数
     * @return 公开可见或指定机构可见的指定研究学科的顶层数据集分页结果
     */
    @Query("SELECT d FROM Dataset d WHERE " +
            "d.parentDatasetId IS NULL AND d.subjectAreaId = :subjectAreaId AND " +
            "(d.published = true OR (d.published = false AND :institutionId IN (d.applicationInstitutionIds))) AND " +
            "EXISTS (SELECT 1 FROM DatasetVersion dv WHERE dv.datasetId = d.id AND dv.approved = true)")
    Page<Dataset> findPublicVisibleTopLevelDatasetsBySubjectAreaId(
            @Param("subjectAreaId") UUID subjectAreaId, @Param("institutionId") UUID institutionId, Pageable pageable);

}