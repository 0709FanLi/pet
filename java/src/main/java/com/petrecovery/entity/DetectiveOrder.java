package com.petrecovery.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 侦探订单实体类
 */
@Entity
@Table(name = "detective_orders")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetectiveOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "lost_pet_id", nullable = false)
    private Long lostPetId;
    
    @Column(name = "detective_id", nullable = false)
    private Long detectiveId;
    
    @Column(nullable = false, length = 20)
    private String status = "intention";
    
    @Column(name = "intention_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime intentionAt;
    
    @Column(name = "confirmed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime confirmedAt;
    
    @Column(name = "estimated_completion", length = 50)
    private String estimatedCompletion;
    
    @Column(name = "service_description", columnDefinition = "TEXT")
    private String serviceDescription;
    
    @Column(name = "progress_updates", columnDefinition = "JSON")
    private String progressUpdates;
    
    @Column(name = "completed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;
    
    @Column(name = "withdrawn_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime withdrawnAt;
    
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 关联实体
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lost_pet_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private LostPet lostPet;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detective_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User detective;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status.equals("intention") && intentionAt == null) {
            intentionAt = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (status.equals("confirmed") && confirmedAt == null) {
            confirmedAt = LocalDateTime.now();
        }
        if (status.equals("completed") && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
        if (status.equals("withdrawn") && withdrawnAt == null) {
            withdrawnAt = LocalDateTime.now();
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getLostPetId() { return lostPetId; }
    public void setLostPetId(Long lostPetId) { this.lostPetId = lostPetId; }
    
    public Long getDetectiveId() { return detectiveId; }
    public void setDetectiveId(Long detectiveId) { this.detectiveId = detectiveId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getIntentionAt() { return intentionAt; }
    public void setIntentionAt(LocalDateTime intentionAt) { this.intentionAt = intentionAt; }
    
    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }
    
    public String getEstimatedCompletion() { return estimatedCompletion; }
    public void setEstimatedCompletion(String estimatedCompletion) { this.estimatedCompletion = estimatedCompletion; }
    
    public String getServiceDescription() { return serviceDescription; }
    public void setServiceDescription(String serviceDescription) { this.serviceDescription = serviceDescription; }
    
    public String getProgressUpdates() { return progressUpdates; }
    public void setProgressUpdates(String progressUpdates) { this.progressUpdates = progressUpdates; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public LocalDateTime getWithdrawnAt() { return withdrawnAt; }
    public void setWithdrawnAt(LocalDateTime withdrawnAt) { this.withdrawnAt = withdrawnAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LostPet getLostPet() { return lostPet; }
    public void setLostPet(LostPet lostPet) { this.lostPet = lostPet; }
    
    public User getDetective() { return detective; }
    public void setDetective(User detective) { this.detective = detective; }
    
    /**
     * 状态枚举
     */
    public static class Status {
        public static final String INTENTION = "intention";      // 已表达意向
        public static final String CONFIRMED = "confirmed";      // 已确认接单
        public static final String IN_PROGRESS = "in_progress";  // 进行中
        public static final String COMPLETED = "completed";      // 已完成
        public static final String CLOSED = "closed";            // 已关闭
        public static final String WITHDRAWN = "withdrawn";      // 已撤回
    }
}
