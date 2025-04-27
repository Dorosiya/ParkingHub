package com.example.parking_hub.mapper;

import com.example.parking_hub.model.ParkingOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParkingOperationMapper {
    
    /**
     * 주차장 운영 정보 등록
     */
    void insertParkingOperation(ParkingOperation parkingOperation);
    
    /**
     * 주차장 운영 정보 조회 - 전체
     */
    List<ParkingOperation> selectAllParkingOperations();
    
    /**
     * 주차장 운영 정보 조회 - 특정 ID
     */
    ParkingOperation selectParkingOperationById(@Param("id") String id);
    
    /**
     * 특정 ID 존재 여부 확인
     */
    boolean existsById(@Param("id") String id);
    
    /**
     * 주차장 운영 정보 수정
     */
    void updateParkingOperation(ParkingOperation parkingOperation);
    
    /**
     * 주차장 운영 정보 삭제
     */
    void deleteParkingOperation(@Param("id") String id);
    
    /**
     * 무료 운영 주차장 조회
     */
    List<ParkingOperation> selectFreeParkingOperations();
    
    /**
     * 요금별 주차장 조회
     */
    List<ParkingOperation> selectParkingByFeeRange(@Param("minFee") Integer minFee, 
                                                  @Param("maxFee") Integer maxFee);
    
    /**
     * 주차장 운영 정보 등록 또는 수정 (Upsert)
     * 이미 존재하는 경우 업데이트, 없는 경우 삽입
     */
    void insertOrUpdateParkingOperation(ParkingOperation parkingOperation);
    
    /**
     * 주차장 운영 정보 총 개수 조회
     * @return 주차장 운영 정보 개수
     */
    int countParkingOperation();
    
    /**
     * 모든 주차장 운영 정보 삭제
     */
    int deleteAllParkingOperation();
    
    /**
     * ID 패턴으로 주차장 운영 정보 삭제
     */
    int deleteParkingOperationByIdPattern(@Param("idPattern") String idPattern);
}
