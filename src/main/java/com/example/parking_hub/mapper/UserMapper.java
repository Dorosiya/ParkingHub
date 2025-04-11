package com.example.parking_hub.mapper;

import com.example.parking_hub.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 모든 사용자 조회
     */
    List<User> selectAllUsers();

    /**
     * ID로 사용자 조회
     */
    User selectUserById(@Param("id") Long id);

    /**
     * 이메일로 사용자 조회
     */
    User selectUserByEmail(@Param("email") String email);

    /**
     * 사용자명(username)으로 사용자 조회 (Spring Security 인증용)
     */
    User findByUsername(@Param("username") String username);

    /**
     * ID로 사용자 조회 (별칭)
     */
    User findById(@Param("id") Long id);

    /**
     * 사용자 등록
     */
    void insertUser(User user);

    /**
     * 사용자 정보 수정
     */
    void updateUser(User user);

    /**
     * 사용자 삭제
     */
    void deleteUser(@Param("id") Long id);
}
