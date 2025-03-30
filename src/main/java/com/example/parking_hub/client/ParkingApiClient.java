package com.example.parking_hub.client;

import com.example.parking_hub.dto.api.PrkSttusInfoResponse;
import com.example.parking_hub.dto.api.PrkOprInfoResponse;

public interface ParkingApiClient {
    /**
     * 주차장 기본정보 조회
     * @param pageNo 페이지 번호
     * @param numOfRows 한 페이지 결과 수
     * @return 주차장 기본정보 응답
     */
    PrkSttusInfoResponse getPrkSttusInfo(int pageNo, int numOfRows);

    /**
     * 주차장 운영정보 조회
     * @param pageNo 페이지 번호
     * @param numOfRows 한 페이지 결과 수
     * @return 주차장 운영정보 응답
     */
    PrkOprInfoResponse getPrkOprInfo(int pageNo, int numOfRows);
} 