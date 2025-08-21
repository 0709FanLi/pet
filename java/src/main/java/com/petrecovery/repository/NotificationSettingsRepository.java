package com.petrecovery.repository;

import com.petrecovery.entity.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {
    
    /**
     * 根据用户ID查询通知设置
     */
    Optional<NotificationSettings> findByUserId(Long userId);
    
    /**
     * 检查用户是否存在通知设置
     */
    boolean existsByUserId(Long userId);
}
