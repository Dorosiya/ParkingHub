package com.example.parking_hub.controller;

import com.example.parking_hub.model.User;
import com.example.parking_hub.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String email,
                             @RequestParam String username,
                             @RequestParam String password,
                             @RequestParam(required = false) String phone,
                             Model model) {
        try {
            logger.info("회원가입 시도: 이메일={}, 사용자명={}", email, username);
            
            // 이메일 중복 체크
            if (userService.findByEmail(email) != null) {
                logger.warn("회원가입 실패: 이메일 중복 - {}", email);
                model.addAttribute("error", "이미 사용 중인 이메일입니다.");
                return "register";
            }
            
            // 사용자명 중복 체크
            if (userService.findByUsername(username) != null) {
                logger.warn("회원가입 실패: 사용자명 중복 - {}", username);
                model.addAttribute("error", "이미 사용 중인 닉네임입니다.");
                return "register";
            }
            
            // 사용자 등록 서비스 호출
            User user = userService.registerUser(email, username, password, phone);
            logger.info("회원가입 성공: 사용자 ID={}", user.getId());
            
            // 회원가입 성공 시 로그인 페이지로 리다이렉트
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            logger.error("회원가입 처리 중 예외 발생", e);
            model.addAttribute("error", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            return "register";
        }
    }
} 