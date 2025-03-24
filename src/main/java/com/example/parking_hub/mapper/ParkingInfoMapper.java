package com.example.parking_hub.mapper;

import com.example.parking_hub.model.ParkingInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParkingInfoMapper {
    // 주차장 목록 조회
    List<ParkingInfo> selectAllParkingInfos();

    // 주차장 정보 조회
    ParkingInfo selectParkingInfoById(@Param("prkCenterId") String prkCenterId);

    // 위치 기반 주차장 정보 조회 (위도, 경도, 반경)
    List<ParkingInfo> selectParkingInfosByLocation(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius);

    // 주차장 정보 등록
    int insertParkingInfo(ParkingInfo parkingInfo);

    // 주차장 정보 수정
    int updateParkingInfo(ParkingInfo parkingInfo);

    // 주차장 정보 등록 또는 수정 (UPSERT)
    int insertOrUpdateParkingInfo(ParkingInfo parkingInfo);

    // 주차장 정보 삭제
    int deleteParkingInfo(@Param("prkCenterId") String prkCenterId);
}
