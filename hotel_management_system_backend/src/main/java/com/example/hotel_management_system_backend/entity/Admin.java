package com.example.hotel_management_system_backend.entity;

import lombok.Data;

@Data
public class Admin {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String createTime;
    private String realName;
}
