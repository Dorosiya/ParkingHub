package com.example.parking_hub.controller;

import com.example.parking_hub.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 로그아웃 처리를 담당하는 컨트롤러
 */
@Controller
public class LogoutController {

    private final CookieUtil cookieUtil;

    @Autowired
    public LogoutController(CookieUtil cookieUtil) {
        this.cookieUtil = cookieUtil;
    }

    /**
     * 로그아웃 요청 처리
     * GET 방식의 로그아웃 요청을 처리하여 홈으로 리다이렉트합니다.
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키 삭제
        cookieUtil.clearAuthCookies(response);
        
        // 세션 인증 정보 제거
        SecurityContextHolder.clearContext();
        
        // 홈으로 리다이렉트
        return "redirect:/";
    }
} 