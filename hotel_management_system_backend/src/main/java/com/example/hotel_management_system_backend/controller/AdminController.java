package com.example.hotel_management_system_backend.controller;

import com.example.hotel_management_system_backend.entity.Admin;
import com.example.hotel_management_system_backend.service.AdminService;
import com.example.hotel_management_system_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String USER_LIST_KEY = "user_list";
    private static final String ORDER_LIST_KEY = "order_list";
    private static final String ORDER_WAIT_LIST_KEY = "order_wait_list";
    private static final String ROOM_LIST_KEY = "room_list";
    private static final String ROOM_TYPE_LIST_KEY = "room_type_list";

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

    // 获取所有用户（带缓存）
    @GetMapping("/user-list")
    public List<Map<String, Object>> userList() {
        String json = redisTemplate.opsForValue().get(USER_LIST_KEY);
        if (json != null) {
            try {
                return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
            } catch (Exception ignored) {}
        }
        // 字段名全部用下划线风格，和前端一致
        String sql = "SELECT id, username, email, gender, phone, points, status, create_time FROM customer";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        // 只缓存非空数据
        if (list != null && !list.isEmpty()) {
            try {
                redisTemplate.opsForValue().set(USER_LIST_KEY, objectMapper.writeValueAsString(list), 10, TimeUnit.MINUTES);
            } catch (Exception ignored) {}
        }
        return list;
    }

    // 获取所有未处理订单（带缓存）
    @GetMapping("/order-wait")
    public List<Map<String, Object>> orderWaitList() {
        String json = redisTemplate.opsForValue().get(ORDER_WAIT_LIST_KEY);
        if (json != null) {
            try {
                return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
            } catch (Exception ignored) {}
        }
        String sql = "SELECT * FROM booking WHERE status = 0";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        if (list != null && !list.isEmpty()) {
            try {
                redisTemplate.opsForValue().set(ORDER_WAIT_LIST_KEY, objectMapper.writeValueAsString(list), 10, TimeUnit.MINUTES);
            } catch (Exception ignored) {}
        }
        return list;
    }

    // 获取所有订单（带缓存）
    @GetMapping("/order-list")
    public List<Map<String, Object>> orderList() {
        String json = redisTemplate.opsForValue().get(ORDER_LIST_KEY);
        if (json != null) {
            try {
                return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
            } catch (Exception ignored) {}
        }
        String sql = "SELECT * FROM booking";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        if (list != null && !list.isEmpty()) {
            try {
                redisTemplate.opsForValue().set(ORDER_LIST_KEY, objectMapper.writeValueAsString(list), 10, TimeUnit.MINUTES);
            } catch (Exception ignored) {}
        }
        return list;
    }

    // 获取所有房间，带类型名称（带缓存）
    @GetMapping("/room-list")
    public List<Map<String, Object>> roomList() {
        String json = redisTemplate.opsForValue().get(ROOM_LIST_KEY);
        if (json != null) {
            try {
                return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
            } catch (Exception ignored) {}
        }
        String sql = "SELECT r.*, t.name AS type_name FROM room r LEFT JOIN room_type t ON r.type_id = t.id";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        if (list != null && !list.isEmpty()) {
            try {
                redisTemplate.opsForValue().set(ROOM_LIST_KEY, objectMapper.writeValueAsString(list), 10, TimeUnit.MINUTES);
            } catch (Exception ignored) {}
        }
        return list;
    }

    // 获取所有房间类型（带缓存）
    @GetMapping("/room-type-list")
    public List<Map<String, Object>> roomTypeList() {
        String json = redisTemplate.opsForValue().get(ROOM_TYPE_LIST_KEY);
        if (json != null) {
            try {
                return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
            } catch (Exception ignored) {}
        }
        String sql = "SELECT id, name FROM room_type";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        if (list != null && !list.isEmpty()) {
            try {
                redisTemplate.opsForValue().set(ROOM_TYPE_LIST_KEY, objectMapper.writeValueAsString(list), 10, TimeUnit.MINUTES);
            } catch (Exception ignored) {}
        }
        return list;
    }

    // 添加房间（清理房间列表缓存）
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
        redisTemplate.delete(ROOM_LIST_KEY);
        result.put("success", n > 0);
        return result;
    }

    // 删除房间（清理房间列表缓存）
    @DeleteMapping("/room/{id}")
    public Map<String, Object> deleteRoom(@PathVariable Integer id) {
        int n = jdbcTemplate.update("DELETE FROM room WHERE id = ?", id);
        redisTemplate.delete(ROOM_LIST_KEY);
        Map<String, Object> result = new HashMap<>();
        result.put("success", n > 0);
        return result;
    }

    // 修改房间（清理房间列表缓存）
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
        redisTemplate.delete(ROOM_LIST_KEY);
        Map<String, Object> result = new HashMap<>();
        result.put("success", n > 0);
        return result;
    }

    // 添加房型（清理房型列表缓存）
    @PostMapping("/room-type-add")
    public Map<String, Object> addRoomType(@RequestBody Map<String, Object> params) {
        String sql = "INSERT INTO room_type (name, price, description, feature, cover_image) VALUES (?, ?, ?, ?, ?)";
        int n = jdbcTemplate.update(sql,
            params.get("name"),
            params.get("price"),
            params.get("description"),
            params.get("feature"),
            params.get("cover_image")
        );
        redisTemplate.delete(ROOM_TYPE_LIST_KEY);
        Map<String, Object> result = new HashMap<>();
        result.put("success", n > 0);
        return result;
    }

    // 修改房型（清理房型列表缓存）
    @PutMapping("/room-type/{id}")
    public Map<String, Object> updateRoomType(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        String sql = "UPDATE room_type SET name=?, price=?, description=?, feature=?, cover_image=? WHERE id=?";
        int n = jdbcTemplate.update(sql,
            params.get("name"),
            params.get("price"),
            params.get("description"),
            params.get("feature"),
            params.get("cover_image"),
            id
        );
        redisTemplate.delete(ROOM_TYPE_LIST_KEY);
        Map<String, Object> result = new HashMap<>();
        result.put("success", n > 0);
        return result;
    }

    // 删除房型（清理房型列表缓存）
    @DeleteMapping("/room-type/{id}")
    public Map<String, Object> deleteRoomType(@PathVariable Integer id) {
        int n = jdbcTemplate.update("DELETE FROM room_type WHERE id = ?", id);
        redisTemplate.delete(ROOM_TYPE_LIST_KEY);
        Map<String, Object> result = new HashMap<>();
        result.put("success", n > 0);
        return result;
    }

    // 用户相关变更操作后清理用户列表缓存
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
        redisTemplate.delete(USER_LIST_KEY);
        Map<String, Object> result = new HashMap<>();
        result.put("success", n > 0);
        return result;
    }

    @DeleteMapping("/user/{id}")
    public Map<String, Object> deleteUser(@PathVariable Integer id) {
        int n = jdbcTemplate.update("DELETE FROM customer WHERE id = ?", id);
        redisTemplate.delete(USER_LIST_KEY);
        Map<String, Object> result = new HashMap<>();
        result.put("success", n > 0);
        return result;
    }

    // 订单相关变更操作后清理订单缓存
    @PostMapping("/order/{id}/accept")
    public Map<String, Object> acceptOrder(@PathVariable Integer id) {
        int n = jdbcTemplate.update("UPDATE booking SET status = 1 WHERE id = ?", id);
        redisTemplate.delete(ORDER_LIST_KEY);
        redisTemplate.delete(ORDER_WAIT_LIST_KEY);
        Map<String, Object> result = new HashMap<>();
        result.put("success", n > 0);
        return result;
    }

    @PostMapping("/order/{id}/cancel")
    public Map<String, Object> cancelOrder(@PathVariable Integer id) {
        int n = jdbcTemplate.update("UPDATE booking SET status = 2 WHERE id = ?", id);
        redisTemplate.delete(ORDER_LIST_KEY);
        redisTemplate.delete(ORDER_WAIT_LIST_KEY);
        Map<String, Object> result = new HashMap<>();
        result.put("success", n > 0);
        return result;
    }

    // 其它接口保持不变
    @GetMapping("/user/{id}")
    public Map<String, Object> getUserById(@PathVariable Integer id) {
        String sql = "SELECT id, create_time, email, username, password, gender, phone, points, status FROM customer WHERE id = ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @GetMapping("/room/{id}")
    public Map<String, Object> getRoomById(@PathVariable Integer id) {
        String sql = "SELECT r.*, t.name AS type_name, t.price, t.description FROM room r LEFT JOIN room_type t ON r.type_id = t.id WHERE r.id = ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }
}
