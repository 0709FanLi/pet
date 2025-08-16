package com.petrecovery.controller;

import com.petrecovery.entity.User;
import com.petrecovery.repository.UserRepository;
import com.petrecovery.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/avatar")
@CrossOrigin(origins = "*")
public class AvatarController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // 从配置文件读取上传路径，如果没有配置则使用默认值
    @Value("${file.upload.path:src/main/resources/static/uploads/}")
    private String uploadPath;

    /**
     * 上传头像
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @RequestParam("avatar") MultipartFile file,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取用户信息
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                response.put("success", false);
                response.put("message", "未授权访问");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            User user = userRepository.findByUsername(username).orElse(null);
            
            if (user == null) {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // 验证文件
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "上传文件不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !isValidImageType(contentType)) {
                response.put("success", false);
                response.put("message", "只支持 jpg、jpeg、png、gif 格式的图片");
                return ResponseEntity.badRequest().body(response);
            }

            // 验证文件大小 (5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "文件大小不能超过5MB");
                return ResponseEntity.badRequest().body(response);
            }

            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                response.put("success", false);
                response.put("message", "文件名无效");
                return ResponseEntity.badRequest().body(response);
            }
            originalFilename = StringUtils.cleanPath(originalFilename);
            String fileExtension = getFileExtension(originalFilename);
            String newFileName = "avatar_" + user.getId() + "_" + UUID.randomUUID().toString() + fileExtension;
            
            // 保存文件
            Path filePath = uploadDir.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath);

            // 删除旧头像文件（如果存在）
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                deleteOldAvatar(user.getAvatar());
            }

            // 更新数据库中的头像路径
            String avatarUrl = "/uploads/" + newFileName;
            user.setAvatar(avatarUrl);
            userRepository.save(user);

            response.put("success", true);
            response.put("message", "头像上传成功");
            response.put("avatarUrl", avatarUrl);
            response.put("fullUrl", "http://192.168.1.18:8080" + avatarUrl); // 返回完整URL用于前端显示
            
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系统错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取当前用户头像信息
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getAvatarInfo(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取用户信息
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                response.put("success", false);
                response.put("message", "未授权访问");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            User user = userRepository.findByUsername(username).orElse(null);
            
            if (user == null) {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("success", true);
            response.put("avatar", user.getAvatar());
            response.put("fullUrl", user.getAvatar() != null ? "http://localhost:8080" + user.getAvatar() : null);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取头像信息失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除头像
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteAvatar(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取用户信息
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                response.put("success", false);
                response.put("message", "未授权访问");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            User user = userRepository.findByUsername(username).orElse(null);
            
            if (user == null) {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // 删除文件
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                deleteOldAvatar(user.getAvatar());
            }

            // 清空数据库中的头像字段
            user.setAvatar(null);
            userRepository.save(user);

            response.put("success", true);
            response.put("message", "头像删除成功");
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除头像失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 验证图片类型
     */
    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/gif");
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex) : "";
    }

    /**
     * 删除旧头像文件
     */
    private void deleteOldAvatar(String avatarPath) {
        try {
            if (avatarPath.startsWith("/uploads/")) {
                String fileName = avatarPath.substring("/uploads/".length());
                Path filePath = Paths.get(uploadPath, fileName);
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            // 删除失败不影响主流程，记录日志即可
            System.err.println("删除旧头像文件失败: " + e.getMessage());
        }
    }
}
