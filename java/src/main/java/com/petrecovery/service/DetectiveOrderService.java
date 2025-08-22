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
 * 侦探订单服务类
 */
@Service
public class DetectiveOrderService {
    
    @Autowired
    private DetectiveOrderRepository detectiveOrderRepository;
    
    @Autowired
    private LostPetRepository lostPetRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DetectiveApplicationRepository detectiveApplicationRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 表达接单意向
     */
    @Transactional
    public Map<String, Object> expressIntention(Long detectiveId, Long lostPetId, String estimatedCompletion, String serviceDescription) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 验证侦探身份
            if (!isApprovedDetective(detectiveId)) {
                result.put("success", false);
                result.put("message", "您不是认证侦探，无法接单");
                return result;
            }
            
            // 2. 检查是否已经表达过意向或撤回过
            Optional<DetectiveOrder> existingOrder = detectiveOrderRepository.findByDetectiveIdAndLostPetId(detectiveId, lostPetId);
            if (existingOrder.isPresent()) {
                DetectiveOrder order = existingOrder.get();
                if (DetectiveOrder.Status.WITHDRAWN.equals(order.getStatus())) {
                    result.put("success", false);
                    result.put("message", "您已撤回对该订单的意向，无法再次表达意向");
                    return result;
                } else if (DetectiveOrder.Status.INTENTION.equals(order.getStatus())) {
                    result.put("success", false);
                    result.put("message", "您已经表达过意向，请勿重复操作");
                    return result;
                }
            }
            
            // 3. 验证宠物信息
            Optional<LostPet> lostPetOpt = lostPetRepository.findById(lostPetId);
            if (!lostPetOpt.isPresent()) {
                result.put("success", false);
                result.put("message", "寻宠信息不存在");
                return result;
            }
            
            LostPet lostPet = lostPetOpt.get();
            if (!"approved".equals(lostPet.getStatus())) {
                result.put("success", false);
                result.put("message", "该寻宠信息未通过审核，无法接单");
                return result;
            }
            
            // 4. 检查是否已被其他侦探确认接单
            Optional<DetectiveOrder> confirmedOrder = detectiveOrderRepository.findByLostPetIdAndStatus(lostPetId, DetectiveOrder.Status.CONFIRMED);
            if (confirmedOrder.isPresent()) {
                result.put("success", false);
                result.put("message", "该订单已被其他侦探接单");
                return result;
            }
            
            // 5. 创建意向订单
            DetectiveOrder order = new DetectiveOrder();
            order.setLostPetId(lostPetId);
            order.setDetectiveId(detectiveId);
            order.setStatus(DetectiveOrder.Status.INTENTION);
            order.setEstimatedCompletion(estimatedCompletion);
            order.setServiceDescription(serviceDescription);
            order.setIntentionAt(LocalDateTime.now());
            
            DetectiveOrder savedOrder = detectiveOrderRepository.save(order);
            
            // 6. 发送通知给宠物主人
            notificationService.sendIntentionNotification(lostPet.getUser().getId(), detectiveId, lostPetId, savedOrder.getId());
            
            result.put("success", true);
            result.put("message", "意向表达成功");
            result.put("orderId", savedOrder.getId());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 撤回接单意向
     */
    @Transactional
    public Map<String, Object> withdrawIntention(Long detectiveId, Long lostPetId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<DetectiveOrder> orderOpt = detectiveOrderRepository.findByDetectiveIdAndLostPetId(detectiveId, lostPetId);
            if (!orderOpt.isPresent()) {
                result.put("success", false);
                result.put("message", "未找到相关订单");
                return result;
            }
            
            DetectiveOrder order = orderOpt.get();
            if (!DetectiveOrder.Status.INTENTION.equals(order.getStatus())) {
                result.put("success", false);
                result.put("message", "当前状态不允许撤回");
                return result;
            }
            
            // 更新状态为已撤回
            order.setStatus(DetectiveOrder.Status.WITHDRAWN);
            order.setWithdrawnAt(LocalDateTime.now());
            detectiveOrderRepository.save(order);
            
            // 发送通知给宠物主人
            Optional<LostPet> lostPetOpt = lostPetRepository.findById(lostPetId);
            if (lostPetOpt.isPresent()) {
                notificationService.sendWithdrawNotification(lostPetOpt.get().getUser().getId(), detectiveId, lostPetId);
            }
            
            result.put("success", true);
            result.put("message", "意向撤回成功");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "撤回失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 宠物主人确认侦探
     */
    @Transactional
    public Map<String, Object> confirmDetective(Long userId, Long lostPetId, Long detectiveId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 验证宠物主人权限
            Optional<LostPet> lostPetOpt = lostPetRepository.findById(lostPetId);
            if (!lostPetOpt.isPresent() || !userId.equals(lostPetOpt.get().getUser().getId())) {
                result.put("success", false);
                result.put("message", "无权限操作该订单");
                return result;
            }
            
            // 2. 查找侦探的意向订单
            Optional<DetectiveOrder> orderOpt = detectiveOrderRepository.findByDetectiveIdAndLostPetId(detectiveId, lostPetId);
            if (!orderOpt.isPresent() || !DetectiveOrder.Status.INTENTION.equals(orderOpt.get().getStatus())) {
                result.put("success", false);
                result.put("message", "该侦探未表达意向或状态异常");
                return result;
            }
            
            DetectiveOrder order = orderOpt.get();
            
            // 3. 确认该侦探，状态改为confirmed
            order.setStatus(DetectiveOrder.Status.CONFIRMED);
            order.setConfirmedAt(LocalDateTime.now());
            detectiveOrderRepository.save(order);
            
            // 4. 关闭其他侦探的意向
            detectiveOrderRepository.closeOtherIntentions(lostPetId, detectiveId);
            
            // 5. 发送确认通知给被选中的侦探
            notificationService.sendConfirmationNotification(detectiveId, lostPetId, order.getId());
            
            // 6. 发送拒绝通知给其他侦探
            List<DetectiveOrder> otherIntentions = detectiveOrderRepository.findByLostPetIdAndStatusOrderByCreatedAtDesc(lostPetId, "closed");
            for (DetectiveOrder otherOrder : otherIntentions) {
                if (!otherOrder.getDetectiveId().equals(detectiveId)) {
                    notificationService.sendRejectionNotification(otherOrder.getDetectiveId(), lostPetId);
                }
            }
            
            result.put("success", true);
            result.put("message", "侦探确认成功");
            result.put("orderId", order.getId());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "确认失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取我的接单列表
     */
    public Map<String, Object> getMyOrders(Long detectiveId, String status, int page, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page - 1, pageSize);
            Page<DetectiveOrder> orders;
            
            if (status != null && !status.isEmpty()) {
                orders = detectiveOrderRepository.findByDetectiveIdAndStatusOrderByCreatedAtDesc(detectiveId, status, pageable);
            } else {
                orders = detectiveOrderRepository.findByDetectiveIdOrderByCreatedAtDesc(detectiveId, pageable);
            }
            
            List<Map<String, Object>> orderList = new ArrayList<>();
            for (DetectiveOrder order : orders.getContent()) {
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("id", order.getId());
                orderMap.put("lostPetId", order.getLostPetId());
                orderMap.put("status", order.getStatus());
                orderMap.put("estimatedCompletion", order.getEstimatedCompletion());
                orderMap.put("serviceDescription", order.getServiceDescription());
                orderMap.put("intentionAt", order.getIntentionAt());
                orderMap.put("confirmedAt", order.getConfirmedAt());
                orderMap.put("createdAt", order.getCreatedAt());
                
                // 获取宠物信息
                Optional<LostPet> lostPetOpt = lostPetRepository.findById(order.getLostPetId());
                if (lostPetOpt.isPresent()) {
                    LostPet lostPet = lostPetOpt.get();
                    Map<String, Object> petInfo = new HashMap<>();
                    petInfo.put("petName", lostPet.getPetName());
                    petInfo.put("petBreed", lostPet.getPetBreed());
                    petInfo.put("lostLocation", lostPet.getLostLocation());
                    petInfo.put("reward", lostPet.getReward());
                    petInfo.put("images", lostPet.getImages());
                    orderMap.put("petInfo", petInfo);
                }
                
                orderList.add(orderMap);
            }
            
            result.put("success", true);
            result.put("data", Map.of(
                "list", orderList,
                "total", orders.getTotalElements(),
                "totalPages", orders.getTotalPages(),
                "currentPage", page
            ));
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取宠物的意向列表
     */
    public Map<String, Object> getPetIntentions(Long lostPetId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<DetectiveOrder> intentions = detectiveOrderRepository.findByLostPetIdAndStatusOrderByCreatedAtDesc(lostPetId, DetectiveOrder.Status.INTENTION);
            
            List<Map<String, Object>> intentionList = new ArrayList<>();
            for (DetectiveOrder order : intentions) {
                Map<String, Object> intentionMap = new HashMap<>();
                intentionMap.put("orderId", order.getId());
                intentionMap.put("detectiveId", order.getDetectiveId());
                intentionMap.put("estimatedCompletion", order.getEstimatedCompletion());
                intentionMap.put("serviceDescription", order.getServiceDescription());
                intentionMap.put("intentionAt", order.getIntentionAt());
                
                // 获取侦探信息
                Optional<User> detectiveOpt = userRepository.findById(order.getDetectiveId());
                if (detectiveOpt.isPresent()) {
                    User detective = detectiveOpt.get();
                    Map<String, Object> detectiveInfo = new HashMap<>();
                    detectiveInfo.put("realName", detective.getUsername());
                    detectiveInfo.put("phone", detective.getPhoneNumber());
                    // 这里可以添加侦探的评级、成功率等信息
                    intentionMap.put("detectiveInfo", detectiveInfo);
                }
                
                intentionList.add(intentionMap);
            }
            
            result.put("success", true);
            result.put("data", intentionList);
            result.put("total", intentionList.size());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取侦探对特定宠物的订单状态
     */
    public Map<String, Object> getIntentionStatus(Long detectiveId, Long lostPetId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<DetectiveOrder> orderOpt = detectiveOrderRepository.findByDetectiveIdAndLostPetId(detectiveId, lostPetId);
            
            if (!orderOpt.isPresent()) {
                result.put("status", "none");
                result.put("canExpress", true);
            } else {
                DetectiveOrder order = orderOpt.get();
                result.put("status", order.getStatus());
                result.put("orderId", order.getId());
                result.put("canExpress", false);
                result.put("intentionAt", order.getIntentionAt());
                result.put("confirmedAt", order.getConfirmedAt());
            }
            
            // 获取该宠物的意向统计
            long intentionCount = detectiveOrderRepository.countByLostPetIdAndStatus(lostPetId, DetectiveOrder.Status.INTENTION);
            result.put("intentionCount", intentionCount);
            
            // 检查是否已被其他侦探确认
            Optional<DetectiveOrder> confirmedOrder = detectiveOrderRepository.findByLostPetIdAndStatus(lostPetId, DetectiveOrder.Status.CONFIRMED);
            result.put("isConfirmed", confirmedOrder.isPresent());
            
            result.put("success", true);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取订单数量统计
     */
    public Map<String, Object> getOrderCounts(Long detectiveId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Long> counts = new HashMap<>();
            
            // 全部订单
            long allCount = detectiveOrderRepository.countByDetectiveId(detectiveId);
            counts.put("all", allCount);
            
            // 按状态统计
            counts.put("intention", detectiveOrderRepository.countByDetectiveIdAndStatus(detectiveId, DetectiveOrder.Status.INTENTION));
            counts.put("confirmed", detectiveOrderRepository.countByDetectiveIdAndStatus(detectiveId, DetectiveOrder.Status.CONFIRMED));
            counts.put("in_progress", detectiveOrderRepository.countByDetectiveIdAndStatus(detectiveId, DetectiveOrder.Status.IN_PROGRESS));
            counts.put("completed", detectiveOrderRepository.countByDetectiveIdAndStatus(detectiveId, DetectiveOrder.Status.COMPLETED));
            
            result.put("success", true);
            result.put("data", counts);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "统计失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 开始工作
     */
    @Transactional
    public Map<String, Object> startWork(Long detectiveId, Long orderId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<DetectiveOrder> orderOpt = detectiveOrderRepository.findById(orderId);
            if (!orderOpt.isPresent()) {
                result.put("success", false);
                result.put("message", "订单不存在");
                return result;
            }
            
            DetectiveOrder order = orderOpt.get();
            
            // 验证权限
            if (!order.getDetectiveId().equals(detectiveId)) {
                result.put("success", false);
                result.put("message", "无权操作此订单");
                return result;
            }
            
            // 验证状态
            if (order.getStatus() != DetectiveOrder.Status.CONFIRMED) {
                result.put("success", false);
                result.put("message", "只有已确认的订单才能开始工作");
                return result;
            }
            
            // 更新状态
            order.setStatus(DetectiveOrder.Status.IN_PROGRESS);
            detectiveOrderRepository.save(order);
            
            // 发送通知给宠物主人
            notificationService.sendOrderStatusNotification(
                order.getLostPet().getUser().getId(),
                "订单状态更新",
                "侦探已开始为您寻找宠物",
                "order",
                order.getId(),
                order.getLostPet().getPetName(),
                order.getDetectiveUser().getNickname()
            );
            
            result.put("success", true);
            result.put("message", "已开始工作");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 更新进度
     */
    @Transactional
    public Map<String, Object> updateProgress(Long detectiveId, Long orderId, String progressUpdate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<DetectiveOrder> orderOpt = detectiveOrderRepository.findById(orderId);
            if (!orderOpt.isPresent()) {
                result.put("success", false);
                result.put("message", "订单不存在");
                return result;
            }
            
            DetectiveOrder order = orderOpt.get();
            
            // 验证权限
            if (!order.getDetectiveId().equals(detectiveId)) {
                result.put("success", false);
                result.put("message", "无权操作此订单");
                return result;
            }
            
            // 验证状态
            if (order.getStatus() != DetectiveOrder.Status.IN_PROGRESS) {
                result.put("success", false);
                result.put("message", "只有进行中的订单才能更新进度");
                return result;
            }
            
            // 更新进度
            String currentProgress = order.getProgressUpdates();
            String newProgress = "[" + LocalDateTime.now() + "] " + progressUpdate;
            
            if (currentProgress != null && !currentProgress.isEmpty()) {
                order.setProgressUpdates(currentProgress + "\n" + newProgress);
            } else {
                order.setProgressUpdates(newProgress);
            }
            
            detectiveOrderRepository.save(order);
            
            // 发送通知给宠物主人
            notificationService.sendOrderStatusNotification(
                order.getLostPet().getUser().getId(),
                "进度更新",
                "侦探更新了寻宠进度：" + progressUpdate,
                "order",
                order.getId(),
                order.getLostPet().getPetName(),
                order.getDetectiveUser().getNickname()
            );
            
            result.put("success", true);
            result.put("message", "进度更新成功");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 完成订单
     */
    @Transactional
    public Map<String, Object> completeOrder(Long detectiveId, Long orderId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<DetectiveOrder> orderOpt = detectiveOrderRepository.findById(orderId);
            if (!orderOpt.isPresent()) {
                result.put("success", false);
                result.put("message", "订单不存在");
                return result;
            }
            
            DetectiveOrder order = orderOpt.get();
            
            // 验证权限
            if (!order.getDetectiveId().equals(detectiveId)) {
                result.put("success", false);
                result.put("message", "无权操作此订单");
                return result;
            }
            
            // 验证状态
            if (order.getStatus() != DetectiveOrder.Status.IN_PROGRESS) {
                result.put("success", false);
                result.put("message", "只有进行中的订单才能完成");
                return result;
            }
            
            // 更新状态
            order.setStatus(DetectiveOrder.Status.COMPLETED);
            order.setCompletedAt(LocalDateTime.now());
            detectiveOrderRepository.save(order);
            
            // 发送通知给宠物主人
            notificationService.sendOrderStatusNotification(
                order.getLostPet().getUser().getId(),
                "订单完成",
                "侦探已完成寻宠任务，请联系侦探了解详情",
                "order",
                order.getId(),
                order.getLostPet().getPetName(),
                order.getDetectiveUser().getNickname()
            );
            
            result.put("success", true);
            result.put("message", "订单已完成");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 验证是否为认证侦探
     */
    private boolean isApprovedDetective(Long userId) {
        return detectiveApplicationRepository.existsByUserIdAndStatus(userId, "approved");
    }
}
