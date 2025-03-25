package com.example.parking_hub.security;

public enum RoleType {
    USER(1, "ROLE_USER"),
    ADMIN(2, "ROLE_ADMIN");

    private final int id;
    private final String roleName;

    RoleType(int id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    public int getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public static RoleType fromId(Integer id) {
        if (id == null) {
            return USER; // Default role
        }
        
        for (RoleType roleType : RoleType.values()) {
            if (roleType.id == id) {
                return roleType;
            }
        }
        return USER; // Default role if not found
    }
}
