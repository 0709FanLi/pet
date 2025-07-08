package com.petrecovery.service;

import com.petrecovery.entity.User;
import com.petrecovery.repository.UserRepository;
import com.petrecovery.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    // 简化的验证码存储（生产环境应使用Redis等缓存）
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册结果
     */
    public Map<String, Object> register(User user) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查用户名是否已存在
            if (userRepository.existsByUsername(user.getUsername())) {
                result.put("code", 400);
                result.put("message", "用户名已存在");
                return result;
            }
            
            // 检查邮箱是否已存在
            if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
                result.put("code", 400);
                result.put("message", "邮箱已被注册");
                return result;
            }
            
            // 加密密码
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            // 保存用户
            User savedUser = userRepository.save(user);
            
            // 生成token
            String token = jwtUtil.generateToken(savedUser.getUsername());
            
            Map<String, Object> data = new HashMap<>();
            data.put("user", getUserInfo(savedUser));
            data.put("token", token);
            
            result.put("code", 200);
            result.put("message", "注册成功");
            result.put("data", data);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "注册失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 查找用户
            Optional<User> userOptional = userRepository.findByUsername(username);
            
            if (!userOptional.isPresent()) {
                result.put("code", 401);
                result.put("message", "用户名或密码错误");
                return result;
            }
            
            User user = userOptional.get();
            
            // 验证密码
            if (!passwordEncoder.matches(password, user.getPassword())) {
                result.put("code", 401);
                result.put("message", "用户名或密码错误");
                return result;
            }
            
            // 生成token
            String token = jwtUtil.generateToken(user.getUsername());
            
            Map<String, Object> data = new HashMap<>();
            data.put("user", getUserInfo(user));
            data.put("token", token);
            
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("data", data);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "登录失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 统一登录/注册接口
     * 如果用户已存在则直接登录，如果不存在则先注册再登录
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱（注册时需要）
     * @return 登录结果
     */
    public Map<String, Object> loginOrRegister(String username, String password, String email) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查用户是否已存在
            Optional<User> userOptional = userRepository.findByUsername(username);
            
            if (userOptional.isPresent()) {
                // 用户已存在，直接登录
                return login(username, password);
            } else {
                // 用户不存在，先注册
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setEmail(email);
                
                Map<String, Object> registerResult = register(newUser);
                
                // 如果注册成功，则自动登录
                if ((Integer) registerResult.get("code") == 200) {
                    return login(username, password);
                } else {
                    return registerResult;
                }
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "操作失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * 获取用户信息（不包含密码）
     * @param user 用户实体
     * @return 用户信息
     */
    /**
     * 手机验证码登录/注册
     * @param phone 手机号
     * @param verificationCode 验证码
     * @return 登录结果
     */
    public Map<String, Object> phoneLogin(String phone, String verificationCode) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证验证码
            String storedCode = verificationCodes.get(phone);
            // 如果没有存储的验证码，且输入的是1111，则允许通过（开发测试用）
            if (storedCode == null && "1111".equals(verificationCode)) {
                // 开发测试模式：直接允许1111验证码
            } else if (storedCode == null || !storedCode.equals(verificationCode)) {
                result.put("code", 400);
                result.put("message", "验证码错误或已过期");
                return result;
            }
            
            // 验证成功后移除验证码（一次性使用）
            verificationCodes.remove(phone);
            
            // 查找用户是否已存在（通过手机号）
            Optional<User> userOptional = userRepository.findByPhoneNumber(phone);
            
            User user;
            if (userOptional.isPresent()) {
                // 用户已存在，直接登录
                user = userOptional.get();
            } else {
                // 用户不存在，自动注册
                user = new User();
                user.setPhoneNumber(phone);
                user.setUsername("user_" + phone); // 自动生成用户名
                user.setPassword(passwordEncoder.encode("123456")); // 默认密码
                user = userRepository.save(user);
            }
            
            // 生成JWT token
            String token = jwtUtil.generateToken(user.getUsername());
            
            result.put("code", 200);
            result.put("message", "登录成功");
            
            Map<String, Object> data = getUserInfo(user);
            data.put("token", token);
            result.put("data", data);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "登录失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 发送验证码
     * @param phone 手机号
     * @param type 验证码类型
     * @return 发送结果
     */
    public Map<String, Object> sendCode(String phone, String type) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 简化实现：固定验证码为1111
            String code = "1111";
            verificationCodes.put(phone, code);
            
            result.put("code", 200);
            result.put("message", "验证码发送成功（演示：1111）");
            result.put("data", "验证码已发送到手机号: " + phone);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "验证码发送失败: " + e.getMessage());
        }
        
        return result;
    }
    
    private Map<String, Object> getUserInfo(User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("phoneNumber", user.getPhoneNumber());
        userInfo.put("createdAt", user.getCreatedAt());
        return userInfo;
    }
}