package cn.com.nabotix.shareplatform.repository;

import cn.com.nabotix.shareplatform.entity.DatasetStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DatasetStatisticRepository extends JpaRepository<DatasetStatistic, UUID> {
    // 可以在这里添加自定义查询方法
}