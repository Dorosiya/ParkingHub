package com.example.parking_hub.controller.api;

import com.example.parking_hub.model.Review;
import com.example.parking_hub.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 주차장 리뷰 관련 API를 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewApiController.class);
    
    private final ReviewService reviewService;
    
    @Autowired
    public ReviewApiController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    
    /**
     * 특정 주차장의 모든 리뷰를 조회합니다.
     *
     * @param prkCenterId 주차장 ID
     * @return 해당 주차장의 리뷰 목록
     */
    @GetMapping("/parking/{prkCenterId}")
    public ResponseEntity<Map<String, Object>> getReviewsByParkingId(@PathVariable String prkCenterId) {
        logger.info("주차장 리뷰 조회 API 호출: {}", prkCenterId);
        
        List<Review> reviews = reviewService.getReviewsByParkingId(prkCenterId);
        double averageRating = reviewService.getAverageRatingByParkingId(prkCenterId);
        int reviewCount = reviewService.getReviewCountByParkingId(prkCenterId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviews);
        response.put("averageRating", averageRating);
        response.put("reviewCount", reviewCount);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 특정 리뷰의 상세 정보를 조회합니다.
     *
     * @param id 리뷰 ID
     * @return 리뷰 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable Long id) {
        logger.info("리뷰 상세 조회 API 호출: {}", id);
        
        Review review = reviewService.getReviewById(id);
        if (review == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "리뷰를 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        
        return ResponseEntity.ok(review);
    }
    
    /**
     * 현재 로그인한 사용자가 작성한 모든 리뷰를 조회합니다.
     *
     * @param authentication 인증 정보
     * @return 해당 사용자의 리뷰 목록
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyReviews(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        
        String userId = authentication.getName();
        logger.info("내 리뷰 조회 API 호출: {}", userId);
        
        List<Review> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * 새로운 리뷰를 등록합니다.
     *
     * @param reviewRequest 리뷰 정보
     * @param authentication 인증 정보
     * @return 저장된 리뷰 정보
     */
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest reviewRequest, 
                                         Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        
        String userId = authentication.getName();
        String username = authentication.getName(); // 실제로는 사용자의 실제 이름이나 닉네임을 가져와야 함
        
        logger.info("리뷰 등록 API 호출: {} - {}", reviewRequest.getPrkCenterId(), userId);
        
        try {
            Review review = new Review(
                    reviewRequest.getPrkCenterId(),
                    userId,
                    username,
                    reviewRequest.getRating(),
                    reviewRequest.getContent()
            );
            
            Review savedReview = reviewService.createReview(review);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
        } catch (IllegalStateException | IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * 리뷰 정보를 업데이트합니다.
     *
     * @param id 리뷰 ID
     * @param reviewRequest 업데이트할 리뷰 정보
     * @param authentication 인증 정보
     * @return 업데이트된 리뷰 정보
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id,
                                        @RequestBody ReviewRequest reviewRequest,
                                        Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        
        String userId = authentication.getName();
        logger.info("리뷰 수정 API 호출: ID={}, 요청자={}", id, userId);
        
        try {
            Review existingReview = reviewService.getReviewById(id);
            if (existingReview == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "리뷰를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            existingReview.setRating(reviewRequest.getRating());
            existingReview.setContent(reviewRequest.getContent());
            
            Review updatedReview = reviewService.updateReview(existingReview, userId);
            return ResponseEntity.ok(updatedReview);
        } catch (IllegalStateException | IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * 특정 ID의 리뷰를 삭제합니다.
     *
     * @param id 삭제할 리뷰 ID
     * @param authentication 인증 정보
     * @return 삭제 결과
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        
        String userId = authentication.getName();
        logger.info("리뷰 삭제 API 호출: ID={}, 요청자={}", id, userId);
        
        try {
            reviewService.deleteReview(id, userId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "리뷰가 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalStateException | IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * 특정 사용자가 특정 주차장에 리뷰를 작성했는지 확인합니다.
     *
     * @param prkCenterId 주차장 ID
     * @param authentication 인증 정보
     * @return 리뷰 작성 여부
     */
    @GetMapping("/check/{prkCenterId}")
    public ResponseEntity<?> checkUserReview(@PathVariable String prkCenterId, 
                                           Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> response = new HashMap<>();
            response.put("hasReviewed", false);
            return ResponseEntity.ok(response);
        }
        
        String userId = authentication.getName();
        logger.info("리뷰 작성 여부 확인 API 호출: {} - {}", prkCenterId, userId);
        
        boolean hasReviewed = reviewService.hasUserReviewedParking(prkCenterId, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("hasReviewed", hasReviewed);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 리뷰 요청 DTO
     */
    public static class ReviewRequest {
        private String prkCenterId;
        private int rating;
        private String content;
        
        public String getPrkCenterId() {
            return prkCenterId;
        }
        
        public void setPrkCenterId(String prkCenterId) {
            this.prkCenterId = prkCenterId;
        }
        
        public int getRating() {
            return rating;
        }
        
        public void setRating(int rating) {
            this.rating = rating;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
} 