package com.example.parking_hub.mapper;

import com.example.parking_hub.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    // 사용자 목록 조회
    List<User> selectAllUsers();

    // 사용자 정보 조회
    User selectUserById(@Param("id") Long id);

    // 사용자 정보 조회 (이메일)
    User selectUserByEmail(@Param("email") String email);

    // username으로 사용자 조회 (Spring Security 인증용)
    User findByUsername(@Param("username") String username);

    // 사용자 등록
    void insertUser(User user);

    // 사용자 정보 수정
    void updateUser(User user);

    // 사용자 삭제
    void deleteUser(@Param("id") Long id);
    
    // ID로 사용자 조회
    User findById(@Param("id") Long id);
}
