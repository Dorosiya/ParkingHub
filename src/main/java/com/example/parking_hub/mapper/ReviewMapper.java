package com.example.parking_hub.mapper;

import com.example.parking_hub.model.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 주차장 리뷰 정보에 대한 데이터 접근 인터페이스
 */
@Mapper
public interface ReviewMapper {
    
    /**
     * 새로운 리뷰를 등록합니다.
     *
     * @param review 저장할 리뷰 정보
     * @return 영향받은 행 수
     */
    int insertReview(Review review);
    
    /**
     * 특정 주차장의 모든 리뷰를 조회합니다.
     *
     * @param prkCenterId 주차장 ID
     * @return 해당 주차장의 리뷰 목록
     */
    List<Review> selectReviewsByParkingId(@Param("prkCenterId") String prkCenterId);
    
    /**
     * 특정 사용자가 작성한 모든 리뷰를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 리뷰 목록
     */
    List<Review> selectReviewsByUserId(@Param("userId") String userId);
    
    /**
     * 특정 ID의 리뷰를 조회합니다.
     *
     * @param id 리뷰 ID
     * @return 리뷰 정보
     */
    Review selectReviewById(@Param("id") Long id);
    
    /**
     * 리뷰 정보를 업데이트합니다.
     *
     * @param review 업데이트할 리뷰 정보
     * @return 영향받은 행 수
     */
    int updateReview(Review review);
    
    /**
     * 특정 ID의 리뷰를 삭제합니다.
     *
     * @param id 삭제할 리뷰 ID
     * @return 영향받은 행 수
     */
    int deleteReview(@Param("id") Long id);
    
    /**
     * 특정 주차장의 리뷰 개수를 조회합니다.
     * 
     * @param prkCenterId 주차장 ID
     * @return 리뷰 개수
     */
    int countReviewsByParkingId(@Param("prkCenterId") String prkCenterId);
    
    /**
     * 특정 주차장의 평균 평점을 조회합니다.
     * 
     * @param prkCenterId 주차장 ID
     * @return 평균 평점
     */
    double selectAverageRatingByParkingId(@Param("prkCenterId") String prkCenterId);
    
    /**
     * 특정 사용자가 특정 주차장에 작성한 리뷰를 조회합니다.
     * 
     * @param prkCenterId 주차장 ID
     * @param userId 사용자 ID
     * @return 리뷰 정보
     */
    Review selectReviewByParkingIdAndUserId(@Param("prkCenterId") String prkCenterId, @Param("userId") String userId);
}
