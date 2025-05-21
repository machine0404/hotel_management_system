package com.example.hotel_management_system_backend.controller;

import com.example.hotel_management_system_backend.entity.Customer;
import com.example.hotel_management_system_backend.entity.Room;
import com.example.hotel_management_system_backend.entity.RoomType;
import com.example.hotel_management_system_backend.entity.Booking;
import com.example.hotel_management_system_backend.service.UserService;
import com.example.hotel_management_system_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody Customer param) {
        String username = param.getUsername();
        String password = param.getPassword();
        Customer user = userService.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            return new LoginResponse(null, "用户名或密码错误");
        }
        String token = JwtUtil.generateToken(username);
        return new LoginResponse(token, "登录成功");
    }

    @PostMapping("/register")
    public ApiResponse register(@RequestBody Customer param) {
        String username = param.getUsername();
        String password = param.getPassword();
        if (username == null || password == null) {
            return new ApiResponse(false, "用户名和密码不能为空");
        }
        Customer exist = userService.findByUsername(username);
        if (exist != null) {
            return new ApiResponse(false, "用户名已存在");
        }
        param.setEmail(username + "@example.com");
        param.setPhone("");
        param.setGender(0);
        param.setPoints(0);
        param.setStatus(0);
        userService.save(param);
        return new ApiResponse(true, "注册成功");
    }

    @GetMapping("/room-list")
    public List<Room> roomList(@RequestParam(required = false) String check_in_date,
                               @RequestParam(required = false) String check_out_date) {
        return userService.getRoomList(check_in_date, check_out_date);
    }

    @GetMapping("/room-type-list")
    public List<RoomType> roomTypeList() {
        return userService.getRoomTypeList();
    }

    @GetMapping("/me")
    public Customer getMyInfo(@RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        String username = JwtUtil.parseToken(token).getSubject();
        Customer user = userService.findByUsername(username);
        if (user == null) throw new RuntimeException("用户不存在");
        return user;
    }

    @PutMapping("/me")
    public ApiResponse updateMyInfo(@RequestBody Customer param, @RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        String username = JwtUtil.parseToken(token).getSubject();
        Customer user = userService.findByUsername(username);
        if (user == null) return new ApiResponse(false, "用户不存在");
        boolean ok = userService.updateMyInfo(user.getId(), param.getEmail(), param.getPhone(), param.getGender());
        return new ApiResponse(ok, ok ? "修改成功" : "修改失败");
    }

    @GetMapping("/my-orders")
    public List<Booking> getMyOrders(@RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        String username = JwtUtil.parseToken(token).getSubject();
        Customer user = userService.findByUsername(username);
        if (user == null) throw new RuntimeException("用户不存在");
        return userService.getMyOrders(user.getId());
    }

    @PutMapping("/change-password")
    public ApiResponse changePassword(@RequestBody ChangePasswordParam param, @RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        String username = JwtUtil.parseToken(token).getSubject();
        Customer user = userService.findByUsername(username);
        if (user == null) return new ApiResponse(false, "用户不存在");
        boolean ok = userService.changePassword(user.getId(), param.old_password, param.new_password);
        return new ApiResponse(ok, ok ? "修改成功" : "原密码错误");
    }

    @PostMapping("/book-room")
    public ApiResponse bookRoom(@RequestBody BookRoomParam param, @RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        String username = JwtUtil.parseToken(token).getSubject();
        Customer user = userService.findByUsername(username);
        if (user == null) return new ApiResponse(false, "请先登录");
        List<Room> rooms = userService.getRoomList(null, null);
        Room room = rooms.stream().filter(r -> r.getId().equals(param.room_id)).findFirst().orElse(null);
        if (room == null) return new ApiResponse(false, "房间不存在");

        // 兼容前端传来的 ISO 日期格式
        LocalDate checkinDate;
        LocalDate checkoutDate;
        try {
            checkinDate = LocalDate.parse(param.checkin.substring(0, 10));
            checkoutDate = LocalDate.parse(param.checkout.substring(0, 10));
        } catch (Exception e) {
            return new ApiResponse(false, "日期格式错误");
        }

        long days = java.time.temporal.ChronoUnit.DAYS.between(checkinDate, checkoutDate);
        if (days <= 0) return new ApiResponse(false, "离店时间必须晚于入住时间");
        double total = room.getPrice() * days;
        boolean ok = userService.bookRoom(user.getId().longValue(), param.room_id, param.checkin, param.checkout, param.people, param.need_invoice, total);
        return new ApiResponse(ok, ok ? "预定成功" : "预定失败");
    }

    @DeleteMapping("/my-orders/{id}")
    public ApiResponse deleteMyOrder(@PathVariable Integer id, @RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        String username = JwtUtil.parseToken(token).getSubject();
        Customer user = userService.findByUsername(username);
        if (user == null) return new ApiResponse(false, "用户不存在");
        boolean ok = userService.deleteMyOrder(id, user.getId());
        return new ApiResponse(ok, ok ? "删除成功" : "删除失败");
    }

    // DTO
    public static class ApiResponse {
        private boolean success;
        private String message;
        public ApiResponse() {}
        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    public static class LoginResponse {
        private String token;
        private String message;
        public LoginResponse() {}
        public LoginResponse(String token, String message) {
            this.token = token;
            this.message = message;
        }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    // 关键：参数全部下划线风格
    public static class ChangePasswordParam {
        public String old_password;
        public String new_password;
    }
    public static class BookRoomParam {
        public Integer room_id;
        public String checkin;
        public String checkout;
        public Integer people;
        public Boolean need_invoice;
    }
}