package com.example.parking_hub.client.impl;

import com.example.parking_hub.client.ParkingApiClient;
import com.example.parking_hub.dto.api.PrkSttusInfoResponse;
import com.example.parking_hub.dto.api.PrkOprInfoResponse;
import com.example.parking_hub.dto.api.PrkRealtimeInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 한국교통안전공단 주차장 API 클라이언트 구현체
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KotsaParkingApiClient implements ParkingApiClient {

    private final RestTemplate restTemplate;
    
    @Value("${kotsa.api.key}")
    private String apiKey;
    
    private static final String BASE_URL = "https://apis.data.go.kr/1613000/ParkingInfoService";
    private static final String PRK_STTUS_INFO_PATH = "/getPrkSttusInfo";
    private static final String PRK_OPR_INFO_PATH = "/getPrkOprInfo";
    private static final String PRK_REALTIME_INFO_PATH = "/getPrkRealtimeInfo";

    @Override
    public PrkSttusInfoResponse getPrkSttusInfo(int pageNo, int numOfRows) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + PRK_STTUS_INFO_PATH)
                .queryParam("serviceKey", apiKey)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo)
                .queryParam("type", "json")
                .build()
                .toUriString();
        
        log.info("주차장 기본정보 API 호출: {}", url);
        
        try {
            return restTemplate.getForObject(url, PrkSttusInfoResponse.class);
        } catch (Exception e) {
            log.error("주차장 기본정보 API 호출 중 오류 발생", e);
            throw new RuntimeException("주차장 기본정보 API 호출 실패", e);
        }
    }

    @Override
    public PrkOprInfoResponse getPrkOprInfo(int pageNo, int numOfRows) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + PRK_OPR_INFO_PATH)
                .queryParam("serviceKey", apiKey)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo)
                .queryParam("type", "json")
                .build()
                .toUriString();
        
        log.info("주차장 운영정보 API 호출: {}", url);
        
        try {
            return restTemplate.getForObject(url, PrkOprInfoResponse.class);
        } catch (Exception e) {
            log.error("주차장 운영정보 API 호출 중 오류 발생", e);
            throw new RuntimeException("주차장 운영정보 API 호출 실패", e);
        }
    }

    @Override
    public PrkRealtimeInfoResponse getPrkRealtimeInfo(int pageNo, int numOfRows) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + PRK_REALTIME_INFO_PATH)
                .queryParam("serviceKey", apiKey)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo)
                .queryParam("type", "json")
                .build()
                .toUriString();
        
        log.info("주차장 실시간정보 API 호출: {}", url);
        
        try {
            return restTemplate.getForObject(url, PrkRealtimeInfoResponse.class);
        } catch (Exception e) {
            log.error("주차장 실시간정보 API 호출 중 오류 발생", e);
            throw new RuntimeException("주차장 실시간정보 API 호출 실패", e);
        }
    }
} 