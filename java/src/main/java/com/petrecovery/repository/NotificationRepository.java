package com.petrecovery.repository;

import com.petrecovery.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * 分页查询用户的通知列表
     */
    Page<Notification> findByUserIdAndStatusNotOrderByCreatedAtDesc(Long userId, String excludeStatus, Pageable pageable);
    
    /**
     * 根据用户ID和类型查询通知
     */
    Page<Notification> findByUserIdAndTypeAndStatusNotOrderByCreatedAtDesc(Long userId, String type, String excludeStatus, Pageable pageable);
    
    /**
     * 查询用户未读通知数量
     */
    long countByUserIdAndStatus(Long userId, String status);
    
    /**
     * 查询用户未读通知数量（按类型）
     */
    long countByUserIdAndTypeAndStatus(Long userId, String type, String status);
    
    /**
     * 批量标记用户所有未读消息为已读
     */
    @Modifying
    @Query("UPDATE Notification n SET n.status = 'read', n.readAt = CURRENT_TIMESTAMP WHERE n.userId = :userId AND n.status = 'unread'")
    int markAllAsRead(@Param("userId") Long userId);
    
    /**
     * 批量标记某类型的未读消息为已读
     */
    @Modifying
    @Query("UPDATE Notification n SET n.status = 'read', n.readAt = CURRENT_TIMESTAMP WHERE n.userId = :userId AND n.type = :type AND n.status = 'unread'")
    int markTypeAsRead(@Param("userId") Long userId, @Param("type") String type);
    
    /**
     * 删除过期消息
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.expiresAt < :now")
    int deleteExpiredNotifications(@Param("now") LocalDateTime now);
    
    /**
     * 查询最新的几条未读消息
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.status = 'unread' ORDER BY n.createdAt DESC")
    Page<Notification> findLatestUnreadNotifications(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 根据关联ID查询通知（用于避免重复发送）
     */
    boolean existsByUserIdAndTypeAndRelatedId(Long userId, String type, Long relatedId);
}
