package com.example.parking_hub.mapper;

import com.example.parking_hub.model.ParkingOperationHours;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParkingOperationHoursMapper {
    // 특정 주차장의 모든 운영시간 조회
    List<ParkingOperationHours> selectOperationHoursByParkingId(@Param("prkCenterId") String prkCenterId);

    // 특정 주차장의 특정 요일 운영시간 조회
    ParkingOperationHours selectOperationHoursByParkingIdAndDay(
            @Param("prkCenterId") String prkCenterId,
            @Param("dayOfWeek") String dayOfWeek);

    // 운영시간 등록
    int insertOperationHours(ParkingOperationHours operationHours);

    // 운영시간 수정
    int updateOperationHours(ParkingOperationHours operationHours);

    // 운영시간 등록 또는 수정 (UPSERT)
    int insertOrUpdateParkingOperationHours(ParkingOperationHours operationHours);

    // 운영시간 삭제
    int deleteOperationHours(@Param("id") int id);

    // 특정 주차장의 모든 운영시간 삭제
    int deleteAllOperationHoursByParkingId(@Param("prkCenterId") String prkCenterId);
}
