package com.example.parking_hub.mapper;

import com.example.parking_hub.model.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {
    // 모든 역할 조회
    List<Role> selectAllRoles();
    
    // 특정 역할 조회
    Role selectRoleById(@Param("id") Long id);
    
    // 역할명으로 역할 조회
    Role selectRoleByName(@Param("name") String name);
    
    // 사용자 ID로 역할 조회
    List<Role> selectRolesByUserId(@Param("userId") Long userId);
    
    // 역할 생성
    int insertRole(Role role);
    
    // 역할 수정
    int updateRole(Role role);
    
    // 역할 삭제
    int deleteRole(@Param("id") Long id);
    
    // 사용자에 역할 할당
    int assignRoleToUser(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    // 사용자의 역할 제거
    int removeRoleFromUser(@Param("userId") Long userId, @Param("roleId") Long roleId);
} 