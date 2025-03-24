package com.example.parking_hub.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Comment {

    private int id;
    private int postId;
    private int userId;
    private String comment;
    private LocalDateTime createdAt;
}
