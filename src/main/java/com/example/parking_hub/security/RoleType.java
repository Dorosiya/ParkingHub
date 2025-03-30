package com.example.parking_hub.security;

import java.util.HashMap;
import java.util.Map;

public enum RoleType {
    ADMIN(1, "ROLE_ADMIN"),
    USER(2, "ROLE_USER"),
    MANAGER(3, "ROLE_MANAGER");

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

    // ✅ 여기부터 중요! 일반 for문 사용
    private static final Map<Integer, RoleType> idToEnumMap = new HashMap<>();

    static {
        for (RoleType role : RoleType.values()) {
            idToEnumMap.put(role.getId(), role);
        }
    }

    public static RoleType fromId(int id) {
        return idToEnumMap.get(id);
    }
}
