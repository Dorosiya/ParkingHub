package com.example.parking_hub.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class Review {

    private int id;
    private int userId;
    private String parkingCode;
    private BigDecimal rating;
    private String comment;
    private LocalDateTime createdAt;
}
