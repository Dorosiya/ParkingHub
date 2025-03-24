package com.example.parking_hub.mapper;

import com.example.parking_hub.model.ParkingRealtime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParkingRealtimeMapper {
    // 모든 주차장 실시간 정보 조회
    List<ParkingRealtime> selectAllParkingRealtimes();

    // 특정 주차장 실시간 정보 조회
    ParkingRealtime selectParkingRealtimeById(@Param("prkCenterId") String prkCenterId);

    // 가용 주차면 기준 주차장 조회
    List<ParkingRealtime> selectParkingRealtimesByAvailableSlots(@Param("minSlots") int minSlots);

    // 실시간 정보 등록
    int insertParkingRealtime(ParkingRealtime parkingRealtime);

    // 실시간 정보 수정
    int updateParkingRealtime(ParkingRealtime parkingRealtime);

    // 실시간 정보 등록 또는 수정 (UPSERT)
    int insertOrUpdateParkingRealtime(ParkingRealtime parkingRealtime);

    // 실시간 정보 삭제
    int deleteParkingRealtime(@Param("prkCenterId") String prkCenterId);
}
