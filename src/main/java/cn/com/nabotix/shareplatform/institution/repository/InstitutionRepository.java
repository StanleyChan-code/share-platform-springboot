package cn.com.nabotix.shareplatform.institution.repository;

import cn.com.nabotix.shareplatform.institution.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * 机构数据访问仓库接口
 * 提供对机构实体的数据库操作方法
 *
 * @author 陈雍文
 */
@Repository
public interface InstitutionRepository extends JpaRepository<Institution, UUID> {
    // 可以在这里添加自定义查询方法
}