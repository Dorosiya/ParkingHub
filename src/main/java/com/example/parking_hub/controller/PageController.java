package com.example.parking_hub.controller;

import com.example.parking_hub.dto.UserDto;
import com.example.parking_hub.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PageController(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
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
        // 사용자 이름 중복 확인
        if (userMapper.findByUsername(username) != null) {
            model.addAttribute("error", "이미 사용 중인 아이디입니다.");
            return "register";
        }

        // 이메일 중복 확인
        if (userMapper.selectUserByEmail(email) != null) {
            model.addAttribute("error", "이미 사용 중인 이메일입니다.");
            return "register";
        }

        // 사용자 등록
        UserDto user = new UserDto();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        // 전화번호는 선택 사항이므로 null이 아닐 때만 설정
        if (phone != null && !phone.trim().isEmpty()) {
            // 추가 필드에 대한 처리 로직이 필요한 경우 여기에 추가
            // 예: user.setPhone(phone);
        }
        
        userMapper.insertUser(user);

        return "redirect:/login?registered=true";
    }
} 