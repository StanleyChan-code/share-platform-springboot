package cn.com.nabotix.shareplatform.researchsubject.repository;

import cn.com.nabotix.shareplatform.researchsubject.entity.ResearchSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResearchSubjectRepository extends JpaRepository<ResearchSubject, UUID> {
    /**
     * 查找所有激活的研究学科
     * @return 激活的研究学科列表
     */
    List<ResearchSubject> findByActiveTrue();
    
    /**
     * 查找所有未激活的研究学科
     * @return 未激活的研究学科列表
     */
    List<ResearchSubject> findByActiveFalse();
}