package com.example.parking_hub.mapper;

import com.example.parking_hub.model.ParkingOperationTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParkingOperationTimeMapper {
    
    /**
     * 주차장 운영시간 정보 등록
     */
    void insertParkingOperationTime(ParkingOperationTime parkingOperationTime);
    
    /**
     * 주차장 운영시간 정보 목록 조회 - 특정 주차장 ID
     */
    List<ParkingOperationTime> selectParkingOperationTimesByParkingId(@Param("prkCenterId") String prkCenterId);
    
    /**
     * 주차장 운영시간 정보 조회 - ID로
     */
    ParkingOperationTime selectParkingOperationTimeById(@Param("id") Long id);
    
    /**
     * 주차장 운영시간 정보 수정
     */
    void updateParkingOperationTime(ParkingOperationTime parkingOperationTime);
    
    /**
     * 주차장 운영시간 정보 삭제 - ID로
     */
    void deleteParkingOperationTimeById(@Param("id") Long id);
    
    /**
     * 주차장 운영시간 정보 삭제 - 주차장 ID로 (해당 주차장의 모든 운영시간 삭제)
     * @return 삭제된 레코드 수
     */
    int deleteParkingOperationTimesByParkingId(@Param("prkCenterId") String prkCenterId);
    
    /**
     * 주차장 운영시간 정보 등록 또는 수정 (Upsert) - 주차장 ID와 요일로
     */
    void insertOrUpdateParkingOperationTime(ParkingOperationTime parkingOperationTime);
    
    /**
     * 주차장 운영시간 정보 체크 - 주차장 ID와 요일로
     */
    boolean existsByParkingIdAndDayOfWeek(@Param("prkCenterId") String prkCenterId, @Param("dayOfWeek") String dayOfWeek);
    
    /**
     * 주차장 운영시간 정보 조회 - 주차장 ID와 요일로
     */
    ParkingOperationTime selectParkingOperationTimeByParkingIdAndDay(
            @Param("prkCenterId") String prkCenterId, 
            @Param("dayOfWeek") String dayOfWeek);
    
    /**
     * 모든 주차장 운영시간 정보 삭제
     */
    int deleteAllParkingOperationTimes();
} 