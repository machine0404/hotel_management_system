package com.example.hotel_management_system_backend.service;

import com.example.hotel_management_system_backend.entity.Customer;
import com.example.hotel_management_system_backend.mapper.CustomerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Customer findByUsername(String username) {
        String sql = "SELECT * FROM customer WHERE username = ?";
        return jdbcTemplate.query(sql, new CustomerMapper(), username)
                .stream().findFirst().orElse(null);
    }

    public void save(Customer user) {
        String sql = "INSERT INTO customer (username, password, email, phone, gender, create_time, points, status) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?)";
        jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getEmail(), user.getPhone(), user.getGender(), user.getPoints(), user.getStatus());
    }
}