package com.example.parking_hub.security;

import com.example.parking_hub.model.User;
import com.example.parking_hub.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Autowired
    public CustomUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        // RoleType enum을 사용하여 역할 정보 가져오기
        RoleType roleType = RoleType.fromId(user.getRoleId());
        if (roleType == null) {
            throw new RuntimeException("유효하지 않은 역할 ID입니다: roleId=" + user.getRoleId());
        }

        return new CustomUserDetails(user, roleType.getRoleName());
    }
} 