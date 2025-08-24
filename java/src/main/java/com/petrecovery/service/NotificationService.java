package com.petrecovery.service;

import com.petrecovery.entity.*;
import com.petrecovery.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 通知服务类
 */
@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private NotificationSettingsRepository notificationSettingsRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LostPetRepository lostPetRepository;
    
    @Autowired
    private MqttService mqttService;
    
    @Autowired
    private SimpleWebSocketService simpleWebSocketService;
    
    /**
     * 发送通知
     */
    @Transactional
    public void sendNotification(Long userId, String title, String content, String type, Long relatedId, String priority, Map<String, Object> extraData) {
        try {
            // 检查用户通知设置
            if (!shouldSendNotification(userId, type)) {
                return;
            }
            
            // 检查是否已存在相同通知（避免重复）
            if (relatedId != null && notificationRepository.existsByUserIdAndTypeAndRelatedId(userId, type, relatedId)) {
                return;
            }
            
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setTitle(title);
            notification.setContent(content);
            notification.setType(type);
            notification.setRelatedId(relatedId);
            notification.setPriority(priority != null ? priority : Notification.Priority.NORMAL);
            
            if (extraData != null && !extraData.isEmpty()) {
                // 简单的JSON序列化
                StringBuilder jsonBuilder = new StringBuilder("{");
                int count = 0;
                for (Map.Entry<String, Object> entry : extraData.entrySet()) {
                    if (count > 0) jsonBuilder.append(",");
                    jsonBuilder.append("\"").append(entry.getKey()).append("\":");
                    if (entry.getValue() instanceof String) {
                        jsonBuilder.append("\"").append(entry.getValue()).append("\"");
                    } else {
                        jsonBuilder.append(entry.getValue());
                    }
                    count++;
                }
                jsonBuilder.append("}");
                notification.setExtraData(jsonBuilder.toString());
            }
            
            notificationRepository.save(notification);
            
            // 发送MQTT实时推送
            try {
                mqttService.sendUserNotification(userId, title, content, type, extraData);
            } catch (Exception e) {
                System.err.println("MQTT推送失败，但数据库通知已保存: " + e.getMessage());
            }
            
            // 发送WebSocket实时推送（模拟）
            try {
                simpleWebSocketService.sendNotificationToUser(userId, title, content, type, extraData);
            } catch (Exception e) {
                System.err.println("模拟WebSocket推送失败，但数据库通知已保存: " + e.getMessage());
            }
            // sendMQTTNotification(userId, notification);
            
        } catch (Exception e) {
            System.err.println("发送通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 侦探表达意向通知
     */
    public void sendIntentionNotification(Long petOwnerId, Long detectiveId, Long lostPetId, Long orderId) {
        try {
            Optional<User> detectiveOpt = userRepository.findById(detectiveId);
            Optional<LostPet> petOpt = lostPetRepository.findById(lostPetId);
            
            if (detectiveOpt.isPresent() && petOpt.isPresent()) {
                User detective = detectiveOpt.get();
                LostPet pet = petOpt.get();
                
                String title = "有侦探表达接单意向";
                String content = String.format("侦探 %s 对您发布的寻找 %s 表达了接单意向，请查看详情", 
                    detective.getUsername(), pet.getPetName() != null ? pet.getPetName() : "宠物");
                
                Map<String, Object> extraData = new HashMap<>();
                extraData.put("detectiveId", detectiveId);
                extraData.put("detectiveName", detective.getUsername());
                extraData.put("petName", pet.getPetName());
                extraData.put("orderId", orderId);
                
                sendNotification(petOwnerId, title, content, Notification.Type.ORDER, orderId, Notification.Priority.HIGH, extraData);
            }
        } catch (Exception e) {
            System.err.println("发送意向通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 宠物主人确认侦探通知
     */
    public void sendConfirmationNotification(Long detectiveId, Long lostPetId, Long orderId) {
        try {
            Optional<LostPet> petOpt = lostPetRepository.findById(lostPetId);
            
            if (petOpt.isPresent()) {
                LostPet pet = petOpt.get();
                
                String title = "您的接单意向已被确认！";
                String content = String.format("恭喜！宠物主人已选择您接单寻找 %s，请及时联系宠物主人开始工作", 
                    pet.getPetName() != null ? pet.getPetName() : "宠物");
                
                Map<String, Object> extraData = new HashMap<>();
                extraData.put("lostPetId", lostPetId);
                extraData.put("petName", pet.getPetName());
                extraData.put("orderId", orderId);
                extraData.put("reward", pet.getReward());
                
                sendNotification(detectiveId, title, content, Notification.Type.ORDER, orderId, Notification.Priority.URGENT, extraData);
            }
        } catch (Exception e) {
            System.err.println("发送确认通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 侦探被拒绝通知
     */
    public void sendRejectionNotification(Long detectiveId, Long lostPetId) {
        try {
            Optional<LostPet> petOpt = lostPetRepository.findById(lostPetId);
            
            if (petOpt.isPresent()) {
                LostPet pet = petOpt.get();
                
                String title = "订单已被其他侦探接单";
                String content = String.format("很遗憾，寻找 %s 的订单已被其他侦探接单，感谢您的关注", 
                    pet.getPetName() != null ? pet.getPetName() : "宠物");
                
                Map<String, Object> extraData = new HashMap<>();
                extraData.put("lostPetId", lostPetId);
                extraData.put("petName", pet.getPetName());
                
                sendNotification(detectiveId, title, content, Notification.Type.ORDER, lostPetId, Notification.Priority.NORMAL, extraData);
            }
        } catch (Exception e) {
            System.err.println("发送拒绝通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 侦探撤回意向通知
     */
    public void sendWithdrawNotification(Long petOwnerId, Long detectiveId, Long lostPetId) {
        try {
            Optional<User> detectiveOpt = userRepository.findById(detectiveId);
            Optional<LostPet> petOpt = lostPetRepository.findById(lostPetId);
            
            if (detectiveOpt.isPresent() && petOpt.isPresent()) {
                User detective = detectiveOpt.get();
                LostPet pet = petOpt.get();
                
                String title = "侦探撤回了接单意向";
                String content = String.format("侦探 %s 撤回了对寻找 %s 的接单意向", 
                    detective.getUsername(), pet.getPetName() != null ? pet.getPetName() : "宠物");
                
                Map<String, Object> extraData = new HashMap<>();
                extraData.put("detectiveId", detectiveId);
                extraData.put("detectiveName", detective.getUsername());
                extraData.put("lostPetId", lostPetId);
                extraData.put("petName", pet.getPetName());
                
                sendNotification(petOwnerId, title, content, Notification.Type.ORDER, lostPetId, Notification.Priority.NORMAL, extraData);
            }
        } catch (Exception e) {
            System.err.println("发送撤回通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取通知列表
     */
    public Map<String, Object> getNotificationList(Long userId, String type, int page, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page - 1, pageSize);
            Page<Notification> notifications;
            
            if (type != null && !type.isEmpty() && !"all".equals(type)) {
                notifications = notificationRepository.findByUserIdAndTypeAndStatusNotOrderByCreatedAtDesc(userId, type, Notification.Status.DELETED, pageable);
            } else {
                notifications = notificationRepository.findByUserIdAndStatusNotOrderByCreatedAtDesc(userId, Notification.Status.DELETED, pageable);
            }
            
            List<Map<String, Object>> notificationList = new ArrayList<>();
            for (Notification notification : notifications.getContent()) {
                Map<String, Object> notificationMap = new HashMap<>();
                notificationMap.put("id", notification.getId());
                notificationMap.put("title", notification.getTitle());
                notificationMap.put("content", notification.getContent());
                notificationMap.put("type", notification.getType());
                notificationMap.put("status", notification.getStatus());
                notificationMap.put("priority", notification.getPriority());
                notificationMap.put("relatedId", notification.getRelatedId());
                notificationMap.put("extraData", notification.getExtraData());
                notificationMap.put("createdAt", notification.getCreatedAt());
                notificationMap.put("readAt", notification.getReadAt());
                
                notificationList.add(notificationMap);
            }
            
            result.put("success", true);
            result.put("data", Map.of(
                "list", notificationList,
                "total", notifications.getTotalElements(),
                "totalPages", notifications.getTotalPages(),
                "currentPage", page
            ));
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取未读数量
     */
    public Map<String, Object> getUnreadCount(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            long totalCount = notificationRepository.countByUserIdAndStatus(userId, Notification.Status.UNREAD);
            long orderCount = notificationRepository.countByUserIdAndTypeAndStatus(userId, Notification.Type.ORDER, Notification.Status.UNREAD);
            long systemCount = notificationRepository.countByUserIdAndTypeAndStatus(userId, Notification.Type.SYSTEM, Notification.Status.UNREAD);
            long recommendCount = notificationRepository.countByUserIdAndTypeAndStatus(userId, Notification.Type.RECOMMEND, Notification.Status.UNREAD);
            
            Map<String, Object> counts = new HashMap<>();
            counts.put("total", totalCount);
            counts.put("order", orderCount);
            counts.put("system", systemCount);
            counts.put("recommend", recommendCount);
            
            result.put("success", true);
            result.put("data", counts);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 标记消息为已读
     */
    @Transactional
    public Map<String, Object> markAsRead(Long userId, Long notificationId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
            
            if (!notificationOpt.isPresent()) {
                result.put("success", false);
                result.put("message", "通知不存在");
                return result;
            }
            
            Notification notification = notificationOpt.get();
            if (!notification.getUserId().equals(userId)) {
                result.put("success", false);
                result.put("message", "无权限操作");
                return result;
            }
            
            if (Notification.Status.UNREAD.equals(notification.getStatus())) {
                notification.setStatus(Notification.Status.READ);
                notification.setReadAt(LocalDateTime.now());
                notificationRepository.save(notification);
            }
            
            result.put("success", true);
            result.put("message", "标记成功");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 全部标记为已读
     */
    @Transactional
    public Map<String, Object> markAllAsRead(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            int count = notificationRepository.markAllAsRead(userId);
            
            result.put("success", true);
            result.put("message", "全部标记成功");
            result.put("count", count);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 删除通知
     */
    @Transactional
    public Map<String, Object> deleteNotification(Long userId, Long notificationId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
            
            if (!notificationOpt.isPresent()) {
                result.put("success", false);
                result.put("message", "通知不存在");
                return result;
            }
            
            Notification notification = notificationOpt.get();
            if (!notification.getUserId().equals(userId)) {
                result.put("success", false);
                result.put("message", "无权限操作");
                return result;
            }
            
            notification.setStatus(Notification.Status.DELETED);
            notificationRepository.save(notification);
            
            result.put("success", true);
            result.put("message", "删除成功");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 检查是否应该发送通知
     */
    private boolean shouldSendNotification(Long userId, String type) {
        try {
            Optional<NotificationSettings> settingsOpt = notificationSettingsRepository.findByUserId(userId);
            
            if (!settingsOpt.isPresent()) {
                // 如果没有设置，创建默认设置
                NotificationSettings settings = new NotificationSettings();
                settings.setUserId(userId);
                notificationSettingsRepository.save(settings);
                return true;
            }
            
            NotificationSettings settings = settingsOpt.get();
            
            // 检查勿扰时间
            if (settings.isQuietTime()) {
                return false;
            }
            
            // 检查类型开关
            switch (type) {
                case Notification.Type.ORDER:
                    return settings.getOrderNotifications();
                case Notification.Type.SYSTEM:
                    return settings.getSystemNotifications();
                case Notification.Type.RECOMMEND:
                    return settings.getRecommendNotifications();
                default:
                    return true;
            }
            
        } catch (Exception e) {
            System.err.println("检查通知设置失败: " + e.getMessage());
            return true; // 默认发送
        }
    }
    
    /**
     * 发送订单状态通知
     */
    @Transactional
    public void sendOrderStatusNotification(Long userId, String title, String content, String type, Long orderId, String petName, String detectiveName) {
        try {
            Map<String, Object> extraData = new HashMap<>();
            extraData.put("orderId", orderId);
            extraData.put("petName", petName);
            extraData.put("detectiveName", detectiveName);
            extraData.put("actionType", "order_status_update");
            
            sendNotification(userId, title, content, "order", orderId, "normal", extraData);
            
        } catch (Exception e) {
            System.err.println("发送订单状态通知失败: " + e.getMessage());
        }
    }
}
