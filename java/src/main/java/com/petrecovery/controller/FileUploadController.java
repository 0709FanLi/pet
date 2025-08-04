package com.petrecovery.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/upload")
@Tag(name = "文件上传", description = "文件上传相关接口")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @PostMapping("/image")
    @Operation(summary = "上传单张图片", description = "上传单张图片文件")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file,
                                                          @RequestParam(value = "type", defaultValue = "pet") String type) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                response.put("code", 400);
                response.put("message", "文件不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 验证文件大小
            if (file.getSize() > MAX_FILE_SIZE) {
                response.put("code", 400);
                response.put("message", "文件大小不能超过10MB");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 验证文件扩展名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !isValidFileExtension(originalFilename)) {
                response.put("code", 400);
                response.put("message", "不支持的文件格式，只支持: " + String.join(", ", ALLOWED_EXTENSIONS));
                return ResponseEntity.badRequest().body(response);
            }
            
            // 创建上传目录
            File uploadDir = new File(UPLOAD_DIR + type);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 生成唯一文件名
            String fileExtension = getFileExtension(originalFilename);
            String fileName = UUID.randomUUID().toString() + "." + fileExtension;
            String relativePath = type + "/" + fileName;
            
            // 保存文件
            Path filePath = Paths.get(UPLOAD_DIR + relativePath);
            Files.write(filePath, file.getBytes());
            
            // 构建访问URL
            String fileUrl = "/uploads/" + relativePath;
            
            response.put("code", 200);
            response.put("message", "上传成功");
            Map<String, Object> data = new HashMap<>();
            data.put("url", fileUrl);
            data.put("filename", fileName);
            data.put("originalName", originalFilename);
            data.put("size", file.getSize());
            data.put("type", type);
            response.put("data", data);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("code", 500);
            response.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/images")
    @Operation(summary = "批量上传图片", description = "批量上传多张图片文件")
    public ResponseEntity<Map<String, Object>> uploadImages(@RequestParam("files") MultipartFile[] files,
                                                           @RequestParam(value = "type", defaultValue = "pet") String type) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> uploadResults = new ArrayList<>();
        
        try {
            if (files == null || files.length == 0) {
                response.put("code", 400);
                response.put("message", "请选择要上传的文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 限制批量上传数量
            if (files.length > 10) {
                response.put("code", 400);
                response.put("message", "一次最多只能上传10张图片");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 创建上传目录
            File uploadDir = new File(UPLOAD_DIR + type);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        // 验证文件大小
                        if (file.getSize() > MAX_FILE_SIZE) {
                            Map<String, Object> errorResult = new HashMap<>();
                            errorResult.put("success", false);
                            errorResult.put("message", "文件 " + file.getOriginalFilename() + " 大小超过10MB");
                            uploadResults.add(errorResult);
                            continue;
                        }
                        
                        // 验证文件扩展名
                        String originalFilename = file.getOriginalFilename();
                        if (originalFilename == null || !isValidFileExtension(originalFilename)) {
                            Map<String, Object> errorResult = new HashMap<>();
                            errorResult.put("success", false);
                            errorResult.put("message", "文件 " + originalFilename + " 格式不支持");
                            uploadResults.add(errorResult);
                            continue;
                        }
                        
                        // 生成唯一文件名
                        String fileExtension = getFileExtension(originalFilename);
                        String fileName = UUID.randomUUID().toString() + "." + fileExtension;
                        String relativePath = type + "/" + fileName;
                        
                        // 保存文件
                        Path filePath = Paths.get(UPLOAD_DIR + relativePath);
                        Files.write(filePath, file.getBytes());
                        
                        // 构建访问URL
                        String fileUrl = "/uploads/" + relativePath;
                        
                        Map<String, Object> successResult = new HashMap<>();
                        successResult.put("success", true);
                        successResult.put("url", fileUrl);
                        successResult.put("filename", fileName);
                        successResult.put("originalName", originalFilename);
                        successResult.put("size", file.getSize());
                        uploadResults.add(successResult);
                        
                    } catch (IOException e) {
                        Map<String, Object> errorResult = new HashMap<>();
                        errorResult.put("success", false);
                        errorResult.put("message", "文件 " + file.getOriginalFilename() + " 上传失败: " + e.getMessage());
                        uploadResults.add(errorResult);
                    }
                }
            }
            
            response.put("code", 200);
            response.put("message", "批量上传完成");
            response.put("data", uploadResults);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "批量上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 验证文件扩展名
     */
    private boolean isValidFileExtension(String filename) {
        String extension = getFileExtension(filename);
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}