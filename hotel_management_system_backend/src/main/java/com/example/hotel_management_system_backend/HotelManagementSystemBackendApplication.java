package com.example.hotel_management_system_backend;

import org.mybatis.spring.annotation.MapperScan; // 新增
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.hotel_management_system_backend.mapper") // 新增
public class HotelManagementSystemBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelManagementSystemBackendApplication.class, args);
    }

}
