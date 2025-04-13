package com.example.parking_hub.controller;

import com.example.parking_hub.dto.UserDto;
import com.example.parking_hub.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AuthController {

    /**
     * 로그아웃 API
     * JWT 토큰 쿠키를 제거합니다.
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletResponse response) {
        // JWT 토큰 쿠키 제거
        Cookie jwtCookie = new Cookie("jwt_token", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // 즉시 만료
        response.addCookie(jwtCookie);
        
        // logged_in 쿠키도 제거 (클라이언트 측에서 확인용)
        Cookie loggedInCookie = new Cookie("logged_in", null);
        loggedInCookie.setPath("/");
        loggedInCookie.setMaxAge(0);
        response.addCookie(loggedInCookie);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "로그아웃 되었습니다.");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 현재 로그인한 사용자 정보 API
     */
    @GetMapping("/user/current")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "인증되지 않은 사용자입니다.");
            return ResponseEntity.status(401).body(errorResponse);
        }
        
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        UserDto user = new UserDto();
        user.setUsername(userDetails.getUsername());
        user.setRoles(userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList()));
        
        return ResponseEntity.ok(user);
    }
} 