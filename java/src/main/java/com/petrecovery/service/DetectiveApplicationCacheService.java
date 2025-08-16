package com.petrecovery.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petrecovery.entity.DetectiveApplicationCache;
import com.petrecovery.entity.User;
import com.petrecovery.repository.DetectiveApplicationCacheRepository;
import com.petrecovery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * 宠物侦探申请信息缓存服务层
 */
@Service
public class DetectiveApplicationCacheService {
    
    @Autowired
    private DetectiveApplicationCacheRepository cacheRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 保存申请信息缓存
     */
    @Transactional
    public void saveApplicationCache(String username, Map<String, Object> applicationData) {
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 查找是否已有缓存记录
            Optional<DetectiveApplicationCache> existingCache = 
                cacheRepository.findByUserIdOrderByUpdatedAtDesc(user.getId());
            
            DetectiveApplicationCache cache;
            if (existingCache.isPresent()) {
                cache = existingCache.get();
            } else {
                cache = new DetectiveApplicationCache();
                cache.setUserId(user.getId());
            }
            
            // 设置基础信息
            cache.setRealName((String) applicationData.get("realName"));
            cache.setPhone((String) applicationData.get("phone"));
            cache.setCity((String) applicationData.get("city"));
            cache.setCompanyName((String) applicationData.get("companyName"));
            cache.setAddress((String) applicationData.get("address"));
            cache.setTeamSize((Integer) applicationData.get("teamSize"));
            cache.setExperienceYears((Integer) applicationData.get("experienceYears"));
            cache.setBio((String) applicationData.get("bio"));
            cache.setIdCardFront((String) applicationData.get("idCardFront"));
            cache.setIdCardBack((String) applicationData.get("idCardBack"));
            
            // 处理JSON字段
            cache.setDevices(convertToJson(applicationData.get("devices")));
            cache.setDevicePhotos(convertToJson(applicationData.get("devicePhotos")));
            cache.setServiceAreas(convertToJson(applicationData.get("serviceAreas")));
            cache.setAvailableTimes(convertToJson(applicationData.get("availableTimes")));
            cache.setCertificates(convertToJson(applicationData.get("certificates")));
            
            // 处理收款信息
            @SuppressWarnings("unchecked")
            Map<String, Object> payout = (Map<String, Object>) applicationData.get("payout");
            if (payout != null) {
                cache.setPayoutType((String) payout.get("type"));
                cache.setPayoutAccount((String) payout.get("account"));
            }
            
            // 处理紧急联系人
            @SuppressWarnings("unchecked")
            Map<String, Object> emergencyContact = (Map<String, Object>) applicationData.get("emergencyContact");
            if (emergencyContact != null) {
                cache.setEmergencyContactName((String) emergencyContact.get("name"));
                cache.setEmergencyContactPhone((String) emergencyContact.get("phone"));
            }
            
            cacheRepository.save(cache);
            
        } catch (Exception e) {
            throw new RuntimeException("保存申请缓存失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的申请信息缓存
     */
    public Map<String, Object> getApplicationCache(String username) {
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            Optional<DetectiveApplicationCache> cache = 
                cacheRepository.findByUserIdOrderByUpdatedAtDesc(user.getId());
            
            if (!cache.isPresent()) {
                return null;
            }
            
            DetectiveApplicationCache cacheData = cache.get();
            
            // 构建返回数据
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("realName", cacheData.getRealName());
            result.put("phone", cacheData.getPhone());
            result.put("city", cacheData.getCity());
            result.put("companyName", cacheData.getCompanyName());
            result.put("address", cacheData.getAddress());
            result.put("teamSize", cacheData.getTeamSize());
            result.put("experienceYears", cacheData.getExperienceYears());
            result.put("bio", cacheData.getBio());
            result.put("idCardFront", cacheData.getIdCardFront());
            result.put("idCardBack", cacheData.getIdCardBack());
            
            // 解析JSON字段
            result.put("devices", parseJsonField(cacheData.getDevices()));
            result.put("devicePhotos", parseJsonField(cacheData.getDevicePhotos()));
            result.put("serviceAreas", parseJsonField(cacheData.getServiceAreas()));
            result.put("availableTimes", parseJsonField(cacheData.getAvailableTimes()));
            result.put("certificates", parseJsonField(cacheData.getCertificates()));
            
            // 构建收款信息
            Map<String, Object> payout = new java.util.HashMap<>();
            payout.put("type", cacheData.getPayoutType());
            payout.put("account", cacheData.getPayoutAccount());
            result.put("payout", payout);
            
            // 构建紧急联系人信息
            Map<String, Object> emergencyContact = new java.util.HashMap<>();
            emergencyContact.put("name", cacheData.getEmergencyContactName());
            emergencyContact.put("phone", cacheData.getEmergencyContactPhone());
            result.put("emergencyContact", emergencyContact);
            
            result.put("updatedAt", cacheData.getUpdatedAt());
            
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("获取申请缓存失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查用户是否有缓存信息
     */
    public boolean hasCachedApplication(String username) {
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                return false;
            }
            return cacheRepository.existsByUserId(user.getId());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 删除用户的申请缓存
     */
    @Transactional
    public void clearApplicationCache(String username) {
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                cacheRepository.deleteByUserId(user.getId());
            }
        } catch (Exception e) {
            throw new RuntimeException("清除申请缓存失败: " + e.getMessage());
        }
    }
    
    /**
     * 将对象转换为JSON字符串
     */
    private String convertToJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 将JSON字符串解析为对象
     */
    private Object parseJsonField(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            return null;
        }
    }
}
