package com.example.parking_hub.security;

import com.example.parking_hub.config.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 인증 확인 필터
 * 요청에서 JWT 토큰을 확인하고 유효한 경우 인증 정보를 설정
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
    private final JwtUtil jwtUtil;

    public JwtAuthorizationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = resolveToken(request);
        
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                Authentication auth = jwtUtil.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                logger.debug("유효한 JWT 토큰을 통해 인증 정보 설정: {}", auth.getName());
            } else {
                logger.debug("유효하지 않은 JWT 토큰");
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        // 헤더에서 토큰 확인
        String bearerToken = request.getHeader(jwtUtil.getHeaderString());
        if (bearerToken != null && bearerToken.startsWith(jwtUtil.getTokenPrefix())) {
            return bearerToken.substring(jwtUtil.getTokenPrefix().length());
        }
        return null;
    }
} 