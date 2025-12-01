package cn.com.nabotix.shareplatform.repository;

import cn.com.nabotix.shareplatform.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, UUID> {
    // 可以在这里添加自定义查询方法
}