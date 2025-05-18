package com.example.hotel_management_system_backend.mapper;

import com.example.hotel_management_system_backend.entity.Customer;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerMapper implements RowMapper<Customer> {
    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getInt("id"));
        c.setUsername(rs.getString("username"));
        c.setPassword(rs.getString("password"));
        c.setEmail(rs.getString("email"));
        c.setPhone(rs.getString("phone"));
        c.setGender(rs.getInt("gender"));
        c.setCreateTime(rs.getString("create_time"));
        c.setPoints(rs.getInt("points"));
        c.setStatus(rs.getInt("status"));
        return c;
    }
}