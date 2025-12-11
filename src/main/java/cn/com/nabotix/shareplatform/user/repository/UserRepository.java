package cn.com.nabotix.shareplatform.user.repository;

import cn.com.nabotix.shareplatform.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByPhone(String phone);

    Boolean existsByPhone(String phone);
    
    // 根据机构ID获取用户列表
    List<User> findByInstitutionId(UUID institutionId);
}