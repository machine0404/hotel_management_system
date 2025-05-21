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
    private Integer gender;
    private Date create_time;
    private Integer points;
    private Integer status;
}