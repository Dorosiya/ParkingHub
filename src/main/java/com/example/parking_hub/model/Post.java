package com.example.parking_hub.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Post {

    private int id;
    private int userId;
    private String title;
    private String content;
    private int views;
    private LocalDateTime createdAt;
}
