package com.petrecovery.controller;

import com.petrecovery.entity.LostPet;
import com.petrecovery.entity.User;
import com.petrecovery.repository.UserRepository;
import com.petrecovery.service.DetectiveOrderService;
import com.petrecovery.service.LostPetService;
import com.petrecovery.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
// removed unused imports
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/lost-pets")
@Tag(name = "丢失宠物管理", description = "丢失宠物信息相关接口")
@CrossOrigin(origins = "*")
public class LostPetController {

    @Autowired
    private LostPetService lostPetService;
    
    @Autowired
    private DetectiveOrderService detectiveOrderService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @Operation(summary = "发布丢失宠物信息", description = "创建新的丢失宠物记录")
    public ResponseEntity<LostPet> createLostPet(@RequestParam Long userId,
                                                @RequestParam String petName,
                                                @RequestParam String petType,
                                                @RequestParam(required = false) String petBreed,
                                                @RequestParam String petDescription,
                                                @RequestParam String city,
                                                @RequestParam String address,
                                                @RequestParam String lostTime,
                                                @RequestParam String contactInfo,
                                                @RequestParam(required = false) String reward,
                                                @RequestParam(required = false) String[] images) {
        LostPet lostPet = new LostPet();
        lostPet.setPetName(petName);
        lostPet.setPetType(petType);
        lostPet.setPetBreed(petBreed);
        lostPet.setPetDescription(petDescription);
        lostPet.setCity(city);
        lostPet.setAddress(address);
        // 兼容拼接
        lostPet.setLostLocation((city != null ? city : "") + (address != null ? (" " + address) : ""));
        // 解析时间字符串为LocalDateTime
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            lostPet.setLostTime(LocalDateTime.parse(lostTime, formatter));
        } catch (Exception e) {
            // 如果解析失败，使用当前时间
            lostPet.setLostTime(LocalDateTime.now());
        }
        lostPet.setContactInfo(contactInfo);
        lostPet.setReward(reward);
        // 处理图片URL数组
        List<String> imageUrls = new ArrayList<>();
        if (images != null && images.length > 0) {
            for (String imageUrl : images) {
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    imageUrls.add(imageUrl.trim());
                }
            }
        }
        
        // 将图片URL数组转换为JSON字符串存储
        try {
            if (!imageUrls.isEmpty()) {
                lostPet.setImages("[\"" + String.join("\",\"", imageUrls) + "\"]");
            } else {
                lostPet.setImages("[]");
            }
        } catch (Exception e) {
            lostPet.setImages("[]");
        }
        
        lostPet.setStatus("lost"); // 设置默认状态
        LostPet created = lostPetService.createLostPet(lostPet, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * 新增：JSON 提交方式，便于前端直接以 application/json 发送
     */
    @PostMapping(value = "/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "发布丢失宠物信息(JSON)", description = "接收 JSON 请求创建记录")
    public ResponseEntity<LostPet> createLostPetJson(@RequestBody CreateLostPetRequest req) {
        LostPet lostPet = new LostPet();
        lostPet.setPetName(req.getPetName());
        lostPet.setPetType(req.getPetType());
        lostPet.setPetBreed(req.getPetBreed());
        lostPet.setPetDescription(req.getPetDescription());
        lostPet.setCity(req.getCity());
        lostPet.setAddress(req.getAddress());
        // 兼容拼接
        String city = req.getCity();
        String address = req.getAddress();
        lostPet.setLostLocation((city != null ? city : "") + (address != null ? (" " + address) : ""));
        // 解析时间
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            lostPet.setLostTime(LocalDateTime.parse(req.getLostTime(), formatter));
        } catch (Exception e) {
            lostPet.setLostTime(LocalDateTime.now());
        }
        lostPet.setContactInfo(req.getContactInfo());
        lostPet.setReward(req.getReward());
        // 处理图片
        List<String> imageUrls = new ArrayList<>();
        if (req.getImages() != null) {
            for (String imageUrl : req.getImages()) {
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    imageUrls.add(imageUrl.trim());
                }
            }
        }
        if (!imageUrls.isEmpty()) {
            lostPet.setImages("[\"" + String.join("\",\"", imageUrls) + "\"]");
        } else {
            lostPet.setImages("[]");
        }
        lostPet.setStatus("lost");
        LostPet created = lostPetService.createLostPet(lostPet, req.getUserId());
        return ResponseEntity.ok(created);
    }

    public static class CreateLostPetRequest {
        private Long userId;
        private String petName;
        private String petType;
        private String petBreed;
        private String petDescription;
        private String city;      // 丢失城市
        private String address;   // 具体地点（门牌号）
        private String lostLocation; // 兼容老字段
        private String lostTime; // 格式：yyyy-MM-dd HH:mm:ss
        private String contactInfo;
        private String reward;
        private List<String> images;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getPetName() { return petName; }
        public void setPetName(String petName) { this.petName = petName; }
        public String getPetType() { return petType; }
        public void setPetType(String petType) { this.petType = petType; }
        public String getPetBreed() { return petBreed; }
        public void setPetBreed(String petBreed) { this.petBreed = petBreed; }
        public String getPetDescription() { return petDescription; }
        public void setPetDescription(String petDescription) { this.petDescription = petDescription; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getLostLocation() { return lostLocation; }
        public void setLostLocation(String lostLocation) { this.lostLocation = lostLocation; }
        public String getLostTime() { return lostTime; }
        public void setLostTime(String lostTime) { this.lostTime = lostTime; }
        public String getContactInfo() { return contactInfo; }
        public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
        public String getReward() { return reward; }
        public void setReward(String reward) { this.reward = reward; }
        public List<String> getImages() { return images; }
        public void setImages(List<String> images) { this.images = images; }
    }

    @GetMapping
    @Operation(summary = "获取所有丢失宠物列表", description = "获取当前所有丢失状态的宠物列表")
    public ResponseEntity<List<LostPet>> getAllLostPets() {
        List<LostPet> lostPets = lostPetService.getAllLostPets();
        return ResponseEntity.ok(lostPets);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户丢失宠物列表", description = "获取指定用户的丢失宠物记录")
    public ResponseEntity<List<LostPet>> getUserLostPets(@PathVariable Long userId) {
        List<LostPet> lostPets = lostPetService.getUserLostPets(userId);
        return ResponseEntity.ok(lostPets);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取丢失宠物详情", description = "根据ID获取丢失宠物信息")
    public ResponseEntity<LostPet> getLostPetById(@PathVariable Long id) {
        Optional<LostPet> lostPet = lostPetService.getLostPetById(id);
        return lostPet.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新丢失宠物信息", description = "更新指定丢失宠物记录")
    public ResponseEntity<LostPet> updateLostPet(@PathVariable Long id, @RequestBody LostPet updatedLostPet) {
        LostPet updated = lostPetService.updateLostPet(id, updatedLostPet);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除丢失宠物记录", description = "删除指定丢失宠物信息")
    public ResponseEntity<Void> deleteLostPet(@PathVariable Long id) {
        lostPetService.deleteLostPet(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 获取宠物的意向侦探列表
     */
    @GetMapping("/{id}/intentions")
    @Operation(summary = "获取宠物的意向侦探列表")
    public ResponseEntity<Map<String, Object>> getPetIntentions(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        
        try {
            Long userId = getUserFromAuth(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "用户未登录"));
            }
            
            // 验证权限：只有宠物主人才能查看意向列表
            Optional<LostPet> lostPetOpt = lostPetService.getLostPetById(id);
            if (!lostPetOpt.isPresent()) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "宠物信息不存在"));
            }
            
            LostPet lostPet = lostPetOpt.get();
            if (!userId.equals(lostPet.getUser().getId())) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "无权限查看"));
            }
            
            Map<String, Object> result = detectiveOrderService.getPetIntentions(id);
            
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
     * 宠物主人确认侦探
     */
    @PostMapping("/{id}/confirm-detective")
    @Operation(summary = "宠物主人确认侦探")
    public ResponseEntity<Map<String, Object>> confirmDetective(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        
        try {
            Long userId = getUserFromAuth(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "用户未登录"));
            }
            
            Long detectiveId = Long.valueOf(request.get("detectiveId").toString());
            
            Map<String, Object> result = detectiveOrderService.confirmDetective(userId, id, detectiveId);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "确认失败：" + e.getMessage()));
        }
    }
    
    /**
     * 查询订单接单状态
     */
    @GetMapping("/{id}/order-status")
    @Operation(summary = "查询订单接单状态")
    public ResponseEntity<Map<String, Object>> getOrderStatus(@PathVariable Long id) {
        try {
            Map<String, Object> result = detectiveOrderService.getIntentionStatus(null, id);
            
            // 移除个人相关的状态信息，只保留公共信息
            result.remove("canExpress");
            result.remove("status");
            result.remove("orderId");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "查询失败：" + e.getMessage()));
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