package com.example.hotel_management_system_backend.service;

import com.example.hotel_management_system_backend.entity.Customer;
import com.example.hotel_management_system_backend.entity.Room;
import com.example.hotel_management_system_backend.entity.RoomType;
import com.example.hotel_management_system_backend.entity.Booking;
import com.example.hotel_management_system_backend.mapper.CustomerMapper;
import com.example.hotel_management_system_backend.mapper.RoomMapper;
import com.example.hotel_management_system_backend.mapper.RoomTypeMapper;
import com.example.hotel_management_system_backend.mapper.BookingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Customer findByUsername(String username) {
        String sql = "SELECT \"id\", \"username\", \"password\", \"email\", \"phone\", \"gender\", \"create_time\", \"points\", \"status\" FROM \"customer\" WHERE \"username\" = ?";
        return jdbcTemplate.query(sql, new CustomerMapper(), username)
                .stream().findFirst().orElse(null);
    }

    public void save(Customer user) {
        String sql = "INSERT INTO \"customer\" (\"username\", \"password\", \"email\", \"phone\", \"gender\", \"create_time\", \"points\", \"status\") VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?)";
        jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getEmail(), user.getPhone(), user.getGender(), user.getPoints(), user.getStatus());
        // 新增：注册后清理用户列表缓存
        redisTemplate.delete("user_list");
    }

    public List<Room> getRoomList(String checkIn, String checkOut) {
        String sql = "SELECT r.\"id\", r.\"room_number\", r.\"type_id\", r.\"status\", r.\"max_people\", r.\"introduce\", " +
                "t.\"name\" AS type_name, t.\"price\", t.\"feature\", t.\"description\", t.\"cover_image\" " +
                "FROM \"room\" r LEFT JOIN \"room_type\" t ON r.\"type_id\" = t.\"id\"";
        if (checkIn != null && checkOut != null) {
            sql += " WHERE r.\"id\" NOT IN (SELECT \"room_id\" FROM \"booking\" WHERE NOT (\"check_out_date\" <= ? OR \"check_in_date\" >= ?))";
            return jdbcTemplate.query(sql, new RoomMapper(), checkIn, checkOut);
        } else {
            return jdbcTemplate.query(sql, new RoomMapper());
        }
    }

    public List<RoomType> getRoomTypeList() {
        String sql = "SELECT \"id\", \"name\", \"price\", \"feature\", \"description\", \"cover_image\" FROM \"room_type\"";
        return jdbcTemplate.query(sql, new RoomTypeMapper());
    }

    public List<Booking> getMyOrders(int userId) {
        String sql = "SELECT b.*, rt.\"name\" AS type_name FROM \"booking\" b " +
                "LEFT JOIN \"room\" r ON b.\"room_id\" = r.\"id\" " +
                "LEFT JOIN \"room_type\" rt ON r.\"type_id\" = rt.\"id\" " +
                "WHERE b.\"user_id\" = ? ORDER BY b.\"create_time\" DESC";
        return jdbcTemplate.query(sql, new BookingMapper(), userId);
    }

    public boolean deleteMyOrder(int orderId, int userId) {
        int n = jdbcTemplate.update("DELETE FROM \"booking\" WHERE \"id\"=? AND \"user_id\"=?", orderId, userId);
        // 删除订单后清理订单相关缓存
        redisTemplate.delete("order_list");
        redisTemplate.delete("order_wait_list");
        return n > 0;
    }

    public boolean updateMyInfo(int userId, String email, String phone, Integer gender) {
        int n = jdbcTemplate.update("UPDATE \"customer\" SET \"email\"=?, \"phone\"=?, \"gender\"=? WHERE \"id\"=?", email, phone, gender, userId);
        // 用户信息修改后清理用户列表缓存
        redisTemplate.delete("user_list");
        return n > 0;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        Customer user = jdbcTemplate.query("SELECT * FROM \"customer\" WHERE \"id\"=?", new CustomerMapper(), userId)
                .stream().findFirst().orElse(null);
        if (user == null || !user.getPassword().equals(oldPassword)) return false;
        int n = jdbcTemplate.update("UPDATE \"customer\" SET \"password\"=? WHERE \"id\"=?", newPassword, userId);
        return n > 0;
    }

    public boolean bookRoom(Long userId, Integer roomId, String checkin, String checkout, Integer people, Boolean needInvoice, double total) {
        // 再次保险：只保留日期部分
        String checkinDate = checkin.substring(0, 10);
        String checkoutDate = checkout.substring(0, 10);

        int rows = jdbcTemplate.update(
            "INSERT INTO \"booking\" (\"user_id\", \"room_id\", \"check_in_date\", \"check_out_date\", \"adults\", \"invoice_needed\", \"total_amount\", \"status\", \"create_time\") VALUES (?, ?, ?, ?, ?, ?, ?, 0, NOW())",
            userId, roomId, checkinDate, checkoutDate, people, needInvoice ? 1 : 0, total
        );
        // 新增：下单后清理订单相关缓存
        redisTemplate.delete("order_list");
        redisTemplate.delete("order_wait_list");
        return rows > 0;
    }
}