package com.example.parking_hub.client;

import com.example.parking_hub.dto.api.PrkSttusInfoResponse;
import com.example.parking_hub.dto.api.PrkOprInfoResponse;
import com.example.parking_hub.dto.api.PrkRealtimeInfoResponse;

/**
 * 주차장 API 클라이언트 인터페이스
 * 한국교통안전공단의 주차장 정보 API를 호출합니다.
 */
public interface ParkingApiClient {
    
    /**
     * 주차장 기본정보를 조회합니다.
     * 
     * @param pageNo 페이지 번호
     * @param numOfRows 한 페이지 결과 수
     * @return 주차장 기본정보 응답
     */
    PrkSttusInfoResponse getPrkSttusInfo(int pageNo, int numOfRows);
    
    /**
     * 주차장 운영정보를 조회합니다.
     * 
     * @param pageNo 페이지 번호
     * @param numOfRows 한 페이지 결과 수
     * @return 주차장 운영정보 응답
     */
    PrkOprInfoResponse getPrkOprInfo(int pageNo, int numOfRows);
    
    /**
     * 주차장 실시간 정보를 조회합니다.
     * 
     * @param pageNo 페이지 번호
     * @param numOfRows 한 페이지 결과 수
     * @return 주차장 실시간 정보 응답
     */
    PrkRealtimeInfoResponse getPrkRealtimeInfo(int pageNo, int numOfRows);
} 