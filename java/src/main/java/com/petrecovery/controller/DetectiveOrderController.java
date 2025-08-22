package com.petrecovery.controller;

import com.petrecovery.entity.User;
import com.petrecovery.repository.UserRepository;
import com.petrecovery.service.DetectiveOrderService;
import com.petrecovery.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 侦探订单控制器
 */
@RestController
@RequestMapping("/api/detective/orders")
@Tag(name = "侦探订单管理", description = "侦探意向接单相关接口")
public class DetectiveOrderController {
    
    @Autowired
    private DetectiveOrderService detectiveOrderService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 表达接单意向
     */
    @PostMapping("/intention")
    @Operation(summary = "表达接单意向")
    public ResponseEntity<Map<String, Object>> expressIntention(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> request) {
        
        try {
            Long userId = getUserFromAuth(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "用户未登录"));
            }
            
            Long lostPetId = Long.valueOf(request.get("lostPetId").toString());
            String estimatedCompletion = (String) request.get("estimatedCompletion");
            String serviceDescription = (String) request.get("serviceDescription");
            
            if (lostPetId == null || serviceDescription == null || serviceDescription.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "参数不完整"));
            }
            
            Map<String, Object> result = detectiveOrderService.expressIntention(userId, lostPetId, estimatedCompletion, serviceDescription);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "操作失败：" + e.getMessage()));
        }
    }
    
    /**
     * 撤回接单意向
     */
    @DeleteMapping("/intention/{lostPetId}")
    @Operation(summary = "撤回接单意向")
    public ResponseEntity<Map<String, Object>> withdrawIntention(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long lostPetId) {
        
        try {
            Long userId = getUserFromAuth(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "用户未登录"));
            }
            
            Map<String, Object> result = detectiveOrderService.withdrawIntention(userId, lostPetId);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "操作失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取我的接单列表
     */
    @GetMapping("/my")
    @Operation(summary = "获取我的接单列表")
    public ResponseEntity<Map<String, Object>> getMyOrders(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        try {
            Long userId = getUserFromAuth(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "用户未登录"));
            }
            
            Map<String, Object> result = detectiveOrderService.getMyOrders(userId, status, page, pageSize);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "查询失败：" + e.getMessage()));
        }
    }
    
    /**
     * 查询侦探对特定订单的意向状态
     */
    @GetMapping("/intention-status/{lostPetId}")
    @Operation(summary = "查询意向状态")
    public ResponseEntity<Map<String, Object>> getIntentionStatus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long lostPetId) {
        
        try {
            Long userId = getUserFromAuth(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "用户未登录"));
            }
            
            Map<String, Object> result = detectiveOrderService.getIntentionStatus(userId, lostPetId);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "查询失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取订单数量统计
     */
    @GetMapping("/counts")
    @Operation(summary = "获取订单数量统计")
    public ResponseEntity<Map<String, Object>> getOrderCounts(
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            Long userId = getUserFromAuth(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "用户未登录"));
            }
            
            Map<String, Object> result = detectiveOrderService.getOrderCounts(userId);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "查询失败：" + e.getMessage()));
        }
    }
    
    /**
     * 开始工作
     */
    @PutMapping("/{id}/start")
    @Operation(summary = "开始工作")
    public ResponseEntity<Map<String, Object>> startWork(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        
        try {
            Long userId = getUserFromAuth(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "用户未登录"));
            }
            
            Map<String, Object> result = detectiveOrderService.startWork(userId, id);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "操作失败：" + e.getMessage()));
        }
    }
    
    /**
     * 更新进度
     */
    @PutMapping("/{id}/progress")
    @Operation(summary = "更新进度")
    public ResponseEntity<Map<String, Object>> updateProgress(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        
        try {
            Long userId = getUserFromAuth(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "用户未登录"));
            }
            
            String progressUpdate = (String) request.get("progressUpdate");
            if (progressUpdate == null || progressUpdate.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "进度描述不能为空"));
            }
            
            Map<String, Object> result = detectiveOrderService.updateProgress(userId, id, progressUpdate);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "操作失败：" + e.getMessage()));
        }
    }
    
    /**
     * 完成订单
     */
    @PutMapping("/{id}/complete")
    @Operation(summary = "完成订单")
    public ResponseEntity<Map<String, Object>> completeOrder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        
        try {
            Long userId = getUserFromAuth(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "用户未登录"));
            }
            
            Map<String, Object> result = detectiveOrderService.completeOrder(userId, id);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "操作失败：" + e.getMessage()));
        }
    }
    
    /**
     * 从Authorization头部解析用户ID
     */
    private Long getUserFromAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        
        try {
            String token = authHeader.substring(7);
            String subject = jwtUtil.getSubjectFromToken(token);
            
            if (subject != null && subject.startsWith("user_")) {
                String phone = subject.substring(5);
                Optional<User> userOpt = userRepository.findByPhoneNumber(phone);
                return userOpt.map(User::getId).orElse(null);
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
