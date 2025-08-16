package com.petrecovery.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 宠物侦探申请信息缓存实体
 * 用于存储用户填写的申请信息，方便重新申请时恢复数据
 */
@Entity
@Table(name = "detective_application_cache")
public class DetectiveApplicationCache {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "real_name")
    private String realName;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "company_name")
    private String companyName;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "team_size")
    private Integer teamSize;
    
    @Column(name = "devices", columnDefinition = "JSON")
    private String devices;
    
    @Column(name = "device_photos", columnDefinition = "JSON")
    private String devicePhotos;
    
    @Column(name = "experience_years")
    private Integer experienceYears;
    
    @Column(name = "service_areas", columnDefinition = "JSON")
    private String serviceAreas;
    
    @Column(name = "available_times", columnDefinition = "JSON")
    private String availableTimes;
    
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "id_card_front")
    private String idCardFront;
    
    @Column(name = "id_card_back")
    private String idCardBack;
    
    @Column(name = "certificates", columnDefinition = "JSON")
    private String certificates;
    
    @Column(name = "payout_type")
    private String payoutType;
    
    @Column(name = "payout_account")
    private String payoutAccount;
    
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public DetectiveApplicationCache() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public Integer getTeamSize() { return teamSize; }
    public void setTeamSize(Integer teamSize) { this.teamSize = teamSize; }
    
    public String getDevices() { return devices; }
    public void setDevices(String devices) { this.devices = devices; }
    
    public String getDevicePhotos() { return devicePhotos; }
    public void setDevicePhotos(String devicePhotos) { this.devicePhotos = devicePhotos; }
    
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
    
    public String getServiceAreas() { return serviceAreas; }
    public void setServiceAreas(String serviceAreas) { this.serviceAreas = serviceAreas; }
    
    public String getAvailableTimes() { return availableTimes; }
    public void setAvailableTimes(String availableTimes) { this.availableTimes = availableTimes; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getIdCardFront() { return idCardFront; }
    public void setIdCardFront(String idCardFront) { this.idCardFront = idCardFront; }
    
    public String getIdCardBack() { return idCardBack; }
    public void setIdCardBack(String idCardBack) { this.idCardBack = idCardBack; }
    
    public String getCertificates() { return certificates; }
    public void setCertificates(String certificates) { this.certificates = certificates; }
    
    public String getPayoutType() { return payoutType; }
    public void setPayoutType(String payoutType) { this.payoutType = payoutType; }
    
    public String getPayoutAccount() { return payoutAccount; }
    public void setPayoutAccount(String payoutAccount) { this.payoutAccount = payoutAccount; }
    
    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
    
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
