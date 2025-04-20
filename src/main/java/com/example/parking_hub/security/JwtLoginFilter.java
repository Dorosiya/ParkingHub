package com.example.parking_hub.security;

import com.example.parking_hub.util.JwtUtil;
import com.example.parking_hub.dto.LoginRequest;
import com.example.parking_hub.util.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 로그인 처리 필터
 * 사용자명과 비밀번호로 인증 후 JWT 토큰 발급
 * JSON 형식 요청만 처리
 */
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtLoginFilter.class);
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtLoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, CookieUtil cookieUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
        // 로그인 URL 설정
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) 
            throws AuthenticationException {
        
        try {
            // 요청 본문을 UTF-8 문자열로 변환
            String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            
            // JSON 문자열을 객체로 변환
            LoginRequest loginRequest = objectMapper.readValue(messageBody, LoginRequest.class);
            logger.info("JSON 로그인 시도: username={}", loginRequest.getUsername());
            
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            logger.error("로그인 요청 처리 중 오류", e);
            throw new RuntimeException("로그인 요청을 처리할 수 없습니다", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                           FilterChain chain, Authentication authResult) 
            throws IOException, ServletException {
        
        UserDetails user = (UserDetails) authResult.getPrincipal();
        String username = user.getUsername();
        String token = jwtUtil.createToken(username);
        
        // CookieUtil을 사용하여 JWT 토큰 쿠키 설정
        int maxAge = 86400; // 24시간 (초 단위) - JwtUtil에 정의된 것과 동일한 값
        cookieUtil.createAuthCookie(request, response, token, maxAge);
        
        // 응답 본문에 토큰 및 사용자 정보 포함
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // 사용자 ID 추출 (CustomUserDetails로 캐스팅 가능한 경우)
        Long userId = null;
        if (user instanceof CustomUserDetails) {
            userId = ((CustomUserDetails) user).getUser().getId();
        }
        
        String responseJson = String.format(
            "{\"success\":true,\"message\":\"로그인에 성공했습니다.\",\"token\":\"%s\",\"username\":\"%s\"%s}",
            token, 
            username,
            userId != null ? String.format(",\"id\":%d", userId) : ""
        );
        
        response.getWriter().write(responseJson);
    }
    
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                             AuthenticationException failed) 
            throws IOException, ServletException {
        
        logger.warn("로그인 실패: {}", failed.getMessage());
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"success\":false,\"message\":\"아이디 또는 비밀번호가 올바르지 않습니다.\"}");
    }
} 