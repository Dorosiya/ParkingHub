package com.example.parking_hub.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    /**
     * 메인 페이지 반환
     */
    @GetMapping("/")
    public String home(Model model) {
        // 현재 인증된 사용자 정보 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
        
        // 인증 여부를 모델에 추가
        model.addAttribute("isAuthenticated", isAuthenticated);
        
        // 인증된 경우 사용자 이름 추가
        if (isAuthenticated) {
            model.addAttribute("username", auth.getName());
        }
        
        return "home";
    }
    
    /**
     * 로그인 페이지 반환
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 회원가입 페이지 반환
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    /**
     * 대시보드 페이지 반환
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    
    /**
     * 커뮤니티 페이지 반환
     */
    @GetMapping("/community")
    public String community() {
        return "community";
    }
    
    /**
     * 주차장 검색 페이지 반환
     */
    @GetMapping("/search")
    public String search() {
        return "search";
    }
} 