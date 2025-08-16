package com.petrecovery.controller;

import com.petrecovery.service.DetectiveApplicationCacheService;
import com.petrecovery.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 宠物侦探申请信息缓存控制器
 */
@RestController
@RequestMapping("/api/detective/cache")
@CrossOrigin(origins = "*")
public class DetectiveApplicationCacheController {
    
    @Autowired
    private DetectiveApplicationCacheService cacheService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 保存申请信息缓存
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveApplicationCache(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求头获取JWT token
            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("success", false);
                response.put("message", "未提供有效的认证信息");
                return ResponseEntity.status(401).body(response);
            }
            
            String token = authHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            
            if (username == null) {
                response.put("success", false);
                response.put("message", "认证信息无效");
                return ResponseEntity.status(401).body(response);
            }
            
            // 保存缓存信息
            cacheService.saveApplicationCache(username, request);
            
            response.put("success", true);
            response.put("message", "申请信息已保存");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "保存失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取申请信息缓存
     */
    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> getApplicationCache(HttpServletRequest httpRequest) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求头获取JWT token
            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("success", false);
                response.put("message", "未提供有效的认证信息");
                return ResponseEntity.status(401).body(response);
            }
            
            String token = authHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            
            if (username == null) {
                response.put("success", false);
                response.put("message", "认证信息无效");
                return ResponseEntity.status(401).body(response);
            }
            
            // 获取缓存信息
            Map<String, Object> cacheData = cacheService.getApplicationCache(username);
            
            if (cacheData == null) {
                response.put("success", false);
                response.put("message", "暂无缓存信息");
                response.put("data", null);
            } else {
                response.put("success", true);
                response.put("message", "获取成功");
                response.put("data", cacheData);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 检查是否有缓存信息
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkCachedApplication(HttpServletRequest httpRequest) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求头获取JWT token
            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("success", false);
                response.put("message", "未提供有效的认证信息");
                response.put("hasCache", false);
                return ResponseEntity.status(401).body(response);
            }
            
            String token = authHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            
            if (username == null) {
                response.put("success", false);
                response.put("message", "认证信息无效");
                response.put("hasCache", false);
                return ResponseEntity.status(401).body(response);
            }
            
            // 检查是否有缓存
            boolean hasCache = cacheService.hasCachedApplication(username);
            
            response.put("success", true);
            response.put("hasCache", hasCache);
            response.put("message", hasCache ? "有缓存信息" : "暂无缓存信息");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "检查失败: " + e.getMessage());
            response.put("hasCache", false);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 清除申请信息缓存
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearApplicationCache(HttpServletRequest httpRequest) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求头获取JWT token
            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("success", false);
                response.put("message", "未提供有效的认证信息");
                return ResponseEntity.status(401).body(response);
            }
            
            String token = authHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            
            if (username == null) {
                response.put("success", false);
                response.put("message", "认证信息无效");
                return ResponseEntity.status(401).body(response);
            }
            
            // 清除缓存信息
            cacheService.clearApplicationCache(username);
            
            response.put("success", true);
            response.put("message", "缓存信息已清除");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "清除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
