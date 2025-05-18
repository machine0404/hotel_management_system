package com.example.hotel_management_system_backend.controller;

import com.example.hotel_management_system_backend.entity.Admin;
import com.example.hotel_management_system_backend.service.AdminService;
import com.example.hotel_management_system_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    // 获取所有用户
    @GetMapping("/user-list")
    public List<Map<String, Object>> userList() {
        String sql = "SELECT id, create_time, email, username, gender, phone, points, status FROM customer";
        return jdbcTemplate.queryForList(sql);
    }

    // 获取所有未处理订单
    @GetMapping("/order-wait")
    public List<Map<String, Object>> orderWaitList() {
        String sql = "SELECT * FROM booking WHERE status = 0";
        return jdbcTemplate.queryForList(sql);
    }

    // 获取所有订单
    @GetMapping("/order-list")
    public List<Map<String, Object>> orderList() {
        String sql = "SELECT * FROM booking";
        return jdbcTemplate.queryForList(sql);
    }

    // 获取所有房间，带类型名称
    @GetMapping("/room-list")
    public List<Map<String, Object>> roomList() {
        String sql = "SELECT r.*, t.name AS type_name FROM room r LEFT JOIN room_type t ON r.type_id = t.id";
        return jdbcTemplate.queryForList(sql);
    }

    // 添加房间
    @PostMapping("/room-add")
    public Map<String, Object> addRoom(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String sql = "INSERT INTO room (room_number, type_id, status, max_people, introduce) VALUES (?, ?, ?, ?, ?)";
        int n = jdbcTemplate.update(sql,
            params.get("room_number"),
            params.get("type_id"),
            params.get("status"),
            params.get("max_people"),
            params.get("introduce")
        );
        result.put("success", n > 0);
        return result;
    }

    @GetMapping("/user/{id}")
    public Map<String, Object> getUserById(@PathVariable Integer id) {
        String sql = "SELECT id, create_time, email, username, gender, phone, points, status FROM customer WHERE id = ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @PutMapping("/user/{id}")
    public Map<String, Object> updateUser(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        String sql = "UPDATE customer SET username=?, password=?, points=?, phone=?, email=?, gender=?, status=? WHERE id=?";
        int n = jdbcTemplate.update(sql,
            params.get("username"),
            params.get("password"),
            params.get("points"),
            params.get("phone"),
            params.get("email"),
            params.get("gender"),
            params.get("status"),
            id
        );
        Map<String, Object> result = new HashMap<>();
        result.put("success", n > 0);
        return result;
    }

    // 删除用户
    @DeleteMapping("/user/{id}")
    public Map<String, Object> deleteUser(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        int n = jdbcTemplate.update("DELETE FROM customer WHERE id = ?", id);
        result.put("success", n > 0);
        return result;
    }

    // 接受订单
    @PostMapping("/order/{id}/accept")
    public Map<String, Object> acceptOrder(@PathVariable Integer id) {
        int n = jdbcTemplate.update("UPDATE booking SET status = 1 WHERE id = ?", id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", n > 0);
        return result;
    }

    // 取消订单
    @PostMapping("/order/{id}/cancel")
    public Map<String, Object> cancelOrder(@PathVariable Integer id) {
        int n = jdbcTemplate.update("UPDATE booking SET status = 2 WHERE id = ?", id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", n > 0);
        return result;
    }

    // 删除房间
    @DeleteMapping("/room/{id}")
    public Map<String, Object> deleteRoom(@PathVariable Integer id) {
        int n = jdbcTemplate.update("DELETE FROM room WHERE id = ?", id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", n > 0);
        return result;
    }

    // 查询房间详情，带类型信息
    @GetMapping("/room/{id}")
    public Map<String, Object> getRoomById(@PathVariable Integer id) {
        String sql = "SELECT r.*, t.name AS type_name, t.price, t.description FROM room r LEFT JOIN room_type t ON r.type_id = t.id WHERE r.id = ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    // 修改房间
    @PutMapping("/room/{id}")
    public Map<String, Object> updateRoom(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        String sql = "UPDATE room SET room_number=?, type_id=?, status=?, max_people=?, introduce=? WHERE id=?";
        int n = jdbcTemplate.update(sql,
            params.get("room_number"),
            params.get("type_id"),
            params.get("status"),
            params.get("max_people"),
            params.get("introduce"),
            id
        );
        Map<String, Object> result = new HashMap<>();
        result.put("success", n > 0);
        return result;
    }
}
