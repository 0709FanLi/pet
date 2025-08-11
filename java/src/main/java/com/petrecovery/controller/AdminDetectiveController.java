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
@Tag(name = "后台-侦探管理", description = "侦探列表/查询/状态变更")
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
        // 聚合为每个用户最近一次申请
        List<DetectiveApplication> all = applicationRepository.findAll();
        Map<Long, DetectiveApplication> latestByUser = new HashMap<>();
        for (DetectiveApplication app : all) {
            User u = app.getUser();
            if (u == null) continue;
            DetectiveApplication exist = latestByUser.get(u.getId());
            if (exist == null || (app.getCreatedAt() != null && exist.getCreatedAt() != null && app.getCreatedAt().isAfter(exist.getCreatedAt()))) {
                latestByUser.put(u.getId(), app);
            }
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        for (DetectiveApplication app : latestByUser.values()) {
            String st = app.getStatus();
            // 默认仅展示 通过/不通过/下架 三类，如需包含 pending 可传入 status=pending
            if (status == null || status.isEmpty()) {
                if ("pending".equalsIgnoreCase(st)) continue;
            } else {
                if (!status.equalsIgnoreCase(st)) continue;
            }
            if (keyword != null && !keyword.isEmpty()) {
                if (!(String.valueOf(app.getRealName()).contains(keyword) || String.valueOf(app.getPhone()).contains(keyword))) {
                    continue;
                }
            }
            User u = app.getUser();
            Map<String, Object> m = new HashMap<>();
            m.put("id", app.getId()); // 应用ID
            m.put("userId", u.getId());
            m.put("realName", app.getRealName());
            m.put("phone", app.getPhone());
            m.put("status", st); // approved|rejected|disabled
            m.put("orders", 0);
            m.put("successRate", "-");
            m.put("createdAt", String.valueOf(app.getCreatedAt()));
            rows.add(m);
        }

        // 排序（最新在前）
        rows.sort((a, b) -> String.valueOf(b.get("createdAt")).compareTo(String.valueOf(a.get("createdAt"))));

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

    @PutMapping("/{id}/approve")
    @Operation(summary = "通过申请")
    public ResponseEntity<Map<String, Object>> approve(@PathVariable Long id) {
        return changeStatus(id, "approved", null);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "拒绝申请")
    public ResponseEntity<Map<String, Object>> reject(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> body) {
        String reason = body == null ? null : String.valueOf(body.getOrDefault("reason", ""));
        return changeStatus(id, "rejected", reason);
    }

    @PutMapping("/{id}/disable")
    @Operation(summary = "下架侦探")
    public ResponseEntity<Map<String, Object>> disable(@PathVariable Long id) {
        return changeStatus(id, "disabled", null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "侦探申请详情")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable Long id) {
        Optional<DetectiveApplication> opt = applicationRepository.findById(id);
        Map<String, Object> res = new HashMap<>();
        if (!opt.isPresent()) {
            res.put("code", 404);
            res.put("message", "申请不存在");
            return ResponseEntity.status(404).body(res);
        }
        DetectiveApplication app = opt.get();
        Map<String, Object> data = new HashMap<>();
        data.put("id", app.getId());
        Map<String, Object> user = new HashMap<>();
        if (app.getUser() != null) {
            user.put("id", app.getUser().getId());
            user.put("username", String.valueOf(app.getUser().getUsername()));
            user.put("phoneNumber", String.valueOf(app.getUser().getPhoneNumber()));
        }
        data.put("user", user);
        data.put("realName", app.getRealName());
        data.put("phone", app.getPhone());
        data.put("city", app.getCity());
        data.put("teamSize", app.getTeamSize());
        data.put("experienceYears", app.getExperienceYears());
        data.put("serviceAreas", app.getServiceAreas());
        data.put("availableTimes", app.getAvailableTimes());
        data.put("bio", app.getBio());
        data.put("devices", app.getDevices());
        data.put("devicePhotos", app.getDevicePhotos());
        data.put("idCardFront", app.getIdCardFront());
        data.put("idCardBack", app.getIdCardBack());
        data.put("certificates", app.getCertificates());
        data.put("payoutType", app.getPayoutType());
        data.put("payoutAccount", app.getPayoutAccount());
        data.put("status", app.getStatus());
        data.put("reason", app.getReason());
        data.put("createdAt", String.valueOf(app.getCreatedAt()));
        data.put("updatedAt", String.valueOf(app.getUpdatedAt()));

        res.put("code", 200);
        res.put("message", "success");
        res.put("data", data);
        return ResponseEntity.ok(res);
    }

    private ResponseEntity<Map<String, Object>> changeStatus(Long id, String status, String reason) {
        Optional<DetectiveApplication> opt = applicationRepository.findById(id);
        Map<String, Object> res = new HashMap<>();
        if (!opt.isPresent()) {
            res.put("code", 404);
            res.put("message", "申请不存在");
            return ResponseEntity.status(404).body(res);
        }
        DetectiveApplication app = opt.get();
        app.setStatus(status);
        if (reason != null) app.setReason(reason);
        applicationRepository.save(app);
        res.put("code", 200);
        res.put("message", "success");
        Map<String, Object> data = new HashMap<>();
        data.put("id", app.getId());
        data.put("status", app.getStatus());
        data.put("reason", app.getReason());
        res.put("data", data);
        return ResponseEntity.ok(res);
    }
}


