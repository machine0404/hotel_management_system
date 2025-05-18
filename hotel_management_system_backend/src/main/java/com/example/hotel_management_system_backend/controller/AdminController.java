package com.example.hotel_management_system_backend.controller;

import com.example.hotel_management_system_backend.entity.Admin;
import com.example.hotel_management_system_backend.service.AdminService;
import com.example.hotel_management_system_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        Map<String, Object> result = new HashMap<>();
        Admin admin = adminService.findByUsername(username);
        if (admin == null || !admin.getPassword().equals(password)) {
            result.put("message", "用户名或密码错误");
            throw new RuntimeException("用户名或密码错误");
        }
        String token = JwtUtil.generateToken(username);
        result.put("token", token);
        result.put("message", "登录成功");
        return result;
    }
}
