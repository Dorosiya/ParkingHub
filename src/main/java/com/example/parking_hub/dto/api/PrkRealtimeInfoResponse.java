package com.example.parking_hub.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PrkRealtimeInfoResponse {
    private String resultCode;
    private String resultMsg;
    private int numOfRows;
    private int pageNo;
    private int totalCount;
    
    @JsonProperty("PrkRealtimeInfo")
    private List<PrkRealtimeInfo> prkRealtimeInfoList;

    @Getter
    @Setter
    public static class PrkRealtimeInfo {
        private String prkCenterId;
        private int pkfcParkingLotsTotal;        // 총 주차 구획 수
        private int pkfcAvailableParkingLotsTotal; // 주차 가능 구획 수
    }
} 