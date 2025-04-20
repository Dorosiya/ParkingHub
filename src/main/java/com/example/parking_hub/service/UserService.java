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
    
    /**
     * ID로 사용자 조회
     * @param id 사용자 ID
     * @return 조회된 사용자 객체, 없으면 null
     */
    User findById(Long id);
    
    /**
     * 사용자 프로필 정보 업데이트
     * @param userId 사용자 ID
     * @param phone 전화번호
     * @return 업데이트된 사용자 객체
     */
    User updateProfile(Long userId, String phone);
    
    /**
     * 사용자 비밀번호 변경
     * @param userId 사용자 ID
     * @param currentPassword 현재 비밀번호
     * @param newPassword 새 비밀번호
     * @return 성공 여부
     * @throws IllegalArgumentException 현재 비밀번호가 일치하지 않는 경우
     */
    boolean changePassword(Long userId, String currentPassword, String newPassword) throws IllegalArgumentException;
} 