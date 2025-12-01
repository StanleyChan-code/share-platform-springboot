package cn.com.nabotix.shareplatform.repository;

import cn.com.nabotix.shareplatform.entity.DatasetVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DatasetVersionRepository extends JpaRepository<DatasetVersion, UUID> {
    // 可以在这里添加自定义查询方法
}