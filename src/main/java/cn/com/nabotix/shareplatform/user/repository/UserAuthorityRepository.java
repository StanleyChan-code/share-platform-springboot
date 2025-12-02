package cn.com.nabotix.shareplatform.user.repository;

import cn.com.nabotix.shareplatform.user.entity.UserAuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAuthorityRepository extends JpaRepository<UserAuthorityEntity, UUID> {
    // 添加按用户ID查询所有角色的方法
    List<UserAuthorityEntity> findByUserId(UUID userId);
}