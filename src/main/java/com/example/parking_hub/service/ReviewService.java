package com.example.parking_hub.service;

import com.example.parking_hub.mapper.ReviewMapper;
import com.example.parking_hub.model.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 주차장 리뷰 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
public class ReviewService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    
    private final ReviewMapper reviewMapper;
    
    @Autowired
    public ReviewService(ReviewMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }
    
    /**
     * 특정 주차장의 모든 리뷰를 조회합니다.
     *
     * @param prkCenterId 주차장 ID
     * @return 해당 주차장의 리뷰 목록
     */
    public List<Review> getReviewsByParkingId(String prkCenterId) {
        logger.info("주차장 리뷰 조회: {}", prkCenterId);
        return reviewMapper.selectReviewsByParkingId(prkCenterId);
    }
    
    /**
     * 특정 사용자가 작성한 모든 리뷰를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 리뷰 목록
     */
    public List<Review> getReviewsByUserId(String userId) {
        logger.info("사용자 리뷰 조회: {}", userId);
        return reviewMapper.selectReviewsByUserId(userId);
    }
    
    /**
     * 특정 ID의 리뷰를 조회합니다.
     *
     * @param id 리뷰 ID
     * @return 리뷰 정보
     */
    public Review getReviewById(Long id) {
        logger.info("리뷰 상세 조회: {}", id);
        return reviewMapper.selectReviewById(id);
    }
    
    /**
     * 새로운 리뷰를 등록합니다.
     * 이미 해당 사용자가 해당 주차장에 리뷰를 작성한 경우 예외가 발생합니다.
     *
     * @param review 저장할 리뷰 정보
     * @return 저장된 리뷰 정보
     * @throws IllegalStateException 이미 리뷰가 존재하는 경우
     */
    @Transactional
    public Review createReview(Review review) {
        logger.info("리뷰 등록: {} - {}", review.getPrkCenterId(), review.getUserId());
        
        // 이미 리뷰를 작성했는지 확인
        Review existingReview = reviewMapper.selectReviewByParkingIdAndUserId(
                review.getPrkCenterId(), review.getUserId());
        
        if (existingReview != null) {
            logger.warn("이미 리뷰가 존재합니다: {} - {}", review.getPrkCenterId(), review.getUserId());
            throw new IllegalStateException("이미 이 주차장에 리뷰를 작성하셨습니다.");
        }
        
        // 유효성 검사
        validateReview(review);
        
        // 리뷰 등록
        reviewMapper.insertReview(review);
        logger.info("리뷰 등록 완료: ID={}", review.getId());
        
        return review;
    }
    
    /**
     * 리뷰 정보를 업데이트합니다.
     * 본인이 작성한 리뷰만 수정할 수 있습니다.
     *
     * @param review 업데이트할 리뷰 정보
     * @param currentUserId 현재 로그인한 사용자 ID
     * @return 업데이트된 리뷰 정보
     * @throws IllegalStateException 본인의 리뷰가 아닌 경우
     */
    @Transactional
    public Review updateReview(Review review, String currentUserId) {
        logger.info("리뷰 수정: ID={}", review.getId());
        
        // 리뷰 존재 여부 및 권한 확인
        Review existingReview = reviewMapper.selectReviewById(review.getId());
        
        if (existingReview == null) {
            logger.warn("존재하지 않는 리뷰: ID={}", review.getId());
            throw new IllegalArgumentException("존재하지 않는 리뷰입니다.");
        }
        
        if (!existingReview.getUserId().equals(currentUserId)) {
            logger.warn("리뷰 수정 권한 없음: ID={}, 요청자={}", review.getId(), currentUserId);
            throw new IllegalStateException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }
        
        // 유효성 검사
        validateReview(review);
        
        // 기존 정보 유지하고 수정할 정보만 업데이트
        existingReview.setRating(review.getRating());
        existingReview.setContent(review.getContent());
        existingReview.setUpdatedAt(LocalDateTime.now());
        
        // 리뷰 수정
        reviewMapper.updateReview(existingReview);
        logger.info("리뷰 수정 완료: ID={}", existingReview.getId());
        
        return existingReview;
    }
    
    /**
     * 특정 ID의 리뷰를 삭제합니다.
     * 본인이 작성한 리뷰만 삭제할 수 있습니다.
     *
     * @param id 삭제할 리뷰 ID
     * @param currentUserId 현재 로그인한 사용자 ID
     * @throws IllegalStateException 본인의 리뷰가 아닌 경우
     */
    @Transactional
    public void deleteReview(Long id, String currentUserId) {
        logger.info("리뷰 삭제: ID={}, 요청자={}", id, currentUserId);
        
        // 리뷰 존재 여부 및 권한 확인
        Review existingReview = reviewMapper.selectReviewById(id);
        
        if (existingReview == null) {
            logger.warn("존재하지 않는 리뷰: ID={}", id);
            throw new IllegalArgumentException("존재하지 않는 리뷰입니다.");
        }
        
        if (!existingReview.getUserId().equals(currentUserId)) {
            logger.warn("리뷰 삭제 권한 없음: ID={}, 요청자={}", id, currentUserId);
            throw new IllegalStateException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }
        
        // 리뷰 삭제
        reviewMapper.deleteReview(id);
        logger.info("리뷰 삭제 완료: ID={}", id);
    }
    
    /**
     * 특정 주차장의 리뷰 개수를 조회합니다.
     *
     * @param prkCenterId 주차장 ID
     * @return 리뷰 개수
     */
    public int getReviewCountByParkingId(String prkCenterId) {
        return reviewMapper.countReviewsByParkingId(prkCenterId);
    }
    
    /**
     * 특정 주차장의 평균 평점을 조회합니다.
     *
     * @param prkCenterId 주차장 ID
     * @return 평균 평점
     */
    public double getAverageRatingByParkingId(String prkCenterId) {
        return reviewMapper.selectAverageRatingByParkingId(prkCenterId);
    }
    
    /**
     * 특정 사용자가 특정 주차장에 리뷰를 작성했는지 확인합니다.
     *
     * @param prkCenterId 주차장 ID
     * @param userId 사용자 ID
     * @return 리뷰를 작성했으면 true, 아니면 false
     */
    public boolean hasUserReviewedParking(String prkCenterId, String userId) {
        return reviewMapper.selectReviewByParkingIdAndUserId(prkCenterId, userId) != null;
    }
    
    /**
     * 리뷰 정보의 유효성을 검사합니다.
     *
     * @param review 검사할 리뷰 정보
     * @throws IllegalArgumentException 유효하지 않은 리뷰 정보인 경우
     */
    private void validateReview(Review review) {
        if (review.getPrkCenterId() == null || review.getPrkCenterId().isEmpty()) {
            throw new IllegalArgumentException("주차장 ID는 필수입니다.");
        }
        
        if (review.getUserId() == null || review.getUserId().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        
        if (review.getUsername() == null || review.getUsername().isEmpty()) {
            throw new IllegalArgumentException("사용자 이름은 필수입니다.");
        }
        
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("평점은 1-5 사이여야 합니다.");
        }
        
        if (review.getContent() == null || review.getContent().isEmpty()) {
            throw new IllegalArgumentException("리뷰 내용은 필수입니다.");
        }
        
        if (review.getContent().length() > 500) {
            throw new IllegalArgumentException("리뷰 내용은 500자 이내여야 합니다.");
        }
    }
} 