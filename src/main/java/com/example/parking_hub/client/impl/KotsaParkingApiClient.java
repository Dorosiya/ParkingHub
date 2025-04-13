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
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;

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
                    .queryParam("serviceKey", encodedKey) // 디코딩 키 그대로 사용
                    .build(true)
                    .toUri();

            logger.info("{} API 호출: {}", logName, uri);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> rawResponse =
                    restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

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
            throw new RuntimeException(logName + " API 호출 실패: " + e.getMessage(), e);
        }
    }
}