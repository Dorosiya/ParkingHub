package com.example.parking_hub.client.impl;

import com.example.parking_hub.client.ParkingApiClient;
import com.example.parking_hub.dto.api.PrkSttusInfoResponse;
import com.example.parking_hub.dto.api.PrkOprInfoResponse;
import com.example.parking_hub.dto.api.PrkRealtimeInfoResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

/**
 * 한국교통안전공단 주차장 API 클라이언트 구현체
 */
@Component
@RequiredArgsConstructor
public class KotsaParkingApiClient implements ParkingApiClient {

    private static final Logger logger = LoggerFactory.getLogger(KotsaParkingApiClient.class);
    
    private final RestTemplate restTemplate;
    
    @Value("${kotsa.api.key}")
    private String apiKey;
    
    // API 명세에 맞게 URL 수정
    private static final String BASE_URL = "http://apis.data.go.kr/B553881/Parking";
    private static final String PRK_STTUS_INFO_PATH = "/PrkSttusInfo";
    private static final String PRK_OPR_INFO_PATH = "/PrkOprInfo";
    private static final String PRK_REALTIME_INFO_PATH = "/PrkRealtimeInfo";

    @Override
    public PrkSttusInfoResponse getPrkSttusInfo(int pageNo, int numOfRows) {
        URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL + PRK_STTUS_INFO_PATH)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo)
                .queryParam("format", 2) // JSON 형식: format=2
                .build(false) // false = 인코딩 하지 않음
                .toUri();
                
        // serviceKey는 별도로 추가 (인코딩 방지)
        String url = uri.toString() + "&serviceKey=" + apiKey;
        
        logger.info("주차장 기본정보 API 호출: {}", url);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<PrkSttusInfoResponse> response = 
                restTemplate.exchange(url, HttpMethod.GET, entity, PrkSttusInfoResponse.class);
            
            return response.getBody();
        } catch (Exception e) {
            logger.error("주차장 기본정보 API 호출 중 오류 발생", e);
            throw new RuntimeException("주차장 기본정보 API, 호출 실패", e);
        }
    }

    @Override
    public PrkOprInfoResponse getPrkOprInfo(int pageNo, int numOfRows) {
        URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL + PRK_OPR_INFO_PATH)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo)
                .queryParam("format", 2) // JSON 형식: format=2
                .build(false) // false = 인코딩 하지 않음
                .toUri();
                
        // serviceKey는 별도로 추가 (인코딩 방지)
        String url = uri.toString() + "&serviceKey=" + apiKey;
        
        logger.info("주차장 운영정보 API 호출: {}", url);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<PrkOprInfoResponse> response = 
                restTemplate.exchange(url, HttpMethod.GET, entity, PrkOprInfoResponse.class);
            
            return response.getBody();
        } catch (Exception e) {
            logger.error("주차장 운영정보 API 호출 중 오류 발생", e);
            throw new RuntimeException("주차장 운영정보 API 호출 실패", e);
        }
    }

    @Override
    public PrkRealtimeInfoResponse getPrkRealtimeInfo(int pageNo, int numOfRows) {
        URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL + PRK_REALTIME_INFO_PATH)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo)
                .queryParam("format", 2) // JSON 형식: format=2
                .build(false) // false = 인코딩 하지 않음
                .toUri();
                
        // serviceKey는 별도로 추가 (인코딩 방지)
        String url = uri.toString() + "&serviceKey=" + apiKey;
        
        logger.info("주차장 실시간정보 API 호출: {}", url);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<PrkRealtimeInfoResponse> response = 
                restTemplate.exchange(url, HttpMethod.GET, entity, PrkRealtimeInfoResponse.class);
            
            return response.getBody();
        } catch (Exception e) {
            logger.error("주차장 실시간정보 API 호출 중 오류 발생", e);
            throw new RuntimeException("주차장 실시간정보 API 호출 실패", e);
        }
    }
} 