package com.example.parking_hub.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrkRealtimeInfoResponse {
    
    private String resultCode;
    private String resultMsg;
    private String numOfRows;
    private String pageNo;
    private String totalCount;
    
    @JsonProperty("PrkRealtimeInfo")
    private List<PrkRealtimeItem> prkRealtimeInfo;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PrkRealtimeItem {
        @JsonProperty("prk_center_id")
        private String prkCenterId;
        
        @JsonProperty("pkfc_ParkingLots_total")
        private Integer pkfcParkingLotsTotal;
        
        @JsonProperty("pkfc_Available_ParkingLots_total")
        private Integer pkfcAvailableParkingLotsTotal;
        
        // 기본 getter 메서드들
        public String getPrkCenterId() {
            return prkCenterId;
        }
        
        public Integer getPkfcParkingLotsTotal() {
            return pkfcParkingLotsTotal;
        }
        
        public Integer getPkfcAvailableParkingLotsTotal() {
            return pkfcAvailableParkingLotsTotal;
        }
    }
    
    // ParkingDataSyncService에서 사용하는 getItems() 메서드와의 호환성 유지
    public List<PrkRealtimeItem> getItems() {
        return prkRealtimeInfo;
    }
} 