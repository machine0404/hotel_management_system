package com.example.hotel_management_system_backend.entity;

import lombok.Data;

@Data
public class Room {
    private Integer id;
    private String roomNumber;
    private Integer typeId;
    private Integer status; // 0-空闲，1-已用
    private Integer maxPeople;
    private String introduce;
}