package com.petrecovery.controller;

import com.petrecovery.entity.LostPet;
import com.petrecovery.service.LostPetService;
import com.petrecovery.repository.LostPetRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/notices")
@Tag(name = "启事审核", description = "后台启事审核相关接口（基于现有 LostPet 数据模拟）")
@CrossOrigin(origins = "*")
public class AdminNoticeController {

    @Autowired
    private LostPetService lostPetService;
    
    @Autowired
    private LostPetRepository lostPetRepository;

    @GetMapping("/pending")
    @Operation(summary = "查询待审核启事列表", description = "当前以 LostPet 中 status=lost 的数据作为待审核数据源，支持关键词与城市筛选，简单分页")
    public ResponseEntity<Map<String, Object>> getPendingNotices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city
    ) {
        // 从现有服务拉取 lost 状态数据，并按需过滤
        List<LostPet> all = lostPetService.getAllLostPets();

        String kw = keyword == null ? null : keyword.trim().toLowerCase(Locale.ROOT);
        String cityFilter = city == null ? null : city.trim();

        List<LostPet> filtered = all.stream()
                .filter(lp -> kw == null || kw.isEmpty() ||
                        (safe(lp.getPetName()).toLowerCase(Locale.ROOT).contains(kw) ||
                         safe(lp.getPetDescription()).toLowerCase(Locale.ROOT).contains(kw)))
                .filter(lp -> cityFilter == null || cityFilter.isEmpty() ||
                        safe(lp.getLostLocation()).contains(cityFilter))
                .collect(Collectors.toList());

        int total = filtered.size();
        int fromIndex = Math.max(0, Math.min((page - 1) * pageSize, total));
        int toIndex = Math.max(fromIndex, Math.min(fromIndex + pageSize, total));
        List<LostPet> pageList = filtered.subList(fromIndex, toIndex);

        // 映射为管理端需要的精简字段
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> list = new ArrayList<>();
        for (LostPet lp : pageList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", lp.getId());
            // 标题/摘要：名称 + 描述前 20 字
            String desc = safe(lp.getPetDescription());
            String shortDesc = desc.length() > 20 ? desc.substring(0, 20) + "…" : desc;
            String title = safe(lp.getPetName());
            if (!shortDesc.isEmpty()) {
                title = title.isEmpty() ? shortDesc : (title + " · " + shortDesc);
            }
            item.put("title", title);
            item.put("city", safe(lp.getLostLocation()));
            item.put("reward", safe(lp.getReward()));
            item.put("createdAt", lp.getCreatedAt() == null ? "" : dtf.format(lp.getCreatedAt()));
            list.add(item);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);

        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 0);
        resp.put("message", "ok");
        resp.put("data", data);
        return ResponseEntity.ok(resp);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    @GetMapping("/cities")
    @Operation(summary = "获取审核用城市列表", description = "与 /api/config/cities 一致，供管理端下拉使用")
    public ResponseEntity<Map<String, Object>> getCities() {
        List<String> cities = java.util.Arrays.asList("北京", "上海", "广州", "深圳", "杭州", "厦门", "郑州");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("message", "ok");
        result.put("data", cities);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询启事详情", description = "基于 LostPet 数据返回启事详情，含基本信息与图片、发布人等")
    public ResponseEntity<Map<String, Object>> getNoticeDetail(@PathVariable Long id) {
        Map<String, Object> resp = new HashMap<>();
        LostPet lp = lostPetRepository.findById(id).orElse(null);
        if (lp == null) {
            resp.put("code", 404);
            resp.put("message", "not found");
            return ResponseEntity.status(404).body(resp);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Map<String, Object> detail = new HashMap<>();
        detail.put("id", lp.getId());
        detail.put("title", safe(lp.getPetName()));
        detail.put("content", safe(lp.getPetDescription()));
        detail.put("city", safe(lp.getLostLocation()));
        detail.put("location", safe(lp.getLostLocation()));
        detail.put("reward", safe(lp.getReward()));
        detail.put("lostTime", lp.getLostTime() == null ? "" : dtf.format(lp.getLostTime()));
        detail.put("createdAt", lp.getCreatedAt() == null ? "" : dtf.format(lp.getCreatedAt()));

        java.util.List<String> imgs = new java.util.ArrayList<>();
        try {
            String json = safe(lp.getImages());
            if (!json.isEmpty()) {
                json = json.trim();
                if (json.startsWith("[") && json.endsWith("]")) {
                    String body = json.substring(1, json.length() - 1).trim();
                    if (!body.isEmpty()) {
                        for (String part : body.split(",")) {
                            String p = part.trim();
                            if (p.startsWith("\"") && p.endsWith("\"")) {
                                p = p.substring(1, p.length() - 1);
                            }
                            if (!p.isEmpty()) imgs.add(p);
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        detail.put("images", imgs);

        Map<String, Object> user = new HashMap<>();
        if (lp.getUser() != null) {
            user.put("id", lp.getUser().getId());
            user.put("username", safe(lp.getUser().getUsername()));
            user.put("phoneNumber", safe(lp.getUser().getPhoneNumber()));
        }
        detail.put("user", user);

        resp.put("code", 0);
        resp.put("message", "ok");
        resp.put("data", detail);
        return ResponseEntity.ok(resp);
    }
    @PutMapping("/{id}/approve")
    @Operation(summary = "审核通过", description = "将启事标记为已通过（示例：更新 LostPet.status=approved）")
    public ResponseEntity<Map<String, Object>> approve(@PathVariable Long id) {
        Map<String, Object> resp = new HashMap<>();
        LostPet lp = lostPetRepository.findById(id).orElse(null);
        if (lp == null) {
            resp.put("code", 404);
            resp.put("message", "not found");
            return ResponseEntity.status(404).body(resp);
        }
        lp.setStatus("approved");
        lostPetRepository.save(lp);
        resp.put("code", 0);
        resp.put("message", "ok");
        return ResponseEntity.ok(resp);
    }

    public static class RejectBody {
        public String reason;
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "审核不通过", description = "将启事标记为不通过（示例：更新 LostPet.status=rejected）")
    public ResponseEntity<Map<String, Object>> reject(@PathVariable Long id, @RequestBody RejectBody body) {
        Map<String, Object> resp = new HashMap<>();
        LostPet lp = lostPetRepository.findById(id).orElse(null);
        if (lp == null) {
            resp.put("code", 404);
            resp.put("message", "not found");
            return ResponseEntity.status(404).body(resp);
        }
        // 简化：仅记录状态。原因可扩展记录至独立表。
        lp.setStatus("rejected");
        lostPetRepository.save(lp);
        resp.put("code", 0);
        resp.put("message", "ok");
        return ResponseEntity.ok(resp);
    }
}


