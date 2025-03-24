package com.example.parking_hub.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class UserHistory {

    private int id;
    private int userId;
    private String parkingCode;
    private LocalDateTime visitedAt;
}
