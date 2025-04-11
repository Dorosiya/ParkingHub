package com.example.parking_hub.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrkSttusInfoResponse {
    
    @JsonProperty("response")
    private Response response;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        @JsonProperty("header")
        private Header header;
        
        @JsonProperty("body")
        private Body body;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {
        @JsonProperty("resultCode")
        private String resultCode;
        
        @JsonProperty("resultMsg")
        private String resultMsg;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        @JsonProperty("items")
        private Items items;
        
        @JsonProperty("numOfRows")
        private Integer numOfRows;
        
        @JsonProperty("pageNo")
        private Integer pageNo;
        
        @JsonProperty("totalCount")
        private Integer totalCount;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        @JsonProperty("item")
        private List<PrkSttusInfo> item;
    }
    
    // 기존 메서드들을 호환성 유지를 위해 제공
    public String getResultCode() {
        if (response != null && response.getHeader() != null) {
            return response.getHeader().getResultCode();
        }
        return null;
    }
    
    public String getResultMsg() {
        if (response != null && response.getHeader() != null) {
            return response.getHeader().getResultMsg();
        }
        return null;
    }
    
    public Integer getNumOfRows() {
        if (response != null && response.getBody() != null) {
            return response.getBody().getNumOfRows();
        }
        return null;
    }
    
    public Integer getPageNo() {
        if (response != null && response.getBody() != null) {
            return response.getBody().getPageNo();
        }
        return null;
    }
    
    public Integer getTotalCount() {
        if (response != null && response.getBody() != null) {
            return response.getBody().getTotalCount();
        }
        return null;
    }
    
    public List<PrkSttusInfo> getItems() {
        if (response != null && response.getBody() != null && 
            response.getBody().getItems() != null) {
            return response.getBody().getItems().getItem();
        }
        return null;
    }
    
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