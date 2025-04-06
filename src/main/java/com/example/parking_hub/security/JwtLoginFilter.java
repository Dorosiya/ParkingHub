package com.example.parking_hub.security;

import com.example.parking_hub.config.JwtUtil;
import com.example.parking_hub.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtLoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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
                            loginRequest.getPassword()
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
        
        String username = authResult.getName();
        logger.info("로그인 성공: username={}", username);
        
        // JWT 토큰 생성
        String token = jwtUtil.createToken(username);
        
        // 토큰을 응답 헤더에 추가
        response.addHeader(jwtUtil.getHeaderString(), jwtUtil.getTokenPrefix() + token);
        
        // 토큰을 응답 본문에도 포함
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", token);
        responseBody.put("username", username);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
    
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                             AuthenticationException failed) 
            throws IOException, ServletException {
        
        logger.warn("로그인 실패: {}", failed.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", "인증에 실패했습니다");
        errorDetails.put("error", failed.getMessage());
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
} 