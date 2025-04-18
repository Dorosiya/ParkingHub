package com.example.parking_hub.util;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 쿠키 관리를 위한 유틸리티 클래스
 * 쿠키 생성, 조회, 삭제 등의 기능 제공
 */
@Component
public class CookieUtil {

    private static final String AUTH_COOKIE_NAME = "jwt_token";

    /**
     * 쿠키 생성 메서드
     * 
     * @param response HTTP 응답 객체
     * @param name 쿠키 이름
     * @param value 쿠키 값
     * @param maxAge 쿠키 유효 시간 (초)
     * @param httpOnly HTTP Only 설정 여부
     * @param secure Secure 설정 여부
     * @param path 쿠키 경로
     */
    public void createCookie(HttpServletResponse response, String name, String value, 
                             int maxAge, boolean httpOnly, boolean secure, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path != null ? path : "/");
        response.addCookie(cookie);
    }
    
    /**
     * 인증 토큰 쿠키 생성 (JWT 토큰용)
     */
    public void createAuthCookie(HttpServletRequest request, HttpServletResponse response, 
                                String token, int maxAge) {
        // HTTP 전용 쿠키로 토큰 설정 (JavaScript에서 접근 불가)
        createCookie(
            response, 
            AUTH_COOKIE_NAME, 
            token, 
            maxAge, 
            true, 
            request.isSecure(), 
            "/"
        );
        
        // 클라이언트측 로그인 상태 확인용 쿠키 (JavaScript에서 접근 가능)
        createCookie(
            response, 
            "logged_in", 
            "true", 
            maxAge, 
            false, 
            request.isSecure(), 
            "/"
        );
    }
    
    /**
     * 인증 쿠키 삭제 (로그아웃용)
     */
    public void clearAuthCookies(HttpServletResponse response) {
        // JWT 토큰 쿠키 제거
        createCookie(response, AUTH_COOKIE_NAME, null, 0, true, false, "/");
        
        // 로그인 상태 쿠키 제거
        createCookie(response, "logged_in", null, 0, false, false, "/");
    }
    
    /**
     * 요청에서 쿠키 값 조회
     * 
     * @param request HTTP 요청 객체
     * @param name 쿠키 이름
     * @return 쿠키 값 (없으면 null)
     */
    public String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
} 