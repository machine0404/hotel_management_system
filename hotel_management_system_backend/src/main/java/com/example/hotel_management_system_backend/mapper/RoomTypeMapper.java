package com.example.hotel_management_system_backend.mapper;

import com.example.hotel_management_system_backend.entity.RoomType;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomTypeMapper implements RowMapper<RoomType> {
    @Override
    public RoomType mapRow(ResultSet rs, int rowNum) throws SQLException {
        RoomType t = new RoomType();
        t.setId(rs.getInt("id"));
        t.setName(rs.getString("name"));
        t.setPrice(rs.getDouble("price"));
        t.setDescription(rs.getString("description"));
        t.setFeature(rs.getString("feature"));
        t.setCover_image(rs.getString("cover_image"));
        return t;
    }
}
