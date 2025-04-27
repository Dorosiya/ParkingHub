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
import java.util.stream.Collectors;

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
        logger.info("주차장 검색 시작: 키워드={}, 지역={}", keyword, region);
        
        try {
            // 파라미터 전처리
            keyword = keyword != null ? keyword.trim() : "";
            region = region != null ? region.trim() : "";
            
            // 빈 키워드일 경우 빈 리스트 반환
            if (keyword.isEmpty() && region.isEmpty()) {
                logger.info("검색어와 지역이 모두 비어있어 빈 결과 반환");
                return new ArrayList<>();
            }

            List<ParkingInfo> parkingList = new ArrayList<>();
            
            // 키워드가 있는 경우에만 키워드 검색 수행
            if (!keyword.isEmpty()) {
                // 이름으로 검색
                try {
                    List<ParkingInfo> nameResults = parkingService.findParkingByName(keyword);
                    if (nameResults != null) {
                        logger.info("이름으로 검색된 주차장 수: {}", nameResults.size());
                        parkingList.addAll(nameResults);
                    } else {
                        logger.warn("이름으로 검색 결과가 null입니다.");
                    }
                } catch (Exception e) {
                    logger.error("이름으로 검색 중 오류 발생: {}", e.getMessage());
                }

                // 주소로 검색
                try {
                    List<ParkingInfo> addressResults = parkingService.findParkingByAddress(keyword);
                    if (addressResults != null) {
                        logger.info("주소로 검색된 주차장 수: {}", addressResults.size());
                        // 중복 제거하면서 추가
                        for (ParkingInfo parking : addressResults) {
                            if (!parkingList.contains(parking)) {
                                parkingList.add(parking);
                            }
                        }
                    } else {
                        logger.warn("주소로 검색 결과가 null입니다.");
                    }
                } catch (Exception e) {
                    logger.error("주소로 검색 중 오류 발생: {}", e.getMessage());
                }
            } else if (!region.isEmpty()) {
                // 키워드가 없고 지역만 있는 경우 모든 주차장 조회
                try {
                    List<ParkingInfo> allParking = parkingService.getAllParkingInfo();
                    if (allParking != null) {
                        logger.info("전체 주차장 조회 결과 수: {}", allParking.size());
                        parkingList.addAll(allParking);
                    } else {
                        logger.warn("전체 주차장 조회 결과가 null입니다.");
                    }
                } catch (Exception e) {
                    logger.error("전체 주차장 조회 중 오류 발생: {}", e.getMessage());
                }
            }

            // 검색 결과가 비어있으면 빈 리스트 반환
            if (parkingList.isEmpty()) {
                logger.info("검색 결과가 없습니다.");
                return parkingList;
            }

            // 지역 필터링 (지역이 있고 주차장 리스트가 있는 경우에만)
            if (!region.isEmpty() && !parkingList.isEmpty()) {
                final String regionFilter = region; // 람다 표현식에서 사용하기 위한 effectively final 변수
                List<ParkingInfo> filteredList = parkingList.stream()
                        .filter(p -> p.getPrkPlceAdres() != null && p.getPrkPlceAdres().contains(regionFilter))
                        .collect(Collectors.toList());
                
                // 결과가 있으면 필터링된 리스트 사용, 없으면 원본 리스트 유지
                if (!filteredList.isEmpty()) {
                    logger.info("지역으로 필터링된 주차장 수: {}", filteredList.size());
                    return filteredList;
                } else {
                    logger.info("지역으로 필터링된 결과가 없어 원본 리스트 유지 (개수: {})", parkingList.size());
                }
            }

            logger.info("검색 완료, 결과 개수: {}", parkingList.size());
            return parkingList;
        } catch (Exception e) {
            logger.error("주차장 검색 중 예외 발생: {}", e.getMessage(), e);
            return new ArrayList<>();
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
            logger.info("주차장 상세 정보 조회 시작 - ID: {}", id);
            Map<String, Object> details = parkingService.getParkingDetailsById(id);
            if (details == null || details.isEmpty()) {
                logger.warn("주차장 상세 정보 없음 - ID: {}", id);
            } else {
                logger.info("주차장 상세 정보 조회 성공 - ID: {}", id);
            }
            return details;
        } catch (Exception e) {
            logger.error("주차장 상세 정보 조회 중 오류 발생: ID={}, 오류={}", id, e.getMessage(), e);
            return null;
        }
    }
} 