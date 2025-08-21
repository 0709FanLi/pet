package com.petrecovery.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 用户通知设置实体类
 */
@Entity
@Table(name = "notification_settings")
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    @Column(name = "order_notifications", nullable = false)
    private Boolean orderNotifications = true;
    
    @Column(name = "system_notifications", nullable = false)
    private Boolean systemNotifications = true;
    
    @Column(name = "recommend_notifications", nullable = false)
    private Boolean recommendNotifications = true;
    
    @Column(name = "quiet_start_time")
    private LocalTime quietStartTime = LocalTime.of(22, 0);
    
    @Column(name = "quiet_end_time")
    private LocalTime quietEndTime = LocalTime.of(8, 0);
    
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 关联用户
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Boolean getOrderNotifications() { return orderNotifications; }
    public void setOrderNotifications(Boolean orderNotifications) { this.orderNotifications = orderNotifications; }
    
    public Boolean getSystemNotifications() { return systemNotifications; }
    public void setSystemNotifications(Boolean systemNotifications) { this.systemNotifications = systemNotifications; }
    
    public Boolean getRecommendNotifications() { return recommendNotifications; }
    public void setRecommendNotifications(Boolean recommendNotifications) { this.recommendNotifications = recommendNotifications; }
    
    public LocalTime getQuietStartTime() { return quietStartTime; }
    public void setQuietStartTime(LocalTime quietStartTime) { this.quietStartTime = quietStartTime; }
    
    public LocalTime getQuietEndTime() { return quietEndTime; }
    public void setQuietEndTime(LocalTime quietEndTime) { this.quietEndTime = quietEndTime; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    /**
     * 检查当前时间是否在勿扰时间段内
     */
    public boolean isQuietTime() {
        LocalTime now = LocalTime.now();
        if (quietStartTime.isBefore(quietEndTime)) {
            // 勿扰时间在同一天内，如 22:00-08:00 （次日）
            return now.isAfter(quietStartTime) || now.isBefore(quietEndTime);
        } else {
            // 勿扰时间跨天，如 22:00-08:00
            return now.isAfter(quietStartTime) && now.isBefore(quietEndTime);
        }
    }
}
