package com.example.hotel_management_system_backend.mapper;

import com.example.hotel_management_system_backend.entity.Customer;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerMapper implements RowMapper<Customer> {
    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
        customer.setUsername(rs.getString("username"));
        customer.setPassword(rs.getString("password"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setGender(rs.getInt("gender"));
        customer.setCreateTime(rs.getTimestamp("create_time")); // 修正
        customer.setPoints(rs.getInt("points"));
        customer.setStatus(rs.getInt("status"));
        return customer;
    }
}