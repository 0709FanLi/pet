package com.petrecovery.controller;

import com.petrecovery.entity.User;
import com.petrecovery.repository.UserRepository;
import com.petrecovery.service.NotificationService;
import com.petrecovery.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * 消息通知控制器
 */
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "消息通知管理", description = "消息通知相关接口")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 获取消息列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取消息列表")
    public ResponseEntity<Map<String, Object>> getNotificationList(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(defaultValue = "") String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long userId) {
        
        try {
            Long currentUserId = userId;
            
            // 如果没有传userId参数，尝试从认证头获取
            if (currentUserId == null && authHeader != null) {
                currentUserId = getUserFromAuth(authHeader);
            }
            
            // 临时调试：如果仍然没有userId，使用默认值
            if (currentUserId == null) {
                // 从token中尝试解析，如果失败则返回错误
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    currentUserId = getUserFromAuth(authHeader);
                }
                if (currentUserId == null) {
                    return ResponseEntity.status(401).body(Map.of("success", false, "message", "用户未登录或用户ID无效"));
                }
            }
            
            Map<String, Object> result = notificationService.getNotificationList(currentUserId, type, page, pageSize);
            
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
     * 获取消息详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取消息详情")
    public ResponseEntity<Map<String, Object>> getNotificationDetail(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        
        try {
            Long userId = getUserFromAuth(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "用户未登录"));
            }
            
            // 获取详情的同时自动标记为已读
            Map<String, Object> result = notificationService.markAsRead(userId, id);
            
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
     * 获取未读数量
     */
    @GetMapping("/unread-count")
    @Operation(summary = "获取未读数量")
    public ResponseEntity<Map<String, Object>> getUnreadCount(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Long userId) {
        
        try {
            Long currentUserId = userId;
            
            // 如果没有传userId参数，尝试从认证头获取
            if (currentUserId == null && authHeader != null) {
                currentUserId = getUserFromAuth(authHeader);
            }
            
            if (currentUserId == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "用户未登录或用户ID无效"));
            }
            
            Map<String, Object> result = notificationService.getUnreadCount(currentUserId);
            
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
     * 标记消息为已读
     */
    @PutMapping("/{id}/read")
    @Operation(summary = "标记消息为已读")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        
        try {
            Long userId = getUserFromAuth(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "用户未登录"));
            }
            
            Map<String, Object> result = notificationService.markAsRead(userId, id);
            
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
     * 全部标记为已读
     */
    @PutMapping("/read-all")
    @Operation(summary = "全部标记为已读")
    public ResponseEntity<Map<String, Object>> markAllAsRead(
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            Long userId = getUserFromAuth(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "用户未登录"));
            }
            
            Map<String, Object> result = notificationService.markAllAsRead(userId);
            
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
     * 删除消息
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除消息")
    public ResponseEntity<Map<String, Object>> deleteNotification(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        
        try {
            Long userId = getUserFromAuth(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "用户未登录"));
            }
            
            Map<String, Object> result = notificationService.deleteNotification(userId, id);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "删除失败：" + e.getMessage()));
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
