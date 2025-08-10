package com.petrecovery.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.petrecovery.entity.DetectiveApplication;
import com.petrecovery.entity.User;
import com.petrecovery.repository.DetectiveApplicationRepository;
import com.petrecovery.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/detectives")
@Tag(name = "后台-侦探管理", description = "侦探列表/查询")
@CrossOrigin(origins = "*")
public class AdminDetectiveController {

    @Autowired
    private DetectiveApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @Operation(summary = "侦探列表")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status
    ) {
        // 简化：以已通过或最近一次申请存在的用户作为侦探候选
        List<DetectiveApplication> all = applicationRepository.findAll();
        // 这里模拟状态，真实应有独立侦探表或状态字段
        List<Map<String, Object>> rows = new ArrayList<>();
        for (DetectiveApplication app : all) {
            User u = app.getUser();
            if (u == null) continue;
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId());
            m.put("realName", app.getRealName());
            m.put("phone", app.getPhone());
            m.put("status", "active");
            m.put("orders", 0);
            m.put("successRate", "-");
            m.put("createdAt", String.valueOf(u.getCreatedAt()));
            rows.add(m);
        }
        // 关键词过滤
        if (keyword != null && !keyword.isEmpty()) {
            rows = rows.stream().filter(m -> String.valueOf(m.get("realName")).contains(keyword)
                    || String.valueOf(m.get("phone")).contains(keyword)).collect(Collectors.toList());
        }
        int total = rows.size();
        int from = Math.max(0, (page - 1) * pageSize);
        int to = Math.min(total, from + pageSize);
        List<Map<String, Object>> pageList = from < to ? rows.subList(from, to) : Collections.emptyList();

        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        Map<String, Object> data = new HashMap<>();
        data.put("list", pageList);
        data.put("total", total);
        res.put("data", data);
        return ResponseEntity.ok(res);
    }
}


