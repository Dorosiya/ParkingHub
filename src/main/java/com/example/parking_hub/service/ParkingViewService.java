package com.example.parking_hub.service;

import com.example.parking_hub.model.ParkingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 주차장 정보 화면 표시를 위한 서비스
 */
@Service
public class ParkingViewService {
    
    private static final Logger logger = LoggerFactory.getLogger(ParkingViewService.class);
    private final SearchService searchService;
    
    @Autowired
    public ParkingViewService(SearchService searchService) {
        this.searchService = searchService;
    }
    
    /**
     * 검색 결과에 표시할 주차장 목록에 요금 정보를 설정합니다.
     * @param parkingList 주차장 목록
     * @return 요금 정보가 설정된 주차장 목록
     */
    public List<ParkingInfo> setParkingFeeInfo(List<ParkingInfo> parkingList) {
        for (ParkingInfo parking : parkingList) {
            // 주차장 ID 기반으로 임의 요금 정보 설정 (실제 구현에서는 DB 데이터 사용)
            if (parking.getPrkCenterId() != null) {
                // 주차장 ID의 마지막 글자가 짝수이면 유료, 홀수이면 무