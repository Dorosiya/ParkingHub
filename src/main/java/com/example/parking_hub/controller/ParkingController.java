package com.example.parking_hub.controller;

import com.example.parking_hub.model.ParkingInfo;
import com.example.parking_hub.model.ParkingOperation;
import com.example.parking_hub.model.ParkingRealtime;
import com.example.parking_hub.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {

    private final ParkingService parkingService;

    @Autowired
    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
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
            return ResponseEntity.notFound().build();
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
            return ResponseEntity.notFound().build();
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
            return ResponseEntity.notFound().build();
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
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(parkingDetails);
    }

    /**
     * 위치 기반 주차장 검색
     */
    @GetMapping("/search/location")
    public ResponseEntity<List<ParkingInfo>> searchParkingByLocation(
            @RequestParam(value = "lat") Double latitude,
            @RequestParam(value = "lng") Double longitude,
            @RequestParam(value = "radius", defaultValue = "2") Double radiusKm) {
        List<ParkingInfo> parkingInfoList = parkingService.findParkingNearby(latitude, longitude, radiusKm);
        return ResponseEntity.ok(parkingInfoList);
    }

    /**
     * 이름 기반 주차장 검색
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<ParkingInfo>> searchParkingByName(@RequestParam("query") String name) {
        List<ParkingInfo> parkingInfoList = parkingService.findParkingByName(name);
        return ResponseEntity.ok(parkingInfoList);
    }

    /**
     * 주소 기반 주차장 검색
     */
    @GetMapping("/search/address")
    public ResponseEntity<List<ParkingInfo>> searchParkingByAddress(@RequestParam("query") String address) {
        List<ParkingInfo> parkingInfoList = parkingService.findParkingByAddress(address);
        return ResponseEntity.ok(parkingInfoList);
    }

    /**
     * 주차 가능 여부로 주차장 검색
     */
    @GetMapping("/search/available")
    public ResponseEntity<List<Map<String, Object>>> searchAvailableParking(
            @RequestParam(value = "minSpots", defaultValue = "1") Integer minAvailableSpots) {
        List<Map<String, Object>> availableParkingList = parkingService.findAvailableParking(minAvailableSpots);
        return ResponseEntity.ok(availableParkingList);
    }
} 