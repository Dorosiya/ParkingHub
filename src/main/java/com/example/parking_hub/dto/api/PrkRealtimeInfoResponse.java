package com.example.parking_hub.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrkRealtimeInfoResponse {
    
    @JsonProperty("resultCode")
    private String resultCode;
    
    @JsonProperty("resultMsg")
    private String resultMsg;
    
    @JsonProperty("numOfRows")
    private Integer numOfRows;
    
    @JsonProperty("pageNo")
    private Integer pageNo;
    
    @JsonProperty("totalCount")
    private Integer totalCount;
    
    @JsonProperty("items")
    private List<PrkRealtimeInfo> items;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PrkRealtimeInfo {
        
        @JsonProperty("prkCenterId")
        private String prkCenterId;
        
        @JsonProperty("pkfcParkingLotsTotal")
        private Integer pkfcParkingLotsTotal;
        
        @JsonProperty("pkfcAvailableParkingLotsTotal")
        private Integer pkfcAvailableParkingLotsTotal;
    }
} 