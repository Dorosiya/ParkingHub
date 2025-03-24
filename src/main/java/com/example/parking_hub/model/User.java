package com.example.parking_hub.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class User {

    private int id;
    private String email;
    private String username;
    private String password;
    private LocalDateTime createdAt;

}
