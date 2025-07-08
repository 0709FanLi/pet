package com.petrecovery.controller;

import com.petrecovery.entity.User;
import com.petrecovery.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户注册、登录相关接口")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册接口")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody User user) {
        Map<String, Object> result = userService.register(user);
        
        if ((Integer) result.get("code") == 200) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> result = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        
        if ((Integer) result.get("code") == 200) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 统一登录/注册接口
     * 如果用户已存在则直接登录，如果不存在则先注册再登录
     * @param loginOrRegisterRequest 登录/注册请求
     * @return 登录结果
     */
    @PostMapping("/login-or-register")
    @Operation(summary = "统一登录/注册", description = "如果用户已存在则直接登录，如果不存在则先注册再登录")
    public ResponseEntity<Map<String, Object>> loginOrRegister(@RequestBody LoginOrRegisterRequest loginOrRegisterRequest) {
        Map<String, Object> result = userService.loginOrRegister(
            loginOrRegisterRequest.getUsername(), 
            loginOrRegisterRequest.getPassword(),
            loginOrRegisterRequest.getEmail()
        );
        
        if ((Integer) result.get("code") == 200) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 手机验证码登录/注册
     * @param phoneLoginRequest 手机登录请求
     * @return 登录结果
     */
    @PostMapping("/phone-login")
    @Operation(summary = "手机验证码登录/注册", description = "使用手机号和验证码进行登录或注册")
    public ResponseEntity<Map<String, Object>> phoneLogin(@RequestBody PhoneLoginRequest phoneLoginRequest) {
        Map<String, Object> result = userService.phoneLogin(phoneLoginRequest.getPhone(), phoneLoginRequest.getVerificationCode());
        
        if ((Integer) result.get("code") == 200) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 发送验证码
     * @param sendCodeRequest 发送验证码请求
     * @return 发送结果
     */
    @PostMapping("/send-code")
    @Operation(summary = "发送验证码", description = "向指定手机号发送验证码")
    public ResponseEntity<Map<String, Object>> sendCode(@RequestBody SendCodeRequest sendCodeRequest) {
        Map<String, Object> result = userService.sendCode(sendCodeRequest.getPhone(), sendCodeRequest.getType());
        
        if ((Integer) result.get("code") == 200) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 测试接口
     * @return 测试结果
     */
    @GetMapping("/test")
    @Operation(summary = "测试接口", description = "测试服务是否正常运行")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "服务运行正常");
        result.put("data", "Hello from Pet Recovery API!");
        return ResponseEntity.ok(result);
    }
    
    /**
     * 登录请求类
     */
    public static class LoginRequest {
        private String username;
        private String password;
        
        public LoginRequest() {}
        
        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
    
    /**
     * 统一登录/注册请求类
     */
    public static class LoginOrRegisterRequest {
        private String username;
        private String password;
        private String email;
        
        public LoginOrRegisterRequest() {}
        
        public LoginOrRegisterRequest(String username, String password, String email) {
            this.username = username;
            this.password = password;
            this.email = email;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
    }

    /**
     * 手机验证码登录请求
     */
    public static class PhoneLoginRequest {
        private String phone;
        private String verificationCode;

        public PhoneLoginRequest() {}

        public PhoneLoginRequest(String phone, String verificationCode) {
            this.phone = phone;
            this.verificationCode = verificationCode;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getVerificationCode() {
            return verificationCode;
        }

        public void setVerificationCode(String verificationCode) {
            this.verificationCode = verificationCode;
        }
    }

    /**
     * 发送验证码请求
     */
    public static class SendCodeRequest {
        private String phone;
        private String type; // auth: 登录注册, reset: 重置密码

        public SendCodeRequest() {}

        public SendCodeRequest(String phone, String type) {
            this.phone = phone;
            this.type = type;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}