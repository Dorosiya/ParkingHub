package com.example.parking_hub.mapper;

import com.example.parking_hub.model.ParkingRealtime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParkingRealtimeMapper {
    
    /**
     * 주차장 실시간 정보 등록
     */
    void insertParkingRealtime(ParkingRealtime parkingRealtime);
    
    /**
     * 주차장 실시간 정보 조회 - 전체
     */
    List<ParkingRealtime> selectAllParkingRealtimes();
    
    /**
     * 주차장 실시간 정보 조회 - 특정 ID
     */
    ParkingRealtime selectParkingRealtimeById(@Param("id") String id);
    
    /**
     * 주차장 실시간 정보 수정
     */
    void updateParkingRealtime(ParkingRealtime parkingRealtime);
    
    /**
     * 주차장 실시간 정보 삭제
     */
    void deleteParkingRealtime(@Param("id") String id);
    
    /**
     * 주차 가능한 주차장 목록 조회
     */
    List<ParkingRealtime> selectAvailableParkingLots(@Param("minSpots") Integer minAvailableSpots);
    
    /**
     * 주차 공간 이용률이 높은 주차장 목록 조회
     */
    List<ParkingRealtime> selectHighOccupancyParkingLots(@Param("occupancyRate") Double occupancyRate);
    
    /**
     * 주차장 ID 기준으로 실시간 정보 일괄 조회
     */
    List<ParkingRealtime> selectParkingRealtimeByIds(@Param("ids") List<String> parkingIds);
}
