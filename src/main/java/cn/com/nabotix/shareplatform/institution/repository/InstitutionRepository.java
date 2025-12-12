package cn.com.nabotix.shareplatform.institution.repository;

import cn.com.nabotix.shareplatform.institution.entity.Institution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    // 查询所有已验证的机构（分页）
    Page<Institution> findByVerifiedTrue(Pageable pageable);
}