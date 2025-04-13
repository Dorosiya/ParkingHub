package com.example.parking_hub.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrkSttusInfoResponse {
    
    private String resultCode;
    private String resultMsg;
    private String numOfRows;
    private String pageNo;
    private String totalCount;
    
    @JsonProperty("PrkSttusInfo")
    private List<PrkSttusInfo> prkSttusInfo;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PrkSttusInfo {
        
        @JsonProperty("prk_center_id")
        private String prkCenterId;
        
        @JsonProperty("prk_center_nm")
        private String prkCenterNm;
        
        @JsonProperty("prk_plce_nm")
        private String prkPlceNm;
        
        @JsonProperty("prk_plce_adres")
        private String prkPlceAdres;
        
        @JsonProperty("prk_plce_entrc_la")
        private Double latitude;
        
        @JsonProperty("prk_plce_entrc_lo")
        private Double longitude;
        
        @JsonProperty("prk_cmprt_co")
        private Integer prkCmprtCo;
        
        @JsonProperty("prk_plce_se")
        private String prkPlceSe;
        
        @JsonProperty("prk_plce_adres_sido")
        private String prkPlceAdresSido;
        
        @JsonProperty("prk_plce_adres_sigungu")
        private String prkPlceAdresSigungu;

        // 기본 getter 메서드들
        public String getPrkCenterId() {
            return prkCenterId;
        }
        
        public String getPrkCenterNm() {
            return prkCenterNm;
        }
        
        public String getPrkPlceNm() {
            return prkPlceNm;
        }
        
        public String getPrkPlceAdres() {
            return prkPlceAdres;
        }
        
        public Double getLatitude() {
            return latitude;
        }
        
        public Double getLongitude() {
            return longitude;
        }
        
        public Integer getPrkCmprtCo() {
            return prkCmprtCo;
        }
        
        public String getPrkPlceSe() {
            return prkPlceSe;
        }
        
        public String getPrkPlceAdresSido() {
            return prkPlceAdresSido;
        }
        
        public String getPrkPlceAdresSigungu() {
            return prkPlceAdresSigungu;
        }
    }
    
    // ParkingDataSyncService에서 사용하는 getItems() 메서드와의 호환성 유지
    public List<PrkSttusInfo> getItems() {
        return prkSttusInfo;
    }
} 