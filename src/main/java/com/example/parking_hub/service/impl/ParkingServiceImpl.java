package com.example.parking_hub.service.impl;

import com.example.parking_hub.mapper.ParkingInfoMapper;
import com.example.parking_hub.mapper.ParkingOperationMapper;
import com.example.parking_hub.mapper.ParkingRealtimeMapper;
import com.example.parking_hub.model.ParkingInfo;
import com.example.parking_hub.model.ParkingOperation;
import com.example.parking_hub.model.ParkingRealtime;
import com.example.parking_hub.service.ParkingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParkingServiceImpl implements ParkingService {

    private static final Logger logger = LoggerFactory.getLogger(ParkingServiceImpl.class);
    
    private final ParkingInfoMapper parkingInfoMapper;
    private final ParkingOperationMapper parkingOperationMapper;
    private final ParkingRealtimeMapper parkingRealtimeMapper;

    @Autowired
    public ParkingServiceImpl(ParkingInfoMapper parkingInfoMapper,
                              ParkingOperationMapper parkingOperationMapper,
                              ParkingRealtimeMapper parkingRealtimeMapper) {
        this.parkingInfoMapper = parkingInfoMapper;
        this.parkingOperationMapper = parkingOperationMapper;
        this.parkingRealtimeMapper = parkingRealtimeMapper;
    }

    @Override
    public List<ParkingInfo> getAllParkingInfo() {
        logger.info("모든 주차장 기본 정보 조회");
        return parkingInfoMapper.selectAllParkingInfo();
    }

    @Override
    public ParkingInfo getParkingInfoById(String id) {
        logger.info("주차장 기본 정보 조회: id={}", id);
        return parkingInfoMapper.selectParkingInfoById(id);
    }

    @Override
    public ParkingOperation getParkingOperationById(String id) {
        logger.info("주차장 운영 정보 조회: id={}", id);
        return parkingOperationMapper.selectParkingOperationById(id);
    }

    @Override
    public ParkingRealtime getParkingRealtimeById(String id) {
        logger.info("주차장 실시간 정보 조회: id={}", id);
        return parkingRealtimeMapper.selectParkingRealtimeById(id);
    }

    @Override
    public Map<String, Object> getParkingDetailsById(String id) {
        logger.info("주차장 통합 정보 조회: id={}", id);
        
        ParkingInfo info = parkingInfoMapper.selectParkingInfoById(id);
        if (info == null) {
            return null;
        }
        
        Map<String, Object> details = new HashMap<>();
        details.put("basicInfo", info);
        
        // 운영 정보 추가
        ParkingOperation operation = parkingOperationMapper.selectParkingOperationById(id);
        if (operation != null) {
            details.put("operationInfo", operation);
        }
        
        // 실시간 정보 추가
        ParkingRealtime realtime = parkingRealtimeMapper.selectParkingRealtimeById(id);
        if (realtime != null) {
            details.put("realtimeInfo", realtime);
        }
        
        return details;
    }

    @Override
    public List<ParkingInfo> findParkingNearby(Double latitude, Double longitude, Double radiusKm) {
        logger.info("위치 기반 주차장 검색: lat={}, lng={}, radius={}km", latitude, longitude, radiusKm);
        return parkingInfoMapper.selectParkingNearby(latitude, longitude, radiusKm);
    }

    @Override
    public List<ParkingInfo> findParkingByName(String name) {
        logger.info("이름으로 주차장 검색: name={}", name);
        return parkingInfoMapper.selectParkingByName("%" + name + "%");
    }

    @Override
    public List<ParkingInfo> findParkingByAddress(String address) {
        logger.info("주소로 주차장 검색: address={}", address);
        return parkingInfoMapper.selectParkingByAddress("%" + address + "%");
    }

    @Override
    public List<Map<String, Object>> findAvailableParking(Integer minAvailableSpots) {
        logger.info("주차 가능 공간으로 주차장 검색: minSpots={}", minAvailableSpots);
        
        // 실시간 정보에서 주차 가능한 주차장 ID 목록 조회
        List<ParkingRealtime> availableList = parkingRealtimeMapper.selectAvailableParkingLots(minAvailableSpots);
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 조회된 각 주차장에 대한 기본 정보와 실시간 정보 결합
        for (ParkingRealtime realtime : availableList) {
            String parkingId = realtime.getParkingId();
            ParkingInfo info = parkingInfoMapper.selectParkingInfoById(parkingId);
            
            if (info != null) {
                Map<String, Object> parkingData = new HashMap<>();
                parkingData.put("basicInfo", info);
                parkingData.put("realtimeInfo", realtime);
                result.add(parkingData);
            }
        }
        
        return result;
    }
} 