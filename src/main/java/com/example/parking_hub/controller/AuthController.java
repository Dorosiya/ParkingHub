package com.example.parking_hub.controller;

import com.example.parking_hub.dto.UserDto;
import com.example.parking_hub.security.CustomUserDetails;
import com.example.parking_hub.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CookieUtil cookieUtil;

    @Autowired
    public AuthController(CookieUtil cookieUtil) {
        this.cookieUtil = cookieUtil;
    }

    /**
     * 현재 로그인한 사용자 정보 조회
     * @param authentication 인증 정보
     * @return 사용자 정보
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).build();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserDto userDto = new UserDto();
        userDto.setId(userDetails.getUser().getId());
        userDto.setUsername(userDetails.getUsername());
        userDto.setEmail(userDetails.getUser().getEmail());
        userDto.setPhone(userDetails.getUser().getPhoneNumber()); // phoneNumber 필드 사용
        
        // LocalDateTime을 Date로 변환
        if (userDetails.getUser().getCreatedAt() != null) {
            Date createdAt = Date.from(userDetails.getUser().getCreatedAt()
                    .atZone(ZoneId.systemDefault())
                    .toInstant());
            userDto.setCreatedAt(createdAt);
        }
        
        userDto.setRoles(userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList()));

        return ResponseEntity.ok(userDto);
    }

    /**
     * 로그아웃 처리
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @return 로그아웃 결과
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            HttpServletRequest request, HttpServletResponse response) {
        
        // 쿠키 삭제
        cookieUtil.clearAuthCookies(response);
        
        // 인증 정보 제거
        SecurityContextHolder.clearContext();
        
        // 응답 데이터
        Map<String, String> result = new HashMap<>();
        result.put("message", "로그아웃 되었습니다.");
        
        return ResponseEntity.ok(result);
    }
} 