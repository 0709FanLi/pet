package com.petrecovery.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息通知实体类
 */
@Entity
@Table(name = "notifications")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(nullable = false, length = 50)
    private String type;
    
    @Column(name = "related_id")
    private Long relatedId;
    
    @Column(nullable = false, length = 20)
    private String status = "unread";
    
    @Column(nullable = false, length = 20)
    private String priority = "normal";
    
    @Column(name = "extra_data", columnDefinition = "JSON")
    private String extraData;
    
    @Column(name = "expires_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;
    
    @Column(name = "read_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readAt;
    
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    // 关联用户
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // 默认30天过期
        if (expiresAt == null) {
            expiresAt = LocalDateTime.now().plusDays(30);
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Long getRelatedId() { return relatedId; }
    public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { 
        this.status = status; 
        if ("read".equals(status) && readAt == null) {
            readAt = LocalDateTime.now();
        }
    }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getExtraData() { return extraData; }
    public void setExtraData(String extraData) { this.extraData = extraData; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    /**
     * 消息类型枚举
     */
    public static class Type {
        public static final String ORDER = "order";               // 订单相关
        public static final String SYSTEM = "system";             // 系统通知
        public static final String RECOMMEND = "recommend";       // 推荐通知
        public static final String ANNOUNCEMENT = "announcement"; // 系统公告
    }
    
    /**
     * 消息状态枚举
     */
    public static class Status {
        public static final String UNREAD = "unread";   // 未读
        public static final String READ = "read";       // 已读
        public static final String DELETED = "deleted"; // 已删除
    }
    
    /**
     * 优先级枚举
     */
    public static class Priority {
        public static final String LOW = "low";         // 低优先级
        public static final String NORMAL = "normal";   // 普通优先级
        public static final String HIGH = "high";       // 高优先级
        public static final String URGENT = "urgent";   // 紧急
    }
}
