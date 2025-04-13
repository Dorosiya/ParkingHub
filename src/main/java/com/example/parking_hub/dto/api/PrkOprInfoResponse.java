package com.example.parking_hub.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrkOprInfoResponse {
    
    private String resultCode;
    private String resultMsg;
    private String numOfRows;
    private String pageNo;
    private String totalCount;
    
    @JsonProperty("PrkOprInfo")
    private List<PrkOprInfo> prkOprInfo;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PrkOprInfo {
        
        @JsonProperty("prk_center_id")
        private String prkCenterId;
        
        @JsonProperty("opertn_bs_free_time")
        private String opertnBsFreeTime;
        
        @JsonProperty("parking_chrge_bs_time")
        private String parkingChrgeBsTime;
        
        @JsonProperty("parking_chrge_bs_chrg")
        private String parkingChrgeBsChrg;
        
        @JsonProperty("operation_day_info")
        private String operationDayInfo;
        
        @JsonProperty("sat_oper_open_hhmm")
        private String satOperOpenHhmm;
        
        @JsonProperty("sat_oper_close_hhmm")
        private String satOperCloseHhmm;
        
        @JsonProperty("holiday_oper_open_hhmm")
        private String holidayOperOpenHhmm;
        
        @JsonProperty("holiday_oper_close_hhmm")
        private String holidayOperCloseHhmm;
        
        // 기본 getter 메서드들
        public String getPrkCenterId() {
            return prkCenterId;
        }
        
        public String getOpertnBsFreeTime() {
            return opertnBsFreeTime;
        }
        
        public String getParkingChrgeBsTime() {
            return parkingChrgeBsTime;
        }
        
        public String getParkingChrgeBsChrg() {
            return parkingChrgeBsChrg;
        }
        
        public String getOperationDayInfo() {
            return operationDayInfo;
        }
        
        public String getSatOperOpenHhmm() {
            return satOperOpenHhmm;
        }
        
        public String getSatOperCloseHhmm() {
            return satOperCloseHhmm;
        }
        
        public String getHolidayOperOpenHhmm() {
            return holidayOperOpenHhmm;
        }
        
        public String getHolidayOperCloseHhmm() {
            return holidayOperCloseHhmm;
        }
    }
    
    // ParkingDataSyncService에서 사용하는 getItems() 메서드와의 호환성 유지
    public List<PrkOprInfo> getItems() {
        return prkOprInfo;
    }
} 