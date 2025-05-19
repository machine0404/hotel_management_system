package com.example.hotel_management_system_backend.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Customer {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer gender; // 0-女，1-男
    private Date createTime;
    private Integer points;
    private Integer status; // 0-未消费，1-已消费
}