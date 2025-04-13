package com.example.parking_hub.dto;

import java.util.List;

/**
 * 사용자 정보 DTO
 */
public class UserDto {
    private String username;
    private List<String> roles;

    public UserDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}