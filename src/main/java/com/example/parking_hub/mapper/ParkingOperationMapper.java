package com.example.parking_hub.mapper;

import com.example.parking_hub.model.ParkingOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParkingOperationMapper {
    // 주차장 운영 정보 목록 조회
    List<ParkingOperation> selectAllParkingOperations();

    // 주차장 운영 정보 조회
    ParkingOperation selectParkingOperationById(@Param("prkCenterId") String prkCenterId);

    // 주차장 운영 정보 등록
    int insertParkingOperation(ParkingOperation parkingOperation);

    // 주차장 운영 정보 수정
    int updateParkingOperation(ParkingOperation parkingOperation);

    // 주차장 운영 정보 등록 또는 수정 (UPSERT)
    int insertOrUpdateParkingOperation(ParkingOperation parkingOperation);

    // 주차장 운영 정보 삭제
    int deleteParkingOperation(@Param("prkCenterId") String prkCenterId);
}
