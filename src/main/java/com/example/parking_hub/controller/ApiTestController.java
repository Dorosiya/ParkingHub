package com.example.parking_hub.controller;

import com.example.parking_hub.client.ParkingApiClient;
import com.example.parking_hub.dto.api.PrkSttusInfoResponse;
import com.example.parking_hub.dto.api.PrkOprInfoResponse;
import com.example.parking_hub.dto.api.PrkRealtimeInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API 호출 테스트용 컨트롤러
 */
@RestController
@RequestMapping("/api/test")
public class ApiTestController {

    private static final Logger logger = LoggerFactory.getLogger(ApiTestController.class);
    
    @Autowired
    private ParkingApiClient parkingApiClient;
    
    /**
     * 주차장 기본정보 API 테스트
     */
    @GetMapping("/parking/basic")
    public ResponseEntity<?> testParkingBasicInfo(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int numOfRows) {
        
        try {
            logger.info("주차장 기본정보 API 테스트 호출: pageNo={}, numOfRows={}", pageNo, numOfRows);
            PrkSttusInfoResponse response = parkingApiClient.getPrkSttusInfo(pageNo, numOfRows);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("주차장 기본정보 API 테스트 중 오류", e);
            return ResponseEntity.status(500).body("API 호출 실패: " + e.getMessage());
        }
    }
    
    /**
     * 주차장 운영정보 API 테스트
     */
    @GetMapping("/parking/operation")
    public ResponseEntity<?> testParkingOperationInfo(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int numOfRows) {
        
        try {
            logger.info("주차장 운영정보 API 테스트 호출: pageNo={}, numOfRows={}", pageNo, numOfRows);
            PrkOprInfoResponse response = parkingApiClient.getPrkOprInfo(pageNo, numOfRows);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("주차장 운영정보 API 테스트 중 오류", e);
            return ResponseEntity.status(500).body("API 호출 실패: " + e.getMessage());
        }
    }
    
    /**
     * 주차장 실시간정보 API 테스트
     */
    @GetMapping("/parking/realtime")
    public ResponseEntity<?> testParkingRealtimeInfo(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int numOfRows) {
        
        try {
            logger.info("주차장 실시간정보 API 테스트 호출: pageNo={}, numOfRows={}", pageNo, numOfRows);
            PrkRealtimeInfoResponse response = parkingApiClient.getPrkRealtimeInfo(pageNo, numOfRows);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("주차장 실시간정보 API 테스트 중 오류", e);
            return ResponseEntity.status(500).body("API 호출 실패: " + e.getMessage());
        }
    }
} 