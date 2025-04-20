package com.example.parking_hub.controller.api;

import com.example.parking_hub.model.User;
import com.example.parking_hub.service.UserService;
import com.example.parking_hub.util.CookieUtil;
import com.example.parking_hub.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserApiController.class);
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    
    @Autowired
    public UserApiController(UserService userService, JwtUtil jwtUtil, CookieUtil cookieUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
    }
    
    /**
     * 현재 인증된 사용자 정보 조회
     */
    @GetMapping("/users/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            // JWT 토큰에서 사용자 ID 추출
            String token = cookieUtil.getCookieValue(request, "jwt_token");
            if (token == null || token.isEmpty()) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("message", "인증되지 않은 요청입니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMap);
            }
            
            Long userId = jwtUtil.getUserIdFromToken(token);
            User user = userService.findById(userId);
            
            if (user == null) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
            }
            
            // 중요 정보 제외하고 반환
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("email", user.getEmail());
            userInfo.put("phone", user.getPhoneNumber());
            
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            logger.error("사용자 정보 조회 중 오류 발생", e);
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("message", "사용자 정보를 조회하는 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
        }
    }
    
    /**
     * 사용자 프로필 정보 업데이트
     */
    @PutMapping("/users/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> profileData, HttpServletRequest request) {
        try {
            // JWT 토큰에서 사용자 ID 추출
            String token = cookieUtil.getCookieValue(request, "jwt_token");
            if (token == null || token.isEmpty()) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("message", "인증되지 않은 요청입니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMap);
            }
            
            Long userId = jwtUtil.getUserIdFromToken(token);
            String phone = profileData.get("phone");
            
            User updatedUser = userService.updateProfile(userId, phone);
            
            // 중요 정보 제외하고 반환
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", updatedUser.getId());
            userInfo.put("username", updatedUser.getUsername());
            userInfo.put("email", updatedUser.getEmail());
            userInfo.put("phone", updatedUser.getPhoneNumber());
            userInfo.put("message", "프로필이 성공적으로 업데이트되었습니다.");
            
            return ResponseEntity.ok(userInfo);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
        } catch (Exception e) {
            logger.error("프로필 업데이트 중 오류 발생", e);
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("message", "프로필을 업데이트하는 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
        }
    }
    
    /**
     * 사용자 비밀번호 변경
     */
    @PutMapping("/users/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordData, HttpServletRequest request) {
        try {
            // JWT 토큰에서 사용자 ID 추출
            String token = cookieUtil.getCookieValue(request, "jwt_token");
            if (token == null || token.isEmpty()) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("message", "인증되지 않은 요청입니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMap);
            }
            
            Long userId = jwtUtil.getUserIdFromToken(token);
            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");
            
            // 필수 필드 확인
            if (currentPassword == null || currentPassword.isEmpty() || newPassword == null || newPassword.isEmpty()) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("message", "현재 비밀번호와 새 비밀번호를 모두 입력해주세요.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
            }
            
            // 새 비밀번호 유효성 검사
            if (newPassword.length() < 8) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("message", "비밀번호는 최소 8자 이상이어야 합니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
            }
            
            boolean result = userService.changePassword(userId, currentPassword, newPassword);
            
            if (result) {
                Map<String, String> successMap = new HashMap<>();
                successMap.put("message", "비밀번호가 성공적으로 변경되었습니다.");
                return ResponseEntity.ok(successMap);
            } else {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("message", "비밀번호 변경에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
        } catch (Exception e) {
            logger.error("비밀번호 변경 중 오류 발생", e);
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("message", "비밀번호를 변경하는 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
        }
    }
} 