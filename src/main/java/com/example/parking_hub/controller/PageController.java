package com.example.parking_hub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.example.parking_hub.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.Cookie;

/**
 * 웹 페이지 뷰를 제공하는 컨트롤러
 */
@Controller
public class PageController {

    private final CookieUtil cookieUtil;

    @Autowired
    public PageController(CookieUtil cookieUtil) {
        this.cookieUtil = cookieUtil;
    }

    /**
     * 메인 페이지
     */
    @GetMapping("/main")
    public String index() {
        return "index";
    }

    /**
     * 로그인 페이지 (충돌 방지를 위해 URL 변경)
     */
    @GetMapping("/login-page")
    public String loginPage(Model model, HttpServletRequest request) {
        // 이미 로그인되어 있는 경우 메인 페이지로 리다이렉션
        String token = cookieUtil.getCookieValue(request, "jwt_token");
        if (token != null && !token.isEmpty()) {
            return "redirect:/";
        }
        
        // 다른 페이지에서 리다이렉션된 경우 (필요한 로그인 메시지가 있을 때)
        if (request.getParameter("needLogin") != null) {
            model.addAttribute("needLogin", true);
        }
        
        return "login";
    }

    /**
     * 회원가입 페이지 (충돌 방지를 위해 URL 변경)
     */
    @GetMapping("/register-page")
    public String registerPage() {
        return "register";
    }

    /**
     * 마이페이지 - 메인
     */
    @GetMapping("/mypage")
    public String myPage(HttpServletRequest request, Model model) {
        String token = cookieUtil.getCookieValue(request, "jwt_token");

        if (token == null || token.isEmpty()) {
            return "redirect:/login-page?message=로그인이 필요합니다.";
        }

        return "myPage";
    }

    /**
     * 마이페이지 - 즐겨찾기
     */
    @GetMapping("/mypage/favorites")
    public String myPageFavorites(HttpServletRequest request) {
        String token = cookieUtil.getCookieValue(request, "jwt_token");

        if (token == null || token.isEmpty()) {
            return "redirect:/login-page?message=로그인이 필요합니다.";
        }
        
        return "favorites";
    }

    /**
     * 주차장 검색 페이지
     */
    @GetMapping("/parking/search")
    public String parkingSearch() {
        return "parking/search";
    }

    /**
     * 주차장 상세 페이지
     */
    @GetMapping("/parking/detail/{prkCenterId}")
    public String parkingDetail() {
        return "parking/detail";
    }

    /**
     * 지도 페이지
     */
    @GetMapping("/map")
    public String map() {
        return "map";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request) {
        String token = cookieUtil.getCookieValue(request, "jwt_token");

        if (token == null || token.isEmpty()) {
            return "redirect:/login-page?message=로그인이 필요합니다.";
        }

        return "redirect:/mypage";
    }
} 