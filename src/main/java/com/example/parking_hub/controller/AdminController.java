package com.example.parking_hub.controller;

import com.example.parking_hub.service.ParkingDataSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 관리자 기능을 위한 컨트롤러
 * 실제 운영 환경에서는 적절한 보안 설정 필요
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    private final ParkingDataSyncService parkingDataSyncService;
    
    @Autowired
    public AdminController(ParkingDataSyncService parkingDataSyncService) {
        this.parkingDataSyncService = parkingDataSyncService;
    }
    
    /**
     * 주차장 데이터 동기화 수동 실행
     */
    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncData() {
        logger.info("관리자 요청: 주차장 데이터 동기화 시작");
        
        try {
            parkingDataSyncService.syncParkingData();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "주차장 데이터 동기화 요청이 성공적으로 처리되었습니다");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("관리자 요청: 주차장 데이터 동기화 중 오류 발생", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "주차장 데이터 동기화 처리 중 오류가 발생했습니다");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 모든 주차장 데이터 초기화 (개발/테스트 환경 전용)
     */
    @DeleteMapping("/data/all")
    public ResponseEntity<Map<String, Object>> cleanAllData() {
        logger.warn("관리자 요청: 모든 주차장 데이터 초기화 시작");
        
        try {
            parkingDataSyncService.cleanAllData();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "모든 주차장 데이터가 성공적으로 초기화되었습니다");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("관리자 요청: 주차장 데이터 초기화 중 오류 발생", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "주차장 데이터 초기화 중 오류가 발생했습니다");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 특정 ID 패턴의 주차장 데이터만 삭제
     */
    @DeleteMapping("/data/pattern")
    public ResponseEntity<Map<String, Object>> cleanDataByPattern(@RequestParam("pattern") String pattern) {
        logger.warn("관리자 요청: ID 패턴 '{}' 으로 시작하는 주차장 데이터 삭제 시작", pattern);
        
        try {
            parkingDataSyncService.cleanDataByIdPattern(pattern);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "ID 패턴 '" + pattern + "' 으로 시작하는 주차장 데이터가 성공적으로 삭제되었습니다");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("관리자 요청: ID 패턴으로 데이터 삭제 중 오류 발생", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "ID 패턴으로 데이터 삭제 중 오류가 발생했습니다");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 