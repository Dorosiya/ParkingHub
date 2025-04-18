package com.example.parking_hub.mapper;

import com.example.parking_hub.model.FavoriteParking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 즐겨찾기 주차장 데이터 접근을 위한 MyBatis 매퍼 인터페이스
 */
@Mapper
public interface FavoriteParkingMapper {

    /**
     * 사용자 ID로 즐겨찾기 목록 조회
     *
     * @param userId 사용자 ID
     * @return 즐겨찾기 목록
     */
    List<FavoriteParking> findAllByUserId(Long userId);

    /**
     * 주차장 정보가 포함된 즐겨찾기 목록 조회
     *
     * @param userId 사용자 ID
     * @return 주차장 정보가 포함된 즐겨찾기 목록
     */
    List<FavoriteParking> findAllWithParkingInfoByUserId(Long userId);

    /**
     * ID로 즐겨찾기 조회
     *
     * @param id 즐겨찾기 ID
     * @return 즐겨찾기 정보
     */
    FavoriteParking findById(Long id);

    /**
     * 사용자 ID와 주차장 ID로 즐겨찾기 조회
     *
     * @param userId 사용자 ID
     * @param prkCenterId 주차장 ID
     * @return 즐겨찾기 정보
     */
    FavoriteParking findByUserIdAndPrkCenterId(@Param("userId") Long userId, @Param("prkCenterId") String prkCenterId);

    /**
     * 즐겨찾기 존재 여부 확인
     *
     * @param userId 사용자 ID
     * @param prkCenterId 주차장 ID
     * @return 존재 여부
     */
    boolean existsByUserAndParkingId(@Param("userId") Long userId, @Param("prkCenterId") String prkCenterId);

    /**
     * 즐겨찾기 추가
     *
     * @param favoriteParking 추가할 즐겨찾기 정보
     * @return 영향받은 행 수
     */
    int insert(FavoriteParking favoriteParking);

    /**
     * 즐겨찾기 수정
     *
     * @param favoriteParking 수정할 즐겨찾기 정보
     * @return 영향받은 행 수
     */
    int update(FavoriteParking favoriteParking);

    /**
     * ID로 즐겨찾기 삭제
     *
     * @param id 즐겨찾기 ID
     * @return 영향받은 행 수
     */
    int deleteById(Long id);

    /**
     * 사용자 ID와 주차장 ID로 즐겨찾기 삭제
     *
     * @param userId 사용자 ID
     * @param prkCenterId 주차장 ID
     * @return 영향받은 행 수
     */
    int deleteByUserIdAndPrkCenterId(@Param("userId") Long userId, @Param("prkCenterId") String prkCenterId);
} 