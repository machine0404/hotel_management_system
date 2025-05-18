package com.example.hotel_management_system_backend.controller;

import com.example.hotel_management_system_backend.entity.Customer;
import com.example.hotel_management_system_backend.service.UserService;
import com.example.hotel_management_system_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        Map<String, Object> result = new HashMap<>();
        Customer user = userService.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            result.put("message", "用户名或密码错误");
            throw new RuntimeException("用户名或密码错误");
        }
        String token = JwtUtil.generateToken(username);
        result.put("token", token);
        result.put("message", "登录成功");
        return result;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        Map<String, Object> result = new HashMap<>();
        if (username == null || password == null) {
            result.put("message", "用户名和密码不能为空");
            return result;
        }
        // 检查用户名是否已存在
        Customer exist = userService.findByUsername(username);
        if (exist != null) {
            result.put("message", "用户名已存在");
            return result;
        }
        // 注册时email等字段给默认值
        Customer user = new Customer();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(username + "@example.com"); // 随便给个默认邮箱
        user.setPhone("");
        user.setGender(0);
        user.setCreateTime("");
        user.setPoints(0);
        user.setStatus(0);
        // 保存到数据库
        userService.save(user);
        result.put("message", "注册成功");
        return result;
    }
}