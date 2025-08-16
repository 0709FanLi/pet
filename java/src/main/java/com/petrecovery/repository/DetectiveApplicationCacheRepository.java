package com.petrecovery.repository;

import com.petrecovery.entity.DetectiveApplicationCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 宠物侦探申请信息缓存数据访问层
 */
@Repository
public interface DetectiveApplicationCacheRepository extends JpaRepository<DetectiveApplicationCache, Long> {
    
    /**
     * 根据用户ID查找最新的申请缓存信息
     */
    Optional<DetectiveApplicationCache> findByUserIdOrderByUpdatedAtDesc(Long userId);
    
    /**
     * 根据用户ID删除所有缓存信息
     */
    void deleteByUserId(Long userId);
    
    /**
     * 检查用户是否有缓存信息
     */
    boolean existsByUserId(Long userId);
}
