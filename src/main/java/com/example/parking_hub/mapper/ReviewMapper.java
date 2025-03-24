package com.example.parking_hub.mapper;

import com.example.parking_hub.model.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {
    // 특정 주차장의 모든 리뷰 조회
    List<Review> selectReviewsByParkingCode(@Param("parkingCode") String parkingCode);

    // 특정 사용자의 모든 리뷰 조회
    List<Review> selectReviewsByUserId(@Param("userId") int userId);

    // 특정 리뷰 조회
    Review selectReviewById(@Param("id") int id);

    // 사용자와 주차장 코드로 리뷰 조회
    Review selectReviewByUserIdAndParkingCode(
            @Param("userId") int userId,
            @Param("parkingCode") String parkingCode);

    // 리뷰 등록
    int insertReview(Review review);

    // 리뷰 수정
    int updateReview(Review review);

    // 리뷰 삭제
    int deleteReview(@Param("id") int id);

    // 특정 주차장의 평균 별점 조회
    double selectAverageRatingByParkingCode(@Param("parkingCode") String parkingCode);
}
