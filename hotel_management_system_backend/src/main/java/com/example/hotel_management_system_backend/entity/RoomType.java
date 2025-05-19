package com.example.hotel_management_system_backend.entity;

import lombok.Data;

@Data
public class RoomType {
    private Integer id;
    private String name;
    private Double price;
    private String description;
    private String feature;
    private String coverImage;
}
