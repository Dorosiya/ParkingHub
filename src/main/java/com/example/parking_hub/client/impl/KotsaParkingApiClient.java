package com.example.parking_hub.client.impl;

import com.example.parking_hub.client.ParkingApiClient;
import com.example.parking_hub.dto.api.PrkOprInfoResponse;
import com.example.parking_hub.dto.api.PrkRealtimeInfoResponse;
import com.example.parking_hub.dto.api.PrkSttusInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;
import java.net.SocketTimeoutException;

/**
 * 한국교통안전공단 주차장 API 클라이언트 구현체
 */
@Component
public class KotsaParkingApiClient implements ParkingApiClient {

    private static final Logger logger = LoggerFactory.getLogger(KotsaParkingApiClient.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${kotsa.api.key}")
    private String apiKey;

    // API 명세에 맞게 URL 수정
    private static final String BASE_URL = "http://apis.data.go.kr/B553881/Parking";
    private static final String PRK_STTUS_INFO_PATH = "/PrkSttusInfo";
    private static final String PRK_OPR_INFO_PATH = "/PrkOprInfo";
    private static final String PRK_REALTIME_INFO_PATH = "/PrkRealtimeInfo";

    @Override
    public PrkSttusInfoResponse getPrkSttusInfo(int pageNo, int numOfRows) {
        return fetchApiData(PRK_STTUS_INFO_PATH, pageNo, numOfRows, PrkSttusInfoResponse.class, "주차장 기본정보");
    }

    @Override
    public PrkOprInfoResponse getPrkOprInfo(int pageNo, int numOfRows) {
        return fetchApiData(PRK_OPR_INFO_PATH, pageNo, numOfRows, PrkOprInfoResponse.class, "주차장 운영정보");
    }

    @Override
    public PrkRealtimeInfoResponse getPrkRealtimeInfo(int pageNo, int numOfRows) {
        return fetchApiData(PRK_REALTIME_INFO_PATH, pageNo, numOfRows, PrkRealtimeInfoResponse.class, "주차장 실시간정보");
    }
    
    @Override
    public PrkSttusInfoResponse getPrkSttusInfoById(String prkCenterId) {
        logger.info("주차장 ID로 기본정보 조회: {}", prkCenterId);
        try {
            String encodedKey = URLEncoder.encode(apiKey, "UTF-8");

            URI uri = UriComponentsBuilder
                    .fromHttpUrl(BASE_URL + PRK_STTUS_INFO_PATH)
                    .queryParam("numOfRows", 10)
                    .queryParam("pageNo", 1)
                    .queryParam("format", 2)
                    .queryParam("serviceKey", encodedKey)
                    .queryParam("parkingId", prkCenterId) // 주차장 ID로 필터링
                    .build(true)
                    .toUri();

            logger.info("주차장 ID 기본정보 API 호출: {}", uri);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 타임아웃 발생 시 재시도 로직
            int maxRetries = 2; // 최대 2번까지 재시도
            int retryCount = 0;
            ResponseEntity<String> rawResponse = null;
            
            while (retryCount <= maxRetries) {
                try {
                    rawResponse = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
                    // 성공하면 재시도 루프 종료
                    break;
                } catch (ResourceAccessException e) {
                    retryCount++;
                    // 마지막 시도에서도 실패한 경우 예외 다시 던지기
                    if (retryCount > maxRetries) {
                        throw e;
                    }
                    
                    logger.warn("주차장 ID 기본정보 API 호출 중 타임아웃 발생, 재시도 ({}/{})", retryCount, maxRetries);
                    
                    // 지수 백오프로 재시도 전 대기 (1초, 2초, 4초...)
                    try {
                        Thread.sleep((long) Math.pow(2, retryCount - 1) * 1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (rawResponse == null) {
                throw new RuntimeException("주차장 ID 기본정보 API 응답을 받지 못했습니다.");
            }

            String rawBody = rawResponse.getBody();
            MediaType contentType = rawResponse.getHeaders().getContentType();
            logger.debug("주차장 ID 기본정보 API 원본 응답: {}", rawBody);

            // 공공 데이터 오류 응답 감지
            if (rawBody != null && rawBody.contains("<OpenAPI_ServiceResponse>")) {
                throw new RuntimeException("API 키 오류 또는 서비스 오류 발생\n응답: " + rawBody);
            }

            // JSON 응답 처리
            if (rawBody != null && (rawBody.trim().startsWith("{") || rawBody.trim().startsWith("["))) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(rawBody, PrkSttusInfoResponse.class);
            } else {
                throw new RuntimeException("예상한 JSON 형식이 아닙니다: " + contentType + "\n본문: " + rawBody);
            }

        } catch (Exception e) {
            logger.error("주차장 ID 기본정보 API 호출 중 오류 발생", e);
            throw new RuntimeException("주차장 ID 기본정보 API 호출 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 공통 API 호출 처리 메서드
     */
    private <T> T fetchApiData(String path, int pageNo, int numOfRows, Class<T> responseType, String logName) {
        try {
            String encodedKey = URLEncoder.encode(apiKey, "UTF-8");

            URI uri = UriComponentsBuilder
                    .fromHttpUrl(BASE_URL + path)
                    .queryParam("numOfRows", numOfRows)
                    .queryParam("pageNo", pageNo)
                    .queryParam("format", 2)
                    .queryParam("serviceKey", encodedKey) // 인코딩된 키 사용
                    .build(true)
                    .toUri();

            logger.info("{} API 호출: {}", logName, uri);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 타임아웃 발생 시 재시도 로직
            int maxRetries = 2; // 최대 2번까지 재시도
            int retryCount = 0;
            ResponseEntity<String> rawResponse = null;
            
            while (retryCount <= maxRetries) {
                try {
                    rawResponse = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
                    // 성공하면 재시도 루프 종료
                    break;
                } catch (ResourceAccessException e) {
                    retryCount++;
                    // 마지막 시도에서도 실패한 경우 예외 다시 던지기
                    if (retryCount > maxRetries) {
                        throw e;
                    }
                    
                    logger.warn("{} API 호출 중 타임아웃 발생, 재시도 ({}/{})", logName, retryCount, maxRetries);
                    
                    // 지수 백오프로 재시도 전 대기 (1초, 2초, 4초...)
                    try {
                        Thread.sleep((long) Math.pow(2, retryCount - 1) * 1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (rawResponse == null) {
                throw new RuntimeException(logName + " API 응답을 받지 못했습니다.");
            }

            String rawBody = rawResponse.getBody();
            MediaType contentType = rawResponse.getHeaders().getContentType();
            logger.debug("API 원본 응답: {}", rawBody);
            logger.debug("응답 Content-Type: {}", contentType);

            // 공공 데이터 오류 응답 감지
            if (rawBody != null && rawBody.contains("<OpenAPI_ServiceResponse>")) {
                throw new RuntimeException("API 키 오류 또는 서비스 오류 발생\n응답: " + rawBody);
            }

            // JSON 응답 처리
            if (rawBody != null && (rawBody.trim().startsWith("{") || rawBody.trim().startsWith("["))) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(rawBody, responseType);
            } else {
                throw new RuntimeException("예상한 JSON 형식이 아닙니다: " + contentType + "\n본문: " + rawBody);
            }

        } catch (Exception e) {
            logger.error("{} API 호출 중 오류 발생", logName, e);
            
            // 타임아웃 관련 예외에 대한 더 자세한 메시지
            if (e instanceof ResourceAccessException && e.getCause() instanceof SocketTimeoutException) {
                throw new RuntimeException(logName + " API 호출 중 타임아웃 발생 - API 서버가 응답하지 않거나 네트워크 문제가 있습니다.", e);
            }
            
            throw new RuntimeException(logName + " API 호출 실패: " + e.getMessage(), e);
        }
    }
}