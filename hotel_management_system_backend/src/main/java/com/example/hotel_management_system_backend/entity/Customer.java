package com.example.hotel_management_system_backend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("customer")
public class Customer {
    @TableId
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer gender;
    private String createTime;
    private Integer points;
    private Integer status;
}