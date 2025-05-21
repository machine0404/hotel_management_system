package com.example.hotel_management_system_backend.mapper;

import com.example.hotel_management_system_backend.entity.Booking;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookingMapper implements RowMapper<Booking> {
    @Override
    public Booking mapRow(ResultSet rs, int rowNum) throws SQLException {
        Booking b = new Booking();
        b.setId(rs.getInt("id"));
        b.setUser_id(rs.getInt("user_id"));
        b.setRoom_id(rs.getInt("room_id"));
        b.setCheck_in_date(rs.getTimestamp("check_in_date"));
        b.setCheck_out_date(rs.getTimestamp("check_out_date"));
        b.setCreate_time(rs.getTimestamp("create_time"));
        b.setTotal_amount(rs.getDouble("total_amount"));
        try { b.setReal_amount(rs.getDouble("real_amount")); } catch (Exception ignored) {}
        b.setAdults(rs.getInt("adults"));
        b.setInvoice_needed(rs.getInt("invoice_needed"));
        b.setStatus(rs.getInt("status"));
        try { b.setType_name(rs.getString("type_name")); } catch (Exception ignored) {}
        try { b.setUser_name(rs.getString("user_name")); } catch (Exception ignored) {}
        return b;
    }
}
