package com.petrecovery.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
@Tag(name = "通用配置", description = "平台通用下拉数据等配置接口")
@CrossOrigin(origins = "*")
public class ConfigController {

    @GetMapping("/pet-types")
    @Operation(summary = "获取宠物种类", description = "返回宠物种类列表")
    public ResponseEntity<Map<String, Object>> getPetTypes() {
        List<String> types = Arrays.asList("猫", "狗", "其它");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", types);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/cities")
    @Operation(summary = "获取城市列表", description = "返回可选城市列表")
    public ResponseEntity<Map<String, Object>> getCities() {
        List<String> cities = Arrays.asList("北京", "上海", "广州", "深圳", "杭州", "厦门", "郑州");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", cities);
        return ResponseEntity.ok(result);
    }
}