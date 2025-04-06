package com.example.parking_hub.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 요청 DTO
 */
@Getter
@Setter
public class LoginRequest {
    private String username;
    private String password;
} 