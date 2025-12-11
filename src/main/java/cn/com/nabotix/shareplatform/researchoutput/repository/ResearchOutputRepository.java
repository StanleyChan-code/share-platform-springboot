package cn.com.nabotix.shareplatform.researchoutput.repository;

import cn.com.nabotix.shareplatform.researchoutput.entity.ResearchOutput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * 研究成果仓储接口
 * 提供对ResearchOutput实体的基本CRUD操作以及特定业务查询方法
 *
 * @author 陈雍文
 */
@Repository
public interface ResearchOutputRepository extends JpaRepository<ResearchOutput, UUID> {

    Page<ResearchOutput> findByApproved(Boolean approved, Pageable pageable);

    // 根据提交者ID列表查询研究成果
    Page<ResearchOutput> findBySubmitterId(UUID submitterId, Pageable pageable);
    Page<ResearchOutput> findBySubmitterIdAndApproved(UUID submitterId, Boolean approved, Pageable pageable);

    // 根据机构ID列表查询审核状态为approved的研究成果
    @Query("SELECT r FROM ResearchOutput r " +
            "WHERE r.approved = :approved AND " +
            "EXISTS (" +
            "   SELECT 1 FROM User u " +
            "   WHERE u.id = r.submitterId " +
            "   AND u.institutionId = :institutionId" +
            ")")
    Page<ResearchOutput> findByInstitutionIdWithApproved(@Param("institutionId") UUID institutionId, Boolean approved, Pageable pageable);

    // 根据机构ID列表查询所有研究成果
    @Query("SELECT r FROM ResearchOutput r " +
            "WHERE EXISTS (" +
            "   SELECT 1 FROM User u " +
            "   WHERE u.id = r.submitterId " +
            "   AND u.institutionId = :institutionId" +
            ")")
    Page<ResearchOutput> findByInstitutionId(@Param("institutionId") UUID institutionId, Pageable pageable);
}