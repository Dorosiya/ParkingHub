package com.example.parking_hub.service;

import com.example.parking_hub.model.ParkingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {
    
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
    private final ParkingService parkingService;
    
    @Autowired
    public SearchService(ParkingService parkingService) {
        this.parkingService = parkingService;
    }
    
    /**
     * 주차장을 검색합니다.
     * 
     * @param keyword 검색 키워드 (이름 또는 주소)
     * @param region 지역 필터
     * @return 필터링된 주차장 목록
     */
    public List<ParkingInfo> searchParking(String keyword, String region) {
        try {
            // 주차장 목록 가져오기 (검색 조건이 있으면 필터링)
            List<ParkingInfo> parkingList;
            
            if (keyword != null && !keyword.isEmpty()) {
                // 이름 또는 주소로 검색
                List<ParkingInfo> nameResults = parkingService.findParkingByName(keyword);
                List<ParkingInfo> addressResults = parkingService.findParkingByAddress(keyword);
                
                // 두 결과 합치기 (중복 제거)
                parkingList = mergeSearchResults(nameResults, addressResults);
            } else {
                // 전체 목록
                parkingList = parkingService.getAllParkingInfo();
                if (parkingList == null) {
                    parkingList = Collections.emptyList();
                }
            }
            
            // 지역으로 필터링
            if (region != null && !region.isEmpty() && !parkingList.isEmpty()) {
                parkingList = filterByRegion(parkingList, region);
            }
            
            return parkingList;
            
        } catch (Exception e) {
            logger.error("주차장 검색 중 오류 발생", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 주차장 상세 정보를 반환합니다.
     * 
     * @param id 주차장 ID
     * @return 주차장 상세 정보를 담은 Map 또는 null
     */
    public Map<String, Object> getParkingDetail(String id) {
        try {
            return parkingService.getParkingDetailsById(id);
        } catch (Exception e) {
            logger.error("주차장 상세 정보 조회 중 오류 발생: ID=" + id, e);
            return null;
        }
    }
    
    // 검색 결과 병합 (중복 제거)
    private List<ParkingInfo> mergeSearchResults(List<ParkingInfo> list1, List<ParkingInfo> list2) {
        if (list1 == null && list2 == null) {
            return Collections.emptyList();
        }
        
        if (list1 == null) {
            return list2;
        }
        
        if (list2 == null) {
            return list1;
        }
        
        List<ParkingInfo> mergedList = new ArrayList<>(list1);
        for (ParkingInfo item : list2) {
            if (!mergedList.contains(item)) {
                mergedList.add(item);
            }
        }
        
        return mergedList;
    }
    
    // 지역으로 필터링
    private List<ParkingInfo> filterByRegion(List<ParkingInfo> parkingList, String region) {
        List<ParkingInfo> filteredList = new ArrayList<>();
        
        for (ParkingInfo parking : parkingList) {
            String address = parking.getPrkPlceAdres();
            if (address != null && address.contains(region)) {
                filteredList.add(parking);
            }
        }
        
        return filteredList;
    }
} 