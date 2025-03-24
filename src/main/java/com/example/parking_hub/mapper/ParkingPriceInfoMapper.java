package com.example.parking_hub.mapper;

import com.example.parking_hub.model.ParkingPriceInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParkingPriceInfoMapper {
    // 모든 주차장 요금 정보 조회
    List<ParkingPriceInfo> selectAllParkingPriceInfos();

    // 특정 주차장 요금 정보 조회
    ParkingPriceInfo selectParkingPriceInfoById(@Param("prkCenterId") String prkCenterId);

    // 요금 정보 등록
    int insertParkingPriceInfo(ParkingPriceInfo parkingPriceInfo);

    // 요금 정보 수정
    int updateParkingPriceInfo(ParkingPriceInfo parkingPriceInfo);

    // 요금 정보 등록 또는 수정 (UPSERT)
    int insertOrUpdateParkingPriceInfo(ParkingPriceInfo parkingPriceInfo);

    // 요금 정보 삭제
    int deleteParkingPriceInfo(@Param("prkCenterId") String prkCenterId);
}
