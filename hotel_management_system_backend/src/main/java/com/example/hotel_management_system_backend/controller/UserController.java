package com.example.hotel_management_system_backend.controller;

import com.example.hotel_management_system_backend.entity.Customer;
import com.example.hotel_management_system_backend.service.UserService;
import com.example.hotel_management_system_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    @GetMapping("/room-list")
    public List<Map<String, Object>> roomList(
        @RequestParam(required = false) String check_in_date,
        @RequestParam(required = false) String check_out_date
    ) {
        String baseSql = "SELECT r.*, t.name AS type_name, t.price, t.feature, t.description, t.cover_image " +
                         "FROM room r LEFT JOIN room_type t ON r.type_id = t.id";
        if (check_in_date != null && check_out_date != null) {
            // 查询未被预定的房间
            baseSql += " WHERE r.id NOT IN (SELECT room_id FROM booking WHERE " +
                       "NOT (check_out_date <= ? OR check_in_date >= ?))";
            return jdbcTemplate.queryForList(baseSql, check_in_date, check_out_date);
        } else {
            return jdbcTemplate.queryForList(baseSql);
        }
    }

    @PostMapping("/book-room")
    public Map<String, Object> bookRoom(@RequestBody Map<String, Object> params, @RequestHeader("Authorization") String auth) {
        Map<String, Object> result = new HashMap<>();
        // 1. 获取用户名（假设你的JwtUtil有parseToken方法，返回Claims）
        String token = auth.replace("Bearer ", "");
        String username = com.example.hotel_management_system_backend.util.JwtUtil.parseToken(token).getSubject();
        Customer user = userService.findByUsername(username);
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        Integer userId = user.getId();
        Integer roomId = (Integer) params.get("room_id");
        String checkinStr = (String) params.get("checkin");
        String checkoutStr = (String) params.get("checkout");
        Integer people = (Integer) params.get("people");
        Boolean needInvoice = (Boolean) params.get("need_invoice");

        // 解析日期字符串，兼容 "2025-04-30" 和 "2025-04-30T16:00:00.000Z"
        LocalDate checkinDate = parseDate(checkinStr);
        LocalDate checkoutDate = parseDate(checkoutStr);

        // 查询房间单价
        Map<String, Object> room = jdbcTemplate.queryForMap("SELECT t.price FROM room r LEFT JOIN room_type t ON r.type_id = t.id WHERE r.id = ?", roomId);
        double price = Double.parseDouble(room.get("price").toString());

        // 计算天数
        long days = java.time.temporal.ChronoUnit.DAYS.between(
            checkinDate,
            checkoutDate
        );
        if (days <= 0) {
            result.put("success", false);
            result.put("message", "离店时间必须晚于入住时间");
            return result;
        }
        double total = price * days;

        // 插入订单
        int n = jdbcTemplate.update(
            "INSERT INTO booking (user_id, room_id, check_in_date, check_out_date, adults, invoice_needed, total_amount, status, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, 0, NOW())",
            userId, roomId, checkinDate, checkoutDate, people, needInvoice != null && needInvoice ? 1 : 0, total
        );
        result.put("success", n > 0);
        return result;
    }

    @GetMapping("/room-type-list")
    public List<Map<String, Object>> roomTypeList() {
        String sql = "SELECT id, name FROM room_type";
        return jdbcTemplate.queryForList(sql);
    }

    // 获取当前登录用户信息
    @GetMapping("/me")
    public Map<String, Object> getMyInfo(@RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        String username = JwtUtil.parseToken(token).getSubject();
        Customer user = userService.findByUsername(username);
        if (user == null) throw new RuntimeException("用户不存在");
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("gender", user.getGender());
        result.put("create_time", user.getCreateTime());
        result.put("points", user.getPoints());
        result.put("status", user.getStatus());
        return result;
    }

    // 只允许修改邮箱、性别、手机号
    @PutMapping("/me")
    public Map<String, Object> updateMyInfo(@RequestBody Map<String, Object> params, @RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        String username = JwtUtil.parseToken(token).getSubject();
        Customer user = userService.findByUsername(username);
        if (user == null) throw new RuntimeException("用户不存在");
        String email = (String) params.get("email");
        String phone = (String) params.get("phone");
        Integer gender = params.get("gender") != null ? (Integer) params.get("gender") : user.getGender();
        int n = jdbcTemplate.update(
            "UPDATE customer SET email=?, phone=?, gender=? WHERE id=?",
            email, phone, gender, user.getId()
        );
        Map<String, Object> result = new HashMap<>();
        result.put("success", n > 0);
        return result;
    }

    @GetMapping("/my-orders")
    public List<Map<String, Object>> getMyOrders(@RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        String username = JwtUtil.parseToken(token).getSubject();
        Customer user = userService.findByUsername(username);
        if (user == null) throw new RuntimeException("用户不存在");
        String sql = "SELECT b.id, b.room_id, rt.name AS type_name, b.check_in_date, b.check_out_date, b.create_time, b.total_amount, b.status, b.adults, b.invoice_needed " +
                     "FROM booking b LEFT JOIN room r ON b.room_id = r.id LEFT JOIN room_type rt ON r.type_id = rt.id " +
                     "WHERE b.user_id = ? ORDER BY b.create_time DESC";
        return jdbcTemplate.queryForList(sql, user.getId());
    }

    // 修改密码
    @PutMapping("/change-password")
    public Map<String, Object> changePassword(@RequestBody Map<String, String> params, @RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        String username = JwtUtil.parseToken(token).getSubject();
        Customer user = userService.findByUsername(username);
        Map<String, Object> result = new HashMap<>();
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        if (!user.getPassword().equals(oldPassword)) {
            result.put("success", false);
            result.put("message", "原密码错误");
            return result;
        }
        int n = jdbcTemplate.update("UPDATE customer SET password=? WHERE id=?", newPassword, user.getId());
        result.put("success", n > 0);
        return result;
    }

    // 删除订单（只允许删除自己的订单）
    @DeleteMapping("/my-orders/{id}")
    public Map<String, Object> deleteMyOrder(@PathVariable Integer id, @RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        String username = JwtUtil.parseToken(token).getSubject();
        Customer user = userService.findByUsername(username);
        Map<String, Object> result = new HashMap<>();
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }
        // 只允许删除属于自己的订单
        int n = jdbcTemplate.update("DELETE FROM booking WHERE id=? AND user_id=?", id, user.getId());
        result.put("success", n > 0);
        result.put("message", n > 0 ? "删除成功" : "删除失败");
        return result;
    }

    // 解析日期字符串，兼容 "2025-04-30" 和 "2025-04-30T16:00:00.000Z"
    private LocalDate parseDate(String dateStr) {
        try {
            // 先尝试 yyyy-MM-dd
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            // 再尝试 yyyy-MM-ddTHH:mm:ss.SSSZ 或 Z
            return LocalDate.parse(dateStr.substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }
}