package com.example.parking_hub.service;

import com.example.parking_hub.model.User;

public interface UserService {
    /**
     * 이메일로 사용자 조회
     * @param email 사용자 이메일
     * @return 조회된 사용자 객체, 없으면 null
     */
    User findByEmail(String email);
    
    /**
     * 사용자명으로 사용자 조회
     * @param username 사용자명
     * @return 조회된 사용자 객체, 없으면 null
     */
    User findByUsername(String username);
    
    /**
     * 사용자 등록
     * @param email 이메일
     * @param username 사용자명
     * @param password 비밀번호 (암호화되지 않은 상태)
     * @param phone 전화번호 (선택사항)
     * @return 등록된 사용자 객체
     */
    User registerUser(String email, String username, String password, String phone);
} 