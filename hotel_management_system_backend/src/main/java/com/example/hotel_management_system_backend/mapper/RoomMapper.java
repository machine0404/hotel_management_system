package com.example.hotel_management_system_backend.mapper;

import com.example.hotel_management_system_backend.entity.Room;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomMapper implements RowMapper<Room> {
    @Override
    public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
        Room r = new Room();
        r.setId(rs.getInt("id"));
        r.setRoom_number(rs.getString("room_number"));
        r.setType_id(rs.getInt("type_id"));
        r.setStatus(rs.getInt("status"));
        r.setMax_people(rs.getInt("max_people"));
        r.setIntroduce(rs.getString("introduce"));
        try { r.setType_name(rs.getString("type_name")); } catch (Exception ignored) {}
        try { r.setPrice(rs.getDouble("price")); } catch (Exception ignored) {}
        try { r.setFeature(rs.getString("feature")); } catch (Exception ignored) {}
        try { r.setDescription(rs.getString("description")); } catch (Exception ignored) {}
        try { r.setCover_image(rs.getString("cover_image")); } catch (Exception ignored) {}
        return r;
    }
}
