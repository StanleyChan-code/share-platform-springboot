package cn.com.nabotix.shareplatform.dataset.repository;

import cn.com.nabotix.shareplatform.dataset.entity.DatasetVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DatasetVersionRepository extends JpaRepository<DatasetVersion, UUID> {
    /**
     * 根据数据集ID查找所有版本，按创建时间倒序排列
     * @param datasetId 数据集ID
     * @return 版本列表
     */
    List<DatasetVersion> findByDatasetIdOrderByCreatedAtDesc(UUID datasetId);
    
    /**
     * 根据数据集ID查找所有已审核版本，按创建时间倒序排列
     * @param datasetId 数据集ID
     * @return 已审核版本列表
     */
    List<DatasetVersion> findByDatasetIdAndApprovedTrueOrderByCreatedAtDesc(UUID datasetId);
}