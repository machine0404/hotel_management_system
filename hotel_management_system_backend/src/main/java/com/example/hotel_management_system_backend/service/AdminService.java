package com.example.hotel_management_system_backend.service;

import com.example.hotel_management_system_backend.entity.Admin;
import com.example.hotel_management_system_backend.entity.Booking;
import com.example.hotel_management_system_backend.entity.Customer;
import com.example.hotel_management_system_backend.entity.Room;
import com.example.hotel_management_system_backend.entity.RoomType;
import com.example.hotel_management_system_backend.mapper.AdminMapper;
import com.example.hotel_management_system_backend.mapper.BookingMapper;
import com.example.hotel_management_system_backend.mapper.CustomerMapper;
import com.example.hotel_management_system_backend.mapper.RoomMapper;
import com.example.hotel_management_system_backend.mapper.RoomTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AdminService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Admin findByUsername(String username) {
        String sql = "SELECT \"id\", \"username\", \"password\", \"email\", \"phone\", \"create_time\", \"real_name\" FROM \"admin\" WHERE \"username\" = ?";
        return jdbcTemplate.query(sql, new AdminMapper(), username)
                .stream().findFirst().orElse(null);
    }

    public List<Customer> getUserList() {
        String key = "user_list";
        String json = redisTemplate.opsForValue().get(key);
        if (json != null) {
            try {
                return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Customer.class));
            } catch (Exception ignored) {}
        }
        List<Customer> list = jdbcTemplate.query("SELECT \"id\", \"username\", \"password\", \"email\", \"phone\", \"gender\", \"create_time\", \"points\", \"status\" FROM \"customer\"", new CustomerMapper());
        if (list != null && !list.isEmpty()) {
            try {
                redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(list), 10, TimeUnit.MINUTES);
            } catch (Exception ignored) {}
        }
        return list;
    }

    public List<Booking> getOrderList() {
        String key = "order_list";
        String json = redisTemplate.opsForValue().get(key);
        if (json != null) {
            try {
                return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Booking.class));
            } catch (Exception ignored) {}
        }
        List<Booking> list = jdbcTemplate.query("SELECT * FROM \"booking\"", new BookingMapper());
        if (list != null && !list.isEmpty()) {
            try {
                redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(list), 10, TimeUnit.MINUTES);
            } catch (Exception ignored) {}
        }
        return list;
    }

    public List<Booking> getOrderWaitList() {
        String key = "order_wait_list";
        String json = redisTemplate.opsForValue().get(key);
        if (json != null) {
            try {
                return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Booking.class));
            } catch (Exception ignored) {}
        }
        List<Booking> list = jdbcTemplate.query("SELECT * FROM \"booking\" WHERE \"status\" = 0", new BookingMapper());
        if (list != null && !list.isEmpty()) {
            try {
                redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(list), 10, TimeUnit.MINUTES);
            } catch (Exception ignored) {}
        }
        return list;
    }

    public List<Room> getRoomList() {
        String key = "room_list";
        String json = redisTemplate.opsForValue().get(key);
        if (json != null) {
            try {
                return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Room.class));
            } catch (Exception ignored) {}
        }
        List<Room> list = jdbcTemplate.query(
            "SELECT r.\"id\", r.\"room_number\", r.\"type_id\", r.\"status\", r.\"max_people\", r.\"introduce\", " +
            "t.\"name\" AS type_name, t.\"price\", t.\"feature\", t.\"description\", t.\"cover_image\" " +
            "FROM \"room\" r LEFT JOIN \"room_type\" t ON r.\"type_id\" = t.\"id\"",
            new RoomMapper());
        if (list != null && !list.isEmpty()) {
            try {
                redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(list), 10, TimeUnit.MINUTES);
            } catch (Exception ignored) {}
        }
        return list;
    }

    public List<RoomType> getRoomTypeList() {
        String key = "room_type_list";
        String json = redisTemplate.opsForValue().get(key);
        if (json != null) {
            try {
                return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, RoomType.class));
            } catch (Exception ignored) {}
        }
        List<RoomType> list = jdbcTemplate.query("SELECT \"id\", \"name\", \"price\", \"feature\", \"description\", \"cover_image\" FROM \"room_type\"", new RoomTypeMapper());
        if (list != null && !list.isEmpty()) {
            try {
                redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(list), 10, TimeUnit.MINUTES);
            } catch (Exception ignored) {}
        }
        return list;
    }

    public boolean deleteUserById(Integer id) {
        int rows = jdbcTemplate.update("DELETE FROM customer WHERE id = ?", id);
        // 删除缓存
        redisTemplate.delete("user_list");
        return rows > 0;
    }

    public Customer getUserById(Integer id) {
        List<Customer> list = jdbcTemplate.query(
            "SELECT * FROM customer WHERE id = ?",
            new BeanPropertyRowMapper<>(Customer.class),
            id
        );
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean acceptOrder(Integer id) {
        int rows = jdbcTemplate.update("UPDATE booking SET status = 1 WHERE id = ?", id);
        // 如有订单缓存，记得清理
        redisTemplate.delete("order_wait_list");
        return rows > 0;
    }

    public boolean cancelOrder(Integer id) {
        int rows = jdbcTemplate.update("UPDATE booking SET status = 2 WHERE id = ?", id);
        // 如有订单缓存，记得清理
        redisTemplate.delete("order_wait_list");
        return rows > 0;
    }

    public boolean updateUser(Integer id, Customer param) {
        int rows = jdbcTemplate.update(
            "UPDATE customer SET username=?, password=?, points=?, phone=?, email=?, gender=?, status=? WHERE id=?",
            param.getUsername(), param.getPassword(), param.getPoints(), param.getPhone(),
            param.getEmail(), param.getGender(), param.getStatus(), id
        );
        // 如有用户列表缓存，记得清理
        redisTemplate.delete("user_list");
        return rows > 0;
    }

    public boolean deleteRoomById(Integer id) {
        int rows = jdbcTemplate.update("DELETE FROM room WHERE id = ?", id);
        // 如有房间列表缓存，记得清理
        redisTemplate.delete("room_list");
        return rows > 0;
    }

    public Room getRoomById(Integer id) {
        String sql = "SELECT r.*, t.name AS type_name, t.price, t.feature, t.description, t.cover_image " +
                     "FROM room r LEFT JOIN room_type t ON r.type_id = t.id WHERE r.id = ?";
        List<Room> list = jdbcTemplate.query(sql, new org.springframework.jdbc.core.BeanPropertyRowMapper<>(Room.class), id);
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean addRoom(Room param) {
        int rows = jdbcTemplate.update(
            "INSERT INTO room (room_number, max_people, type_id, status, introduce) VALUES (?, ?, ?, ?, ?)",
            param.getRoom_number(),
            param.getMax_people(),
            param.getType_id(),
            param.getStatus(),
            param.getIntroduce()
        );
        redisTemplate.delete("room_list");
        return rows > 0;
    }

    public boolean updateRoom(Integer id, Room param) {
        int rows = jdbcTemplate.update(
            "UPDATE room SET room_number=?, type_id=?, status=?, max_people=?, introduce=? WHERE id=?",
            param.getRoom_number(),
            param.getType_id(),
            param.getStatus(),
            param.getMax_people(),
            param.getIntroduce(),
            id
        );
        redisTemplate.delete("room_list");
        return rows > 0;
    }

    // 其它增删改查方法同理，参数和返回值都用实体类
    // 例如添加房间、删除房间、修改房间、添加房型、删除房型、修改房型、用户管理、订单管理等
}
