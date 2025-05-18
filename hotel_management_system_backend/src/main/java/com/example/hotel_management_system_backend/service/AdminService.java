package com.example.hotel_management_system_backend.service;

import com.example.hotel_management_system_backend.entity.Admin;
import com.example.hotel_management_system_backend.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Admin findByUsername(String username) {
        String sql = "SELECT * FROM \"admin\" WHERE username = ?";
        return jdbcTemplate.query(sql, new AdminMapper(), username)
                .stream().findFirst().orElse(null);
    }
}
