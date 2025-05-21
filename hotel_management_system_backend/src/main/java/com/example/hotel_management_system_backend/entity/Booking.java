package com.example.hotel_management_system_backend.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Booking {
    private Integer id;
    private Integer user_id;
    private Integer room_id;
    private Date check_in_date;
    private Date check_out_date;
    private Date create_time;
    private Double total_amount;
    private Double real_amount;
    private Integer adults;
    private Integer invoice_needed;
    private Integer status;

    // 扩展字段
    private String type_name;
    private String user_name;
}
