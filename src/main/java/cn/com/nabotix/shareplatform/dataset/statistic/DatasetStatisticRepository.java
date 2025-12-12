package cn.com.nabotix.shareplatform.dataset.statistic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DatasetStatisticRepository extends JpaRepository<DatasetStatistic, UUID> {
    /**
     * 根据数据集版本ID查找统计信息
     * @param datasetVersionId 数据集版本ID
     * @return 数据集统计信息
     */
    Optional<DatasetStatistic> findByDatasetVersionId(UUID datasetVersionId);
}