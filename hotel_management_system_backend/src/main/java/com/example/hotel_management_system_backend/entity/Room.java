package com.example.hotel_management_system_backend.entity;

import lombok.Data;

@Data
public class Room {
    private Integer id;
    private String room_number; // 注意：下划线命名，和前端、数据库一致
    private Integer type_id;
    private Integer status;
    private Integer max_people;
    private String introduce;

    // 扩展字段（用于连表查询房型信息）
    private String type_name;
    private Double price;
    private String feature;
    private String description;
    private String cover_image;
}