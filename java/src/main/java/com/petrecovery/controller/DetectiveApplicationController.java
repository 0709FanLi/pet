package com.petrecovery.controller;

import com.petrecovery.entity.DetectiveApplication;
import com.petrecovery.entity.User;
import com.petrecovery.repository.DetectiveApplicationRepository;
import com.petrecovery.repository.UserRepository;
import com.petrecovery.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/detective")
@Tag(name = "侦探申请", description = "成为宠物侦探的申请接口")
@CrossOrigin(origins = "*")
public class DetectiveApplicationController {

    @Autowired
    private DetectiveApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/apply")
    @Operation(summary = "提交/更新侦探申请")
    public ResponseEntity<Map<String, Object>> apply(@RequestBody Map<String, Object> body, @RequestHeader(value = "Authorization", required = false) String auth) {
        // 优先根据 Authorization: Bearer <token> 解析当前用户，其次用手机号兜底，最后使用首个用户（开发期）
        User user = resolveUserFromAuth(auth);
        if (user == null && body != null) {
            Object phoneObj = body.get("phone");
            if (phoneObj != null) {
                try {
                    Optional<User> byPhone = userRepository.findByPhoneNumber(String.valueOf(phoneObj));
                    if (byPhone.isPresent()) user = byPhone.get();
                } catch (Exception ignored) {}
            }
        }
        if (user == null) {
            user = userRepository.findAll().stream().findFirst().orElse(null);
        }
        if (user == null) {
            Map<String, Object> r = new HashMap<>();
            r.put("code", 400);
            r.put("message", "用户未初始化");
            return ResponseEntity.badRequest().body(r);
        }

        DetectiveApplication app = new DetectiveApplication();
        app.setUser(user);
        app.setRealName((String) body.getOrDefault("realName", ""));
        app.setPhone((String) body.getOrDefault("phone", ""));
        app.setCity((String) body.getOrDefault("city", ""));
        app.setCompanyName((String) body.getOrDefault("companyName", ""));
        app.setAddress((String) body.getOrDefault("address", ""));
        app.setTeamSize(((Number) body.getOrDefault("teamSize", 0)).intValue());
        app.setDevices(toJsonArray(body.get("devices")));
        app.setDevicePhotos(toJsonArray(body.get("devicePhotos")));
        app.setExperienceYears(((Number) body.getOrDefault("experienceYears", 0)).intValue());
        app.setServiceAreas(toJsonArray(body.get("serviceAreas")));
        app.setAvailableTimes(toJsonArray(body.get("availableTimes")));
        app.setBio((String) body.getOrDefault("bio", ""));
        app.setIdCardFront((String) body.getOrDefault("idCardFront", ""));
        app.setIdCardBack((String) body.getOrDefault("idCardBack", ""));
        app.setCertificates(toJsonArray(body.get("certificates")));
        Map payout = (Map) body.getOrDefault("payout", Collections.emptyMap());
        app.setPayoutType((String) payout.getOrDefault("type", ""));
        app.setPayoutAccount((String) payout.getOrDefault("account", ""));
        Map emergency = (Map) body.getOrDefault("emergencyContact", Collections.emptyMap());
        app.setEmergencyContactName((String) emergency.getOrDefault("name", ""));
        app.setEmergencyContactPhone((String) emergency.getOrDefault("phone", ""));
        app.setStatus("pending");
        applicationRepository.save(app);

        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("message", "提交成功");
        Map<String, Object> data = new HashMap<>();
        data.put("applicationId", app.getId());
        data.put("status", app.getStatus());
        res.put("data", data);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/application")
    @Operation(summary = "获取当前侦探申请与状态")
    public ResponseEntity<Map<String, Object>> getApplication(@RequestHeader(value = "Authorization", required = false) String auth) {
        User user = resolveUserFromAuth(auth);
        if (user == null) {
            user = userRepository.findAll().stream().findFirst().orElse(null);
        }
        Map<String, Object> res = new HashMap<>();
        if (user == null) {
            res.put("code", 400);
            res.put("message", "用户未初始化");
            return ResponseEntity.badRequest().body(res);
        }
        DetectiveApplication app = applicationRepository.findTopByUserOrderByCreatedAtDesc(user).orElse(null);
        res.put("code", 200);
        res.put("message", "success");
        Map<String, Object> data = new HashMap<>();
        if (app != null) {
            data.put("applicationId", app.getId());
            data.put("status", app.getStatus());
            data.put("reason", app.getReason());
            data.put("submittedAt", String.valueOf(app.getCreatedAt()));
        }
        res.put("data", data);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/status")
    @Operation(summary = "获取侦探申请状态（简化版）")
    public ResponseEntity<Map<String, Object>> getStatus(@RequestHeader(value = "Authorization", required = false) String auth) {
        User user = resolveUserFromAuth(auth);
        if (user == null) {
            user = userRepository.findAll().stream().findFirst().orElse(null);
        }
        Map<String, Object> res = new HashMap<>();
        String status = "none";
        String reason = null;
        if (user != null) {
            DetectiveApplication app = applicationRepository.findTopByUserOrderByCreatedAtDesc(user).orElse(null);
            if (app != null) {
                status = app.getStatus();
                reason = app.getReason();
            }
        }
        res.put("code", 200);
        Map<String, Object> data = new HashMap<>();
        data.put("status", status);
        if (reason != null) data.put("reason", reason);
        res.put("data", data);
        res.put("message", "success");
        return ResponseEntity.ok(res);
    }

    @GetMapping("/application/review-log")
    @Operation(summary = "获取侦探申请审核记录")
    public ResponseEntity<Map<String, Object>> getReviewLog(@RequestHeader(value = "Authorization", required = false) String auth) {
        // Demo：返回固定示例
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("message", "success");
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(mapOf("time", new Date().toString(), "action", "submit", "comment", "提交申请"));
        res.put("data", list);
        return ResponseEntity.ok(res);
    }

    private String toJsonArray(Object v) {
        if (v == null) return "[]";
        if (v instanceof String) return (String) v;
        if (v instanceof List) {
            StringBuilder sb = new StringBuilder("[");
            List list = (List) v;
            for (int i = 0; i < list.size(); i++) {
                Object it = list.get(i);
                sb.append('"').append(String.valueOf(it).replace("\"", "\\\"")).append('"');
                if (i < list.size() - 1) sb.append(',');
            }
            return sb.append(']').toString();
        }
        return String.valueOf(v);
    }

    private Map<String, Object> mapOf(Object... kv) {
        Map<String, Object> m = new HashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) m.put(String.valueOf(kv[i]), kv[i + 1]);
        return m;
    }

    /**
     * 从 Authorization 头部解析当前用户（Bearer token），失败则返回 null。
     */
    private User resolveUserFromAuth(String authHeader) {
        if (authHeader == null) return null;
        String lower = authHeader.toLowerCase(Locale.ROOT);
        if (!lower.startsWith("bearer ")) return null;
        String token = authHeader.substring(7).trim();
        try {
            String subject = jwtUtil.getUsernameFromToken(token);
            if (subject == null || subject.isEmpty()) return null;
            // admin 登录令牌可能是 "admin:xxx"，普通用户就是用户名
            if (subject.startsWith("admin:")) subject = subject.substring(6);
            Optional<User> u = userRepository.findByUsername(subject);
            return u.orElse(null);
        } catch (Exception ignored) {
            return null;
        }
    }
}


