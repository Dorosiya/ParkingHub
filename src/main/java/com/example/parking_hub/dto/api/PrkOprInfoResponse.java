package com.example.parking_hub.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrkOprInfoResponse {
    
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
    private List<PrkOprInfo> items;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PrkOprInfo {
        
        @JsonProperty("prkCenterId")
        private String prkCenterId;
        
        @JsonProperty("opertnBsFreeTime")
        private String opertnBsFreeTime;
        
        @JsonProperty("parkingChrgeBsTime")
        private String parkingChrgeBsTime;
        
        @JsonProperty("parkingChrgeBsChrg")
        private String parkingChrgeBsChrg;
        
        @JsonProperty("operationDayInfo")
        private String operationDayInfo;
        
        @JsonProperty("satOperOpenHhmm")
        private String satOperOpenHhmm;
        
        @JsonProperty("satOperCloseHhmm")
        private String satOperCloseHhmm;
        
        @JsonProperty("holidayOperOpenHhmm")
        private String holidayOperOpenHhmm;
        
        @JsonProperty("holidayOperCloseHhmm")
        private String holidayOperCloseHhmm;
    }
} 