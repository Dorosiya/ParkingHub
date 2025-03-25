package com.example.parking_hub.model;

public class Role {
    private Integer id;
    private String name;
    private String description;

    // 기본 생성자 추가 (MyBatis용)
    public Role() {
    }

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
