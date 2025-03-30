package com.example.parking_hub.mapper;

import com.example.parking_hub.dto.UserDto;
import com.example.parking_hub.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    // 사용자 목록 조회
    List<UserDto> selectAllUsers();

    // 사용자 정보 조회
    UserDto selectUserById(@Param("id") int id);

    // 사용자 정보 조회 (이메일)
    UserDto selectUserByEmail(@Param("email") String email);

    // username(email)으로 사용자 조회 (Spring Security 인증용)
    UserDto findByUsername(@Param("username") String username);

    // 사용자 등록
    int insertUser(UserDto user);

    // 사용자 정보 수정
    int updateUser(UserDto user);

    // 사용자 삭제
    int deleteUser(@Param("id") int id);

    User findByUsername(@Param("username") String username);
    User selectUserByEmail(@Param("email") String email);
    void insertUser(User user);
    User findById(@Param("id") Long id);
    void updateUser(User user);
    void deleteUser(@Param("id") Long id);
}
