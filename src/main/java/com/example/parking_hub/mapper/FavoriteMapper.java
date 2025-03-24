package com.example.parking_hub.mapper;

import com.example.parking_hub.model.FavoriteParking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteMapper {
    // 사용자별 즐겨찾기 주차장 목록 조회
    List<FavoriteParking> selectFavoritesByUserId(@Param("userId") int userId);

    // 사용자와 주차장 코드로 즐겨찾기 조회
    FavoriteParking selectFavoriteByUserIdAndParkingCode(
            @Param("userId") int userId,
            @Param("parkingCode") String parkingCode);

    // 즐겨찾기 등록
    int insertFavorite(FavoriteParking favorite);

    // 즐겨찾기 삭제 (ID 기준)
    int deleteFavoriteById(@Param("id") int id);

    // 즐겨찾기 삭제 (사용자와 주차장 코드 기준)
    int deleteFavoriteByUserIdAndParkingCode(
            @Param("userId") int userId,
            @Param("parkingCode") String parkingCode);
}
