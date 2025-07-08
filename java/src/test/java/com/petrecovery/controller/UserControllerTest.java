package com.petrecovery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petrecovery.entity.User;
import com.petrecovery.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    public void testRegisterSuccess() throws Exception {
        // 准备测试数据
        User user = new User("testuser", "password123", "test@example.com");
        
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("code", 200);
        mockResult.put("message", "注册成功");
        
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", 1L);
        userInfo.put("username", "testuser");
        userInfo.put("email", "test@example.com");
        data.put("user", userInfo);
        data.put("token", "mock-jwt-token");
        mockResult.put("data", data);
        
        when(userService.register(any(User.class))).thenReturn(mockResult);
        
        // 执行测试
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"));
    }
    
    @Test
    public void testLoginSuccess() throws Exception {
        // 准备测试数据
        UserController.LoginRequest loginRequest = new UserController.LoginRequest("testuser", "password123");
        
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("code", 200);
        mockResult.put("message", "登录成功");
        
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", 1L);
        userInfo.put("username", "testuser");
        userInfo.put("email", "test@example.com");
        data.put("user", userInfo);
        data.put("token", "mock-jwt-token");
        mockResult.put("data", data);
        
        when(userService.login(anyString(), anyString())).thenReturn(mockResult);
        
        // 执行测试
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"));
    }
    
    @Test
    public void testApiTest() throws Exception {
        mockMvc.perform(get("/api/users/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("服务运行正常"));
    }
}