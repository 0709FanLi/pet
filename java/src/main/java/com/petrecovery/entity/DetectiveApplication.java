package com.petrecovery.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "detective_applications")
public class DetectiveApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String realName;
    private String phone;
    private String city;
    private String companyName;
    private String address;
    private Integer teamSize;

    @Column(columnDefinition = "TEXT")
    private String devices; // JSON 数组

    @Column(columnDefinition = "TEXT")
    private String devicePhotos; // JSON 数组

    private Integer experienceYears;

    @Column(columnDefinition = "TEXT")
    private String serviceAreas; // JSON 数组

    @Column(columnDefinition = "TEXT")
    private String availableTimes; // JSON 数组

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String idCardFront;
    private String idCardBack;

    @Column(columnDefinition = "TEXT")
    private String certificates; // JSON 数组

    private String payoutType; // wechat|alipay
    private String payoutAccount;

    private String emergencyContactName;
    private String emergencyContactPhone;

    private String status; // draft|pending|approved|rejected
    private String reason; // 审核不通过原因

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = "pending";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}


