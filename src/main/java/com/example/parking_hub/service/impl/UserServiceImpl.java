package com.example.parking_hub.service.impl;

import com.example.parking_hub.mapper.UserMapper;
import com.example.parking_hub.model.User;
import com.example.parking_hub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

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
} 