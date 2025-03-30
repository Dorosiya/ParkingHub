package com.example.parking_hub.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrkSttusInfoResponse {
    
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
    private List<PrkSttusInfo> items;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PrkSttusInfo {
        
        @JsonProperty("prkCenterId")
        private String prkCenterId;
        
        @JsonProperty("prkCenterNm")
        private String prkCenterNm;
        
        @JsonProperty("prkCenterType")
        private String prkCenterType;
        
        @JsonProperty("rdnmadr")
        private String rdnmadr;
        
        @JsonProperty("lnmadr")
        private String lnmadr;
        
        @JsonProperty("latitude")
        private Double latitude;
        
        @JsonProperty("longitude")
        private Double longitude;
        
        @JsonProperty("parkingLotCount")
        private Integer parkingLotCount;
    }
} 