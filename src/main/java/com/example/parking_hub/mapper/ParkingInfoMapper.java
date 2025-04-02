package com.example.parking_hub.mapper;

import com.example.parking_hub.model.ParkingInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParkingInfoMapper {
    
    /**
     * 주차장 정보 등록
     */
    void insertParkingInfo(ParkingInfo parkingInfo);
    
    /**
     * 주차장 정보 조회 - 전체
     */
    List<ParkingInfo> selectAllParkingInfo();
    
    /**
     * 주차장 정보 조회 - 특정 ID
     */
    ParkingInfo selectParkingInfoById(@Param("id") String id);
    
    /**
     * 주차장 정보 수정
     */
    void updateParkingInfo(ParkingInfo parkingInfo);
    
    /**
     * 주차장 정보 삭제
     */
    void deleteParkingInfo(@Param("id") String id);
    
    /**
     * 주차장 정보 조회 - 이름으로 검색
     */
    List<ParkingInfo> selectParkingByName(@Param("name") String name);
    
    /**
     * 주차장 정보 조회 - 주소로 검색
     */
    List<ParkingInfo> selectParkingByAddress(@Param("address") String address);
    
    /**
     * 주차장 정보 조회 - 위치 기반 검색
     */
    List<ParkingInfo> selectParkingNearby(@Param("latitude") Double latitude, 
                                          @Param("longitude") Double longitude, 
                                          @Param("radiusKm") Double radiusKm);
    
    /**
     * 주차장 ID 존재 여부 확인
     */
    boolean existsById(@Param("id") String id);
}
