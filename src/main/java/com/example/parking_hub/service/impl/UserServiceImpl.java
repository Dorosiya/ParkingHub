package com.example.parking_hub.service.impl;

import com.example.parking_hub.mapper.UserMapper;
import com.example.parking_hub.model.User;
import com.example.parking_hub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findByEmail(String email) {
        return userMapper.selectUserByEmail(email);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public User registerUser(String email, String username, String password, String phone) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 사용자 객체 생성
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(encodedPassword);
        // phone이 null이면 빈 문자열 설정
        user.setPhoneNumber(phone != null ? phone : "");
        user.setRoleId(1); // 기본 사용자 역할 ID

        // DB에 저장
        userMapper.insertUser(user);

        return user;
    }
    
    @Override
    public User updateProfile(Long userId, String phone) {
        try {
            // 현재 사용자 정보 조회
            User user = userMapper.findById(userId);
            if (user == null) {
                logger.error("프로필 업데이트 실패: 사용자를 찾을 수 없음 - ID: {}", userId);
                throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
            }
            
            // 전화번호 업데이트
            user.setPhoneNumber(phone != null ? phone : "");
            
            // DB 업데이트
            userMapper.updateUser(user);
            logger.info("프로필 업데이트 성공: 사용자 ID={}", userId);
            
            return user;
        } catch (Exception e) {
            logger.error("프로필 업데이트 중 오류 발생", e);
            throw e;
        }
    }
    
    @Override
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        try {
            // 현재 사용자 정보 조회
            User user = userMapper.findById(userId);
            if (user == null) {
                logger.error("비밀번호 변경 실패: 사용자를 찾을 수 없음 - ID: {}", userId);
                throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
            }
            
            // 현재 비밀번호 확인
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                logger.warn("비밀번호 변경 실패: 현재 비밀번호 불일치 - 사용자 ID: {}", userId);
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
            
            // 새 비밀번호 암호화
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedNewPassword);
            
            // DB 업데이트
            userMapper.updateUser(user);
            logger.info("비밀번호 변경 성공: 사용자 ID={}", userId);
            
            return true;
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 인자 오류 그대로 전달
            throw e;
        } catch (Exception e) {
            logger.error("비밀번호 변경 중 오류 발생", e);
            throw new RuntimeException("비밀번호 변경 중 오류가 발생했습니다.", e);
        }
    }
} 