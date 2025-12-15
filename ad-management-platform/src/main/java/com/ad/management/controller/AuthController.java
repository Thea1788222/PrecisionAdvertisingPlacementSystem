package com.ad.management.controller;

import com.ad.management.model.*;
import com.ad.management.service.UserService;
import com.ad.management.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     *
     * @param registerRequest 注册请求对象
     * @return 注册成功的用户对象
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.register(registerRequest);
            // 清除密码字段再返回
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求对象
     * @return 登录成功的用户对象和JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 验证用户名和密码
            User user = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            
            if (user == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "用户名或密码错误");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // 生成JWT token
            String token = jwtUtil.generateToken(loginRequest.getUsername());
            
            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(token);
            authResponse.setExpiresIn(jwtUtil.getExpirationTime());
            
            user.setPassword(null); // 清除密码
            authResponse.setUser(user);
            
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "登录失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 用户登出
     *
     * @return 登出成功的响应
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "登出成功");
        return ResponseEntity.ok(response);
    }
}