package com.example.parking_hub.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Admin {

    private int adminId;
    private String adminEmail;
    private String password;
    private LocalDateTime createdAt;
}
