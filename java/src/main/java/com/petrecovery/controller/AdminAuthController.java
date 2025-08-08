package com.petrecovery.controller;

import com.petrecovery.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理后台认证", description = "管理员登录认证接口")
@CrossOrigin(origins = "*")
public class AdminAuthController {

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.password:admin123}")
    private String adminPassword;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "使用用户名与密码登录管理后台")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest req) {
        Map<String, Object> result = new HashMap<>();
        if (req == null || req.getUsername() == null || req.getPassword() == null) {
            result.put("code", 400);
            result.put("message", "用户名或密码不能为空");
            return ResponseEntity.badRequest().body(result);
        }

        if (adminUsername.equals(req.getUsername()) && adminPassword.equals(req.getPassword())) {
            String token = jwtUtil.generateToken("admin:" + req.getUsername());
            Map<String, Object> data = new HashMap<>();
            data.put("username", req.getUsername());
            data.put("token", token);
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("data", data);
            return ResponseEntity.ok(result);
        } else {
            result.put("code", 401);
            result.put("message", "用户名或密码错误");
            return ResponseEntity.status(401).body(result);
        }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}


