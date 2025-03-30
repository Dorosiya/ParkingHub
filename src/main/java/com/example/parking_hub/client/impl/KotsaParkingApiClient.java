package com.example.parking_hub.client.impl;

import com.example.parking_hub.client.ParkingApiClient;
import com.example.parking_hub.dto.api.PrkSttusInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class KotsaParkingApiClient implements ParkingApiClient {

    private static final String BASE_URL = "http://apis.data.go.kr/B553881/Parking";

    @Value("${kotsa.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public KotsaParkingApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public PrkSttusInfoResponse getPrkSttusInfo(int pageNo, int numOfRows) {
        String url = UriComponentsBuilder.fromUriString(BASE_URL + "/PrkSttusInfo")
                .queryParam("serviceKey", apiKey)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("format", "json")
                .build()
                .toUriString();

        log.info("Requesting PrkSttusInfo API: {}", url);
        return restTemplate.getForObject(url, PrkSttusInfoResponse.class);
    }
} 