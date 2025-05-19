package com.example.hotel_management_system_backend.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Booking {
    private Integer id;
    private Integer userId;
    private Integer roomId;
    private Date checkInDate;
    private Date checkOutDate;
    private Date createTime;
    private Double totalAmount;
    private Double realAmount;
    private Integer adults;
    private Integer invoiceNeeded; // 0-不需要，1-需要
    private Integer status; // 0-未处理，1-已接受,2-已取消
}
