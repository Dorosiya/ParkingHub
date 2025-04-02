package com.example.parking_hub.service;

import com.example.parking_hub.model.ParkingInfo;
import com.example.parking_hub.model.ParkingOperation;
import com.example.parking_hub.model.ParkingRealtime;

import java.util.List;
import java.util.Map;

public interface ParkingService {
    
    /**
     * 모든 주차장 기본 정보 조회
     */
    List<ParkingInfo> getAllParkingInfo();
    
    /**
     * 특정 주차장 기본 정보 조회
     */
    ParkingInfo getParkingInfoById(String id);
    
    /**
     * 특정 주차장 운영 정보 조회
     */
    ParkingOperation getParkingOperationById(String id);
    
    /**
     * 특정 주차장 실시간 정보 조회
     */
    ParkingRealtime getParkingRealtimeById(String id);
    
    /**
     * 특정 주차장의 통합 정보 조회
     */
    Map<String, Object> getParkingDetailsById(String id);
    
    /**
     * 위치 기반 주차장 검색
     */
    List<ParkingInfo> findParkingNearby(Double latitude, Double longitude, Double radiusKm);
    
    /**
     * 이름으로 주차장 검색
     */
    List<ParkingInfo> findParkingByName(String name);
    
    /**
     * 주소로 주차장 검색
     */
    List<ParkingInfo> findParkingByAddress(String address);
    
    /**
     * 주차 가능 공간으로 주차장 검색
     */
    List<Map<String, Object>> findAvailableParking(Integer minAvailableSpots);
} 