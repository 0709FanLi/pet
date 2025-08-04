package com.petrecovery.controller;

import com.petrecovery.entity.LostPet;
import com.petrecovery.service.LostPetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@RestController
@RequestMapping("/api/lost-pets")
@Tag(name = "丢失宠物管理", description = "丢失宠物信息相关接口")
@CrossOrigin(origins = "*")
public class LostPetController {

    @Autowired
    private LostPetService lostPetService;

    @PostMapping
    @Operation(summary = "发布丢失宠物信息", description = "创建新的丢失宠物记录")
    public ResponseEntity<LostPet> createLostPet(@RequestParam Long userId,
                                                @RequestParam String petName,
                                                @RequestParam String petType,
                                                @RequestParam(required = false) String petBreed,
                                                @RequestParam String petDescription,
                                                @RequestParam String lostLocation,
                                                @RequestParam String lostTime,
                                                @RequestParam String contactInfo,
                                                @RequestParam(required = false) String reward,
                                                @RequestParam(required = false) String[] images) {
        LostPet lostPet = new LostPet();
        lostPet.setPetName(petName);
        lostPet.setPetType(petType);
        lostPet.setPetBreed(petBreed);
        lostPet.setPetDescription(petDescription);
        lostPet.setLostLocation(lostLocation);
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
}