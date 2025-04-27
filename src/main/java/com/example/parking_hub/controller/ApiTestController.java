package com.example.parking_hub.controller;

import com.example.parking_hub.client.ParkingApiClient;
import com.example.parking_hub.dto.api.PrkSttusInfoResponse;
import com.example.parking_hub.dto.api.PrkOprInfoResponse;
import com.example.parking_hub.dto.api.PrkRealtimeInfoResponse;
import com.example.parking_hub.service.ParkingDataSyncService;
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
    
    @Autowired
    private ParkingDataSyncService parkingDataSyncService;
    
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
            
            if (response != null) {
                logger.info("주차장 기본정보 API 응답 결과: 코드={}, 메시지={}, 데이터 건수={}", 
                    response.getResultCode(), response.getResultMsg(),
                    (response.getItems() != null ? response.getItems().size() : 0));
            } else {
                logger.warn("주차장 기본정보 API 응답이 NULL입니다.");
            }
            
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
            
            if (response != null) {
                logger.info("주차장 운영정보 API 응답 결과: 코드={}, 메시지={}, 데이터 건수={}", 
                    response.getResultCode(), response.getResultMsg(),
                    (response.getItems() != null ? response.getItems().size() : 0));
            } else {
                logger.warn("주차장 운영정보 API 응답이 NULL입니다.");
            }
            
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
            
            if (response != null) {
                logger.info("주차장 실시간정보 API 응답 결과: 코드={}, 메시지={}, 데이터 건수={}", 
                    response.getResultCode(), response.getResultMsg(),
                    (response.getItems() != null ? response.getItems().size() : 0));
            } else {
                logger.warn("주차장 실시간정보 API 응답이 NULL입니다.");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("주차장 실시간정보 API 테스트 중 오류", e);
            return ResponseEntity.status(500).body("API 호출 실패: " + e.getMessage());
        }
    }
    
    /**
     * 모든 주차장 데이터 강제 리로드
     */
    @GetMapping("/parking/reload")
    public ResponseEntity<?> reloadAllParkingData() {
        try {
            logger.info("모든 주차장 데이터 강제 리로드 요청 받음");
            
            // 비동기 방식으로 데이터 로드 시작
            new Thread(() -> {
                try {
                    parkingDataSyncService.forceReloadAllParkingData();
                } catch (Exception e) {
                    logger.error("비동기 데이터 리로드 중 오류 발생", e);
                }
            }).start();
            
            return ResponseEntity.ok("주차장 데이터 리로드가 백그라운드에서 시작되었습니다.");
        } catch (Exception e) {
            logger.error("주차장 데이터 리로드 요청 처리 중 오류", e);
            return ResponseEntity.status(500).body("리로드 요청 실패: " + e.getMessage());
        }
    }
    
    /**
     * 기본 정보만 강제 리로드
     */
    @GetMapping("/parking/reload/basic")
    public ResponseEntity<?> reloadParkingBasicInfo() {
        try {
            logger.info("주차장 기본정보 강제 리로드 요청 받음");
            
            // 비동기 방식으로 데이터 로드 시작
            new Thread(() -> {
                try {
                    parkingDataSyncService.syncParkingBasicInfo();
                } catch (Exception e) {
                    logger.error("비동기 기본정보 리로드 중 오류 발생", e);
                }
            }).start();
            
            return ResponseEntity.ok("주차장 기본정보 리로드가 백그라운드에서 시작되었습니다.");
        } catch (Exception e) {
            logger.error("주차장 기본정보 리로드 요청 처리 중 오류", e);
            return ResponseEntity.status(500).body("기본정보 리로드 요청 실패: " + e.getMessage());
        }
    }
    
    /**
     * 운영 정보만 강제 리로드
     */
    @GetMapping("/parking/reload/operation")
    public ResponseEntity<?> reloadParkingOperationInfo() {
        try {
            logger.info("주차장 운영정보 강제 리로드 요청 받음");
            
            // 비동기 방식으로 데이터 로드 시작
            new Thread(() -> {
                try {
                    parkingDataSyncService.syncParkingOperationInfo();
                } catch (Exception e) {
                    logger.error("비동기 운영정보 리로드 중 오류 발생", e);
                }
            }).start();
            
            return ResponseEntity.ok("주차장 운영정보 리로드가 백그라운드에서 시작되었습니다.");
        } catch (Exception e) {
            logger.error("주차장 운영정보 리로드 요청 처리 중 오류", e);
            return ResponseEntity.status(500).body("운영정보 리로드 요청 실패: " + e.getMessage());
        }
    }
} 