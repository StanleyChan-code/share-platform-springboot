package cn.com.nabotix.shareplatform.analysisresult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, UUID> {
    // 可以在这里添加自定义查询方法
}