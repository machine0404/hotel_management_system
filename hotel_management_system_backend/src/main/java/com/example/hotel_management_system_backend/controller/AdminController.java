package com.example.hotel_management_system_backend.controller;

import com.example.hotel_management_system_backend.entity.Admin;
import com.example.hotel_management_system_backend.entity.Booking;
import com.example.hotel_management_system_backend.entity.Customer;
import com.example.hotel_management_system_backend.entity.Room;
import com.example.hotel_management_system_backend.entity.RoomType;
import com.example.hotel_management_system_backend.service.AdminService;
import com.example.hotel_management_system_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody Admin param) {
        String username = param.getUsername();
        String password = param.getPassword();
        Admin admin = adminService.findByUsername(username);
        if (admin == null || !admin.getPassword().equals(password)) {
            return new LoginResponse(null, "用户名或密码错误");
        }
        String token = JwtUtil.generateToken(username);
        return new LoginResponse(token, "登录成功");
    }

    @GetMapping("/user-list")
    public List<Customer> userList() {
        return adminService.getUserList();
    }

    @GetMapping("/order-list")
    public List<Booking> orderList() {
        return adminService.getOrderList();
    }

    @GetMapping("/order-wait")
    public List<Booking> orderWaitList() {
        return adminService.getOrderWaitList();
    }

    @GetMapping("/room-list")
    public List<Room> roomList() {
        return adminService.getRoomList();
    }

    @GetMapping("/room-type-list")
    public List<RoomType> roomTypeList() {
        return adminService.getRoomTypeList();
    }

    @DeleteMapping("/user/{id}")
    public ApiResponse deleteUser(@PathVariable Integer id) {
        boolean ok = adminService.deleteUserById(id);
        return new ApiResponse(ok, ok ? "删除成功" : "删除失败");
    }

    @GetMapping("/user/{id}")
    public ApiResponse getUserById(@PathVariable Integer id) {
        Customer user = adminService.getUserById(id);
        if (user == null) {
            return new ApiResponse(false, "未查询到用户");
        }
        return new ApiResponse(true, "查询成功", user);
    }

    @PostMapping("/order/{id}/accept")
    public ApiResponse acceptOrder(@PathVariable Integer id) {
        boolean ok = adminService.acceptOrder(id);
        return new ApiResponse(ok, ok ? "操作成功" : "操作失败");
    }

    @PostMapping("/order/{id}/cancel")
    public ApiResponse cancelOrder(@PathVariable Integer id) {
        boolean ok = adminService.cancelOrder(id);
        return new ApiResponse(ok, ok ? "操作成功" : "操作失败");
    }

    @PutMapping("/user/{id}")
    public ApiResponse updateUser(@PathVariable Integer id, @RequestBody Customer param) {
        boolean ok = adminService.updateUser(id, param);
        return new ApiResponse(ok, ok ? "修改成功" : "修改失败");
    }

    @DeleteMapping("/room/{id}")
    public ApiResponse deleteRoom(@PathVariable Integer id) {
        boolean ok = adminService.deleteRoomById(id);
        return new ApiResponse(ok, ok ? "删除成功" : "删除失败");
    }

    @GetMapping("/room/{id}")
    public ApiResponse getRoomById(@PathVariable Integer id) {
        Room room = adminService.getRoomById(id);
        if (room == null) {
            return new ApiResponse(false, "未查询到房间");
        }
        return new ApiResponse(true, "查询成功", room);
    }

    @PostMapping("/room-add")
    public ApiResponse addRoom(@RequestBody Room param) {
        boolean ok = adminService.addRoom(param);
        return new ApiResponse(ok, ok ? "添加成功" : "添加失败");
    }

    @PutMapping("/room/{id}")
    public ApiResponse updateRoom(@PathVariable Integer id, @RequestBody Room param) {
        boolean ok = adminService.updateRoom(id, param);
        return new ApiResponse(ok, ok ? "修改成功" : "修改失败");
    }

    // 其它增删改查接口同理，参数和返回值都用实体类，所有数据库和缓存操作全部调用AdminService
    // ...

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

    // 如果 ApiResponse 没有 data 字段，可以这样定义
    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;
        public ApiResponse() {}
        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        public ApiResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }
}
