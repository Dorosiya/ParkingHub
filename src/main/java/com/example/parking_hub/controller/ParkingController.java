package com.example.parking_hub.controller;

import com.example.parking_hub.exception.ResourceNotFoundException;
import com.example.parking_hub.model.ParkingInfo;
import com.example.parking_hub.model.ParkingOperation;
import com.example.parking_hub.model.ParkingRealtime;
import com.example.parking_hub.service.ParkingService;
import com.example.parking_hub.service.ParkingValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {

    private final ParkingService parkingService;
    private final ParkingValidationService validationService;
    private static final Logger logger = LoggerFactory.getLogger(ParkingController.class);

    @Autowired
    public ParkingController(ParkingService parkingService, ParkingValidationService validationService) {
        this.parkingService = parkingService;
        this.validationService = validationService;
    }

    /**
     * 모든 주차장 기본 정보 조회
     */
    @GetMapping
    public ResponseEntity<List<ParkingInfo>> getAllParkingInfo() {
        List<ParkingInfo> parkingInfoList = parkingService.getAllParkingInfo();
        return ResponseEntity.ok(parkingInfoList);
    }

    /**
     * 특정 주차장 기본 정보 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParkingInfo> getParkingInfoById(@PathVariable("id") String id) {
        ParkingInfo parkingInfo = parkingService.getParkingInfoById(id);
        if (parkingInfo == null) {
            throw new ResourceNotFoundException("Parking", "id", id);
        }
        return ResponseEntity.ok(parkingInfo);
    }

    /**
     * 주차장 운영 정보 조회
     */
    @GetMapping("/{id}/operation")
    public ResponseEntity<ParkingOperation> getParkingOperationById(@PathVariable("id") String id) {
        ParkingOperation parkingOperation = parkingService.getParkingOperationById(id);
        if (parkingOperation == null) {
            throw new ResourceNotFoundException("ParkingOperation", "id", id);
        }
        return ResponseEntity.ok(parkingOperation);
    }

    /**
     * 주차장 실시간 정보 조회
     */
    @GetMapping("/{id}/realtime")
    public ResponseEntity<ParkingRealtime> getParkingRealtimeById(@PathVariable("id") String id) {
        ParkingRealtime parkingRealtime = parkingService.getParkingRealtimeById(id);
        if (parkingRealtime == null) {
            throw new ResourceNotFoundException("ParkingRealtime", "id", id);
        }
        return ResponseEntity.ok(parkingRealtime);
    }

    /**
     * 주차장 통합 정보 조회 (기본 정보 + 운영 정보 + 실시간 정보)
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<Map<String, Object>> getParkingDetailsById(@PathVariable("id") String id) {
        Map<String, Object> parkingDetails = parkingService.getParkingDetailsById(id);
        if (parkingDetails == null || parkingDetails.isEmpty()) {
            throw new ResourceNotFoundException("ParkingDetails", "id", id);
        }
        return ResponseEntity.ok(parkingDetails);
    }
    
    /**
     * 위치 기반 주차장 검색 (지도 연동용 API)
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<ParkingInfo>> findNearbyParking(
            @RequestParam(value = "lat") Double latitude,
            @RequestParam(value = "lng") Double longitude,
            @RequestParam(value = "radius", defaultValue = "1.0") Double radiusKm) {
        
        logger.info("주변 주차장 검색 요청 - 위도: {}, 경도: {}, 반경: {}km", latitude, longitude, radiusKm);
        
        try {
            // 좌표 검증
            validationService.validateCoordinates(latitude, longitude);
            
            // 반경 검증 (기본값 1km, 최대 5km)
            double validRadius = validationService.validateRadius(radiusKm);
            if (validRadius > 5.0) {
                validRadius = 5.0;
            }
            
            // 서비스 호출
            List<ParkingInfo> parkingInfoList = parkingService.findParkingNearby(latitude, longitude, validRadius);
            
            logger.info("주변 주차장 검색 완료 - {} 개 결과 반환", parkingInfoList.size());
            return ResponseEntity.ok(parkingInfoList);
        } catch (Exception e) {
            logger.error("주변 주차장 검색 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 위치 기반 주차장 검색
     */
    @GetMapping("/search/location")
    public ResponseEntity<List<ParkingInfo>> searchParkingByLocation(
            @RequestParam(value = "lat") Double latitude,
            @RequestParam(value = "lng") Double longitude,
            @RequestParam(value = "radius", required = false) Double radiusKm) {
        
        // 좌표 검증
        validationService.validateCoordinates(latitude, longitude);
        
        // 반경 검증
        double validRadius = validationService.validateRadius(radiusKm);
        
        // 서비스 호출
        List<ParkingInfo> parkingInfoList = parkingService.findParkingNearby(latitude, longitude, validRadius);
        
        return ResponseEntity.ok(parkingInfoList);
    }

    /**
     * 이름 기반 주차장 검색
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<ParkingInfo>> searchParkingByName(@RequestParam("query") String name) {
        validationService.validateSearchQuery(name);
        List<ParkingInfo> parkingInfoList = parkingService.findParkingByName(name);
        return ResponseEntity.ok(parkingInfoList);
    }

    /**
     * 주소 기반 주차장 검색
     */
    @GetMapping("/search/address")
    public ResponseEntity<List<ParkingInfo>> searchParkingByAddress(@RequestParam("query") String address) {
        validationService.validateSearchQuery(address);
        List<ParkingInfo> parkingInfoList = parkingService.findParkingByAddress(address);
        return ResponseEntity.ok(parkingInfoList);
    }

    /**
     * 주차 가능 여부로 주차장 검색
     */
    @GetMapping("/search/available")
    public ResponseEntity<List<Map<String, Object>>> searchAvailableParking(
            @RequestParam(value = "minSpots", defaultValue = "1") Integer minAvailableSpots) {
        if (minAvailableSpots < 1) {
            minAvailableSpots = 1; // 최소값 보장
        }
        List<Map<String, Object>> availableParkingList = parkingService.findAvailableParking(minAvailableSpots);
        return ResponseEntity.ok(availableParkingList);
    }
} 